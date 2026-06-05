## What's Changed

* Initial NeoForge **1.21.11** scaffold — ModDevGradle entrypoint only
* `QIOIntegrationsNeoForge` loads `QioIntegrationsCore` and flags the loader via `QioIntegrationsStub.markUnimplemented("neoforge", "1.21.11")`
* `neoforge.mods.toml` targets NeoForge 21.11.x; `QioIntegrationsCore` in `1_21_11/common` is a placeholder
* No storage adapters, external-storage providers, or runtime registration against optional integration mods yet
