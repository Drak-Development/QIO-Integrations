package gg.drak.qiointegrations;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import gg.drak.qiointegrations.ae2.QioAe2Support;
import gg.drak.qiointegrations.refinedstorage.QioRsSupport;
import gg.drak.qiointegrations.toms.QioTomsSupport;

@Mod(QIOIntegrationsMod.ID)
public class QIOIntegrationsForge {
    @SuppressWarnings("removal")
    public QIOIntegrationsForge() {
        QioPlatform.setHooks(new QioPlatform.Hooks() {
            @Override
            public boolean isQioDashboard(net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
                return IntegrationMods.isQioDashboard(blockEntity);
            }

            @Override
            public boolean isModLoaded(String modId) {
                return ModList.get().isLoaded(modId);
            }
        });

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (IntegrationMods.shouldRegisterAe2Qio()) {
            QioAe2Support.initialize();
        }

        if (ModList.get().isLoaded("refinedstorage")) {
            modBus.addListener(QioRsSupport::onCommonSetup);
        }

        if (ModList.get().isLoaded("toms_storage")) {
            QioTomsSupport.initialize();
        }
    }
}
