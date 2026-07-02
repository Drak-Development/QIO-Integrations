## What's Changed

* Fix Refined Storage external storage crashing when connected to blocks other than a QIO dashboard — `QioExternalStorageProviderFactory` always returns `QioEmptyExternalStorageProvider` instead of null (RS `CompositeExternalStorageProvider` does not filter null factory results)
* Add `QioFrequencySnapshot` — shared per-frequency inventory cache reused across Tom's, AE2, and RS for the rest of the game tick, with invalidation on bridge mutations
* `QioStorageAdapter` (AE2), `QioExternalStorageProvider` (RS), and `QioItemHandler` (Tom's) read stored types through the snapshot and call `QioFrequencySnapshot.markDirty` after successful insert/extract
* `InventoryChangeTrackerMixin` only forces a Tom's rescan when the QIO snapshot is stale for that tick, avoiding full connector rescans on every `getChangeTracker` call
