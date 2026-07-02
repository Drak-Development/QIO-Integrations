## What's Changed

* Add `QioFrequencySnapshot` — shared per-frequency inventory cache reused across Tom's, AE2, and RS for the rest of the game tick, with invalidation on bridge mutations
* `QioStorageAdapter` (AE2), `QioExternalStorage` (RS), and `QioItemHandler` (Tom's) read stored types through the snapshot and call `QioFrequencySnapshot.markDirty` after successful insert/extract
* `MultiItemHandlerMixin` only refreshes Tom's combined slot count when `QioFrequencySnapshot.anyMutationDirty()`, instead of on every `getSlots()` query that caused server stalls on large frequencies
