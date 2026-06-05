# Multi-version layout

Each Minecraft version lives under a folder named with underscores (`1_21_1` = 1.21.1).

```
common/                    # Shared mod id and cross-version utilities (no loader code)
1_21_x/
  common/                  # Version-specific integration logic (sources only)
  fabric/
  neoforge/
  forge/
```

## Enabling a version

1. Copy an existing version folder (for example `1_21_1`) to a new folder (for example `1_21_2`).
2. Edit `<version>/gradle.properties` with correct loader versions from Modrinth/CurseForge.
3. Add an entry to `enabled_mc_versions` in the root `gradle.properties` (e.g. `1_21_2:neoforge` or `1_21_2` for all three loaders).

## Currently enabled

Gradle only includes the platforms listed in root `gradle.properties` (`enabled_mc_versions`).

| Gradle project        | Minecraft | Loader   | Implementation status |
|-----------------------|-----------|----------|------------------------|
| `:1_20_1:forge`       | 1.20.1    | Forge 47.4.10 | AE2, Mekanism, Refined Storage, Tom's |
| `:1_21_1:neoforge`    | 1.21.1    | NeoForge 21.1.233 | AE2, Mekanism, Refined Storage, Tom's |

Other folders under the repo (`1_21_11`, stub Fabric/NeoForge modules, etc.) remain on disk but are **not** Gradle subprojects until added to `enabled_mc_versions`.

## Loader wiring

- **Forge / NeoForge**: mod entrypoint class in `<loader>/src/main/java/...`
- **Fabric**: `fabric.mod.json` server entrypoint
- Version-specific shared code lives in `<version>/common/` and is compiled into each loader module for that version
- Root `common/` holds the mod id and other code shared across all Minecraft versions

## Build outputs

```bash
./gradlew :1_20_1:forge:build
./gradlew :1_21_1:neoforge:build
./gradlew buildAll
```

Jars are written under `<version>/<loader>/build/libs/`. Running `build` on any platform module (or `./gradlew deploy` / `./gradlew buildAll` at the root) also copies release JARs into `deploy/` at the project root.

## 1.20.1 notes

- Uses **Java 17** (`java_version=17` in `1_20_1/gradle.properties`).
- **Forge** uses ModDevGradle `legacyforge` (`gradle/mc-legacy-forge.gradle`).
- **NeoForge** 1.20.1 uses compile-only loader deps for stub modules (no ModDevGradle — avoids IntelliJ sync conflicts when multiple MDG projects are present).
- Stub NeoForge and stub Forge modules for 1.21.x use ModDevGradle / ForgeGradle with **client-only** IDE run configs (`disableIdeRun()` on server for NeoForge/Forge 1.20.1).
- Only `:1_20_1:forge` carries the full AE2/Mek/RS/Tom's dependency set.
- AE2, Mekanism, Refined Storage, and Tom's Simple Storage are wired as compile/runtime deps on the Forge module only.

## Forge (MinecraftForge)

| Folder    | Minecraft | Forge     | Gradle plugin |
|-----------|-----------|-----------|---------------|
| `1_20_1`  | 1.20.1    | 47.4.10   | ModDevGradle `legacyforge` 2.0.141 |
| `1_21_1`  | 1.21.1    | 52.1.0    | ForgeGradle 7 (`[7.0.3,8)`) — stub client run |
| `1_21_11` | 1.21.11   | 61.1.5    | ForgeGradle 7 (`[7.0.3,8)`) — stub client run |

## Gradle

The wrapper uses **Gradle 9.3.1** (required by ForgeGradle 7 on 1.21.x Forge stubs). The root project applies **idea-ext 1.4.1** so ModDevGradle and ForgeGradle can register IntelliJ client run configs without Groovy 4 errors.

When the repo lives under OneDrive, Gradle build outputs are redirected to `%LOCALAPPDATA%\\QIO-Integrations-build\\` (see `gradle/onedrive-build-dir.gradle`).

Each platform module exposes a **`<loader> - client`** IntelliJ run configuration (`fabric`, `neoforge`, or `forge` matching the subproject name).
