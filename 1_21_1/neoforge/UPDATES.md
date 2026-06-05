## What's Changed

* First NeoForge **1.21.1** build — Mekanism QIO dashboards bridge to optional storage mods when they are installed
* **Applied Energistics 2** — register `QioStorageAdapter` on the QIO dashboard via `AECapabilities.ME_STORAGE` (`QioAe2Support`)
* **Refined Storage 2** — register `QioExternalStorageProvider` and `QioExternalStorageProviderFactory` through `RefinedStorageApi.INSTANCE`
* **Tom's Simple Storage** — expose `QioItemHandler` on the dashboard back face via `Capabilities.ItemHandler.BLOCK` (`QioTomsSupport`)
* NeoForge entrypoint `QIOIntegrationsNeoForge` installs `QioPlatform` hooks and boots AE2 / RS / Tom's only when those mods are present
* `QioFrequencyAccess` uses Mekanism `IBlockSecurityUtils` for frequency security checks on 1.21.1
* `IntegrationMods` skips AE2 registration when **Applied Mekanistics** (`appmek`) is loaded
* Shared `1_21_1/common` layer — `QioPlatform`, `QioStorageAdapter`, `QIOText`, and `QioFrequencyAccess`
* RS 2.x `ExternalStorageProvider` API (not RS 1.x `IExternalStorageProvider`); factory registered during mod construction so it is ready before external storage blocks initialize
* RS `incomingDirection` is passed through `ExternalStorageProviderFactory.create` without an extra `getOpposite()` call
* `QioEmptyExternalStorageProvider` avoids null factory results that crash RS `CompositeExternalStorageProvider`
* Tom's item-handler capability is registered whenever Tom's is loaded (RS uses `ExternalStorageProviderFactory` separately)
* `InventoryChangeTrackerMixin` forces Tom's to rescan QIO handlers when RS/AE2/dashboard change stored items (Tom's normally caches once per tick)
* `QioItemHandler` implements `IChangeTrackerAccess` with a stable handler per frequency so Tom's keeps the correct change tracker
