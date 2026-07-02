package gg.drak.qiointegrations.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gg.drak.qiointegrations.qio.QioFrequencySnapshot;

/**
 * Tom's caches combined {@code invSize} and only recomputes it on connector scans (~20 ticks).
 * QIO slot counts change when RS/AE2 insert items, so refresh when a shared QIO snapshot is stale.
 * Refreshing on every {@code getSlots()} query caused full connector rescans and server stalls.
 */
@Mixin(targets = "com.tom.storagemod.util.MultiItemHandler", remap = false)
public class MultiItemHandlerMixin {
    @Inject(method = "getSlots", at = @At("HEAD"), remap = false)
    private void qiointegrations$refreshBeforeGetSlots(CallbackInfoReturnable<Integer> cir) {
        if (QioFrequencySnapshot.anyMutationDirty()) {
            ((com.tom.storagemod.util.MultiItemHandler) (Object) this).refresh();
        }
    }
}
