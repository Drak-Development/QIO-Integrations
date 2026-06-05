package gg.drak.qiointegrations.toms;

/**
 * Tom's Simple Storage reads {@link net.minecraftforge.items.IItemHandler} from neighbors.
 * QIO mass storage is exposed via {@link gg.drak.qiointegrations.mixin.CapabilityTileEntityMixin} because Mekanism resolves
 * item handlers before Forge-attached capabilities on {@code CapabilityTileEntity}.
 */
public final class QioTomsSupport {
    private QioTomsSupport() {
    }

    public static void initialize() {
        // Integration is applied through mixins; this hook keeps Tom's init alongside other integrations.
    }
}
