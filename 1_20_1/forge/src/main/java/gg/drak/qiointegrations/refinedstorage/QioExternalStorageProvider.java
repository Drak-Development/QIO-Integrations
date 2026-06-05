package gg.drak.qiointegrations.refinedstorage;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import gg.drak.qiointegrations.QioPlatform;
import gg.drak.qiointegrations.qio.QioFrequencyAccess;

import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.util.NetworkUtils;

public class QioExternalStorageProvider implements IExternalStorageProvider<ItemStack> {
    @Override
    public boolean canProvide(BlockEntity blockEntity, Direction direction) {
        var node = NetworkUtils.getNodeFromBlockEntity(blockEntity);
        if (node instanceof IStorageProvider) {
            return false;
        }

        if (!QioPlatform.isQioDashboard(blockEntity)) {
            return false;
        }

        // Only handle QIO mass storage here; leave slot-based handlers (e.g. Mek crafting grids) to other providers.
        if (QioFrequencyAccess.resolve(blockEntity, direction.getOpposite(), null) == null) {
            return false;
        }

        // If a vanilla item handler exists but we still resolved QIO, we take priority (see getPriority).
        return true;
    }

    @Nonnull
    @Override
    public IExternalStorage<ItemStack> provide(IExternalStorageContext context, BlockEntity blockEntity,
            Direction direction) {
        var frequency = QioFrequencyAccess.resolve(blockEntity, direction.getOpposite(), null);
        if (frequency == null) {
            throw new IllegalStateException("QIO frequency unavailable for external storage");
        }
        return new QioExternalStorage(context, frequency);
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
