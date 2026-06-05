## What's Changed

* First fully wired **1.20.1 Forge** build — QIO dashboards bridge to AE2, Refined Storage, and Tom's when those mods are installed
* **Applied Energistics 2** — `QioStorageAdapter` exposes QIO frequency storage as AE2 `MEStorage`; registered in `QioAe2Support`
* **Refined Storage** — `QioExternalStorageProvider` exposes the dashboard as RS external storage; wired in `QioRsSupport`
* **Tom's Simple Storage** — `QioItemHandler` and `QioTomsSupport` connect inventory connectors on the dashboard back face
* `QIOIntegrationsForge` installs `QioPlatform` hooks and conditionally boots each integration through `IntegrationMods`
* `QioFrequencyAccess` shared helpers for frequency read/write across all three bridges
* `IntegrationMods` gates AE2 registration when Applied Mekanistics is absent and detects optional mods at runtime
* `CapabilityTileEntityMixin` exposes QIO inventory capabilities to adjacent automation
* `MultiItemHandlerMixin` aligns Tom's multi-slot handler behavior with QIO item routing
* Shared `1_20_1/common` — `QioStorageAdapter`, `QioExternalStorage`, and `QIOText` compiled into this artifact
