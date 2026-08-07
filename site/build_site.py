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

SOURCE_NAMES = {
    "en": {
        "modulargolems": "Vanilla",
        "alexscaves": "Alex's Caves",
        "allthemodium": "Allthemodium",
        "blazegear": "Blaze Gear",
        "botania": "Botania",
        "cataclysm": "L_Ender's Cataclysm",
        "composite_material": "Composite Material",
        "create": "Create",
        "goety": "Goety",
        "goety_revelation": "Goety Revelation",
        "iceandfire": "Ice and Fire",
        "l2complements": "L2 Complements",
        "l2hostility": "L2 Hostility",
        "legendary_monsters": "Legendary Monsters",
        "mowziesmobs": "Mowzie's Mobs",
        "tconstruct": "Tinkers' Construct",
        "twilightforest": "Twilight Forest",
    },
    "zh": {
        "modulargolems": "原版",
        "alexscaves": "Alex's Caves",
        "allthemodium": "Allthemodium",
        "blazegear": "Blaze Gear",
        "botania": "Botania",
        "cataclysm": "灾变",
        "composite_material": "Composite Material",
        "create": "机械动力",
        "goety": "Goety",
        "goety_revelation": "Goety Revelation",
        "iceandfire": "冰与火之歌",
        "l2complements": "L2 补全",
        "l2hostility": "L2 敌意",
        "legendary_monsters": "传奇生物",
        "mowziesmobs": "Mowzie's Mobs",
        "tconstruct": "匠魂",
        "twilightforest": "暮色森林",
    },
}

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
    "weight": {"en": "Weight", "zh": "负重", "kind": "PERCENT"},
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


def _set_roots(page_rel):
    global assets_root, book_root
    depth = len(Path(page_rel).parts) - 1
    prefix = "../" * depth
    assets_root = lambda: prefix + "assets/"
    book_root = lambda: prefix + "book/"


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


def resolve_icon(reg_id):
    reg_id = MODULAR_ALIAS.get(reg_id, reg_id)
    ns, path = reg_id.split(":", 1)
    vend = f"{ns}/item/{path}.png"
    if find_tex(vend):
        return vend, None
    if ns == "modulargolems":
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


def book_link(target, lang):
    target = target.split("#")[0]
    if target.startswith("http"):
        return target
    for cand in [target.strip("/"), target.strip("/").split("/")[-1]]:
        p = BOOK_DIR / LANG_CODE[lang] / "entries" / f"{cand}.json"
        if p.exists():
            return f"{book_root()}{lang}/{cand}.html"
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
    links = [
        ("index.html", SITE_TITLES[lang], "brand"),
        (f"book/{lang}/index.html", lbl["guide"], "guide"),
        (f"materials/{lang}/index.html", lbl["materials"], "materials"),
        (f"items/{lang}/index.html", lbl["items"], "items"),
    ]
    buf = ['<nav class="topnav">']
    for href, txt, key in links:
        cls = "active" if key == active else ""
        buf.append(f'<a class="{cls}" href="{root}{href}">{esc(txt)}</a>')
    buf.append('<span class="spacer"></span>')
    buf.append(lang_switch_html(root, lang, page_rel))
    buf.append("</nav>")
    return "".join(buf)


def lang_switch_html(root, lang, page_rel):
    other = "zh" if lang == "en" else "en"
    other_rel = page_rel.replace(f"/{lang}/", f"/{other}/", 1)
    label = "中文" if lang == "en" else "EN"
    return f'<a class="lang" href="{root}{other_rel}" title="Switch language / 切换语言">{esc(label)}</a>'


def render_page(page_rel, title, lang, active, body):
    root = "../" * (len(Path(page_rel).parts) - 1)
    return f"""<!doctype html>
<html lang="en">
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

.matnav{position:sticky;top:52px;display:flex;gap:8px;flex-wrap:wrap;background:var(--bg);padding:10px 0;border-bottom:1px solid var(--line);z-index:40}
.matnav a{font-size:13px;color:var(--muted);background:var(--panel);border:1px solid var(--line);padding:5px 12px;border-radius:999px}
.matnav a:hover{color:var(--accent);border-color:var(--accent)}
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
.itemcat h2{border-bottom:1px solid var(--line);padding-bottom:8px}
.itemgrid{display:grid;grid-template-columns:repeat(auto-fill,minmax(120px,1fr));gap:10px;margin:12px 0}
.itemcell{background:var(--panel);border:1px solid var(--line);border-radius:10px;padding:10px;text-align:center}
.itemcell:hover{border-color:var(--accent)}
.itemcell .slot{width:56px;height:56px;margin:0 auto;background:#0a0e14}
.itemcell .itemimg{width:40px;height:40px}
.itemcell figcaption{margin-top:8px;font-size:12px;line-height:1.3;color:var(--text);word-break:break-word}
.itemcell figcaption small{display:block;color:var(--muted);font-size:10px}
.itemcell figure{margin:0}
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


def modifier_info(mod_id, lang):
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
    return name, desc


# ---------------------------------------------------------------------------
# page bodies
# ---------------------------------------------------------------------------

def build_index():
    _set_roots("index.html")
    mats = len(load_materials())
    _, entries = load_book("en")
    items = len([k for k in LANG["en"] if k.startswith("item.modulargolems.")])
    body = f"""
<div class="hero">
  <img class="bookhero" src="assets/tex/item/book.png" alt="">
  <h1>Modular Golems</h1>
  <p class="lead">Welcome to a Tinker-like golem assembly and upgrade mod for Minecraft 1.20.1 (Forge).</p>
  <div class="statline">
    <span><b>{mats}</b> materials</span>
    <span><b>{len(entries)}</b> guide entries</span>
    <span><b>{items}</b> items</span>
    <span><b>2</b> languages</span>
  </div>
</div>
<div class="wrap">
  <div class="cards">
    <a class="card" href="book/en/index.html">
      <img class="icon" src="assets/tex/item/book.png" alt="">
      <h3>Read the Guide</h3>
      <p>Browse the in-game Patchouli guide on the web: golem types, tools, upgrades and every material entry.</p>
    </a>
    <a class="card" href="materials/en/index.html">
      <img class="icon" src="assets/tex/item/metal_golem_template.png" alt="">
      <h3>Golem Materials</h3>
      <p>Every material's stats, innate modifiers and craft/repair ingredient, grouped by source mod.</p>
    </a>
    <a class="card" href="items/en/index.html">
      <img class="icon" src="assets/tex/item/summon_wand.png" alt="">
      <h3>Item List</h3>
      <p>A searchable catalog of all items added by the mod. Items without a simple 2D sprite use a placeholder tile.</p>
    </a>
  </div>
  <p class="hint">This site is generated from the mod's own data — <code>site/build_site.py</code> writes <code>docs/</code>. Regenerate it after changing the book, materials or items.</p>
</div>"""
    return render_page("index.html", "Modular Golems", "en", "", body)


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
    for p in d.get("pages", []):
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
            icons = "".join(icon_markup(it, lang, size=32, show_name=True) for it in items if it)
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
    mats = load_materials()
    by_ns = {}
    for mid, d in mats.items():
        by_ns.setdefault(d["ns"], []).append((mid, d))
    for ns, lst in by_ns.items():
        lst.sort(key=lambda t: material_display_name(t[0], lang))
    ns_order = sorted(by_ns.keys())
    lead = ("Stats and innate modifiers for every golem material, as loaded from the mod's material configs."
            if lang == "en" else "每种傀儡材料的属性和固有词条，数据来自模组的材料配置。")
    parts = ['<div class="wrap">', "<h1>Golem Materials</h1>", f'<p class="lead">{lead}</p>']
    parts.append('<nav class="matnav">')
    for ns in ns_order:
        parts.append(f'<a href="#ns-{esc(ns)}">{esc(SOURCE_NAMES[lang].get(ns, ns))}</a>')
    parts.append("</nav>")
    for ns in ns_order:
        parts.append(f'<section class="matsource" id="ns-{esc(ns)}"><h2>{esc(SOURCE_NAMES[lang].get(ns, ns))} <span class="misc">({len(by_ns[ns])})</span></h2></section>')
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
                mname, mdesc = modifier_info(mid2, lang)
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
        parts.append("</div>")
    parts.append("</div>")
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
        if pid in ("recycle", "diamond", "netherite", "quartz", "gold", "enchanted_gold", "float",
                   "sponge", "swim", "player", "ender_sight", "bell", "speed", "slow", "weak", "wither",
                   "emerald", "pickup", "pickup_mending", "pickup_no_destroy", "talented", "cauldron",
                   "mount_upgrade", "size_upgrade", "fire_immune", "thunder_immune",
                   "attack_high", "speed_high") or pid.endswith("_upgrade"):
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
        grouped[category(pid)].append(pid)
    parts = ['<div class="wrap">', "<h1>Items</h1>",
             f'<p class="lead">{"All items registered by Modular Golems. Items whose in-game model is a 3D entity (holders, parts, facades) have no simple 2D sprite and are shown with a placeholder tile." if lang == "en" else "模块化傀儡注册的全部物品。游戏内使用 3D 实体模型的物品（傀儡持有物、部件、伪装）没有简单的 2D 贴图，使用占位贴图显示。"}</p>']
    parts.append('<input class="searchbar" id="q" type="search" placeholder="'
                 + ('Filter items…' if lang == "en" else "筛选物品…") + '">')
    for c in CATS:
        lst = grouped[c]
        if not lst:
            continue
        lst.sort()
        label = CAT_LABEL[c][0 if lang == "en" else 1]
        parts.append(f'<section class="itemcat"><h2>{esc(label)} <span class="misc">({len(lst)})</span></h2><div class="itemgrid">')
        for pid in lst:
            reg = f"modulargolems:{pid}"
            rel, slug = resolve_icon(reg)
            if rel:
                src = f"{assets_root()}tex/{rel}"
            else:
                ensure_placeholder(slug)
                src = f"{assets_root()}img/ph-{slug}.svg"
            name = item_name(reg, lang)
            hay = (name + " " + pid + " modulargolems").lower()
            parts.append(
                f'<figure class="itemcell" data-search="{esc(hay)}">'
                f'<span class="slot"><img class="itemimg" width="40" height="40" loading="lazy" src="{src}" '
                f'alt="{esc(name)}" title="{esc(reg)}"></span>'
                f'<figcaption>{esc(name)}<small>{esc(reg)}</small></figcaption></figure>')
        parts.append("</div></section>")
    parts.append("</div>")
    parts.append("""<script>
(function(){
  var q=document.getElementById('q');
  if(!q)return;
  q.addEventListener('input',function(){
    var s=q.value.toLowerCase();
    document.querySelectorAll('.itemcell').forEach(function(c){
      c.style.display = c.getAttribute('data-search').indexOf(s) >= 0 ? '' : 'none';
    });
    document.querySelectorAll('.itemcat').forEach(function(g){
      var any=false;
      g.querySelectorAll('.itemcell').forEach(function(c){ if(c.style.display!=='none') any=true; });
      g.style.display = any ? '' : 'none';
    });
  });
})();
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
                shutil.copyfile(f, dest)

    if VENDOR_TEX.is_dir():
        for f in VENDOR_TEX.rglob("*"):
            if f.is_file():
                rel = f.relative_to(VENDOR_TEX)
                dest = OUT / "assets/tex" / rel
                dest.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(f, dest)

    RECIPE_BY_ID, RECIPE_BY_RESULT = load_recipe_index()

    def write_page(page_rel, html):
        dest = OUT / page_rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_text(html, encoding="utf-8")

    write_page("index.html", build_index())
    for lang in LANGS:
        write_page(f"book/{lang}/index.html", build_book(lang))
        build_book_all(lang, write_page)
        write_page(f"materials/{lang}/index.html", build_materials(lang))
        write_page(f"items/{lang}/index.html", build_items(lang))

    for rel, src in REFERENCED_TEX.items():
        dest = OUT / "assets/tex" / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dest)

    n_tex = sum(1 for _ in (OUT / "assets/tex").rglob("*.png"))
    n_ph = len(list((OUT / "assets/img").glob("ph-*.svg")))
    n_html = len(list(OUT.rglob("*.html")))
    print(f"OK: site written to {OUT}")
    print(f"  html pages: {n_html}  textures: {n_tex}  placeholders: {n_ph}")


if __name__ == "__main__":
    main()
