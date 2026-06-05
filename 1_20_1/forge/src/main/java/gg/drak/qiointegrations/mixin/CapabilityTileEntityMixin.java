package gg.drak.qiointegrations.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import gg.drak.qiointegrations.QioPlatform;
import gg.drak.qiointegrations.qio.QioFrequencyAccess;
import gg.drak.qiointegrations.toms.QioItemHandler;
import mekanism.api.inventory.qio.IQIOFrequency;

/**
 * Mekanism resolves {@link ForgeCapabilities#ITEM_HANDLER} before Forge-attached capabilities,
 * which blocks Tom's Storage from seeing QIO mass storage on dashboards.
 */
@Mixin(targets = "mekanism.common.tile.base.CapabilityTileEntity", remap = false)
public class CapabilityTileEntityMixin {
    @Unique
    @Nullable
    private QioItemHandler qiointegrations$cachedHandler;

    @Unique
    @Nullable
    private IQIOFrequency qiointegrations$cachedFrequency;

    @Unique
    @Nullable
    private LazyOptional<IItemHandler> qiointegrations$cachedOptional;

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, remap = false)
    private <T> void qiointegrations$qioItemHandler(Capability<T> capability, @Nullable Direction side,
            CallbackInfoReturnable<LazyOptional<T>> cir) {
        if (capability != ForgeCapabilities.ITEM_HANDLER || side == null) {
            return;
        }

        BlockEntity self = (BlockEntity) (Object) this;
        if (!QioPlatform.isQioDashboard(self)) {
            return;
        }

        var frequency = QioFrequencyAccess.resolve(self, side, null);
        if (frequency == null) {
            return;
        }

        if (qiointegrations$cachedHandler == null || qiointegrations$cachedFrequency != frequency) {
            qiointegrations$cachedFrequency = frequency;
            qiointegrations$cachedHandler = new QioItemHandler(frequency);
            qiointegrations$cachedOptional = LazyOptional.of(() -> qiointegrations$cachedHandler);
        }

        cir.setReturnValue(qiointegrations$cachedOptional.cast());
    }
}
