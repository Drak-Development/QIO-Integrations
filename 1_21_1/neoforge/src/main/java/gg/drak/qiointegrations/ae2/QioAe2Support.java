/*
 * This file is adapted from Applied Mekanistics (LGPL-3.0).
 * https://github.com/AppliedEnergistics/Applied-Mekanistics
 * You may modify and redistribute this file under the terms of LGPL-3.0.
 */
package gg.drak.qiointegrations.ae2;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import gg.drak.qiointegrations.IntegrationMods;
import mekanism.api.inventory.qio.IQIOComponent;

import appeng.api.AECapabilities;
import appeng.api.networking.GridHelper;
import appeng.api.storage.MEStorage;

/** AE2 storage bus integration for Mekanism QIO dashboards. */
public final class QioAe2Support {
    private static final ResourceLocation QIO_DASHBOARD = ResourceLocation.fromNamespaceAndPath("mekanism", "qio_dashboard");

    private QioAe2Support() {
    }

    public static void initialize(IEventBus modBus) {
        modBus.addListener(EventPriority.HIGH, QioAe2Support::registerMeStorage);
    }

    private static void registerMeStorage(RegisterCapabilitiesEvent event) {
        var dashboardType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(QIO_DASHBOARD);
        if (dashboardType == null) {
            return;
        }

        event.registerBlockEntity(
                AECapabilities.ME_STORAGE,
                dashboardType,
                QioAe2Support::createMeStorage);
    }

    @Nullable
    private static MEStorage createMeStorage(BlockEntity blockEntity, @Nullable Direction side) {
        if (!IntegrationMods.isQioDashboard(blockEntity) || side == null) {
            return null;
        }

        var host = GridHelper.getNodeHost(blockEntity.getLevel(), blockEntity.getBlockPos().relative(side));
        if (host == null) {
            return null;
        }

        var source = host.getGridNode(side.getOpposite());
        if (source == null) {
            source = host.getGridNode(null);
        }

        if (source == null) {
            return null;
        }

        var adapter = new QioStorageAdapter<>((BlockEntity & IQIOComponent) blockEntity, side,
                source.getOwningPlayerProfileId());

        return adapter.getFrequency() != null ? adapter : null;
    }
}
