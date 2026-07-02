package gg.drak.qiointegrations.refinedstorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;

import gg.drak.qiointegrations.qio.QioFrequencySnapshot;
import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

public class QioExternalStorageProvider implements ExternalStorageProvider {
    private final IQIOFrequency frequency;
    @Nullable
    private final ServerLevel level;

    public QioExternalStorageProvider(IQIOFrequency frequency, @Nullable ServerLevel level) {
        this.frequency = frequency;
        this.level = level;
    }

    @Override
    public long insert(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        if (!(resource instanceof ItemResource itemResource) || amount <= 0) {
            return 0;
        }

        var stack = itemResource.toItemStack(amount);
        long inserted = frequency.massInsert(stack, amount, toMekAction(action));
        if (action == com.refinedmods.refinedstorage.api.core.Action.EXECUTE && inserted > 0) {
            QioFrequencySnapshot.markDirty(frequency);
        }
        return inserted;
    }

    @Override
    public long extract(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        if (!(resource instanceof ItemResource itemResource) || amount <= 0) {
            return 0;
        }

        var stack = itemResource.toItemStack(1);
        long extracted = frequency.massExtract(stack, amount, toMekAction(action));
        if (action == com.refinedmods.refinedstorage.api.core.Action.EXECUTE && extracted > 0) {
            QioFrequencySnapshot.markDirty(frequency);
        }
        return extracted;
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        Long gameTime = level == null ? null : level.getGameTime();
        if (gameTime != null) {
            QioFrequencySnapshot.beginTick(gameTime);
        }
        List<ResourceAmount> amounts = new ArrayList<>();
        QioFrequencySnapshot.forAllStored(frequency, gameTime, (type, count) -> {
            var stack = type.getInternalStack();
            amounts.add(new ResourceAmount(ItemResource.ofItemStack(stack), count));
        });
        return amounts.iterator();
    }

    private static Action toMekAction(com.refinedmods.refinedstorage.api.core.Action action) {
        return action == com.refinedmods.refinedstorage.api.core.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE;
    }
}
