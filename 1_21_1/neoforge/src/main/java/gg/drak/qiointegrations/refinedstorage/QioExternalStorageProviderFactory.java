package gg.drak.qiointegrations.refinedstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import gg.drak.qiointegrations.IntegrationMods;
import gg.drak.qiointegrations.qio.QioFrequencyAccess;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.api.storage.externalstorage.ExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;

public class QioExternalStorageProviderFactory implements ExternalStorageProviderFactory {
    @Override
    public ExternalStorageProvider create(ServerLevel level, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity instanceof NetworkNodeContainerProvider) {
            return null;
        }

        if (!IntegrationMods.isQioDashboard(blockEntity)) {
            return null;
        }

        // RS passes incomingDirection (side of the target block the external storage connects from).
        var frequency = QioFrequencyAccess.resolve(blockEntity, direction, null);
        return frequency == null
                ? QioEmptyExternalStorageProvider.INSTANCE
                : new QioExternalStorageProvider(frequency);
    }
}
