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
docs/                       generated output, committed and served by GitHub Pages
├── index.html              landing page (en only)
├── .nojekyll               tells Pages not to run Jekyll
├── css/style.css           single shared stylesheet (embedded in the script)
├── assets/
│   ├── tex/item/           copy of src/main/resources/.../textures/item/
│   └── img/ph-*.svg        generated placeholder tiles (see §3.4)
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
    ├── en/index.html              searchable item list
    └── zh/index.html
```

### 1.1 Data sources read by the generator

| Data | Path |
|---|---|
| Patchouli book | `src/main/resources/assets/modulargolems/patchouli_books/golem_guide/{en_us,zh_cn}/` (categories + entries) |
| Material configs | `src/generated/resources/data/*/modulargolems_config/materials/*.json` (mod namespace + each compat mod) |
| English lang | `src/generated/resources/assets/modulargolems/lang/en_us.json` |
| Chinese lang | `src/main/resources/assets/modulargolems/lang/zh_cn.json` |
| Recipes | `src/generated/resources/data/modulargolems/recipes/**/*.json` |
| Item textures | `src/main/resources/assets/modulargolems/textures/item/` (incl. `equipments/`, `upgrades/`, `card/`, `dog_armor/`) |

### 1.2 Generator pipeline

`main()` (site/build_site.py:1078):

1. Deletes `docs/`, recreates the directory skeleton, writes `.nojekyll` and `css/style.css`.
2. Copies the entire item texture tree into `docs/assets/tex/item/`.
3. Loads the recipe index into `RECIPE_BY_ID` / `RECIPE_BY_RESULT`.
4. Builds and writes every page (see §2), then prints a summary line with page/texture/placeholder counts.

The script has no third-party dependencies — only the Python 3 stdlib
(`hashlib`, `html`, `json`, `re`, `shutil`, `pathlib`).

---

## 2. Pages

### 2.1 Landing page — `index.html`
`build_index()` (site/build_site.py:753). Hero with the book icon, current
material/entry/item counts (computed live from the data), and cards linking to
the three sections. English only.

### 2.2 Guide — `book/{lang}/index.html`
`build_book()` (site/build_site.py:793). TOC listing the three categories
(`golems`, `materials`, `upgrades`) sorted by `sortnum`, each with its icon,
description, and linked entries.

### 2.3 Guide entries — `book/{lang}/{category}/{entry}.html`
`build_book_entry()` (site/build_site.py:827), dispatched from
`build_book_all()` (site/build_site.py:888). Renders:

- header icon + name, breadcrumb to the category,
- each page of the entry, converting Patchouli page types:
  - `patchouli:text` → formatted text,
  - `patchouli:spotlight` → item icon(s) + text,
  - `patchouli:crafting` → recipe grid (see §3.3),
- prev/next pager within the category.

Cross-entry links in the book text are resolved by `book_link()` against the
actual entry files, so `$(l:...)` links point at the corresponding HTML page.

### 2.4 Materials — `materials/{lang}/index.html`
`build_materials()` (site/build_site.py:911). Groups all materials by their
source namespace (Vanilla, Create, Twilight Forest, …) with an anchor nav. Each
material card shows: craft ingredient, stat lines, innate modifier names +
descriptions, and the repair ingredient.

### 2.5 Items — `items/{lang}/index.html`
`build_items()` (site/build_site.py:978). Lists every `item.modulargolems.*`
lang key in seven heuristic categories (tools, parts, upgrades, equipment,
config, ingredients, misc) with a client-side live search filter
(`#q` input + a small vanilla JS snippet at the bottom of the page).

---

## 3. Design notes

### 3.1 Localization
Both languages are generated from the same builders — only the loaded lang
table and `LANG_CODE` mapping differ (`en` → `en_us`, `zh` → `zh_cn`). Every
page carries a **language switch** (`lang_switch_html()`) that rewrites the
`/{lang}/` path segment; the landing page is en-only. Navigation labels and page
titles come from `NAV_LABELS` / `SITE_TITLES`.

Names not present in the mod's lang files fall back through several maps defined
at the top of the script, then to the raw id:

- `VANILLA_NAMES` — vanilla items referenced by recipes/spotlights,
- `TAG_NAMES` — common item tags (`#forge:ingots/...`),
- `SOURCE_NAMES` — compat mod display names,
- `STAT_INFO` — stat labels + formatting kind (`BASE` / `ADD` / `PERCENT`).

### 3.2 Styling
A single inline `CSS` string (site/build_site.py:570) with a dark, Minecraft-adjacent
theme (CSS custom properties: `--bg`, `--panel`, `--accent`, parchment `--parch`
variants, etc.). Sticky top nav, card grids, recipe grids, and a parchment
"book page" treatment for guide entries. Chinese-friendly font stack
(`PingFang SC`, `Microsoft YaHei`).

### 3.3 Recipe grids
`render_recipe()` (site/build_site.py:423) renders the mod's custom recipe types
(`modulargolems:golem_assemble`, `modulargolems:golem_replace_part`, …). Because
the recipe id referenced by the book (`modulargolems:metal_golem_holder`) rarely
matches the file path (e.g. `metal_golem/assemble_holder.json`), the generator
builds a **result-item index** (`RECIPE_BY_RESULT`); when several recipes produce
the same item it prefers a shaped one (`pattern` + `key`).

### 3.4 Icons and placeholders
`resolve_icon()` (site/build_site.py:216) tries a list of candidate texture
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
  html pages: 263  textures: 111  placeholders: 209
```

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

---

## 5. Publishing (GitHub Pages)

The site is published from the `docs/` folder of the `lcy0x1/page` branch.

1. Commit the generator and the generated output:

   ```sh
   git add site/build_site.py docs/
   git commit -m "Add generated docs site and site generator"
   ```

   Keep `doc/site.md` and `doc/todo.md` up to date in the same commit if touched.

2. Push the branch:

   ```sh
   git push origin lcy0x1/page
   ```

3. Enable Pages (one-time, repo settings, GitHub web UI):
   **Settings → Pages → Source → Deploy from a branch** → branch `lcy0x1/page`
   → folder `/docs`. (`docs/.nojekyll` is already generated so Jekyll never runs.)

4. The site is served at:

   ```
   https://<owner>.github.io/ModularGolems/
   ```

5. Republish after every regeneration by repeating steps 1–2. There is no CI;
   publishing is a manual `docs/` commit, which keeps the branch's git history
   as the changelog.

### 5.1 First-time publishing
If Pages is enabled but the URL 404s, verify the branch name and folder
selection in Settings → Pages, and that `docs/index.html` exists at the branch
root. Allow a minute or two for Pages to deploy after the push.
