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

# Per-modifier %s values for each level, used to fill the %s placeholders in
# modifier descriptions (see modifier_info). Maintained manually: update when
# the modifier config values or level scaling change.
MODIFIER_VALUES = load_json(ROOT / "site/modifier_values.json")

# Source mod namespace -> {en, zh} display name. Maintained manually in
# site/mod_names.json; used to label material-source groups and upgrade items,
# and emitted as data/mod_names.json so pages/JS can localize mod names.
MOD_NAMES = load_json(ROOT / "site/mod_names.json")


def mod_name(ns, lang):
    """Localized display name for a source-mod namespace, falling back to the raw id."""
    return MOD_NAMES.get(ns, {}).get(lang, ns)

# ---------------------------------------------------------------------------
# display-name maps for things not in the mod's own lang files
# ---------------------------------------------------------------------------

VANILLA_NAMES = {
    "en": {
        "minecraft:iron_ingot": "Iron Ingot",
        "minecraft:gold_ingot": "Gold Ingot",
        "minecraft:copper_ingot": "Copper Ingot",
        "minecraft:netherite_ingot": "Netherite Ingot",
        "minecraft:echo_shard": "Echo Shard",
        "minecraft:stick": "Stick",
        "minecraft:white_banner": "White Banner",
    },
    "zh": {
        "minecraft:iron_ingot": "铁锭",
        "minecraft:gold_ingot": "金锭",
        "minecraft:copper_ingot": "铜锭",
        "minecraft:netherite_ingot": "下界合金锭",
        "minecraft:echo_shard": "回响碎片",
        "minecraft:stick": "木棍",
        "minecraft:white_banner": "白色旗帜",
    },
}

TAG_NAMES = {
    "en": {
        "forge:ingots/brass": "Brass Ingot",
        "forge:ingots/zinc": "Zinc Ingot",
        "forge:ingots/cobalt": "Cobalt Ingot",
        "forge:ingots/hepatizon": "Hepatizon Ingot",
        "forge:ingots/manyullyn": "Manyullyn Ingot",
        "forge:ingots/rose_gold": "Rose Gold Ingot",
        "forge:ingots/amethyst_bronze": "Amethyst Bronze Ingot",
        "forge:ingots/fiery": "Fiery Ingot",
        "forge:ingots/ironwood": "Ironwood Ingot",
        "forge:ingots/knightmetal": "Knightmetal Ingot",
        "forge:ingots/steeleaf": "Steeleaf Ingot",
        "modulargolems:cardboard": "Cardboard",
        "modulargolems:revelation_ingot": "Revelation Ingot",
        "modulargolems:sculk_materials": "Sculk Materials",
    },
    "zh": {
        "forge:ingots/brass": "黄铜锭",
        "forge:ingots/zinc": "锌锭",
        "forge:ingots/cobalt": "钴锭",
        "forge:ingots/hepatizon": "赫帕铁锭",
        "forge:ingots/manyullyn": "玛玉灵锭",
        "forge:ingots/rose_gold": "玫瑰金锭",
        "forge:ingots/amethyst_bronze": "紫晶青铜锭",
        "forge:ingots/fiery": "炽焰锭",
        "forge:ingots/ironwood": "铁木锭",
        "forge:ingots/knightmetal": "骑士金属锭",
        "forge:ingots/steeleaf": "钢叶锭",
        "modulargolems:cardboard": "纸板",
        "modulargolems:revelation_ingot": "启示锭",
        "modulargolems:sculk_materials": "幽匿材料",
    },
}

# Display names for compat-mod ingredient items (material configs). Names are
# taken from each mod's own lang files; emitted as data/compat_items.json.
COMPAT_ITEM_NAMES = {
    "allthemodium:allthemodium_ingot": {"en": "Allthemodium Ingot", "zh": "ATM锭"},
    "allthemodium:unobtainium_ingot": {"en": "Unobtainium Ingot", "zh": "难得素锭"},
    "allthemodium:vibranium_ingot": {"en": "Vibranium Ingot", "zh": "振金锭"},
    "blazegear:brimsteel_ingot": {"en": "Brimsteel Ingot", "zh": "烈焰钢锭"},
    "botania:elementium_ingot": {"en": "Elementium Ingot", "zh": "源质钢锭"},
    "botania:manasteel_ingot": {"en": "Manasteel Ingot", "zh": "魔力钢锭"},
    "botania:terrasteel_ingot": {"en": "Terrasteel Ingot", "zh": "泰拉钢锭"},
    "cataclysm:ancient_metal_ingot": {"en": "Ancient Metal Ingot", "zh": "远古金属锭"},
    "cataclysm:cursium_ingot": {"en": "Cursium Ingot", "zh": "咒魂锭"},
    "cataclysm:ignitium_ingot": {"en": "Ignitium Ingot", "zh": "腾炎锭"},
    "cataclysm:witherite_ingot": {"en": "Witherite Ingot", "zh": "凋灵合金锭"},
    "composite_material:allay_steel_ingot": {"en": "Allay Steel Ingot", "zh": "悦灵钢锭"},
    "composite_material:dungeon_steel_ingot": {"en": "Dungeon Steel Ingot", "zh": "地牢钢锭"},
    "composite_material:etherite_ingot": {"en": "Etherite Ingot", "zh": "以太合金锭"},
    "composite_material:obsidian_steel_ingot": {"en": "Obsidian Steel Ingot", "zh": "黑曜石钢锭"},
    "composite_material:primitive_tenacity": {"en": "Primitive Tenacity", "zh": "荒古坚材"},
    "create:andesite_alloy": {"en": "Andesite Alloy", "zh": "安山合金"},
    "create:railway_casing": {"en": "Train Casing", "zh": "列车机壳"},
    "goety:cursed_ingot": {"en": "Cursed Metal Ingot", "zh": "诅咒金属锭"},
    "goety:dark_ingot": {"en": "Dark Metal Ingot", "zh": "黑暗金属锭"},
    "iceandfire:dragonsteel_fire_ingot": {"en": "Fire Dragonsteel Ingot", "zh": "龙炎钢锭"},
    "iceandfire:dragonsteel_ice_ingot": {"en": "Ice Dragonsteel Ingot", "zh": "龙霜钢锭"},
    "iceandfire:dragonsteel_lightning_ingot": {"en": "Lightning Dragonsteel Ingot", "zh": "龙霆钢锭"},
    "l2complements:eternium_ingot": {"en": "Eternium Ingot", "zh": "永恒锭"},
    "l2complements:poseidite_ingot": {"en": "Poseidite Ingot", "zh": "海神锭"},
    "l2complements:shulkerate_ingot": {"en": "Shulkerate Ingot", "zh": "潜影锭"},
    "l2complements:totemic_gold_ingot": {"en": "Totemic Gold Ingot", "zh": "生命锭"},
    "l2hostility:chaos_ingot": {"en": "Chaos Ingot", "zh": "混沌锭"},
    "l2hostility:miracle_ingot": {"en": "Miracle Ingot", "zh": "奇迹锭"},
    "legendary_monsters:molten_metal_ingot": {"en": "Molten Metal Ingot", "zh": "熔融金属锭"},
}

# Version this build is generated for. The list mirrors the repo's version
# branches. Per-version supported compat mods are parsed from each branch's
# CompatManager.register() (see compat_mods_for), not hardcoded here.
BUILD_VERSION = "1.20.1"
PAGES_ROOT = "https://minecraft-lightland.github.io/ModularGolems/"
VERSIONS = [
    {"label": "1.19.2", "branch": "1.19"},
    {"label": "1.19.4", "branch": "1.19.4"},
    {"label": "1.20.1", "branch": "1.20"},
    {"label": "1.21.1", "branch": "1.21"},
    {"label": "26.1.2", "branch": "26.1"},
]


def current_version():
    for v in VERSIONS:
        if v["label"] == BUILD_VERSION:
            return v
    raise ValueError(f"unknown BUILD_VERSION {BUILD_VERSION}")


# dispatch class -> material config namespace, mirrored from each *Dispatch.java
DISPATCH_MODID = {
    "BotDispatch": "botania",
    "TFDispatch": "twilightforest",
    "CreateDispatch": "create",
    "LCDispatch": "l2complements",
    "BGDispatch": "blazegear",
    "LHDispatch": "l2hostility",
    "CataDispatch": "cataclysm",
    "ACDispatch": "alexscaves",
    "IAFDispatch": "iceandfire",
    "TCDispatch": "tconstruct",
    "GoetyDispatch": "goety",
    "GRDispatch": "goety_revelation",
    "MowzieDispatch": "mowziesmobs",
    "LMDispatch": "legendary_monsters",
    "ATMDispatch": "allthemodium",
    "CMDispatch": "composite_material",
}
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


STAT_INFO = {
    "max_health": {"en": "Max Health", "zh": "最大生命值", "kind": "BASE"},
    "attack": {"en": "Attack Damage", "zh": "攻击伤害", "kind": "BASE"},
    "armor": {"en": "Armor", "zh": "护甲", "kind": "ADD"},
    "tough": {"en": "Armor Toughness", "zh": "护甲韧性", "kind": "ADD"},
    "knockback_resistance": {"en": "Knockback Resistance", "zh": "击退抗性", "kind": "ADD"},
    "attack_knockback": {"en": "Attack Knockback", "zh": "攻击击退", "kind": "ADD"},
    "regen": {"en": "Regeneration", "zh": "生命回复", "kind": "ADD"},
    "sweep": {"en": "Sweep Range", "zh": "范围攻击", "kind": "ADD"},
    "speed": {"en": "Movement Speed", "zh": "移动速度", "kind": "PERCENT"},
    "weight": {"en": "Speed", "zh": "速度", "kind": "PERCENT"},
    "jump_strength": {"en": "Jump Strength", "zh": "跳跃强度", "kind": "PERCENT"},
    "max_health_percent": {"en": "Max Health", "zh": "最大生命值", "kind": "PERCENT"},
    "max_size": {"en": "Golem Size", "zh": "傀儡体型", "kind": "ADD"},
    "max_size_percentage": {"en": "Golem Size", "zh": "傀儡体型", "kind": "PERCENT"},
    "range": {"en": "Attack Range", "zh": "攻击范围", "kind": "ADD"},
    "dynamic_reduction": {"en": "Dynamic Reduction", "zh": "动态减伤", "kind": "ADD"},
}


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
    """Resolve an item's model layer0 texture id, e.g. 'twilightforest:item/equipments/...'."""
    if path not in _MODEL_TEX:
        m = MODEL_DIR / f"{path}.json"
        tex = None
        if m.is_file():
            try:
                tex = load_json(m).get("textures", {}).get("layer0")
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


# configs reference the compat construct/cube items under the modulargolems
# namespace, but their textures ship under the compat mod's own namespace
MODULAR_ALIAS = {
    "modulargolems:azure_cube": "cataclysm:azure_cube",
    "modulargolems:candy_construct": "alexscaves:candy_construct",
    "modulargolems:candy_mixture": "alexscaves:candy_mixture",
    "modulargolems:cloud_cube": "legendary_monsters:cloud_cube",
    "modulargolems:magnetic_alloy": "alexscaves:magnetic_alloy",
    "modulargolems:magnetic_construct": "alexscaves:magnetic_construct",
    "modulargolems:nuclear_construct": "alexscaves:nuclear_construct",
    "modulargolems:storm_construct": "cataclysm:storm_construct",
    "modulargolems:void_construct": "cataclysm:void_construct",
    "modulargolems:void_cube": "cataclysm:void_cube",
    "modulargolems:wroughtnaut_ingot": "mowziesmobs:wroughtnaut_ingot",
}

# material ingredients that are forge/mod tags -> representative item for the icon
TAG_ITEM = {
    "forge:ingots/brass": "create:brass_ingot",
    "forge:ingots/zinc": "create:zinc_ingot",
    "forge:ingots/cobalt": "tconstruct:cobalt_ingot",
    "forge:ingots/hepatizon": "tconstruct:hepatizon_ingot",
    "forge:ingots/manyullyn": "tconstruct:manyullyn_ingot",
    "forge:ingots/rose_gold": "tconstruct:rose_gold_ingot",
    "forge:ingots/amethyst_bronze": "tconstruct:amethyst_bronze_ingot",
    "forge:ingots/fiery": "twilightforest:fiery_ingot",
    "forge:ingots/ironwood": "twilightforest:ironwood_ingot",
    "forge:ingots/knightmetal": "twilightforest:knightmetal_ingot",
    "forge:ingots/steeleaf": "twilightforest:steeleaf_ingot",
    "modulargolems:cardboard": "create:cardboard",
    "modulargolems:sculk_materials": "minecraft:echo_shard",
}


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

MC_COLORS = {
    "0": "#000000", "1": "#0000AA", "2": "#00AA00", "3": "#00AAAA",
    "4": "#AA0000", "5": "#AA00AA", "6": "#FFAA00", "7": "#AAAAAA",
    "8": "#555555", "9": "#5555FF", "a": "#55FF55", "b": "#55FFFF",
    "c": "#FF5555", "d": "#FF55FF", "e": "#FFFF55", "f": "#FFFFFF",
}


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

SITE_TITLES = {"en": "Modular Golems", "zh": "模块化傀儡"}
NAV_LABELS = {
    "en": {"guide": "Guide", "materials": "Materials", "items": "Items"},
    "zh": {"guide": "指南", "materials": "材料", "items": "物品"},
}
FOOTER = ("Generated from the ModularGolems source. Not affiliated with Mojang. "
          "View on GitHub — Minecraft-LightLand/ModularGolems")


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
    buf.append(version_switch_html())
    buf.append(lang_switch_html(root, lang, page_rel))
    buf.append("</nav>")
    return "".join(buf)


def version_switch_html():
    opts = []
    for v in VERSIONS:
        sel = " selected" if v["label"] == BUILD_VERSION else ""
        opts.append(f'<option value="{esc(v["label"])}"{sel}>MC {esc(v["label"])}</option>')
    return ('<label class="vswitch"><span class="vsr">Version</span>'
            f'<select class="vselect" data-current="{esc(BUILD_VERSION)}" aria-label="Mod version">'
            + "".join(opts) + "</select></label>")


def lang_switch_html(root, lang, page_rel):
    if page_rel in ("index.html", "zh.html"):
        other = "zh" if lang == "en" else "en"
        other_rel = "zh.html" if lang == "en" else "index.html"
    else:
        other = "zh" if lang == "en" else "en"
        other_rel = page_rel.replace(f"/{lang}/", f"/{other}/", 1)
    label = "中文" if lang == "en" else "EN"
    return (f'<a class="lang" data-lang="{other}" href="{root}{other_rel}" '
            f'title="Switch language / 切换语言">{esc(label)}</a>')


def render_page(page_rel, title, lang, active, body):
    root = "../" * (len(Path(page_rel).parts) - 1)
    return f"""<!doctype html>
<html lang="{lang}">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{esc(title)}</title>
<link rel="stylesheet" href="{root}css/style.css">
</head>
<body>
{nav_html(root, lang, active, page_rel)}
{body}
<footer class="pagefoot"><div class="wrap">{esc(FOOTER)}</div></footer>
<script>
(function(){{
  var sel=document.querySelector('.vselect');
  var DATA_CURRENT='{BUILD_VERSION}';
  var langEls=document.querySelectorAll('a.lang');
  for (var i=0;i<langEls.length;i++){{
    langEls[i].addEventListener('click',function(){{
      try{{ localStorage.setItem('mg_lang', this.getAttribute('data-lang')); }}catch(e){{}}
    }});
  }}
  function setV(v){{
    var prev=window.MG_VERSION;
    window.MG_VERSION=v;
    if(prev!==v) window.dispatchEvent(new CustomEvent('mg-version',{{detail:v}}));
  }}
  var m=location.search.match(/[?&]v=([^&]+)/);
  var v=m?decodeURIComponent(m[1]):null;
  if(!v){{ try{{ var s=localStorage.getItem('mg_version'); if(s) v=s; }}catch(e){{}} }}
  if(!v || (sel && !sel.querySelector('option[value="'+v+'"]'))) v=DATA_CURRENT;
  if(sel){{
    sel.value=v;
    sel.addEventListener('change',function(){{
      var nv=sel.value;
      try{{ localStorage.setItem('mg_version',nv); }}catch(e){{}}
      if(history.replaceState) history.replaceState(null,'','?v='+encodeURIComponent(nv));
      setV(nv);
    }});
  }}
  setV(v);
}})();
</script>
</body></html>"""


# ---------------------------------------------------------------------------
# CSS
# ---------------------------------------------------------------------------

CSS = """\
:root{
  --bg:#0d1117; --bg2:#121823; --panel:#171f2c; --panel2:#1d2737;
  --line:#2a3548; --text:#d7e0ea; --muted:#8b98a9; --accent:#4fd1ff;
  --accent2:#7ee0ff;
  --parch:#f3ecd9; --parch2:#e9dfc6; --parchink:#3a2f22;
}
*{box-sizing:border-box}
html,body{margin:0;padding:0}
body{background:var(--bg);color:var(--text);font:15px/1.55 -apple-system,"Segoe UI",Roboto,"Helvetica Neue","PingFang SC","Microsoft YaHei",sans-serif}
a{color:var(--accent);text-decoration:none}
a:hover{color:var(--accent2)}
.wrap{max-width:1100px;margin:0 auto;padding:0 20px}

.topnav{position:sticky;top:0;z-index:50;display:flex;align-items:center;gap:4px;
  padding:10px 20px;background:rgba(13,17,23,.92);border-bottom:1px solid var(--line);backdrop-filter:blur(6px)}
.topnav a{padding:7px 14px;border-radius:8px;color:var(--muted);font-weight:600;white-space:nowrap}
.topnav a:hover{color:var(--text);background:var(--panel)}
.topnav a.brand{color:var(--accent);font-size:16px}
.topnav a.active{color:#fff;background:var(--panel2)}
.topnav .spacer{flex:1}
.topnav a.lang{border:1px solid var(--line);font-size:13px}
.topnav .vswitch{display:flex;align-items:center;gap:6px;border:1px solid var(--line);border-radius:8px;padding:4px 8px}
.topnav .vsr{font-size:11px;color:var(--muted);text-transform:uppercase;letter-spacing:.5px}
.topnav .vselect{background:transparent;border:none;color:var(--text);font-size:13px;font-weight:600;cursor:pointer;outline:none}
.topnav .vselect option{background:var(--panel2);color:var(--text)}
.pagefoot{padding:28px 20px;margin-top:48px;border-top:1px solid var(--line);color:var(--muted);font-size:13px;text-align:center}

h1{font-size:34px;margin:8px 0 6px}
h2{font-size:22px;margin:26px 0 10px;color:var(--accent2)}
.lead{color:var(--muted);max-width:74ch;margin:0 0 8px}
.hero{padding:52px 0 30px;text-align:center;
  background:radial-gradient(1100px 340px at 50% -60px, rgba(79,209,255,.16), transparent 70%)}
.hero h1{font-size:44px;letter-spacing:.5px;margin:12px 0 8px}
.hero .lead{margin:0 auto}
.hero img.bookhero{image-rendering:pixelated;width:64px;height:64px;opacity:.9}

.cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:18px;margin:30px 0}
.card{background:var(--panel);border:1px solid var(--line);border-radius:14px;padding:22px;transition:transform .12s ease,border-color .12s ease}
.card:hover{transform:translateY(-3px);border-color:var(--accent)}
.card .icon{width:44px;height:44px;image-rendering:pixelated}
.card h3{margin-top:12px}
.card p{color:var(--muted);margin:6px 0 0;font-size:14px}

.statline{display:flex;gap:30px;justify-content:center;flex-wrap:wrap;color:var(--muted);margin:18px 0 6px}
.statline b{color:var(--accent)}

.slot{display:inline-flex;flex-direction:column;align-items:center;justify-content:center;gap:3px;
  width:42px;height:42px;margin:2px;background:#0a0e14;border:2px solid #33415c;border-radius:6px;vertical-align:middle}
.slot.empty{background:rgba(255,255,255,.03);border-style:dashed;border-color:#243046}
.itemimg{image-rendering:pixelated;width:32px;height:32px;display:block}
.slotname{font-size:10px;line-height:1.1;color:var(--muted);max-width:52px;text-align:center;word-break:break-all}
.slot .slotname{display:none}
.slot.named .slotname{display:block}

.bookhead{display:flex;align-items:center;gap:16px;margin:26px 0 8px}
.bookhead img{width:40px;height:40px;image-rendering:pixelated}
.breadcrumb{color:var(--muted);font-size:13px;margin-top:20px}
.pages{display:flex;flex-direction:column;gap:22px;margin:20px 0}
.page{background:linear-gradient(180deg,var(--parch),var(--parch2));color:var(--parchink);
  border:1px solid #cbbd96;border-radius:4px 12px 12px 4px;box-shadow:0 6px 18px rgba(0,0,0,.35);
  padding:22px 26px;line-height:1.7;position:relative;overflow:hidden}
.page::before{content:"";position:absolute;left:0;top:0;bottom:0;width:6px;
  background:repeating-linear-gradient(90deg,#8a7a55,#8a7a55 2px,transparent 2px,transparent 4px)}
.page p{margin:0 0 10px}
.page .pageicon{float:left;margin:2px 14px 8px 0}
.page .slot{background:#dccfb0;border-color:#b3a27e}
.page .slot.empty{background:rgba(0,0,0,.04);border-color:#b3a27e}
.page ul.bullets{margin:4px 0 10px;padding-left:22px}
.page ul.bullets li{margin:2px 0}
.page .rwrap{display:flex;align-items:center;gap:8px;flex-wrap:wrap;background:rgba(0,0,0,.05);border:1px dashed #b3a27e;border-radius:8px;padding:12px;margin:10px 0}
.page .rgrid{display:inline-block}
.page .rrow{display:flex}
.page .rarrow{font-size:26px;font-weight:700;color:#8a7a55;padding:0 8px}
.page .count{font-size:11px;color:#8a7a55}
.page .spotlight{border-left:3px solid #b3a27e;padding-left:14px;margin:0 0 12px}

.pager{display:flex;justify-content:space-between;gap:12px;margin:26px 0 8px}
.pager a{background:var(--panel);border:1px solid var(--line);padding:10px 16px;border-radius:10px;color:var(--text);font-weight:600}
.pager a:hover{border-color:var(--accent)}
.pager a.next{margin-left:auto}

.cats{margin-top:24px}
.cat{background:var(--panel);border:1px solid var(--line);border-radius:14px;padding:20px 22px;margin:18px 0}
.cat h2{display:flex;align-items:center;gap:12px;margin:0 0 4px}
.cat h2 img{width:30px;height:30px;image-rendering:pixelated}
.cat > p{color:var(--muted);margin:2px 0 12px}
.entries{list-style:none;margin:0;padding:0;display:grid;grid-template-columns:repeat(auto-fill,minmax(250px,1fr));gap:8px}
.entries li{margin:0}
.entries a{display:flex;align-items:center;gap:10px;padding:8px 10px;border-radius:8px;background:var(--panel2);color:var(--text)}
.entries a:hover{background:#233047;color:#fff}
.entries img{width:24px;height:24px;image-rendering:pixelated}
.entries .en{color:var(--muted);font-size:11px;margin-left:auto}

.matnav{position:sticky;top:52px;background:var(--bg);padding:10px 0;border-bottom:1px solid var(--line);z-index:40}
.matnav summary{cursor:pointer;font-size:13px;color:var(--muted);display:inline-block;background:var(--panel);border:1px solid var(--line);padding:6px 14px;border-radius:999px;user-select:none}
.matnav summary:hover{color:var(--accent);border-color:var(--accent)}
.matnavpills{display:flex;gap:8px;flex-wrap:wrap;margin-top:10px}
.matnavpills a{font-size:13px;color:var(--muted);background:var(--panel);border:1px solid var(--line);padding:5px 12px;border-radius:999px}
.matnavpills a:hover{color:var(--accent);border-color:var(--accent)}
.matsource{margin:28px 0 6px;scroll-margin-top:120px}
.matsource h2{display:flex;align-items:center;gap:12px;margin:0 0 4px}
.matgrid{display:grid;grid-template-columns:repeat(auto-fill,minmax(340px,1fr));gap:16px;margin:14px 0}
.matcard{background:var(--panel);border:1px solid var(--line);border-radius:12px;padding:16px 18px}
.matcard h3{display:flex;align-items:center;gap:10px;margin:0 0 8px}
.matcard h3 img{width:26px;height:26px;image-rendering:pixelated}
.matcard .ing{font-size:13px;color:var(--muted);margin:0 0 10px}
.matcard .ing .slot{width:30px;height:30px}
.matcard .ing .slot .itemimg{width:22px;height:22px}
.matcard .ing .slotname{display:none}
.kv{list-style:none;margin:8px 0;padding:0}
.kv li{padding:3px 0;border-bottom:1px dashed #243046;font-size:13px}
.mods{list-style:none;margin:8px 0 0;padding:0}
.mods li{font-size:12.5px;color:var(--muted);margin:4px 0;padding-left:14px;position:relative}
.mods li::before{content:"◆";position:absolute;left:0;top:0;color:var(--accent);font-size:9px;line-height:1.8}
.mods li b{color:var(--text);font-weight:600}
.modlvl{display:inline-block;background:#263446;border:1px solid var(--line);border-radius:999px;font-size:10px;padding:1px 7px;margin-left:6px;color:var(--accent)}
.misc{color:var(--muted)}
.hint{color:var(--muted);font-size:13px}
code{background:rgba(255,255,255,.07);border-radius:5px;padding:1px 6px;font-size:12px}

.searchbar{width:100%;max-width:440px;padding:10px 14px;margin:16px 0;border-radius:10px;border:1px solid var(--line);background:var(--panel);color:var(--text);font-size:15px}
.searchbar:focus{outline:none;border-color:var(--accent)}
.itemcat{margin:26px 0}
.itemcat h2{border-bottom:1px solid var(--line);padding-bottom:8px;display:inline-block}
.itemcat>summary{cursor:pointer;user-select:none;list-style:none}
.itemcat>summary::-webkit-details-marker{display:none}
.itemcat>summary::before{content:"▸ ";color:var(--muted);font-size:14px;margin-right:4px}
.itemcat[open]>summary::before{content:"▾ "}
.itemcell{cursor:pointer}
.itemgrid{display:grid;grid-template-columns:repeat(auto-fill,minmax(160px,1fr));gap:10px;margin:12px 0}
.itemcell{background:var(--panel);border:1px solid var(--line);border-radius:10px;padding:10px;text-align:center}
.itemcell:hover{border-color:var(--accent)}
.itemcell .slot{width:56px;height:56px;margin:0 auto;background:#0a0e14;position:relative}
.itemcell .itemimg{width:40px;height:40px}
.itemcell .arrow{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:40px;height:40px;image-rendering:pixelated}
.itemcell figcaption{margin-top:8px;font-size:12px;line-height:1.3;color:var(--text);word-break:break-word}
.itemcell figcaption small{display:block;color:var(--muted);font-size:10px}
.itemcell figure{margin:0}
.upgradesub{grid-column:1/-1;font-size:13px;color:var(--accent);font-weight:600;border-bottom:1px solid var(--line);padding-bottom:4px;margin-top:8px}
.itemoverlay{position:fixed;inset:0;background:rgba(4,8,14,.72);display:flex;align-items:center;justify-content:center;z-index:50;padding:20px}
.itemoverlay[hidden]{display:none}
.itempanel{background:var(--panel);border:1px solid var(--line);border-radius:14px;max-width:520px;width:100%;padding:22px 24px;position:relative;box-shadow:0 18px 50px rgba(0,0,0,.5)}
.itempanel .close{position:absolute;top:10px;right:12px;background:none;border:none;color:var(--muted);font-size:22px;line-height:1;cursor:pointer}
.itempanel .close:hover{color:var(--text)}
.ovhead{display:flex;gap:14px;align-items:center;margin-bottom:12px}
.ovhead .slot{width:64px;height:64px;flex:none;background:#0a0e14}
.ovhead h3{margin:0 0 2px;font-size:17px}
.ovhead code{font-size:11px}
.ovbody{color:var(--text);line-height:1.6;font-size:14px;max-height:46vh;overflow:auto}
.ovbody ul.bullets{padding-left:20px}
.ovbody .misc{color:var(--muted)}
"""


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
    items = all_item_pids()
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
    items = len([k for k in LANG["en"] if k.startswith("item.modulargolems.")])
    if lang == "en":
        title = "Welcome to Modular Golems"
        lead = ("A Tinker-like golem assembly and upgrade mod for Minecraft 1.20.1 (Forge): "
                "craft golems from materials, give them upgrades, equipment and personalities.")
        card_guide = ("Read the Guide", "Browse the in-game Patchouli guide on the web: golem types, tools, upgrades and every material entry.")
        card_mats = ("Golem Materials", "Every material's stats, innate modifiers and craft/repair ingredient, grouped by source mod.")
        card_items = ("Item List", "A searchable catalog of all items added by the mod. Items without a simple 2D sprite use a placeholder tile.")
        hint = ("This site is generated from the mod's own data — <code>site/build_site.py</code> writes <code>docs/</code>. "
                "Regenerate it after changing the book, materials or items.")
        lang_label = "中文"
        other_home = "zh.html"
    else:
        title = "欢迎来到模块化傀儡"
        lead = ("一款类匠魂的傀儡组装与升级模组，适用于 Minecraft 1.20.1（Forge）："
                "用材料组装傀儡，为它们装备升级、装备与个性。")
        card_guide = ("阅读指南", "在线阅读游戏内 Patchouli 指南：傀儡类型、工具、升级与全部材料条目。")
        card_mats = ("傀儡材料", "每种材料的属性、固有词条与合成/修复材料，按来源模组分组。")
        card_items = ("物品列表", "模组注册的全部物品的可检索目录。没有简单 2D 贴图的物品使用占位图。")
        hint = ("本站由模组自身数据生成——<code>site/build_site.py</code> 写入 <code>docs/</code>。"
                "修改指南、材料或物品后请重新生成。")
        lang_label = "EN"
        other_home = "index.html"
    body = f"""
<div class="hero">
  <img class="bookhero" src="assets/tex/item/book.png" alt="">
  <h1>{title}</h1>
  <p class="lead">{lead}</p>
  <div class="statline">
    <span><b>{mats}</b> {"materials" if lang == "en" else "种材料"}</span>
    <span><b>{len(entries)}</b> {"guide entries" if lang == "en" else "条指南条目"}</span>
    <span><b>{items}</b> {"items" if lang == "en" else "个物品"}</span>
    <span><b>2</b> {"languages" if lang == "en" else "种语言"}</span>
  </div>
</div>
<div class="wrap">
  <div class="cards">
    <a class="card" href="book/{lang}/index.html">
      <img class="icon" src="assets/tex/item/book.png" alt="">
      <h3>{card_guide[0]}</h3>
      <p>{card_guide[1]}</p>
    </a>
    <a class="card" href="materials/{lang}/index.html">
      <img class="icon" src="assets/tex/item/metal_golem_template.png" alt="">
      <h3>{card_mats[0]}</h3>
      <p>{card_mats[1]}</p>
    </a>
    <a class="card" href="items/{lang}/index.html">
      <img class="icon" src="assets/tex/item/summon_wand.png" alt="">
      <h3>{card_items[0]}</h3>
      <p>{card_items[1]}</p>
    </a>
  </div>
  <p class="hint">{hint}</p>
</div>
<script>
(function(){{
  var pref=null;
  try{{ pref=localStorage.getItem('mg_lang'); }}catch(e){{}}
  if (pref === '{"zh" if lang == "en" else "en"}') {{ location.replace('{other_home}'); return; }}
  if (pref === '{"en" if lang == "en" else "zh"}') return;
  var l=(navigator.language||'').toLowerCase();
  if (l.indexOf('{"zh" if lang == "en" else "en"}')===0) location.replace('{other_home}');
}})();
</script>"""
    return render_page(page_rel, "Modular Golems" if lang == "en" else "模块化傀儡",
                       lang, "", body)


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
    title = LANG[lang].get("patchouli.modulargolems.title") or "Golem Guide"
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
    return render_page(page_rel, f"{title} — Modular Golems", lang, "guide", "".join(parts))


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
                pages_html.append(f'<section class="page"><p>Recipe <code>{esc(rid)}</code> is not available.</p></section>')
            else:
                grid = render_recipe(recipe, lang)
                text_html = patchouli_text(p.get("text", ""), link_resolver)
                pages_html.append(f'<section class="page">{grid}{text_html}</section>')
        else:
            pages_html.append(f'<section class="page"><p class="misc">(Unknown page type: {esc(ptype)})</p></section>')
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
        f'<nav class="breadcrumb"><a href="{book_root()}{lang}/index.html">Guide</a> / '
        f'<a href="{book_root()}{lang}/index.html#{esc(cat_dir)}">{esc(cat_dir)}</a> / {esc(name)}</nav>'
        f'<header class="bookhead">{icon_html}<h1>{esc(name)}</h1></header>'
        f'<div class="pages">{"".join(pages_html)}</div>{pager}'
        "</div>")
    return render_page(page_rel, f"{name} — Modular Golems", lang, "guide", body)


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
    lead = ("Stats and innate modifiers for every golem material, as loaded from the mod's material configs."
            if lang == "en" else "每种傀儡材料的属性和固有词条，数据来自模组的材料配置。")
    parts = ['<div class="wrap">', "<h1>Golem Materials</h1>", f'<p class="lead">{lead}</p>']
    ns_label = "Sources" if lang == "en" else "来源模组"
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
                mod_html = '<li class="misc">None</li>'
            rep_section = (f'<div class="ing" style="margin-top:12px">{"Repair" if lang == "en" else "修复"}: {rep_html}</div>'
                           if rep_html else "")
            parts.append(
                f'<article class="matcard">'
                f'<h3>{ing_html}<span>{esc(name)}</span></h3>'
                f'<div class="ing">{"Ingredient" if lang == "en" else "材料"}: {ing_label}</div>'
                f'<ul class="kv">{stat_html}</ul>'
                f'<div class="hint">{"Modifiers" if lang == "en" else "词条"}</div>'
                f'<ul class="mods">{mod_html}</ul>'
                f'{rep_section}'
                f'</article>')
        parts.append("</div></section>")
    parts.append("</div>")
    parts.append(f"""<script>
(function(){{
  var data=null, universal=['modulargolems','minecraft'];
  function modsFor(v){{
    if(!data)return null;
    for(var i=0;i<data.versions.length;i++)
      if(data.versions[i].label===v) return data.versions[i].mods;
    return null;
  }}
  function apply(){{
    var v=window.MG_VERSION||(data&&data.current)||'';
    var mods=modsFor(v);
    if(!mods)return;
    var cnt=0;
    document.querySelectorAll('.matsource').forEach(function(sec){{
      var ns=sec.getAttribute('data-ns');
      var show=universal.indexOf(ns)>=0||mods.indexOf(ns)>=0;
      sec.style.display=show?'':'none';
      if(show)cnt++;
    }});
    document.querySelectorAll('.matnavpills a').forEach(function(a){{
      var ns=a.getAttribute('data-ns');
      a.style.display=(universal.indexOf(ns)>=0||mods.indexOf(ns)>=0)?'':'none';
    }});
    var cntEl=document.getElementById('matsrccount');
    if(cntEl)cntEl.textContent=cnt;
  }}
  fetch('{data_root()}versions.json')
    .then(function(r){{return r.json();}})
    .then(function(j){{data=j;apply();}}).catch(function(){{}});
  window.addEventListener('mg-version',apply);
}})();
</script>""")
    return render_page(page_rel, "Materials — Modular Golems", lang, "materials", "".join(parts))


def build_items(lang):
    page_rel = f"items/{lang}/index.html"
    _set_roots(page_rel)
    item_ids = sorted({k.split(".")[-1] for k in LANG["en"] if k.startswith("item.modulargolems.")})

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
    CAT_LABEL = {
        "tools": ("Tools & Templates", "工具与模板"),
        "parts": ("Golem Parts", "傀儡部件"),
        "upgrades": ("Upgrades", "升级"),
        "equipment": ("Equipment & Weapons", "装备与武器"),
        "config": ("Config Cards & Filters", "配置卡与筛选器"),
        "ingredients": ("Materials & Ingredients", "材料与物品"),
        "misc": ("Miscellaneous", "其他"),
    }
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
    parts = ['<div class="wrap">', "<h1>Items</h1>",
             f'<p class="lead">{"All items registered by Modular Golems. Items whose in-game model is a 3D entity (holders, parts, facades) have no simple 2D sprite and are shown with a placeholder tile." if lang == "en" else "模块化傀儡注册的全部物品。游戏内使用 3D 实体模型的物品（傀儡持有物、部件、伪装）没有简单的 2D 贴图，使用占位贴图显示。"}</p>']
    parts.append('<input class="searchbar" id="q" type="search" placeholder="'
                 + ('Filter items…' if lang == "en" else "筛选物品…") + '">')
    for c in CATS:
        lst = grouped[c]
        if not lst:
            continue
        label = CAT_LABEL[c][0 if lang == "en" else 1]
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
    no_desc = "No description available." if lang == "en" else "暂无描述。"
    parts.append(f"""<div class="itemoverlay" id="ov" hidden>
  <div class="itempanel" role="dialog" aria-modal="true">
    <button class="close" id="ovclose" aria-label="Close">×</button>
    <div class="ovhead">
      <span class="slot" id="ovslot"></span>
      <div><h3 id="ovname"></h3><code id="ovid" class="misc"></code></div>
    </div>
    <div class="ovbody" id="ovdesc"></div>
  </div>
</div>""")
    parts.append(f"""<script>
(function(){{
  var q=document.getElementById('q');
  var data=null;
  var descs={{}};
  var noDesc={json.dumps(no_desc)};
  function apply(){{
    var s=q?q.value.toLowerCase():'';
    var v=window.MG_VERSION||(data&&data.current)||'';
    var list=data?data[v]:null;
    if(!list&&data)list=data[data.current];
    var filtered=v&&data&&v!==data.current;
    document.querySelectorAll('.itemcell').forEach(function(c){{
      var show=true;
      if(filtered&&list)show=list.indexOf(c.getAttribute('data-pid'))>=0;
      if(show&&s)show=c.getAttribute('data-search').indexOf(s)>=0;
      c.style.display=show?'':'none';
    }});
    document.querySelectorAll('.itemcat').forEach(function(g){{
      var any=false;
      g.querySelectorAll('.itemgrid').forEach(function(gr){{
        var anyIn=false;
        gr.querySelectorAll('.itemcell').forEach(function(c){{ if(c.style.display!=='none')anyIn=true; }});
        gr.style.display=anyIn?'':'none';
        if(anyIn)any=true;
      }});
      g.style.display=any?'':'none';
    }});
  }}
  var ov=document.getElementById('ov');
  var ovslot=document.getElementById('ovslot');
  var ovname=document.getElementById('ovname');
  var ovid=document.getElementById('ovid');
  var ovdesc=document.getElementById('ovdesc');
  function showDesc(pid,name,img){{
    var d=descs['modulargolems:'+pid];
    ovslot.innerHTML='<img class="itemimg" width="48" height="48" src="'+img+'" alt="">';
    ovname.textContent=name;
    ovid.textContent='modulargolems:'+pid;
    ovdesc.innerHTML=d&&d.text?d.text:'<span class="misc">'+noDesc+'</span>';
    ov.hidden=false;
  }}
  document.querySelectorAll('.itemcell').forEach(function(c){{
    c.addEventListener('click',function(){{
      var img=c.querySelector('img.itemimg');
      var nm=c.querySelector('figcaption').childNodes[0];
      showDesc(c.getAttribute('data-pid'),
               nm?nm.textContent.trim():c.getAttribute('data-pid'),
               img?img.src:'');
    }});
  }});
  document.getElementById('ovclose').addEventListener('click',function(){{ov.hidden=true;}});
  ov.addEventListener('click',function(e){{ if(e.target===ov)ov.hidden=true; }});
  document.addEventListener('keydown',function(e){{ if(e.key==='Escape')ov.hidden=true; }});
  fetch('{data_root()}items.json')
    .then(function(r){{return r.json();}})
    .then(function(j){{data=j;apply();}}).catch(function(){{apply();}});
  fetch('{data_root()}item_descs_{lang}.json')
    .then(function(r){{return r.json();}})
    .then(function(j){{descs=j;}}).catch(function(){{}});
  window.addEventListener('mg-version',apply);
  if(q)q.addEventListener('input',apply);
}})();
</script>""")
    return render_page(page_rel, "Items — Modular Golems", lang, "items", "".join(parts))


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
