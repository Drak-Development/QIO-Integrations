package gg.drak.qiointegrations.refinedstorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

public class QioExternalStorageProvider implements ExternalStorageProvider {
    private final IQIOFrequency frequency;

    public QioExternalStorageProvider(IQIOFrequency frequency) {
        this.frequency = frequency;
    }

    @Override
    public long insert(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        if (!(resource instanceof ItemResource itemResource) || amount <= 0) {
            return 0;
        }

        var stack = itemResource.toItemStack(amount);
        return frequency.massInsert(stack, amount, toMekAction(action));
    }

    @Override
    public long extract(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        if (!(resource instanceof ItemResource itemResource) || amount <= 0) {
            return 0;
        }

        var stack = itemResource.toItemStack(1);
        return frequency.massExtract(stack, amount, toMekAction(action));
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        List<ResourceAmount> amounts = new ArrayList<>();
        frequency.forAllHashedStored((type, count) -> {
            if (count <= 0) {
                return;
            }
            var stack = type.getInternalStack();
            if (stack.isEmpty()) {
                return;
            }
            amounts.add(new ResourceAmount(ItemResource.ofItemStack(stack), count));
        });
        return amounts.iterator();
    }

    private static Action toMekAction(com.refinedmods.refinedstorage.api.core.Action action) {
        return action == com.refinedmods.refinedstorage.api.core.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE;
    }
}
