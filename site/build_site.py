#!/usr/bin/env python3
"""Build a static GitHub Pages site for ModularGolems.

Reads the mod's Patchouli book, material configs, lang files, recipes and item
textures from src/ and writes a self-contained static site into docs/.

Run from the repo root:  python3 site/build_site.py
"""

import hashlib
import html
import json
import re
import shutil
import struct
import zlib
from pathlib import Path
from string import Template

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "docs"
SRC_ASSETS = ROOT / "src/main/resources/assets/modulargolems"
SRC_GEN = ROOT / "src/generated/resources/assets/modulargolems"
SRC_GEN_DATA = ROOT / "src/generated/resources/data"
BOOK_DIR = SRC_ASSETS / "patchouli_books/golem_guide"
TEX_DIR = SRC_ASSETS / "textures/item"
VENDOR_TEX = ROOT / "site/textures"
MODEL_DIR = SRC_GEN / "models/item"

LANGS = ["en", "zh"]
LANG_CODE = {"en": "en_us", "zh": "zh_cn"}


def esc(s):
    return html.escape(str(s), quote=True)


def slugify(s):
    return re.sub(r"[^A-Za-z0-9._-]+", "-", s)


def load_json(path):
    return json.loads(Path(path).read_text(encoding="utf-8"))


def load_lang(code):
    f = SRC_GEN / "lang/en_us.json" if code == "en" else SRC_ASSETS / "lang/zh_cn.json"
    return load_json(f)


LANG = {code: load_lang(code) for code in LANGS}

# Source mod namespace -> {en, zh} display name. Maintained manually in
# site/mod_names.json; used to label material-source groups and upgrade items,
# and emitted as data/mod_names.json so pages/JS can localize mod names.
MOD_NAMES = load_json(ROOT / "site/mod_names.json")


def mod_name(ns, lang):
    """Localized display name for a source-mod namespace, falling back to the raw id."""
    return MOD_NAMES.get(ns, {}).get(lang, ns)


# ---------------------------------------------------------------------------
# HTML/CSS/JS templates, kept as text files under site/templates/. Dynamic bits
# use $-placeholders (string.Template); a single trailing newline is trimmed so
# the rendered output matches what the old inline f-strings produced.
# ---------------------------------------------------------------------------

TEMPLATES = ROOT / "site/templates"


def load_template(name):
    return Template((TEMPLATES / name).read_text(encoding="utf-8").rstrip("\n"))


PAGE_TEMPLATE = load_template("page.html.txt")
INDEX_BODY_TEMPLATE = load_template("index_body.html.txt")
MATERIALS_JS = load_template("materials.js.txt")
ITEMS_OVERLAY = (TEMPLATES / "items_overlay.html.txt").read_text(encoding="utf-8").rstrip("\n")
ITEMS_JS = load_template("items.js.txt")
CSS = (TEMPLATES / "style.css.txt").read_text(encoding="utf-8")

# ---------------------------------------------------------------------------
# External data (site/data/*.json). Data tables are kept out of the script and
# loaded at startup; JSON keys match the Python constants they populate.
# ---------------------------------------------------------------------------

DATA_DIR = ROOT / "site/data"


def load_data(name):
    return load_json(DATA_DIR / f"{name}.json")


# per-modifier %s values for each level, used to fill the %s placeholders in
# modifier descriptions (see modifier_info). Maintained manually: update when
# the modifier config values or level scaling change.
MODIFIER_VALUES = load_json(ROOT / "site/modifier_values.json")

# display names for things not in the mod's own lang files
VANILLA_NAMES = load_data("vanilla_names")
TAG_NAMES = load_data("tag_names")
COMPAT_ITEM_NAMES = load_data("compat_item_names")

# page-builder UI strings (site/data/lang.json)
L10N = load_data("lang")


def tr(key, lang):
    """Translatable UI string lookup, keyed per language."""
    return L10N[key][lang]

# site metadata (version this build is generated for + supported version branches)
SITE_CONFIG = load_data("site_config")
BUILD_VERSION = SITE_CONFIG["build_version"]
PAGES_ROOT = SITE_CONFIG["pages_root"]
VERSIONS = SITE_CONFIG["versions"]

# dispatch class -> material config namespace, mirrored from each *Dispatch.java
DISPATCH_MODID = load_data("dispatch_modid")

# stat labels + formatting kind (BASE / ADD / PERCENT)
STAT_INFO = load_data("stat_info")

# compat construct/cube items referenced under the modulargolems namespace
# whose textures ship under the compat mod's own namespace
MODULAR_ALIAS = load_data("modular_alias")

# forge/mod tag ingredients -> representative item for the icon
TAG_ITEM = load_data("tag_item")

# Minecraft text-formatting color codes
MC_COLORS = load_data("mc_colors")

# page chrome strings
CHROME = load_data("chrome")
SITE_TITLES = CHROME["site_titles"]
NAV_LABELS = CHROME["nav_labels"]
FOOTER = CHROME["footer"]


def current_version():
    for v in VERSIONS:
        if v["label"] == BUILD_VERSION:
            return v
    raise ValueError(f"unknown BUILD_VERSION {BUILD_VERSION}")


COMPAT_FILE = "src/main/java/dev/xkmc/modulargolems/compat/materials/common/CompatManager.java"
UNIVERSAL_NS = {"modulargolems", "minecraft"}


def branch_compat_text(branch):
    """CompatManager.java content for a version branch (working tree for current)."""
    if branch == current_version()["branch"]:
        p = ROOT / COMPAT_FILE
        if p.is_file():
            return p.read_text(encoding="utf-8")
    import subprocess
    for ref in (f"origin/{branch}", branch):
        r = subprocess.run(["git", "show", f"{ref}:{COMPAT_FILE}"],
                           capture_output=True, text=True)
        if r.returncode == 0 and r.stdout.strip():
            return r.stdout
    return None


def compat_mods_for(branch):
    """Supported compat namespaces, from active (non-commented) dispatches in
    the branch's CompatManager.register(). Commented-out calls are ignored, so
    e.g. 26.1 (all commented) yields no compat mods."""
    text = branch_compat_text(branch)
    if text is None:
        return []
    mods = set()
    for ln in text.splitlines():
        if "//" in ln:
            ln = ln.split("//", 1)[0]
        m = re.search(r"new\s+(\w+Dispatch)\s*\(", ln)
        if m and m.group(1) in DISPATCH_MODID:
            mods.add(DISPATCH_MODID[m.group(1)])
    return sorted(mods)


MODEL_SUBDIR = "src/generated/resources/assets/modulargolems/models/item"


def branch_item_models(branch):
    """Item model files (basename stem) present on a version branch.

    The current version reads the working tree; other branches are inspected
    via git ls-tree, so the version lists reflect the models that actually
    exist in each version rather than any texture attribution."""
    if branch == current_version()["branch"]:
        if MODEL_DIR.is_dir():
            return {p.stem for p in MODEL_DIR.glob("*.json")}
        return set()
    import subprocess
    for ref in (branch, f"origin/{branch}"):
        r = subprocess.run(["git", "ls-tree", "-r", "--name-only", ref, "--", MODEL_SUBDIR],
                           capture_output=True, text=True)
        if r.returncode == 0 and r.stdout.strip():
            return {Path(ln).stem for ln in r.stdout.splitlines() if ln}
    return set()


def fmt_num(v):
    return f"{v:g}" if v == int(v) else f"{v:g}"


def fmt_stat(stat_id, val, lang):
    info = STAT_INFO.get(stat_id)
    if info is None:
        return f"{stat_id}: {fmt_num(val)}"
    name = info[lang]
    if info["kind"] == "PERCENT":
        v = val * 100
        s = f"{v:+.0f}" if v == int(v) else f"{v:+.1f}"
        return f"{name} {s}%"
    if info["kind"] == "ADD":
        return f"{name} +{fmt_num(val)}"
    return f"{name}: {fmt_num(val)}"


# ---------------------------------------------------------------------------
# per-page URL roots (asset/book links are relative to the generated page)
# ---------------------------------------------------------------------------

assets_root = lambda: ""
book_root = lambda: ""
data_root = lambda: ""


def _set_roots(page_rel):
    global assets_root, book_root, data_root
    depth = len(Path(page_rel).parts) - 1
    prefix = "../" * depth
    assets_root = lambda: prefix + "assets/"
    book_root = lambda: prefix + "book/"
    data_root = lambda: prefix + "data/"


# ---------------------------------------------------------------------------
# item icons
# ---------------------------------------------------------------------------

_EXISTING_TEX = {}


def find_tex(rel):
    """rel is relative to assets/tex/ root. Check vendored textures first,
    then the mod's own item texture dir (which is mounted at assets/tex/item/)."""
    if (VENDOR_TEX / rel).is_file():
        return True
    if rel.startswith("item/"):
        return (TEX_DIR / rel[len("item/"):]).is_file()
    return False


_MODEL_TEX = {}


def model_layer0(path):
    """Resolve an item's model layer0 texture id, e.g. 'twilightforest:item/equipments/...'.

    For forge:separate_transforms models the inventory icon lives in the nested
    gui perspective (perspectives.gui.textures.layer0), which is what the site
    should show."""
    if path not in _MODEL_TEX:
        m = MODEL_DIR / f"{path}.json"
        tex = None
        if m.is_file():
            try:
                d = load_json(m)
                tex = d.get("textures", {}).get("layer0")
                if tex is None:
                    tex = d.get("perspectives", {}).get("gui", {}).get("textures", {}).get("layer0")
            except Exception:
                tex = None
        _MODEL_TEX[path] = tex
    return _MODEL_TEX[path]


REFERENCED_TEX = {}


def src_texture(ns, tpath):
    """Locate a texture file referenced as '<ns>:<tpath>' in the repo or vendor dir."""
    p = ROOT / f"src/main/resources/assets/{ns}/textures/{tpath}.png"
    if p.is_file():
        return p
    v = VENDOR_TEX / f"{ns}/{tpath}.png"
    if v.is_file():
        return v
    return None


def crop_png_first_frame(data):
    """If `data` is a Minecraft animation strip (a vertical stack of square
    frames, i.e. height is a whole multiple of width and height > width),
    return a new PNG containing only the first frame (the top `width` rows).
    Return None for PNGs that cannot be cropped safely."""
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    pos = 8
    w = h = depth = ctype = interlace = None
    idat = bytearray()
    pre_idat = []
    seen_idat = False
    while pos < len(data):
        ln = struct.unpack(">I", data[pos:pos + 4])[0]
        tag = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + ln]
        if tag == b"IHDR":
            w, h, depth, ctype, _, _, interlace = struct.unpack(">IIBBBBB", chunk)
        elif tag == b"IDAT":
            idat += chunk
            seen_idat = True
        elif not seen_idat and tag != b"IEND":
            pre_idat.append((tag, chunk))
        pos += 12 + ln
    if None in (w, h, depth, ctype, interlace):
        return None
    if interlace != 0 or h <= w or h % w != 0 or depth == 16:
        return None
    if ctype not in (0, 2, 3, 4, 6):
        return None
    try:
        raw = zlib.decompress(bytes(idat))
    except zlib.error:
        return None
    bits = {0: depth, 2: 3 * depth, 3: depth, 4: 2 * depth, 6: 4 * depth}[ctype]
    fbpp = max(1, bits // 8)
    row_px = (w * bits + 7) // 8
    full_row = 1 + row_px
    prev = bytes(row_px)
    rows = []
    for r in range(w):
        seg = raw[r * full_row:(r + 1) * full_row]
        if len(seg) < full_row:
            return None
        ft = seg[0]
        if ft > 4:
            return None
        data_row = seg[1:]
        out = bytearray(row_px)
        for i, x in enumerate(data_row):
            a = out[i - fbpp] if i >= fbpp else 0
            b = prev[i] if prev else 0
            c = prev[i - fbpp] if prev and i >= fbpp else 0
            if ft == 0:
                rv = x
            elif ft == 1:
                rv = x + a
            elif ft == 2:
                rv = x + b
            elif ft == 3:
                rv = x + (a + b) // 2
            else:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                rv = x + pr
            out[i] = rv & 0xFF
        rows.append(bytes(out))
        prev = bytes(out)
    out = bytearray(b"\x89PNG\r\n\x1a\n")

    def chunk(tag, cdata):
        nonlocal out
        out += struct.pack(">I", len(cdata)) + tag + cdata
        out += struct.pack(">I", zlib.crc32(tag + cdata) & 0xFFFFFFFF)

    chunk(b"IHDR", struct.pack(">IIBBBBB", w, w, depth, ctype, 0, 0, 0))
    for tag, cdata in pre_idat:
        chunk(tag, cdata)
    chunk(b"IDAT", zlib.compress(b"".join(b"\x00" + row for row in rows), 9))
    chunk(b"IEND", b"")
    return bytes(out)


def copy_texture(src, dest):
    """Copy a texture into the output tree, cropping animated strips to their
    first frame so they render as a single static sprite instead of the whole
    vertical animation strip."""
    data = src.read_bytes()
    cropped = crop_png_first_frame(data)
    dest.write_bytes(cropped if cropped is not None else data)


# rel -> [src0, src1, ...] textures to blend into a single icon at build time
# (used for items whose model renders several texture layers, e.g. dog armor).
COMPOSITE_TEX = {}


def _png_rgba(data):
    """Decode a non-interlaced 8-bit PNG into (w, h, list of RGBA row bytes).
    Supports color types 6 (RGBA), 2 (RGB) and 4 (grayscale+alpha). Returns
    None for anything else."""
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    pos = 8
    w = h = depth = ctype = interlace = None
    idat = bytearray()
    while pos < len(data):
        ln = struct.unpack(">I", data[pos:pos + 4])[0]
        tag = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + ln]
        if tag == b"IHDR":
            w, h, depth, ctype, _, _, interlace = struct.unpack(">IIBBBBB", chunk)
        elif tag == b"IDAT":
            idat += chunk
        pos += 12 + ln
    if None in (w, h, depth, ctype, interlace) or interlace != 0 or depth != 8:
        return None
    if ctype not in (2, 4, 6):
        return None
    bits = {0: depth, 2: 3 * depth, 3: depth, 4: 2 * depth, 6: 4 * depth}[ctype]
    fbpp = max(1, bits // 8)
    row_px = (w * bits + 7) // 8
    full_row = 1 + row_px
    try:
        raw = zlib.decompress(bytes(idat))
    except zlib.error:
        return None
    prev = bytearray(row_px)
    rows = []
    for r in range(h):
        seg = raw[r * full_row:(r + 1) * full_row]
        if len(seg) < full_row:
            return None
        ft = seg[0]
        if ft > 4:
            return None
        dd = seg[1:]
        out = bytearray(row_px)
        for i, x in enumerate(dd):
            a = out[i - fbpp] if i >= fbpp else 0
            b = prev[i] if prev else 0
            c = prev[i - fbpp] if prev and i >= fbpp else 0
            if ft == 0:
                rv = x
            elif ft == 1:
                rv = x + a
            elif ft == 2:
                rv = x + b
            elif ft == 3:
                rv = x + (a + b) // 2
            else:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                rv = x + pr
            out[i] = rv & 0xFF
        rows.append(bytes(out))
        prev = out
    rgba = []
    for row in rows:
        if ctype == 6:
            rgba.append(row)
        elif ctype == 2:
            buf = bytearray()
            for i in range(0, len(row), 3):
                buf += row[i:i + 3] + b"\xff"
            rgba.append(bytes(buf))
        else:
            buf = bytearray()
            for i in range(0, len(row), 2):
                g, al = row[i], row[i + 1]
                buf += bytes((g, g, g, al))
            rgba.append(bytes(buf))
    return w, h, rgba


def _write_png_rgba(w, h, rows):
    out = bytearray(b"\x89PNG\r\n\x1a\n")

    def chunk(tag, cdata):
        nonlocal out
        out += struct.pack(">I", len(cdata)) + tag + cdata
        out += struct.pack(">I", zlib.crc32(tag + cdata) & 0xFFFFFFFF)

    chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    chunk(b"IDAT", zlib.compress(b"".join(b"\x00" + row for row in rows), 9))
    chunk(b"IEND", b"")
    return bytes(out)


def composite_textures(srcs, dest):
    """Blend several RGBA textures (bottom to top) into a single PNG, using
    standard alpha-over compositing. Returns False if any source can't be
    decoded; otherwise writes dest and returns True."""
    imgs = [_png_rgba(p.read_bytes()) for p in srcs]
    if any(i is None for i in imgs):
        return False
    w, h, rows = imgs[0]
    for wi, hi, _ in imgs[1:]:
        if wi != w or hi != h:
            return False
    out_rows = []
    for y in range(h):
        row = bytearray()
        for x in range(w):
            o = x * 4
            ar, ag, ab, aa = imgs[0][2][y][o:o + 4]
            for _, _, layer in imgs[1:]:
                br, bg, bb, ba = layer[y][o:o + 4]
                a0, a1 = aa / 255.0, ba / 255.0
                oa = a1 + a0 * (1 - a1)
                if oa <= 0:
                    ar = ag = ab = aa = 0
                else:
                    ar = round((br * a1 + ar * a0 * (1 - a1)) / oa)
                    ag = round((bg * a1 + ag * a0 * (1 - a1)) / oa)
                    ab = round((bb * a1 + ab * a0 * (1 - a1)) / oa)
                    aa = round(oa * 255)
            row += bytes((ar, ag, ab, aa))
        out_rows.append(bytes(row))
    dest.write_bytes(_write_png_rgba(w, h, out_rows))
    return True


def dog_armor_composite(path):
    """Dog golem armor renders two layered textures (layer0 = collar, layer1 =
    wolf armor). Composite them into a single icon texture."""
    m = MODEL_DIR / f"{path}.json"
    if not m.is_file():
        return None
    try:
        layers = load_json(m).get("textures", {})
    except Exception:
        return None
    srcs = []
    for t in (layers.get("layer0"), layers.get("layer1")):
        if not t:
            return None
        tns, tpath = t.split(":", 1)
        src = src_texture(tns, tpath)
        if src is None:
            return None
        srcs.append(src)
    rel = f"item/{path}.png"
    COMPOSITE_TEX[rel] = srcs
    return rel, None


def resolve_icon(reg_id):
    reg_id = MODULAR_ALIAS.get(reg_id, reg_id)
    ns, path = reg_id.split(":", 1)
    vend = f"{ns}/item/{path}.png"
    if find_tex(vend):
        return vend, None
    if ns == "modulargolems":
        if path.endswith("_dog_golem_armor"):
            comp = dog_armor_composite(path)
            if comp:
                return comp
        mt = model_layer0(path)
        if mt:
            tns, tpath = mt.split(":", 1)
            src = src_texture(tns, tpath)
            if src:
                rel = f"{tns}/{tpath}.png"
                REFERENCED_TEX[rel] = src
                return rel, None
        cands = [f"{path}.png"]
        if path.endswith("_config_card"):
            cands.append(f"card/{path[:-len('_config_card')]}.png")
        if path.endswith("_dog_golem_armor"):
            cands.append(f"dog_armor/{path[:-len('_dog_golem_armor')]}_wolf_armor.png")
        if path.startswith("omnipotent_wand_"):
            cands.append("omnipotent_wand.png")
        cands += [f"equipments/{path}.png", f"equipments/{path}_icon.png",
                  f"upgrades/{path}.png", f"card/{path}.png", f"dog_armor/{path}.png"]
        seen = set()
        for c in cands:
            if c in seen:
                continue
            seen.add(c)
            rel = f"item/{c}"
            if find_tex(rel):
                return rel, None
        return None, slugify(f"{ns}-{path}")
    return None, slugify(f"{ns}-{path}")


def item_name(reg_id, lang):
    ns, path = reg_id.split(":", 1)
    if ns == "modulargolems":
        return (LANG[lang].get(f"item.modulargolems.{path}")
                or LANG["en"].get(f"item.modulargolems.{path}") or path)
    compat = COMPAT_ITEM_NAMES.get(reg_id)
    if compat:
        return compat[lang]
    return VANILLA_NAMES[lang].get(reg_id, TAG_NAMES[lang].get(reg_id, reg_id))


def icon_markup(reg_id, lang, size=32, show_name=False):
    rel, slug = resolve_icon(reg_id)
    name = item_name(reg_id, lang)
    if rel:
        src = f"{assets_root()}tex/{rel}"
    else:
        ensure_placeholder(slug)
        src = f"{assets_root()}img/ph-{slug}.svg"
    img = (f'<img class="itemimg" width="{size}" height="{size}" loading="lazy" '
           f'src="{src}" alt="{esc(name)}" title="{esc(name)}">')
    if show_name:
        return (f'<span class="slot named" title="{esc(reg_id)}">{img}'
                f'<span class="slotname">{esc(name)}</span></span>')
    return img


def placeholder_img(slug, label):
    slug = slugify(slug)
    ensure_placeholder(slug)
    return (f'<img class="itemimg" width="32" height="32" loading="lazy" '
            f'src="{assets_root()}img/ph-{slug}.svg" alt="{esc(label)}" title="{esc(label)}">')


# ---------------------------------------------------------------------------
# placeholder SVG generation
# ---------------------------------------------------------------------------

def placeholder_color(seed):
    h = hashlib.md5(seed.encode()).digest()
    hue = h[0]
    sat = 40 + h[1] % 25
    light = 42 + h[2] % 18
    return f"hsl({hue}, {sat}%, {light}%)", f"hsl({hue}, {sat}%, {light + 16}%)"


def placeholder_svg(slug, label=None):
    base, hi = placeholder_color(slug)
    txt = label or "?"
    return f"""<svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 64 64">
<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
<stop offset="0" stop-color="{base}"/><stop offset="1" stop-color="{hi}"/>
</linearGradient></defs>
<rect x="3" y="3" width="58" height="58" rx="9" fill="url(#g)" stroke="rgba(0,0,0,0.45)" stroke-width="3"/>
<text x="32" y="42" font-family="sans-serif" font-size="26" font-weight="700" text-anchor="middle" fill="rgba(0,0,0,0.35)">{esc(txt)}</text>
<text x="30" y="40" font-family="sans-serif" font-size="26" font-weight="700" text-anchor="middle" fill="#fff">{esc(txt)}</text>
</svg>"""


def ensure_placeholder(slug):
    slug = slugify(slug)
    out = OUT / "assets/img" / f"ph-{slug}.svg"
    if not out.exists():
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(placeholder_svg(slug), encoding="utf-8")


# ---------------------------------------------------------------------------
# patchouli text format -> html
# ---------------------------------------------------------------------------

def patchouli_text(text, link_resolver=None):
    if not text:
        return ""
    out = []
    stack = []
    in_list = False

    def close_ul():
        nonlocal in_list
        if in_list:
            out.append("</ul>")
            in_list = False

    def pop_all():
        while stack:
            close_ul()
            out.append(stack.pop())

    def handle_code(tok):
        nonlocal in_list
        inner = tok[2:-1]
        if inner == "br":
            close_ul()
            out.append("<br>")
        elif inner == "br2":
            close_ul()
            out.append("<br><br>")
        elif inner == "li":
            if not in_list:
                out.append('<ul class="bullets">')
                in_list = True
            out.append("<li>")
        elif inner.startswith("l:"):
            close_ul()
            target = inner[2:]
            url = target
            if link_resolver is not None:
                resolved = link_resolver(target)
                if resolved:
                    url = resolved
            out.append(f'<a href="{esc(url)}">')
            stack.append("</a>")
        elif inner == "/l":
            close_ul()
            for i in range(len(stack) - 1, -1, -1):
                if stack[i] == "</a>":
                    out.append("</a>")
                    del stack[i]
                    break
        elif inner == "":
            pop_all()
        elif inner in ("b", "i", "m", "u"):
            out.append(f"<{inner}>")
            stack.append(f"</{inner}>")
        elif inner == "o":
            out.append('<span style="opacity:0">')
            stack.append("</span>")
        elif inner.startswith("#"):
            color = inner[1:]
            if not color.startswith("#"):
                color = "#" + color
            out.append(f'<span style="color:{esc(color)}">')
            stack.append("</span>")
        elif inner in MC_COLORS:
            out.append(f'<span style="color:{MC_COLORS[inner]}">')
            stack.append("</span>")
        else:
            pass  # k:, playername and unknown codes are dropped

    for tok in re.split(r"(\$\([^)]*\))", text):
        if not tok:
            continue
        if tok.startswith("$("):
            handle_code(tok)
        else:
            if tok.strip():
                close_ul()
            out.append(esc(tok))
    pop_all()
    return "".join(out)


# ---------------------------------------------------------------------------
# recipes
# ---------------------------------------------------------------------------

def load_recipe_index():
    by_id = {}
    by_result = {}
    base = SRC_GEN_DATA / "modulargolems/recipes"
    for f in sorted(base.rglob("*.json")):
        try:
            d = load_json(f)
        except Exception:
            continue
        rel = f.relative_to(base).with_suffix("")
        rid = "modulargolems:" + rel.as_posix()
        by_id[rid] = d
        res = d.get("result") or {}
        if "item" in res:
            by_result.setdefault(res["item"], []).append((rid, d))
    return by_id, by_result


RECIPE_BY_ID = {}
RECIPE_BY_RESULT = {}


def render_recipe(recipe, lang):
    rtype = recipe.get("type", "")

    def cell_icon(item_id):
        return icon_markup(item_id, lang, size=32)

    def tag_cell(tag_id):
        name = TAG_NAMES[lang].get(tag_id, tag_id)
        return (f'<span class="slot" title="#{esc(tag_id)}">'
                f'{placeholder_img(slugify(tag_id), name)}</span>')

    if "pattern" in recipe and "key" in recipe:
        grid = {}
        for k, v in recipe.get("key", {}).items():
            if "item" in v:
                grid[k] = ("item", v["item"])
            elif "tag" in v:
                grid[k] = ("tag", v["tag"])
        rows = []
        for row in recipe.get("pattern", []):
            cells = []
            for ch in row:
                if ch == " ":
                    cells.append('<span class="slot empty"></span>')
                elif ch in grid:
                    kind, val = grid[ch]
                    if kind == "item":
                        cells.append(f'<span class="slot" title="{esc(val)}">{cell_icon(val)}</span>')
                    else:
                        cells.append(tag_cell(val))
                else:
                    cells.append('<span class="slot empty"></span>')
            rows.append('<div class="rrow">' + "".join(cells) + "</div>")
        grid_html = '<div class="rgrid">' + "".join(rows) + "</div>"
    else:
        slots = []
        for ing in recipe.get("ingredients", []):
            if "item" in ing:
                slots.append(f'<span class="slot" title="{esc(ing["item"])}">{cell_icon(ing["item"])}</span>')
            elif "tag" in ing:
                slots.append(tag_cell(ing["tag"]))
        grid_html = '<div class="rrow">' + "".join(slots) + "</div>"

    res = recipe.get("result") or {}
    if "item" in res:
        count = res.get("count", 1)
        res_html = f'<span class="slot" title="{esc(res["item"])}">{cell_icon(res["item"])}</span>'
        if count != 1:
            res_html = f'<span class="count">×{count}</span>' + res_html
    else:
        res_html = '<span class="slot empty"></span>'

    return (f'<div class="rwrap"><div class="rleft">{grid_html}</div>'
            f'<span class="rarrow">→</span><div class="rright">{res_html}</div></div>')


# ---------------------------------------------------------------------------
# book loading
# ---------------------------------------------------------------------------

def load_book(lang):
    base = BOOK_DIR / LANG_CODE[lang]
    cat_meta = {}
    for f in (base / "categories").glob("*.json"):
        d = load_json(f)
        d["_id"] = f.stem
        cat_meta[f.stem] = d
    entries = {}
    for f in sorted((base / "entries").rglob("*.json")):
        d = load_json(f)
        eid = f.relative_to(base / "entries").with_suffix("").as_posix()
        d["_id"] = eid
        d["_cat"] = d.get("category", "").split(":")[-1]
        entries[eid] = d
    return cat_meta, entries


def book_link(target, lang, prefix=None):
    target = target.split("#")[0]
    if target.startswith("http"):
        return target
    for cand in [target.strip("/"), target.strip("/").split("/")[-1]]:
        p = BOOK_DIR / LANG_CODE[lang] / "entries" / f"{cand}.json"
        if p.exists():
            root = prefix if prefix is not None else book_root()
            return f"{root}{lang}/{cand}.html"
    return None


# ---------------------------------------------------------------------------
# page chrome
# ---------------------------------------------------------------------------

def nav_html(root, lang, active, page_rel):
    lbl = NAV_LABELS[lang]
    brand_href = "index.html" if lang == "en" else "zh.html"
    links = [
        (brand_href, SITE_TITLES[lang], "brand"),
        (f"book/{lang}/index.html", lbl["guide"], "guide"),
        (f"materials/{lang}/index.html", lbl["materials"], "materials"),
        (f"items/{lang}/index.html", lbl["items"], "items"),
    ]
    buf = ['<nav class="topnav">']
    for href, txt, key in links:
        cls = "active" if key == active else ""
        buf.append(f'<a class="{cls}" href="{root}{href}">{esc(txt)}</a>')
    buf.append('<span class="spacer"></span>')
    buf.append(version_switch_html(lang))
    buf.append(lang_switch_html(root, lang, page_rel))
    buf.append("</nav>")
    return "".join(buf)


def version_switch_html(lang):
    opts = []
    for v in VERSIONS:
        sel = " selected" if v["label"] == BUILD_VERSION else ""
        opts.append(f'<option value="{esc(v["label"])}"{sel}>MC {esc(v["label"])}</option>')
    return (f'<label class="vswitch"><span class="vsr">{tr("version_label", lang)}</span>'
            f'<select class="vselect" data-current="{esc(BUILD_VERSION)}" aria-label="Mod version">'
            + "".join(opts) + "</select></label>")


def lang_switch_html(root, lang, page_rel):
    if page_rel in ("index.html", "zh.html"):
        other = "zh" if lang == "en" else "en"
        other_rel = "zh.html" if lang == "en" else "index.html"
    else:
        other = "zh" if lang == "en" else "en"
        other_rel = page_rel.replace(f"/{lang}/", f"/{other}/", 1)
    label = tr("lang_switch_label", lang)
    return (f'<a class="lang" data-lang="{other}" href="{root}{other_rel}" '
            f'title="{esc(tr("lang_switch_title", lang))}">{esc(label)}</a>')


def render_page(page_rel, title, lang, active, body):
    root = "../" * (len(Path(page_rel).parts) - 1)
    return PAGE_TEMPLATE.substitute(
        lang=lang,
        title=esc(title),
        root=root,
        nav=nav_html(root, lang, active, page_rel),
        body=body,
        footer=esc(FOOTER),
        build_version=BUILD_VERSION,
    )


# ---------------------------------------------------------------------------
# CSS (site/templates/style.css.txt)
# ---------------------------------------------------------------------------


# ---------------------------------------------------------------------------
# materials data
# ---------------------------------------------------------------------------

def load_materials():
    mats = {}
    for f in sorted(SRC_GEN_DATA.rglob("modulargolems_config/materials/*.json")):
        d = load_json(f)
        for mid, ing in d.get("ingredients", {}).items():
            mats.setdefault(mid, {}).setdefault("ing", {}).update(ing)
        for mid, mods in d.get("modifiers", {}).items():
            mats.setdefault(mid, {})["modifiers"] = mods
        for mid, stats in d.get("stats", {}).items():
            mats.setdefault(mid, {})["stats"] = stats
        for mid, rep in d.get("repairIngredients", {}).items():
            mats.setdefault(mid, {}).setdefault("repair", {}).update(rep)
    out = {}
    for mid, data in mats.items():
        if "ing" not in data:
            continue
        ns, path = mid.split(":", 1)
        data["ns"] = ns
        data["path"] = path
        out[mid] = data
    return out


def material_display_name(mid, lang):
    ns, path = mid.split(":", 1)
    return LANG[lang].get(f"golem_material.{ns}.{path}") or path


# ---------------------------------------------------------------------------
# version-aware filtering (materials/items only valid for the current version)
# ---------------------------------------------------------------------------

def supported_mods():
    """Namespaces backed by actual material configs in this build."""
    return {d["ns"] for d in load_materials().values()}


def is_supported(ns):
    return ns == "modulargolems" or ns in supported_mods()


def load_tag_items(tag_name):
    f = SRC_GEN_DATA / "modulargolems/tags/items" / f"{tag_name}.json"
    if not f.is_file():
        return set()
    out = set()
    for v in load_json(f).get("values", []):
        out.add(v["id"] if isinstance(v, dict) else v)
    return out


BLUE_UPGRADE_ITEMS = load_tag_items("blue_upgrades")
PURPLE_UPGRADE_ITEMS = load_tag_items("potion_upgrades")


def is_upgrade(pid):
    """Upgrade items all ship a '_blue' variant model (see regUpgradeImpl)."""
    return (MODEL_DIR / f"{pid}_blue.json").is_file()


def upgrade_arrow(pid):
    reg = f"modulargolems:{pid}"
    if reg in BLUE_UPGRADE_ITEMS:
        return "blue"
    if reg in PURPLE_UPGRADE_ITEMS:
        return "purple"
    return None


_SRC_MOD = {}


def item_source_mod(pid):
    """Namespace that owns an item's texture (from its model layer0)."""
    if pid not in _SRC_MOD:
        mt = model_layer0(pid)
        _SRC_MOD[pid] = mt.split(":", 1)[0] if mt else "modulargolems"
    return _SRC_MOD[pid]


def all_item_pids():
    return sorted({k.split(".")[-1] for k in LANG["en"] if k.startswith("item.modulargolems.")})


def is_hidden_item(pid):
    """Items kept out of the public listing: dev/test dummy items and WIP
    incomplete parts."""
    return pid.startswith("dummy_") or pid.startswith("incomplete_")


def build_versions_json():
    return {
        "current": BUILD_VERSION,
        "versions": [
            {"label": v["label"], "branch": v["branch"], "mods": compat_mods_for(v["branch"])}
            for v in VERSIONS
        ],
    }


def build_items_json():
    """Per-version item lists, keyed off whether the item model file exists on
    that version branch (git ls-tree), not off texture attribution."""
    items = [p for p in all_item_pids() if not is_hidden_item(p)]
    out = {"current": BUILD_VERSION}
    for v in VERSIONS:
        models = branch_item_models(v["branch"])
        out[v["label"]] = sorted(p for p in items if p in models)
    return out


def build_mod_names_json():
    """Source mod -> {en, zh} display name, so pages/JS can localize mod names."""
    return {ns: {lang: MOD_NAMES[ns][lang] for lang in LANGS}
            for ns in MOD_NAMES}


def build_compat_items_json():
    """Compat ingredient item -> {en, zh} display name."""
    return dict(COMPAT_ITEM_NAMES)


def build_item_descs(lang, prefix=None):
    """Item -> description, pulled from the Patchouli guide entries that
    spotlight the item (entry name + page text), formatted as HTML. `prefix`
    is the URL prefix (relative to the consuming page) used to resolve links
    into the guide."""
    _, entries = load_book(lang)
    descs = {}
    for eid, d in entries.items():
        refs = []
        if d.get("icon"):
            refs.append(d["icon"].split("{")[0].strip())
        for p in d.get("pages", []):
            if isinstance(p, dict) and p.get("type") == "patchouli:spotlight":
                item_str = p.get("item", "") or ""
                if "{" not in item_str and "," in item_str:
                    for x in item_str.split(","):
                        if x.strip():
                            refs.append(x.strip())
                elif item_str.strip():
                    refs.append(item_str.split("{")[0].strip())
        if not refs:
            continue
        texts = []
        for p in d.get("pages", []):
            if isinstance(p, str):
                texts.append(p)
            elif isinstance(p, dict) and p.get("text"):
                texts.append(p["text"])
        text = patchouli_text(" ".join(texts), lambda t: book_link(t, lang, prefix) or t)
        if not text:
            continue
        entry = {"name": d.get("name", eid), "text": text}
        for ref in refs:
            if ref not in descs:
                descs[ref] = entry
    return descs


def fill_modifier_desc(desc, vals):
    """Replace %s and %N$s placeholders in a modifier description with the given
    values (%% becomes a literal %). Unmatched placeholders are kept as-is."""
    out = []
    i = 0
    n = 0
    while i < len(desc):
        ch = desc[i]
        if ch == '%':
            m = re.match(r"%(\d+)\$s", desc[i:])
            if m:
                idx = int(m.group(1)) - 1
                out.append(str(vals[idx]) if 0 <= idx < len(vals) else desc[i:i + m.end()])
                i += m.end()
                continue
            if i + 1 < len(desc) and desc[i + 1] == '%':
                out.append('%')
                i += 2
                continue
            if i + 1 < len(desc) and desc[i + 1] == 's':
                out.append(str(vals[n]) if n < len(vals) else '%s')
                n += 1
                i += 2
                continue
        out.append(ch)
        i += 1
    return ''.join(out)


def modifier_info(mod_id, lang, lvl=1):
    ns, path = mod_id.split(":", 1)
    base = f"modifier.{ns}.{path}"
    name = LANG[lang].get(base) or LANG["en"].get(base) or path
    desc = LANG[lang].get(f"{base}.desc")
    if desc is None:
        parts = []
        i = 1
        while f"{base}.desc{i}" in LANG[lang]:
            parts.append(LANG[lang][f"{base}.desc{i}"])
            i += 1
        if parts:
            desc = "<br>".join(esc(p) for p in parts)
    if desc is None:
        desc = LANG["en"].get(f"{base}.desc")
    vals = MODIFIER_VALUES.get(mod_id, {}).get(str(lvl))
    if desc and vals:
        desc = fill_modifier_desc(desc, vals)
    return name, desc


# ---------------------------------------------------------------------------
# page bodies
# ---------------------------------------------------------------------------

def build_index(lang="en"):
    page_rel = "index.html" if lang == "en" else "zh.html"
    _set_roots(page_rel)
    mats = len(load_materials())
    _, entries = load_book("en")
    items = len([p for p in all_item_pids() if not is_hidden_item(p)])
    title = tr("index_title", lang)
    lead = tr("index_lead", lang)
    card_guide = (tr("card_guide_title", lang), tr("card_guide_desc", lang))
    card_mats = (tr("card_mats_title", lang), tr("card_mats_desc", lang))
    card_items = (tr("card_items_title", lang), tr("card_items_desc", lang))
    hint = tr("index_hint", lang)
    lang_label = tr("lang_switch_label", lang)
    other_home = "zh.html" if lang == "en" else "index.html"
    body = INDEX_BODY_TEMPLATE.substitute(
        title=title,
        lead=lead,
        mats_count=mats,
        mats_label=tr("stat_materials", lang),
        entries_count=len(entries),
        entries_label=tr("stat_entries", lang),
        items_count=items,
        items_label=tr("stat_items", lang),
        langs_label=tr("stat_languages", lang),
        lang=lang,
        card_guide_title=card_guide[0],
        card_guide_desc=card_guide[1],
        card_mats_title=card_mats[0],
        card_mats_desc=card_mats[1],
        card_items_title=card_items[0],
        card_items_desc=card_items[1],
        hint=hint,
        pref_other="zh" if lang == "en" else "en",
        pref_self="en" if lang == "en" else "zh",
        nav_lang="zh" if lang == "en" else "en",
        other_home=other_home,
    )
    return render_page(page_rel, SITE_TITLES[lang], lang, "", body)


def build_book(lang):
    page_rel = f"book/{lang}/index.html"
    _set_roots(page_rel)
    cat_meta, entries = load_book(lang)
    by_cat = {}
    for eid, d in entries.items():
        by_cat.setdefault(d["_cat"], []).append(d)
    cat_order = sorted(by_cat.keys(),
                       key=lambda c: (cat_meta.get(c, {}).get("sortnum", 100), c))
    for lst in by_cat.values():
        lst.sort(key=lambda d: (d.get("sortnum", 100), d["_id"]))
    title = LANG[lang].get("patchouli.modulargolems.title") or tr("book_title", lang)
    landing = LANG[lang].get("patchouli.modulargolems.landing") or ""
    parts = ['<div class="wrap">', f"<h1>{esc(title)}</h1>", f'<p class="lead">{esc(landing)}</p>']
    parts.append('<div class="cats">')
    for ckey in cat_order:
        meta = cat_meta.get(ckey, {})
        icon = meta.get("icon")
        icon_html = icon_markup(icon, lang, size=30) if icon else ""
        parts.append(f'<section class="cat" id="{esc(ckey)}">')
        parts.append(f"<h2>{icon_html}{esc(meta.get('name', ckey))}</h2>")
        parts.append(f"<p>{esc(meta.get('description', ''))}</p>")
        parts.append('<ul class="entries">')
        for d in by_cat[ckey]:
            eid = d["_id"]
            url = f"{book_root()}{lang}/{eid}.html"
            ic = icon_markup(d.get("icon"), lang, size=24) if d.get("icon") else ""
            parts.append(f'<li><a href="{url}">{ic}<span>{esc(d.get("name", eid))}</span>'
                         f'<span class="en">{esc(eid)}</span></a></li>')
        parts.append("</ul></section>")
    parts.append("</div></div>")
    return render_page(page_rel, f"{title}{tr('page_title_suffix', lang)}", lang, "guide", "".join(parts))


def build_book_entry(lang, cat_dir, eid, d, prev, next_):
    page_rel = f"book/{lang}/{eid}.html"
    _set_roots(page_rel)
    link_resolver = lambda t: book_link(t, lang)
    pages_html = []
    pages = d.get("pages", [])
    if cat_dir == "materials":
        pages = pages[:1]
    for p in pages:
        if isinstance(p, str):
            pages_html.append(f'<section class="page">{patchouli_text(p, link_resolver)}</section>')
            continue
        ptype = p.get("type", "")
        if ptype == "patchouli:text":
            pages_html.append(f'<section class="page">{patchouli_text(p.get("text", ""), link_resolver)}</section>')
        elif ptype == "patchouli:spotlight":
            item_str = p.get("item", "") or ""
            if "{" not in item_str and "," in item_str:
                items = [x.strip() for x in item_str.split(",") if x.strip()]
            else:
                items = [item_str.split("{")[0].strip()]
            icons = "".join(icon_markup(it, lang, size=32) for it in items if it)
            text_html = patchouli_text(p.get("text", ""), link_resolver)
            pages_html.append(f'<section class="page"><div class="spotlight"><div class="pageicon">{icons}</div>{text_html}</div></section>')
        elif ptype == "patchouli:crafting":
            rid = p.get("recipe", "")
            recipe = RECIPE_BY_ID.get(rid)
            if recipe is None:
                matches = RECIPE_BY_RESULT.get(rid)
                if matches:
                    shaped = [r for _, r in matches if "pattern" in r and "key" in r]
                    if shaped:
                        recipe = shaped[0]
                    else:
                        _, recipe = matches[0]
            if recipe is None:
                pages_html.append(f'<section class="page"><p>{tr("recipe_unavailable", lang).format(rid=esc(rid))}</p></section>')
            else:
                grid = render_recipe(recipe, lang)
                text_html = patchouli_text(p.get("text", ""), link_resolver)
                pages_html.append(f'<section class="page">{grid}{text_html}</section>')
        else:
            pages_html.append(f'<section class="page"><p class="misc">{tr("unknown_page_type", lang).format(type=esc(ptype))}</p></section>')
    icon_html = icon_markup(d.get("icon"), lang, size=40) if d.get("icon") else ""
    name = d.get("name", eid)
    pager = ""
    if prev or next_:
        bits = ['<nav class="pager">']
        if prev:
            bits.append(f'<a class="prev" href="{book_root()}{lang}/{prev[1]}">← {esc(prev[0])}</a>')
        if next_:
            bits.append(f'<a class="next" href="{book_root()}{lang}/{next_[1]}">→ {esc(next_[0])}</a>')
        bits.append("</nav>")
        pager = "".join(bits)
    body = (
        f'<div class="wrap">'
        f'<nav class="breadcrumb"><a href="{book_root()}{lang}/index.html">{tr("guide_label", lang)}</a> / '
        f'<a href="{book_root()}{lang}/index.html#{esc(cat_dir)}">{esc(cat_dir)}</a> / {esc(name)}</nav>'
        f'<header class="bookhead">{icon_html}<h1>{esc(name)}</h1></header>'
        f'<div class="pages">{"".join(pages_html)}</div>{pager}'
        "</div>")
    return render_page(page_rel, f"{name}{tr('page_title_suffix', lang)}", lang, "guide", body)


def build_book_all(lang, write_page):
    cat_meta, entries = load_book(lang)
    by_cat = {}
    for eid, d in entries.items():
        by_cat.setdefault(d["_cat"], []).append(d)
    cat_order = sorted(by_cat.keys(),
                       key=lambda c: (cat_meta.get(c, {}).get("sortnum", 100), c))
    for lst in by_cat.values():
        lst.sort(key=lambda d: (d.get("sortnum", 100), d["_id"]))
    for ckey in cat_order:
        lst = by_cat[ckey]
        for idx, d in enumerate(lst):
            page_rel = f"book/{lang}/{d['_id']}.html"
            prev = next_ = None
            if idx > 0:
                pd = lst[idx - 1]
                prev = (pd.get("name", pd["_id"]), f"{pd['_id']}.html")
            if idx < len(lst) - 1:
                nd = lst[idx + 1]
                next_ = (nd.get("name", nd["_id"]), f"{nd['_id']}.html")
            write_page(page_rel, build_book_entry(lang, ckey, d["_id"], d, prev, next_))


def build_materials(lang):
    page_rel = f"materials/{lang}/index.html"
    _set_roots(page_rel)
    mats = {mid: d for mid, d in load_materials().items() if d["ns"] in supported_mods()}
    by_ns = {}
    for mid, d in mats.items():
        by_ns.setdefault(d["ns"], []).append((mid, d))
    for ns, lst in by_ns.items():
        lst.sort(key=lambda t: material_display_name(t[0], lang))
    ns_order = sorted(by_ns.keys())
    lead = tr("materials_lead", lang)
    parts = ['<div class="wrap">', f"<h1>{esc(tr('materials_h1', lang))}</h1>", f'<p class="lead">{lead}</p>']
    ns_label = tr("materials_sources", lang)
    parts.append(f'<details class="matnav"><summary>{esc(ns_label)} (<span id="matsrccount">{len(ns_order)}</span>)</summary>')
    parts.append('<div class="matnavpills">')
    for ns in ns_order:
        parts.append(f'<a href="#ns-{esc(ns)}" data-ns="{esc(ns)}">{esc(mod_name(ns, lang))}</a>')
    parts.append("</div></details>")
    for ns in ns_order:
        parts.append(f'<section class="matsource" data-ns="{esc(ns)}" id="ns-{esc(ns)}"><h2>{esc(mod_name(ns, lang))} <span class="misc">({len(by_ns[ns])})</span></h2>')
        parts.append('<div class="matgrid">')
        for mid, d in by_ns[ns]:
            name = material_display_name(mid, lang)
            ing = d.get("ing", {})
            if "item" in ing:
                ing_html = icon_markup(ing["item"], lang, size=22)
                ing_label = f'{item_name(ing["item"], lang)} <small class="misc">({esc(ing["item"])})</small>'
            elif "tag" in ing:
                tname = TAG_NAMES[lang].get(ing["tag"], ing["tag"])
                ti = TAG_ITEM.get(ing["tag"])
                if ti:
                    ing_html = icon_markup(ti, lang, size=22)
                else:
                    ing_html = placeholder_img("ing-" + ing["tag"], tname)
                ing_label = f'{tname} <small class="misc">(#{esc(ing["tag"])})</small>'
            else:
                ing_html, ing_label = "", ""
            rep = d.get("repair", {})
            rep_html = ""
            same_as_ing = (("item" in rep and rep["item"] == ing.get("item"))
                           or ("tag" in rep and rep["tag"] == ing.get("tag")))
            if not same_as_ing:
                if "item" in rep:
                    rep_html = (f'<span class="slot" title="{esc(rep["item"])}">{icon_markup(rep["item"], lang, size=22)}</span>'
                                f' <span>{esc(item_name(rep["item"], lang))}</span>')
                elif "tag" in rep:
                    tname = TAG_NAMES[lang].get(rep["tag"], rep["tag"])
                    ti = TAG_ITEM.get(rep["tag"])
                    if ti:
                        rep_html = (f'<span class="slot" title="{esc(rep["tag"])}">{icon_markup(ti, lang, size=22)}</span>'
                                    f' <span>{esc(tname)}</span>')
                    else:
                        rep_html = placeholder_img("rep-" + rep["tag"], tname) + f" <span>{esc(tname)}</span>"
            stat_html = ""
            for sid, val in (d.get("stats") or {}).items():
                stat_html += f"<li>{esc(fmt_stat(sid.split(':')[-1], val, lang))}</li>"
            mod_html = ""
            for mid2, lvl in (d.get("modifiers") or {}).items():
                mname, mdesc = modifier_info(mid2, lang, lvl)
                lvl_html = f'<span class="modlvl">{lvl}</span>' if lvl > 1 else ""
                desc_html = f" — {mdesc}" if mdesc else ""
                mod_html += f"<li><b>{esc(mname)}</b>{lvl_html}{desc_html}</li>"
            if not mod_html:
                mod_html = f'<li class="misc">{esc(tr("none_label", lang))}</li>'
            rep_section = (f'<div class="ing" style="margin-top:12px">{tr("repair_label", lang)}: {rep_html}</div>'
                           if rep_html else "")
            parts.append(
                f'<article class="matcard">'
                f'<h3>{ing_html}<span>{esc(name)}</span></h3>'
                f'<div class="ing">{tr("ingredient_label", lang)}: {ing_label}</div>'
                f'<ul class="kv">{stat_html}</ul>'
                f'<div class="hint">{tr("modifiers_label", lang)}</div>'
                f'<ul class="mods">{mod_html}</ul>'
                f'{rep_section}'
                f'</article>')
        parts.append("</div></section>")
    parts.append("</div>")
    parts.append(MATERIALS_JS.substitute(versions_url=f"{data_root()}versions.json"))
    return render_page(page_rel, tr("materials_page_title", lang), lang, "materials", "".join(parts))


def build_items(lang):
    page_rel = f"items/{lang}/index.html"
    _set_roots(page_rel)
    item_ids = sorted({k.split(".")[-1] for k in LANG["en"] if k.startswith("item.modulargolems.")})
    item_ids = [p for p in item_ids if not is_hidden_item(p)]

    def category(pid):
        if pid.endswith("_config_card") or pid.startswith("target_filter_") or pid == "patrol_path_recorder":
            return "config"
        if pid.startswith("incomplete_"):
            return "parts"
        if pid in ("metal_golem_body", "metal_golem_arm", "metal_golem_legs",
                   "humanoid_golem_body", "humanoid_golem_arms", "humanoid_golem_legs",
                   "dog_golem_body", "dog_golem_legs", "golem_facade") or "_golem_holder" in pid:
            return "parts"
        if pid in ("metal_golem_template", "empty_upgrade", "diamond_expansion_template",
                   "netherite_expansion_template") or pid.endswith("_wand"):
            return "tools"
        if pid.endswith("_dog_golem_armor") or pid.endswith("_bow") or pid.endswith("_cannon") \
                or pid in ("flame_thrower", "golem_slicing_axe", "beacon_boots") \
                or pid.endswith("_golem_sword") or pid.endswith("_golem_axe") or pid.endswith("_golem_spear") \
                or pid.endswith("_helmet") or pid.endswith("_chestplate") \
                or pid.endswith("_shinguard") or pid.endswith("_boots"):
            return "equipment"
        if pid in ("azure_cube", "void_cube", "cloud_cube", "candy_mixture", "candy_construct",
                   "magnetic_alloy", "magnetic_construct", "nuclear_construct", "storm_construct",
                   "void_construct", "wroughtnaut_ingot"):
            return "ingredients"
        if is_upgrade(pid) or pid.endswith("_upgrade"):
            return "upgrades"
        return "misc"

    CATS = ["tools", "parts", "upgrades", "equipment", "config", "ingredients", "misc"]
    grouped = {c: [] for c in CATS}
    for pid in item_ids:
        if not is_supported(item_source_mod(pid)):
            continue
        grouped[category(pid)].append(pid)

    def item_cell(pid):
        reg = f"modulargolems:{pid}"
        rel, slug = resolve_icon(reg)
        if rel:
            src = f"{assets_root()}tex/{rel}"
        else:
            ensure_placeholder(slug)
            src = f"{assets_root()}img/ph-{slug}.svg"
        name = item_name(reg, lang)
        hay = (name + " " + pid + " modulargolems").lower()
        arrow = ""
        a = upgrade_arrow(pid)
        if a:
            arrow = (f'<img class="arrow" src="{assets_root()}tex/item/{a}_arrow.png" '
                     f'alt="" width="40" height="40">')
        return (f'<figure class="itemcell" data-pid="{esc(pid)}" data-search="{esc(hay)}">'
                f'<span class="slot">{arrow}<img class="itemimg" width="40" height="40" loading="lazy" src="{src}" '
                f'alt="{esc(name)}" title="{esc(reg)}"></span>'
                f'<figcaption>{esc(name)}<small>{esc(reg)}</small></figcaption></figure>')
    parts = ['<div class="wrap">', f"<h1>{esc(tr('items_h1', lang))}</h1>",
             f'<p class="lead">{esc(tr("items_lead", lang))}</p>']
    parts.append(f'<input class="searchbar" id="q" type="search" placeholder="{esc(tr("filter_placeholder", lang))}">')
    for c in CATS:
        lst = grouped[c]
        if not lst:
            continue
        label = tr(f"cat_{c}", lang)
        parts.append(f'<details class="itemcat" open><summary><h2>{esc(label)} <span class="misc">({len(lst)})</span></h2></summary>')
        if c == "upgrades":
            by_mod = {}
            for pid in lst:
                by_mod.setdefault(item_source_mod(pid), []).append(pid)
            order = sorted(by_mod.keys(), key=lambda ns: (ns != "modulargolems", mod_name(ns, lang)))
            for mns in order:
                plist = sorted(by_mod[mns])
                mlabel = mod_name(mns, lang)
                parts.append(f'<div class="itemgrid"><div class="upgradesub">'
                             f'{esc(mlabel)} <span class="misc">({len(plist)})</span></div>')
                for pid in plist:
                    parts.append(item_cell(pid))
                parts.append("</div>")
        else:
            lst.sort()
            parts.append('<div class="itemgrid">')
            for pid in lst:
                parts.append(item_cell(pid))
            parts.append("</div>")
        parts.append("</details>")
    parts.append("</div>")
    no_desc = tr("no_desc", lang)
    parts.append(ITEMS_OVERLAY)
    parts.append(ITEMS_JS.substitute(
        no_desc=json.dumps(no_desc),
        items_url=f"{data_root()}items.json",
        descs_url=f"{data_root()}item_descs_{lang}.json",
    ))
    return render_page(page_rel, tr("items_page_title", lang), lang, "items", "".join(parts))


# ---------------------------------------------------------------------------
# main
# ---------------------------------------------------------------------------

def main():
    global RECIPE_BY_ID, RECIPE_BY_RESULT
    shutil.rmtree(OUT, ignore_errors=True)
    (OUT / "css").mkdir(parents=True)
    (OUT / "assets/tex").mkdir(parents=True)
    (OUT / "assets/img").mkdir(parents=True)

    (OUT / ".nojekyll").write_text("", encoding="utf-8")
    (OUT / "css/style.css").write_text(CSS, encoding="utf-8")

    if TEX_DIR.is_dir():
        for f in TEX_DIR.rglob("*"):
            if f.is_file():
                rel = f.relative_to(TEX_DIR)
                dest = OUT / "assets/tex/item" / rel
                dest.parent.mkdir(parents=True, exist_ok=True)
                copy_texture(f, dest)

    if VENDOR_TEX.is_dir():
        for f in VENDOR_TEX.rglob("*"):
            if f.is_file():
                rel = f.relative_to(VENDOR_TEX)
                dest = OUT / "assets/tex" / rel
                dest.parent.mkdir(parents=True, exist_ok=True)
                copy_texture(f, dest)

    RECIPE_BY_ID, RECIPE_BY_RESULT = load_recipe_index()

    (OUT / "data").mkdir(parents=True, exist_ok=True)
    (OUT / "data/versions.json").write_text(
        json.dumps(build_versions_json(), indent=1, ensure_ascii=False), encoding="utf-8")
    (OUT / "data/items.json").write_text(
        json.dumps(build_items_json(), indent=1, ensure_ascii=False), encoding="utf-8")
    (OUT / "data/mod_names.json").write_text(
        json.dumps(build_mod_names_json(), indent=1, ensure_ascii=False), encoding="utf-8")
    (OUT / "data/compat_items.json").write_text(
        json.dumps(build_compat_items_json(), indent=1, ensure_ascii=False), encoding="utf-8")
    for lang in LANGS:
        (OUT / f"data/item_descs_{lang}.json").write_text(
            json.dumps(build_item_descs(lang, prefix="../../book/"), indent=1, ensure_ascii=False),
            encoding="utf-8")

    def write_page(page_rel, html):
        dest = OUT / page_rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_text(html, encoding="utf-8")

    write_page("index.html", build_index("en"))
    write_page("zh.html", build_index("zh"))
    for lang in LANGS:
        write_page(f"book/{lang}/index.html", build_book(lang))
        build_book_all(lang, write_page)
        write_page(f"materials/{lang}/index.html", build_materials(lang))
        write_page(f"items/{lang}/index.html", build_items(lang))

    for rel, src in REFERENCED_TEX.items():
        dest = OUT / "assets/tex" / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        copy_texture(src, dest)

    for rel, srcs in COMPOSITE_TEX.items():
        dest = OUT / "assets/tex" / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        composite_textures(srcs, dest)

    n_tex = sum(1 for _ in (OUT / "assets/tex").rglob("*.png"))
    n_ph = len(list((OUT / "assets/img").glob("ph-*.svg")))
    n_html = len(list(OUT.rglob("*.html")))
    print(f"OK: site written to {OUT}")
    print(f"  html pages: {n_html}  textures: {n_tex}  placeholders: {n_ph}")


if __name__ == "__main__":
    main()
