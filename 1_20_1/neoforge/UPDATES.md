## What's Changed

* Initial NeoForge **1.20.1** scaffold — `QIOIntegrationsNeoForge` `@Mod` entrypoint only
* Entrypoint calls `QioIntegrationsStub.markUnimplemented("neoforge", "1.20.1")` and compiles shared `1_20_1/common` types without loader wiring
* No `QioAe2Support`, `QioRsSupport`, or `QioTomsSupport` registration on NeoForge for 1.20.1 yet
* No Forge mixins or capability hooks on this platform — integration logic lives in the **Forge 1.20.1** artifact
