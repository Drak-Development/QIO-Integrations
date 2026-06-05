package gg.drak.qiointegrations.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.Level;

import gg.drak.qiointegrations.toms.QioItemHandler;

/**
 * Tom's {@link com.tom.storagemod.inventory.InventoryChangeTracker} only rescans item handlers once per game tick.
 * QIO mass storage can change through RS, AE2, or the dashboard without going through the handler, so force a rescan
 * whenever Tom's queries a QIO-backed handler.
 */
@Mixin(value = com.tom.storagemod.inventory.InventoryChangeTracker.class, remap = false)
public class InventoryChangeTrackerMixin {
    @Shadow(remap = false)
    private long lastUpdate;

    @Shadow(remap = false)
    private java.lang.ref.WeakReference<net.neoforged.neoforge.items.IItemHandler> itemHandler;

    @Inject(method = "getChangeTracker", at = @At("HEAD"), remap = false)
    private void qiointegrations$forceQioRescan(Level level, CallbackInfoReturnable<Long> cir) {
        var handler = itemHandler.get();
        if (handler instanceof QioItemHandler) {
            lastUpdate = -1L;
        }
    }
}
