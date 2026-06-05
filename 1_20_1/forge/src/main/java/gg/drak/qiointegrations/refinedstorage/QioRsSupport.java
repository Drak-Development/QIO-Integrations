package gg.drak.qiointegrations.refinedstorage;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.apiimpl.API;

public final class QioRsSupport {
    private QioRsSupport() {
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> API.instance().addExternalStorageProvider(StorageType.ITEM, new QioExternalStorageProvider()));
    }
}
