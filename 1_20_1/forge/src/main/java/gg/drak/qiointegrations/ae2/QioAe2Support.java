/*
 * This file is adapted from Applied Mekanistics (LGPL-3.0).
 * https://github.com/AppliedEnergistics/Applied-Mekanistics
 * You may modify and redistribute this file under the terms of LGPL-3.0.
 */
package gg.drak.qiointegrations.ae2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import gg.drak.qiointegrations.IntegrationMods;
import mekanism.api.inventory.qio.IQIOComponent;

import appeng.api.networking.GridHelper;
import appeng.api.storage.MEStorage;

/** AE2 storage bus integration for Mekanism QIO dashboards. */
public final class QioAe2Support {
    public static final Capability<MEStorage> STORAGE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private QioAe2Support() {
    }

    public static void initialize() {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, QioAe2Support::onBlockEntityCapability);
    }

    public static void onBlockEntityCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        var object = event.getObject();

        if (!IntegrationMods.isQioDashboard(object)) {
            return;
        }

        event.addCapability(IntegrationMods.id("qio_storage_monitorable"), new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                if (capability == STORAGE && arg != null) {
                    var host = GridHelper.getNodeHost(object.getLevel(), object.getBlockPos().relative(arg));

                    if (host != null) {
                        var source = host.getGridNode(arg.getOpposite());
                        if (source == null) {
                            source = host.getGridNode(null);
                        }

                        if (source != null) {
                            var adapter = new QioStorageAdapter<>((BlockEntity & IQIOComponent) object, arg,
                                    source.getOwningPlayerProfileId());

                            if (adapter.getFrequency() != null) {
                                return LazyOptional.of(() -> adapter).cast();
                            }
                        }
                    }
                }

                return LazyOptional.empty();
            }
        });
    }
}
