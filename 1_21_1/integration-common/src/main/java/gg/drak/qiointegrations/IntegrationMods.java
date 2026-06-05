package gg.drak.qiointegrations;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import mekanism.api.inventory.qio.IQIOComponent;

public final class IntegrationMods {
    private static final ResourceLocation QIO_DASHBOARD = ResourceLocation.fromNamespaceAndPath("mekanism", "qio_dashboard");

    private IntegrationMods() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(QIOIntegrationsMod.ID, path);
    }

    public static boolean shouldRegisterAe2Qio() {
        return QioPlatform.isModLoaded("ae2") && !QioPlatform.isModLoaded("appmek");
    }

    public static boolean isQioDashboard(BlockEntity blockEntity) {
        return blockEntity instanceof IQIOComponent
                && QIO_DASHBOARD.equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()));
    }
}
