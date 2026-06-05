## What's Changed

* Initial Fabric **1.21.1** scaffold — `QIOIntegrationsFabric` server entrypoint only
* `DedicatedServerModInitializer` delegates to `QioIntegrationsStub.markUnimplemented("fabric", "1.21.1")`
* `fabric.mod.json` declares the Fabric Loader entrypoint; `QioIntegrationsCore` in `1_21_1/common` is a placeholder
* No Fabric-side QIO bridge, network hooks, or AE2 / Refined Storage / Tom's integration in this release
