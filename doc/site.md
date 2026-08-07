# Modular Golems — Website

A small static website for the mod, published on GitHub Pages, that lets players
read the in-game Patchouli guide, browse every golem material's stats and
modifiers, and search the item catalog — all without launching Minecraft.

Everything on the site is **generated from the mod's own source data** by a
single Python script, `site/build_site.py`. There is no server, no build CI,
and no hand-edited HTML: the script writes `docs/`, and GitHub Pages serves
`docs/` directly from the repo.

---

## 1. Architecture

```
site/build_site.py          generator (single-file, stdlib-only Python 3)
site/templates/*.txt        page/JS/CSS templates loaded by the script (see §3.9)
site/data/*.json            constants: name maps, stat info, site config, chrome (see §3.1)
site/mod_names.json         source mod -> {en, zh} display name (manual, see §3.7)
site/modifier_values.json   per-modifier, per-level %s values (see §3.8)
docs/                       generated output, committed and served by GitHub Pages
├── index.html              landing page (en)
├── zh.html                 landing page (zh)
├── .nojekyll               tells Pages not to run Jekyll
├── css/style.css           single shared stylesheet (site/templates/style.css.txt)
├── assets/
│   ├── tex/                copies of item + vendor textures (see §3.4)
│   └── img/ph-*.svg        generated placeholder tiles (see §3.4)
├── data/
│   ├── versions.json       per-version compat mods, parsed from each branch's CompatManager
│   ├── items.json          per-version item lists, from model-file existence per branch
│   ├── mod_names.json      source mod -> {en, zh} display name, from site/mod_names.json
│   ├── compat_items.json   compat ingredient item -> {en, zh} display name
│   └── item_descs_{en,zh}.json  item -> guide description (for the items overlay)
├── book/
│   ├── en/  ├── index.html       guide TOC
│   │        ├── golems/<entry>.html
│   │        ├── materials/<entry>.html
│   │        └── upgrades/<entry>.html
│   └── zh/  └── ...               same layout, zh_cn
├── materials/
│   ├── en/index.html              material catalog (grouped by source mod)
│   └── zh/index.html
└── items/
    ├── en/index.html              searchable, collapsible item list
    └── zh/index.html
```

Every page shares the same nav: brand, section links, a **version switcher**
(`<select class="vselect">`, §3.6) and a **language switch** (`a.lang`). A small
script embedded by `render_page()` (site/build_site.py:887) persists the chosen
version in `localStorage`, honors a `?v=<label>` query param, and fires a
`mg-version` custom event that the section pages listen to for filtering.

### 1.1 Data sources read by the generator

| Data | Path |
|---|---|
| Patchouli book | `src/main/resources/assets/modulargolems/patchouli_books/golem_guide/{en_us,zh_cn}/` (categories + entries) |
| Material configs | `src/generated/resources/data/*/modulargolems_config/materials/*.json` (mod namespace + each compat mod) |
| English lang | `src/generated/resources/assets/modulargolems/lang/en_us.json` |
| Chinese lang | `src/main/resources/assets/modulargolems/lang/zh_cn.json` |
| Recipes | `src/generated/resources/data/modulargolems/recipes/**/*.json` |
| Item textures | `src/main/resources/assets/modulargolems/textures/item/` (incl. `equipments/`, `upgrades/`, `card/`, `dog_armor/`) |
| Item models | `src/generated/resources/assets/modulargolems/models/item/*.json` (per branch, via `git ls-tree`) |
| CompatManager | `src/main/java/.../compat/materials/common/CompatManager.java` (per branch, via `git show`) |
| Compat item lang | external mod jars from the Gradle cache (names baked into `COMPAT_ITEM_NAMES`, §3.1) |

### 1.2 Generator pipeline

`main()` (site/build_site.py:1486):

1. Deletes `docs/`, recreates the directory skeleton, writes `.nojekyll` and `css/style.css`.
2. Copies the item texture tree into `docs/assets/tex/`.
3. Loads the recipe index into `RECIPE_BY_ID` / `RECIPE_BY_RESULT`.
4. Writes the `docs/data/` JSON files (versions, items, mod names, compat item
   names, per-language item descriptions) — these back the client-side
   version filtering and the item description overlay. The JSON is emitted with
   `ensure_ascii=False`, so Chinese translations are stored as real UTF-8 text
   rather than `\uXXXX` escapes.
5. Builds and writes every page (see §2), then prints a summary line with page/texture/placeholder counts.

The script has no third-party dependencies — only the Python 3 stdlib
(`hashlib`, `html`, `json`, `re`, `shutil`, `struct`, `subprocess`, `zlib`,
`pathlib`).
`subprocess` is used to inspect other version branches (`git show`, `git ls-tree`).

---

## 2. Pages

### 2.1 Landing page — `index.html` / `zh.html`
`build_index(lang)` (site/build_site.py:1117). Hero with the book icon, current
material/entry/item counts (computed live from the data), and cards linking to
the three sections. Generated once per language (`index.html` en, `zh.html` zh).

### 2.2 Guide — `book/{lang}/index.html`
`build_book()` (site/build_site.py:1172). TOC listing the three categories
(`golems`, `materials`, `upgrades`) sorted by `sortnum`, each with its icon,
description, and linked entries. Category names/descriptions come from the
`patchouli.modulargolems.title` / `.landing` lang keys and the category files.

### 2.3 Guide entries — `book/{lang}/{category}/{entry}.html`
`build_book_entry()` (site/build_site.py:1206), dispatched from
`build_book_all()` (site/build_site.py:1270). Renders:

- header icon + name, breadcrumb to the category,
- each page of the entry, converting Patchouli page types:
  - `patchouli:text` → formatted text,
  - `patchouli:spotlight` → item icon(s) + text (no item name shown below the icon),
  - `patchouli:crafting` → recipe grid (see §3.3),
- prev/next pager within the category.

Material guide entries (`materials` category) show only their **first page** —
the rest are part stats that don't render usefully on the web.

Cross-entry links in the book text are resolved by `book_link()` against the
actual entry files, so `$(l:...)` links point at the corresponding HTML page.

### 2.4 Materials — `materials/{lang}/index.html`
`build_materials()` (site/build_site.py:1293). Groups all materials by their
source namespace (Vanilla, Create, Twilight Forest, …) with an anchor nav. Each
material card shows: craft ingredient, stat lines, innate modifier names +
descriptions, and the repair ingredient (omitted when it is the same as the
craft ingredient). Each group is wrapped in its own `<section class="matsource">`
so the version switcher's filtering hides the whole group (title + cards), not
just the title. The nav's source count also updates when the version changes.

The `%s` placeholders in modifier descriptions are filled with the values for
the material's modifier level, looked up from `site/modifier_values.json`
(a manually maintained per-modifier, per-level value table — see §3.8).

Note on stats: the material configs' `modulargolems:weight` stat is really a
movement-speed multiplier (`STAT_WEIGHT` in `GolemTypes.java` is backed by
 `Attributes.MOVEMENT_SPEED`), so it is labeled **Speed** (`STAT_INFO["weight"]`,
site/build_site.py:116) rather than "Weight".

### 2.5 Items — `items/{lang}/index.html`
`build_items()` (site/build_site.py:1375). Lists every `item.modulargolems.*`
lang key in seven heuristic categories (tools, parts, upgrades, equipment,
config, ingredients, misc):

- categories are **collapsible** (`<details class="itemcat" open>` with a
  `▸/▾` chevron);
- a client-side live search filters cells by name/registry id (`#q` input);
- **clicking a cell opens an overlay** (`.itemoverlay`) showing the item's
  icon, name, registry id and a description from `data/item_descs_{lang}.json`
  (built from the Patchouli entries that spotlight the item); items without an
  entry fall back to a "No description available." placeholder;
- version filtering is driven by `data/items.json` (see §3.6).

---

## 3. Design notes

### 3.1 Localization
Both languages are generated from the same builders — only the loaded lang
table and `LANG_CODE` mapping differ (`en` → `en_us`, `zh` → `zh_cn`). Every
page carries a **language switch** (`lang_switch_html()`, site/build_site.py:875)
that rewrites the `/{lang}/` path segment. Navigation labels and page titles
come from `NAV_LABELS` / `SITE_TITLES`.

Names not present in the mod's lang files fall back through several maps loaded
from `site/data/*.json` at startup (`load_data()`, site/build_site.py:92), then
to the raw id:

- `VANILLA_NAMES` (site/build_site.py:102) — vanilla items referenced by recipes/spotlights,
- `TAG_NAMES` (site/build_site.py:103) — common item tags (`#forge:ingots/...`),
- `MOD_NAMES` (site/build_site.py:56) — compat mod display names, loaded from
  `site/mod_names.json`; also emitted as `data/mod_names.json`
  (`build_mod_names_json`, site/build_site.py:1014) so pages/JS can localize mod
  names,
- `COMPAT_ITEM_NAMES` (site/build_site.py:104) — bilingual names for the compat
  ingredient items used in material configs. These names are taken from each
  mod's own `en_us`/`zh_cn` lang files (blazegear has no zh, so its name is
  aligned with the mod's own `golem_material.blazegear.brimsteel` = 烈焰钢);
  the map is emitted as `data/compat_items.json` (`build_compat_items_json`,
  site/build_site.py:1020). `item_name()` (site/build_site.py:573) consults it
  so material cards and book spotlights show e.g. "ATM锭" instead of
  `allthemodium:allthemodium_ingot`,
- `STAT_INFO` (site/build_site.py:116) — stat labels + formatting kind
  (`BASE` / `ADD` / `PERCENT`).

All page-builder copy (landing hero/cards, materials/items headings, category
labels, page titles, chrome labels) lives in `site/data/lang.json` as
`key -> {en, zh}` and is looked up through `tr(key, lang)` (site/build_site.py:110).
Strings that are currently only rendered in English (page titles, "None",
"Guide", …) have identical `en`/`zh` values there, ready to be translated.

### 3.2 Styling
A single shared stylesheet (`site/templates/style.css.txt`, loaded as `CSS`) with a dark, Minecraft-adjacent
theme (CSS custom properties: `--bg`, `--panel`, `--accent`, parchment `--parch`
variants, etc.). Sticky top nav, card grids, recipe grids, and a parchment
"book page" treatment for guide entries. Chinese-friendly font stack
(`PingFang SC`, `Microsoft YaHei`).

### 3.3 Recipe grids
`render_recipe()` (site/build_site.py:730) renders the mod's custom recipe types
(`modulargolems:golem_assemble`, `modulargolems:golem_replace_part`, …). Because
the recipe id referenced by the book (`modulargolems:metal_golem_holder`) rarely
matches the file path (e.g. `metal_golem/assemble_holder.json`), the generator
builds a **result-item index** (`RECIPE_BY_RESULT`); when several recipes produce
the same item it prefers a shaped one (`pattern` + `key`).

### 3.4 Icons and placeholders
`resolve_icon()` (site/build_site.py:533) tries a list of candidate texture
paths for a registry id (exact name, `_icon`, then `equipments/`, `upgrades/`,
`card/`, `dog_armor/`). If nothing matches, the item has no simple 2D sprite
and gets a deterministic placeholder tile:

- `placeholder_svg()` renders a 64×64 SVG with an md5-derived color gradient and
  a centered letter,
- the file is written once to `docs/assets/img/ph-<slug>.svg` by
  `ensure_placeholder()` and reused across pages,
- slugs are sanitized with `slugify()` (this matters for tags containing `/`,
  e.g. `l2hostility:hostility/miracle`).

Items that use in-game 3D entity models — holders, parts, facades — are expected
to end up as placeholders; that is the intended behavior.

Animated item textures (Minecraft animation strips — a vertical stack of square
frames) are cropped to their **first frame** when copied into `docs/assets/tex/`
by `copy_texture()` (site/build_site.py:368), so they render as a single static
sprite instead of the whole stretched strip. Cropping is stdlib-only
(`struct` + `zlib`, filters 0–4, non-interlaced 8-bit RGBA/RGB/grayscale/palette
and 4-bit palette); anything it can't handle is copied unchanged.

### 3.5 Version-aware data
The site covers five version branches (1.19.2 → `1.19`, 1.19.4 → `1.19.4`,
1.20.1 → `1.20`, 1.21.1 → `1.21`, 26.1.2 → `26.1`). Two generated JSON files
describe what each version contains:

- `data/versions.json` — per-version **compat mods**, parsed from each branch's
  `CompatManager.register()` via `git show` (`compat_mods_for()`, commented-out
  dispatches are ignored, so 26.1.2 has none). This filters the **materials**
  page: sections whose namespace isn't supported in the selected version are
  hidden.
- `data/items.json` — per-version **item lists**, keyed off **model-file
  existence**: `branch_item_models()` (site/build_site.py:181) lists
  `src/generated/resources/assets/modulargolems/models/item/*.json` on each
  branch via `git ls-tree` (the current version reads the working tree), then
  intersects with the known `item.modulargolems.*` ids. The **items** page
  filters cells by this list when a non-current version is selected.

Both pages listen for the shared `mg-version` event (set by the nav switcher)
and re-apply filtering client-side.

### 3.6 Version switcher
`version_switch_html()` (site/build_site.py:865) renders the `<select class="vselect">`
in the nav with one option per version. The script embedded by `render_page()`
persists the choice in `localStorage["mg_version"]`, syncs the `?v=` query
param via `history.replaceState`, and dispatches `mg-version`. The section
pages (materials/items) use it with the JSON above to hide content that does
not exist in the selected version; book pages do not filter (the guide is
current-version content).

### 3.7 Mod names
`site/mod_names.json` maps each source-mod namespace to `{en, zh}` display names
(e.g. `alexscaves` → `Alex's Caves` / `艾利克斯的洞穴`). It is **maintained
manually** — add a key when a new compat mod is supported. Pages read it through
`mod_name(ns, lang)` (site/build_site.py:59) for the materials page's source
groups and the items page's upgrade subgroups, and the same data is emitted as
`data/mod_names.json` for pages/JS to localize on the fly.

### 3.8 Modifier values
`site/modifier_values.json` maps each modifier id to per-level arrays of values
for the `%s` placeholders in its description (e.g. `add_slot` → level 2 → `[2]`).
`fill_modifier_desc()` (site/build_site.py) substitutes them on the materials
page, handling both `%s` and positional `%N$s` (used by some zh_cn strings) and
converting `%%` to a literal `%`. The JSON is **maintained manually** — update it
when a modifier's config value or level scaling changes.

### 3.9 Multi-layer item icons
Items whose model renders several texture layers — dog golem armor (collar +
wolf armor) — are blended into a single icon by `composite_textures()`
(site/build_site.py:475) at build time, using alpha-over compositing
(`COMPOSITE_TEX` collects the sources during icon resolution).

### 3.10 Page templates
The page skeleton, the CSS, and the per-page JS blocks live as plain text files
under `site/templates/` and are loaded at startup by `load_template()`
(site/build_site.py): `page.html.txt` (shared `<html>` skeleton + version/lang
script), `index_body.html.txt` (landing hero + cards + lang-redirect script),
`materials.js.txt` / `items.js.txt` (version-filter scripts) and
`items_overlay.html.txt` (the item-detail dialog). Dynamic values use
`string.Template` `$placeholders`, substituted with `.substitute()` at build
time; a trailing newline is trimmed on load so output matches the older inline
f-strings. `style.css.txt` is written to `docs/css/style.css` verbatim.

---

## 4. Maintenance flow

Regenerate the site after any change to the book, materials, lang files,
recipes, or textures:

```sh
python3 site/build_site.py
```

The script is deterministic and regenerates `docs/` from scratch, so a diff
check after `runData` (see AGENTS.md) is the way to validate it:

1. Make the mod-side change (new material in `MGConfigGen`, new book entry,
   new item, updated translation, …).
2. Run `./gradlew runData` and commit generated resources if anything changed.
3. Run `python3 site/build_site.py`.
4. Sanity-check the summary line and `git status`:

```sh
OK: site written to .../docs
  html pages: 264  textures: 338  placeholders: 29
```

The version-aware JSONs (`data/versions.json`, `data/items.json`) depend on the
other branches' `CompatManager.java` and item models. New branches must be
fetched locally and added to `VERSIONS` (and `BUILD_VERSION`) in the script
before they can appear in the switcher.

A rough link-integrity check (every local `href`/`src` resolves):

```sh
python3 - <<'EOF'
import re, pathlib
out = pathlib.Path("docs")
bad = []
for f in out.rglob("*.html"):
    for m in re.finditer(r'(?:href|src)="([^"#]+)(?:#[^"]*)?"', f.read_text(encoding="utf-8")):
        u = m.group(1)
        if not u.startswith("http") and not (f.parent / u).exists():
            bad.append((str(f), u))
print("broken refs:", len(bad)); [print(" ", b) for b in bad[:20]]
EOF
```

### 4.1 Adding a category or reclassifying items
Category behavior lives in `build_items()` (the `category(pid)` heuristic and
`CAT_LABEL` map) and in the `golems`/`materials`/`upgrades` Patchouli categories
(read from the book itself). If a new Patchouli category is added to the book,
`build_book()`/`build_book_all()` pick it up automatically; only the entry sort
order and the `cat_dir` breadcrumb anchor rely on category ids.

### 4.2 Item descriptions
`data/item_descs_{lang}.json` is built by `build_item_descs()` (site/build_site.py:1025)
from the Patchouli guide entries: an entry is mapped to every item it spotlights
(top-level `icon` or `patchouli:spotlight` pages), with the entry's page text
formatted via `patchouli_text()`. Guide links inside those descriptions are
resolved relative to the consuming items page (`prefix="../../book/"`, so the
overlay's links reach the actual entry pages). Regenerating after editing the
book updates the descriptions automatically.

---

## 5. Publishing (GitHub Pages)

The site is published from the `docs/` folder of the `gh-pages` branch of the
`Minecraft-LightLand/ModularGolems` repository.

1. Commit the generator and the generated output:

   ```sh
   git add site/build_site.py docs/
   git commit -m "Update generated docs site"
   ```

   Keep `doc/site.md` and `doc/todo.md` up to date in the same commit if touched.

2. Push the branch:

   ```sh
   git push origin gh-pages
   ```

3. Enable Pages (one-time, repo settings, GitHub web UI):
   **Settings → Pages → Source → Deploy from a branch** → branch `gh-pages`
   → folder `/docs`. (`docs/.nojekyll` is already generated so Jekyll never runs.)

4. The site is served at:

   ```
   https://minecraft-lightland.github.io/ModularGolems/
   ```

5. Republish after every regeneration by repeating steps 1–2. There is no CI;
   publishing is a manual `docs/` commit, which keeps the branch's git history
   as the changelog.

### 5.1 First-time publishing
If Pages is enabled but the URL 404s, verify the branch name and folder
selection in Settings → Pages, and that `docs/index.html` exists at the branch
root. Allow a minute or two for Pages to deploy after the push.
