# Modular Golems Datapack Editor (`dev.xkmc.modulargolems.editor`)

Client-side datapack editor for the two `modulargolems_config` datapack types (materials + parts).
It writes a new datapack into the active singleplayer world's `datapacks/` folder and offers a
datapack reload. Built for Forge 1.20.1 / official (Mojang) mappings, Java 17.

The `base/` layer is **mod-independent and shared with L2Hostility's editor** (which was copied
from here and then improved). Improvements made there are ported back to keep the two copies in
sync: searchable home lists, foldable group headers, double-click-to-open/edit, per-row tooltips /
grey rows, `searchKey` registry-name matching in pickers, `EditorTab` tooltips, a configurable save
root, and double-value rounding.

---

## Package layout

```
dev.xkmc.modulargolems.editor
├── base/      # generic editor UI + helpers, NO modulargolems imports (reusable by other l2-config mods)
├── util/      # modulargolems-specific shared helpers
├── material/  # material config screens
└── part/      # part config screens
```

### base (mod-independent)

| Class | Purpose |
|---|---|
| `EditorFile` | generic config file machinery: `save(type,id,config,packFolder)`, `copy`, `parseId`, `validNamespace`, `worldDatapacks`/`currentWorldDir`, `configRoot` (honors the pluggable `saveRootOverride` supplier before falling back to the current world's datapacks folder), writes `pack.mcmeta` |
| `EditorUtil` | generic pickers/labels: `listItems`, `listTags`, `itemName`, `tagName`, `itemIngredient`, `tagIngredient`, `ingredientIcon`, `ingredientText`, `save`, `copy`, `byId` |
| `EditorSaveState` | static `savedFlag` (a save is pending a datapack reload) + `canEdit()` (singleplayer + cheats + creative) |
| `EditorText` | generic lang enum, keys under `editor.*` (neutral namespace, shared by any mod using base) |
| `EditorSession` | `{boolean dirty}` shared across a file's edit tree |
| `EditorList` | `ObjectSelectionList` wrapper; `Entry` supports icon / `data` payload / group header / `grey` rows / hover tooltip / rotating `iconSupplier`; headers may carry an `onClick` + `collapsed` marker; `setOnSelect(Runnable)` + `setOnDoubleClick(Runnable)`; `setData` keeps scroll position; `renderRowTooltip` draws the hovered row's tooltip after the list |
| `LinkButton` | button that underlines its label on hover (replaced by `TabButton` for home-screen switching) |
| `EditorTab` | record `(label, onSelect)` (plus a `(label, tooltip, onSelect)` variant) describing one switchable config kind in a home screen's tab bar |
| `TabButton` | tab-styled button (highlighted when active, continuous bottom edge) used by `EditorHomeScreen`'s tab bar |
| `EditorLayout` | static `centerRow(List<Button>, centerX, y, gap)` helper that centers a row of buttons on the screen |
| `EditorToast` | `SystemToast` wrapper |
| `PickListScreen<T>` | searchable picker (EditBox + list), Cancel button, Esc/parent navigation. The search box matches both the row label (translated display name) and the handler's `searchKey` (registry name) |
| `PromptScreen` | modal labeled EditBox with validator + Cancel/Confirm; returns to parent |
| `DoubleMapScreen<T>` | value-map editor (Add/Edit/Remove), optional per-entry percent display, double-click a row to edit; `format` rounds to 4 decimals to avoid float artifacts |
| `Obj2IntMapScreen<M>` | int-map editor with **per-object max level** (`Function<M,Integer>`), shows `Lv x/y`, validates `1..max`, double-click a row to edit |
| `ItemListScreen<T>` | set editor over candidates with icon+label; its `Handler` also provides the pick `searchKey` (registry name) |
| `EditorHandler<T>` | single interface extending `PickListScreen.Handler`, `ItemListScreen.Handler`, `DoubleMapScreen.Handler` and `Obj2IntMapScreen.Handler` (defaults for `icon`/`percent`/`maxLevel`/`onSelect`, required `searchKey`); `of(label[, searchKey])` factories + `Impl` record + `Pick` adapter. Ported from L2Hostility; currently **unused** in golems |
| `FormScreen<T>` | generic multi-field form (text boxes + bool toggles in a scrollable row list) with per-field validation + tooltips; `FormSpec`/`FormField` builders. Ported from L2Hostility; currently **unused** in golems |
| `ListEditScreen<T>` | generic editable list screen (Add/Edit/Remove over an `EditorList`, double-click to edit, optional dirty-confirm Save). Ported from L2Hostility; currently **unused** in golems |
| `ValueMapScreen<K,V>` | generic map editor (Add via remaining-key picker or typed id, Edit, Remove; sorted rows). Ported from L2Hostility; currently **unused** in golems |
| `IngredientScreen` | item/tag/clear picker for an `Ingredient`; uses `EditorUtil` + `EditorText` directly (no Source/provider arg) |
| `EditorTip` | `EditorTip.tip(button, tooltip)` helper attaching a hover `Tooltip`. Ported from L2Hostility; currently **unused** in golems |
| `TagFile` | reads/writes `data/<ns>/tags/entity_types/<path>.json` (`replace:true`) through `EditorFile.configRoot()`. Ported from L2Hostility; currently **unused** in golems |
| `ConfigEdit` | project-agnostic Forge-config editor base: `FieldDef` (BOOL/INT/DOUBLE/STRING get/set/reset/tooltip/toFormField), `Section`, abstract `commonSpec`/`clientSpec`/`configLangPrefix`/`configId`/`homeSections`, `saveConfig`, `openSectionForm`, `resetToDefault`. Ported from L2Hostility; currently **unused** in golems |
| `ExitConfirmScreen` | Save / Discard / Cancel dialog for leaving a dirty file |
| `ReloadConfirmScreen` | "Reload now / Later" dialog shown on editor exit when a save is pending |
| `EditorHomeScreen` | abstract shared home: grouped file list (by namespace, mod-name headers), a top tab bar of config kinds, New/Edit/Reload/Back bottom row; abstract hooks (`listFiles`, `fileCount`, `emptyMessage`, `newFileDefault`, `openNew`, `openEdit`, `tabs`, `activeTab`, `fileIdLabel`, `validateId`, `hasPendingReload`, `setReloaded`) plus overridable hooks: `hasSearch()` (default `false`, shows an EditBox search bar that filters rows by `namespace path label`), `canCreate()` (default `true`, drives the New button's `active`), `hasNew()` (default `true`), `hasReload()` (default `true`), `extraButtons()`, `groupName(ns)`, `fileLabel(id)`, `rowSuffix(id)`, `isDisabled(id)` (rows drawn light gray), `fileTooltip(id)` (hover tooltip). Group headers are **foldable** (click toggles collapse, `[+]/[-]` marker; collapsed groups auto-expand on a search match). Edit is disabled while no file is selected; double-clicking a row opens it; the bottom row is centered |

All `base` package classes are annotated `@MethodsReturnNonnullByDefault` + `@ParametersAreNonnullByDefault`
(`package-info.java`), matching the L2 convention.

### util (mod-specific shared)

| Class | Purpose |
|---|---|
| `GolemEditorLang` | golem-specific lang, keys under `modulargolems.editor.*` (titles, ingredient/repair/limitation, stats/modifiers, stat filters, add/select for parts/magnifiers/types, no_materials/material) |
| `GolemEditorUtil` | golem registry/data access: `listStats`, `listModifiers` (excludes `AttributeGolemModifier`), `listGolemTypes`, `listParts` (from `MGTagGen.GENERIC_PARTS`), `statName`, `statFilterName`, `validateFileId`, `newMaterial`, `save` (wraps `EditorFile.save` with `PACK_FOLDER`) |
| `EditorReloadHooks` | client Forge-bus subscriber: clears `EditorSaveState.savedFlag` on `TagsUpdatedEvent` with cause `CLIENT_PACKET_RECEIVED` (fires on manual `/reload` and world rejoin) |

### material / part

- `material`: `MaterialHomeScreen`, `MaterialFileScreen`, `MaterialEntryScreen`
- `part`: `PartHomeScreen`, `PartFileScreen`

Both home screens enable `hasSearch()` (search bar over the file list).

---

## Entry point & gating

- The **"Edit Datapacks"** button lives in `GolemInfoScreen.init()` (the golem **tracker** screen),
  not the equipment screen.
- It is only added when `EditorSaveState.canEdit()` is true: singleplayer server present AND
  `getWorldData().getAllowCommands()` AND the player is creative (`player.isCreative()`).
- `MaterialHomeScreen` is the landing screen; `PartHomeScreen` is reached via the top tab bar
  (`EditorTab` row). The tab bar is rendered generically by `EditorHomeScreen` from the subclass's
  `tabs()` + `activeTab()`; the active tab is highlighted and clicking another tab runs its
  `onSelect`. Any editor with multiple config kinds can reuse this by supplying its own tab list.

## Navigation / screen-return contract

Every editor screen stores its `parent` `Screen` and overrides `onClose()` to
`setScreen(parent)`. Back buttons and Esc always return to the previous screen instead of
closing to the world. `Minecraft.setScreen` re-inits the parent, so returning re-runs `init()`
(which refreshes button enabled-state and the list).

Top-level file screens (`MaterialFileScreen`/`PartFileScreen`) navigate to the home screen
instance they were created with.

## File editing lifecycle

- Home list groups files by namespace; group header = mod display name
  (`ModList.getModContainerById(ns).getModInfo().getDisplayName()`, namespace fallback). Every file
  row shows the path + `(count)` (file's entry count). Group headers are **foldable** (click toggles
  collapse). Both home tabs show a **search box** (`hasSearch()`): it filters rows by
  `namespace path label` and auto-expands a collapsed group when its namespace matches.
- **Edit** = deep copy of the in-memory config (`EditorUtil.copy` → `JsonCodec` round-trip),
  **New** = fresh `GolemMaterialConfig`/`GolemPartConfig` with a default id.
- **Dirty tracking**: a shared `EditorSession` is passed down the whole edit tree; every real
  mutation sets `session.dirty`. The **Save** button is disabled unless dirty.
- **Selection**: Edit/Remove buttons are disabled while no list row is selected (via
  `EditorList.setOnSelect`), instead of showing a "select a file first" toast. File screens put all
  bottom buttons (Add/Edit/Remove/Save/Back) on one centered row (`EditorLayout.centerRow`).
- **Save** prompts for a file id (prefilled current), writes via
  `GolemEditorUtil.save` → `EditorFile.save(type, id, config, PACK_FOLDER)` and stays on the file
  screen (dirty cleared). On success `EditorSaveState.savedFlag = true` (enables the home Reload
  button).
- **Exit with unsaved changes** → `ExitConfirmScreen` (Save / Discard / Cancel).

### JSON output path

```
<world>/datapacks/modulargolems_editor/data/<namespace>/modulargolems_config/<materials|parts>/<path>.json
```

(`PACK_FOLDER = "modulargolems_editor"`, `ConfigTypeEntry.asPath` formula). `pack.mcmeta`
(`pack_format: 15`) is written once. Serialization uses
`JsonCodec.toJson(config, type.cls())` + pretty GSON.

**Configurable save root**: every save goes through `EditorFile.configRoot()`. The base layer keeps
a `saveRootOverride` supplier (null by default); `GolemEditorUtil`'s static init wires it to the
client config `MGConfig.CLIENT.editorSavePath` (`modulargolems-client.toml`, default `""`). When set
to an absolute path of a datapacks folder, saves go there instead of the current world's
`datapacks/` folder — e.g. OpenLoader's datapacks folder or a modpack's global data folder. The
editor pack folder name (`modulargolems_editor`) is still resolved underneath the chosen root;
defaulting to the world path keeps existing saves working unchanged.

## Reload handling

- `EditorSaveState.savedFlag` is set on every successful save and cleared only when:
  - an actual reload happens (Reload button / exit "Reload now"), or
  - the client receives a tag update (`TagsUpdatedEvent`/`CLIENT_PACKET_RECEIVED`) — i.e. manual
    `/reload` or rejoining a world. It persists across all in-editor navigation and home switches.
- Home screens show a **Reload** button (enabled while `savedFlag`). Clicking it reloads the
  datapacks immediately; exiting the editor with `savedFlag` still set shows `ReloadConfirmScreen`
  ("Reload now" / "Later"). "Later" keeps the flag.
- Reload triggers `server.execute(() -> server.reloadResources(server.getPackRepository().getSelectedIds()))`.

## Lang system

- Two enums, both registered in `ModularGolems.gatherData` via
  `REGISTRATE.addDataGenerator(ProviderType.LANG, ...)`:
  - `EditorText::genLang` → generic `editor.*` keys.
  - `GolemEditorLang::genLang` → `modulargolems.editor.*` keys.
- Rule of thumb: anything not golem-specific lives in `base/EditorText`
  (buttons, prompts, file/save/namespace/validation strings, level strings, select-item/tag);
  content-category names (materials/parts, stats/modifiers, stat filters, golem types) stay in
  `GolemEditorLang`.
- Chinese translations: edit `src/test/resources/modulargolems/lang/zh_cn/editor.json` (nested
  `editor.*` and `modulargolems.editor.*` sections) and run the lang merger (see below).

## Build / verification

- `./gradlew build` — compiles + reobf + jars. This is the "verification".
- `./gradlew runData` — regenerates `src/generated/resources` lang (`en_us.json`, `en_ud.json`).
  **Re-run and commit after changing any lang enum.**
- Lang merger (for `zh_cn.json`, which is hand-translated via test-resource files):
  compiled once into `/var/folders/.../T/opencode/organizer_out` with Java 17 + gson/guava/
  datafixerupper from the gradle cache, then run from the project root:
  ```
  "$JAVA17/bin/java" -cp "$OUT:$GSON:$GUAVA:$DFU" organize.ResourceOrganizer
  ```
  Do not rely on the Gradle init-script approach (breaks on the default JDK 25).
- No tests / CI / lint.

## Gotchas

- `src/generated/resources` is a real source set — never delete it.
- New packs written to a world's `datapacks/` are **not auto-enabled**; apply via `/reload`,
  the editor's Reload button, or the world's Datapack Selection screen.
- Modifier levels: use the per-modifier `GolemModifier.maxLevel` field, **not** the static
  `GolemModifier.MAX_LEVEL` (each modifier may have a unique max).
- When renaming a class with a shared substring (e.g. `EditorLang` → `GolemEditorLang`), prefer
  targeted edits over a global `sed`, or it can double-rename already-prefixed identifiers.
- `material` depends on `part` and `part` depends on `material` (home screens switch to each other);
  `util` depends on `base`; `base` must never import `dev.xkmc.modulargolems.*` (non-editor).

## Known / pending items

- **Handler refactor (requested, NOT done)**: parameterized screens
  (`PickListScreen`, `ItemListScreen`, `DoubleMapScreen`, `Obj2IntMapScreen`) still take several
  functional-interface constructor args; the user asked to consolidate these into a single
  "handler" object that extracts info (label/icon/percent/maxLevel/pickTitle) from the parameter.
- **Ingredient serialization**: a user report that ingredients originally defined as tags can be
  re-saved in the expanded `{"items": [...]}` form. Unverified — check how l2serial `JsonCodec`
  round-trips `Ingredient` (it may expand `TagValue` into concrete stacks).
- **Vanilla attribute names**: raised that "vanilla attributes have resource locations mapped
  wrongly; not all attributes have modulargolems namespace". Current `GolemEditorUtil.statName`
  uses `stat.getAttribute().getDescriptionId()`, which is already namespace-agnostic and correct
  for both vanilla (`attribute.name.generic.*`) and custom attributes — verify against an actual
  in-game render before changing.
