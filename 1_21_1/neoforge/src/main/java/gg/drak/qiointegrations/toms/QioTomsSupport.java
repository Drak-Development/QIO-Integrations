package gg.drak.qiointegrations.toms;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import gg.drak.qiointegrations.IntegrationMods;
import gg.drak.qiointegrations.qio.QioFrequencyAccess;

/**
 * Tom's Simple Storage reads {@link net.neoforged.neoforge.items.IItemHandler} from neighbors.
 * QIO mass storage is registered on the dashboard block entity type with high priority so it wins over Mekanism's handler.
 */
public final class QioTomsSupport {
    private static final ResourceLocation QIO_DASHBOARD = ResourceLocation.fromNamespaceAndPath("mekanism", "qio_dashboard");

    private QioTomsSupport() {
    }

    public static void initialize(IEventBus modBus) {
        modBus.addListener(EventPriority.HIGH, QioTomsSupport::registerItemHandler);
    }

    private static void registerItemHandler(RegisterCapabilitiesEvent event) {
        var dashboardType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(QIO_DASHBOARD);
        if (dashboardType == null) {
            return;
        }

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                dashboardType,
                (blockEntity, side) -> {
                    if (!IntegrationMods.isQioDashboard(blockEntity) || side == null) {
                        return null;
                    }

                    var frequency = QioFrequencyAccess.resolve(blockEntity, side, null);
                    return frequency == null ? null : QioItemHandler.forFrequency(frequency);
                });
    }
}
