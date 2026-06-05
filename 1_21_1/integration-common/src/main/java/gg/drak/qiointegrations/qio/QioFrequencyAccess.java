package gg.drak.qiointegrations.qio;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import gg.drak.qiointegrations.QioPlatform;
import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.api.inventory.qio.IQIOFrequency;
import mekanism.api.security.IBlockSecurityUtils;
import mekanism.api.security.SecurityMode;

public final class QioFrequencyAccess {
    private QioFrequencyAccess() {
    }

    @Nullable
    public static IQIOFrequency resolve(BlockEntity blockEntity, @Nullable Direction queriedSide, @Nullable UUID owner) {
        if (!QioPlatform.isQioDashboard(blockEntity) || !(blockEntity instanceof IQIOComponent qio)) {
            return null;
        }

        if (queriedSide == null
                || blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getOpposite() != queriedSide) {
            return null;
        }

        var freq = qio.getQIOFrequency();
        if (freq == null || !freq.isValid()) {
            return null;
        }

        var level = blockEntity.getLevel();
        var blockUtils = IBlockSecurityUtils.INSTANCE;
        var securityMode = blockUtils.getSecurityMode(level, blockEntity.getBlockPos(), blockEntity);
        if (securityMode != SecurityMode.PUBLIC) {
            if (!blockUtils.canAccess(owner, level, blockEntity.getBlockPos(), blockEntity)) {
                return null;
            }
        }

        return freq;
    }
}
