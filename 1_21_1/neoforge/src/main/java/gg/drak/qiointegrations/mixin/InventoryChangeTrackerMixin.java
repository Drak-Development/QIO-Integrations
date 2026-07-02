package gg.drak.qiointegrations.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.Level;

import gg.drak.qiointegrations.qio.QioFrequencySnapshot;
import gg.drak.qiointegrations.toms.QioItemHandler;

/**
 * Tom's {@link com.tom.storagemod.inventory.InventoryChangeTracker} only rescans item handlers once per game tick.
 * QIO mass storage can change through RS, AE2, or the dashboard without going through the handler, so force a rescan
 * when the shared snapshot is stale for this tick. Avoid forcing a rescan on every query in the same tick, which
 * previously caused quadratic scans over large QIO frequencies.
 */
@Mixin(value = com.tom.storagemod.inventory.InventoryChangeTracker.class, remap = false)
public class InventoryChangeTrackerMixin {
    @Shadow(remap = false)
    private long lastUpdate;

    @Shadow(remap = false)
    private java.lang.ref.WeakReference<net.neoforged.neoforge.items.IItemHandler> itemHandler;

    @Inject(method = "getChangeTracker", at = @At("HEAD"), remap = false)
    private void qiointegrations$forceQioRescan(Level level, CallbackInfoReturnable<Long> cir) {
        QioFrequencySnapshot.beginTick(level.getGameTime());
        var handler = itemHandler.get();
        if (handler instanceof QioItemHandler qioHandler
                && QioFrequencySnapshot.needsRescan(qioHandler.getFrequency(), level.getGameTime())) {
            lastUpdate = -1L;
        }
    }
}
