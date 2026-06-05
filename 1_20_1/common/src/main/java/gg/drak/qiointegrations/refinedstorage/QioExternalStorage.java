package gg.drak.qiointegrations.refinedstorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;

import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.apiimpl.API;

public class QioExternalStorage implements IExternalStorage<ItemStack> {
    private final IExternalStorageContext context;
    private final IQIOFrequency frequency;
    @Nullable
    private List<ItemStack> cache;

    public QioExternalStorage(IExternalStorageContext context, IQIOFrequency frequency) {
        this.context = context;
        this.frequency = frequency;
    }

    /**
     * Mirrors {@link com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.ItemExternalStorageCache#initCache}:
     * the network cache is already rebuilt from {@link #getStacks()} on connect, so the first {@link #update}
     * must only seed the local diff cache and not push another full copy into the grid.
     */
    private boolean initCache(List<ItemStack> current) {
        if (cache != null) {
            return false;
        }

        cache = new ArrayList<>();
        for (var stack : current) {
            if (!stack.isEmpty()) {
                cache.add(stack.copy());
            }
        }
        return true;
    }

    @Override
    public void update(INetwork network) {
        if (getAccessType() == AccessType.INSERT) {
            return;
        }

        var current = buildSnapshot();

        if (initCache(current)) {
            return;
        }

        assert cache != null;

        for (var cached : new ArrayList<>(cache)) {
            var match = findEqual(current, cached);
            if (match == null) {
                network.getItemStorageCache().remove(cached, cached.getCount(), true);
            } else if (match.getCount() != cached.getCount()) {
                int delta = match.getCount() - cached.getCount();
                if (delta > 0) {
                    var add = match.copy();
                    add.setCount(delta);
                    network.getItemStorageCache().add(add, delta, false, true);
                } else {
                    network.getItemStorageCache().remove(cached, Math.abs(delta), true);
                }
            }
        }

        for (var stack : current) {
            if (findEqual(cache, stack) == null) {
                network.getItemStorageCache().add(stack, stack.getCount(), false, true);
            }
        }

        cache.clear();
        for (var stack : current) {
            cache.add(stack.copy());
        }

        network.getItemStorageCache().flush();
    }

    private List<ItemStack> buildSnapshot() {
        List<ItemStack> stacks = new ArrayList<>();
        frequency.forAllHashedStored((type, count) -> {
            var stack = type.getInternalStack().copy();
            stack.setCount(Math.toIntExact(Math.min(count, Integer.MAX_VALUE)));
            stacks.add(stack);
        });
        return stacks;
    }

    @Nullable
    private static ItemStack findEqual(List<ItemStack> stacks, ItemStack prototype) {
        for (var stack : stacks) {
            if (API.instance().getComparer().isEqualNoQuantity(stack, prototype)) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return buildSnapshot();
    }

    @Override
    @Nonnull
    public ItemStack insert(@Nonnull ItemStack stack, int size, com.refinedmods.refinedstorage.api.util.Action action) {
        if (stack.isEmpty() || !context.acceptsItem(stack)) {
            return stack;
        }

        var toInsert = stack.copy();
        toInsert.setCount(size);
        long inserted = frequency.massInsert(toInsert, size, toMekAction(action));

        if (inserted >= size) {
            return ItemStack.EMPTY;
        }

        var remainder = stack.copy();
        remainder.setCount(size - (int) inserted);
        return remainder;
    }

    @Override
    @Nonnull
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags,
            com.refinedmods.refinedstorage.api.util.Action action) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        long extracted = frequency.massExtract(stack, size, toMekAction(action));

        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }

        var result = stack.copy();
        result.setCount((int) extracted);
        return result;
    }

    private static Action toMekAction(com.refinedmods.refinedstorage.api.util.Action action) {
        return action == com.refinedmods.refinedstorage.api.util.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE;
    }

    @Override
    public int getStored() {
        return buildSnapshot().stream().mapToInt(ItemStack::getCount).sum();
    }

    @Override
    public int getPriority() {
        return context.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return context.getAccessType();
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }
        return remainder == null ? size : (size - remainder.getCount());
    }
}
