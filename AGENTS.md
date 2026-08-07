# AGENTS.md

Minecraft Forge mod (1.20.1, Forge 47.4.10) in xkmc's "L2" mod family. Core runtime deps are `l2library`/`l2serial`/`l2damagetracker`; sibling sources live in `../` (e.g. `L2Library/`). This repo is the 1.20.1 line of ModularGolems — other branches (`1.19`, `1.21`, `26.1`) are separate codebases.

## Build / run

- Java 17, Gradle 8.8 (wrapper). `org.gradle.daemon=false`, so every Gradle invocation is slow (~minutes).
- `./gradlew build` — compiles + reobf + jars into `build/libs/`.
- `./gradlew runData` — runs datagen, writes to `src/generated/resources/` (which is committed). Re-run and commit after touching anything generated: recipes, lang, advancements, tags, material configs.
- `./gradlew runClient` / `runServer` — launch in the `run/` dev dir (gitignored; has a persistent dev world).
- Mappings are `official` (NOT parchment). Java code uses Mojang names.
- No tests, no CI, no lint. "Verification" = successful `build` + `runData` diff looks sane + client launches.

## Architecture

- Entry: `dev.xkmc.modulargolems.init.ModularGolems`. All registration goes through `L2Registrate` (`REGISTRATE`) — see `init/registrate/`. Never hand-register.
- Modifiers: extend `GolemModifier` (`content/modifier/base/`), registered in `GolemModifiers`. Per-part filter via `StatFilterType`; `AttributeGolemModifier` subclasses are attribute-level, not per-material.
- Stats: `GolemStatType` enums (`content/core/`).
- Materials are **data-driven** through l2library configs (`GolemMaterialConfig`, `GolemPartConfig` in `content/config/`). JSON lives under `data/<namespace>/modulargolems_config/materials|parts/`. Materials are declared in Java (`.addMaterial(id, ingredient).addStat(...).addModifier(...).end()`) inside `MGConfigGen` or a compat dispatch's config gen. `Builder.addModifier` asserts the modifier is NOT an `AttributeGolemModifier`.
- Compat system: `compat/materials/common/` defines `ModDispatch` (server) + `ClientModDispatch` (client). Each supported external mod = one package under `compat/materials/<mod>/` + one `*Dispatch` class registered in `CompatManager.register()` (guarded by `ModList.get().isLoaded`). Compat data (materials, lang, recipes) is generated under the **compat mod's own namespace**, not `modulargolems`.
- Entity layouts: `content/entity/` — `metalgolem`, `dog`, `humanoid`, `hostile` + `common/` (`AbstractGolemEntity`). Entity behavior is driven by modifier `on*` hooks, not hardcoded goals.

## Code style

- Annotate methods and fields as `@Nullable` when they are nullable.
- Try not to use anonymous classes. Use records instead.

## Guidelines
- Minecraft source code is at ./build/fg_cache/net/minecraftforge/forge/1.20.1-47.4.10_mapped_official_1.20.1/forge-1.20.1-47.4.10_mapped_official_1.20.1-sources.jar
- Read doc/*.md if relevant.
- **Before analyzing or executing any task**, first write a short description of the request to `doc/todo.md` (append to the list if it is non-empty). Remove that entry once the task is fully completed. This is a hard requirement: do not skip it, do not do it later, and do not remove the entry before the work is actually done.
- Do not try to access files you don't have permission over unless otherwise agreed.
- Do not try to analyze texture unless explicitly stated.

## Gotchas

- `src/generated/resources` is a real source set dir (see `build.gradle`) — do not delete it; regenerated output is part of the diff.
- `libs/` holds local jars referenced as `zip.local.*`; `run/mods/` is empty by design (mods come from the dev classpath).
- `rootMod=false`, `lljij=true`: this mod bundles l2 deps via jarJar; when adding/upgrading l2 deps keep the `jarJar.ranged` versions consistent with the other L2 mods' expectations.
- Material modifiers reference modifier registry names like `modulargolems:thunder_immune`; a material is only valid if it has entries in all three maps (stats, modifiers, ingredients).
