package gg.drak.qiointegrations.neoforge;

import gg.drak.qiointegrations.IntegrationMods;
import gg.drak.qiointegrations.QIOIntegrationsMod;
import gg.drak.qiointegrations.QioPlatform;
import gg.drak.qiointegrations.ae2.QioAe2Support;
import gg.drak.qiointegrations.refinedstorage.QioExternalStorageProviderFactory;
import gg.drak.qiointegrations.toms.QioTomsSupport;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;

@Mod(QIOIntegrationsMod.ID)
public class QIOIntegrationsNeoForge {
    public QIOIntegrationsNeoForge(IEventBus modBus) {
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

        if (IntegrationMods.shouldRegisterAe2Qio()) {
            QioAe2Support.initialize(modBus);
        }

        if (ModList.get().isLoaded("refinedstorage")) {
            RefinedStorageApi.INSTANCE.addExternalStorageProviderFactory(new QioExternalStorageProviderFactory());
        }

        if (ModList.get().isLoaded("toms_storage")) {
            QioTomsSupport.initialize(modBus);
        }
    }
}
