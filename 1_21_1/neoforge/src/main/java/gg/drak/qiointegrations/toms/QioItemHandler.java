package gg.drak.qiointegrations.toms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.item.ItemStack;

import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;

import net.neoforged.neoforge.items.IItemHandler;

import com.tom.storagemod.inventory.IChangeTrackerAccess;
import com.tom.storagemod.inventory.IInventoryAccess;
import com.tom.storagemod.inventory.InventoryChangeTracker;

/**
 * Mass-storage {@link IItemHandler} for Tom's Simple Storage.
 * <p>
 * Tom's uses {@link ItemHandlerHelper#insertItemStacked}, which only inserts into empty slots or
 * slots with a matching stack. Slot 0 is always empty so inserts succeed immediately without
 * waiting for Tom's {@code MultiItemHandler} to refresh its cached slot count.
 * <p>
 * Item lists are read live from the QIO frequency. A dedicated {@link InventoryChangeTracker} is exposed
 * through {@link IInventoryAccess.IChangeTrackerAccess} so Tom's does not reuse a stale tracker instance
 * when the handler is recreated.
 */
public class QioItemHandler implements IItemHandler, IChangeTrackerAccess {
    private static final int INSERT_SLOT = 0;
    private static final Map<IQIOFrequency, QioItemHandler> HANDLERS = new WeakHashMap<>();

    private final IQIOFrequency frequency;
    private final InventoryChangeTracker changeTracker;

    public static QioItemHandler forFrequency(IQIOFrequency frequency) {
        synchronized (HANDLERS) {
            return HANDLERS.computeIfAbsent(frequency, QioItemHandler::new);
        }
    }

    private QioItemHandler(IQIOFrequency frequency) {
        this.frequency = frequency;
        this.changeTracker = new InventoryChangeTracker(this);
    }

    IQIOFrequency getFrequency() {
        return frequency;
    }

    @Override
    public IInventoryAccess.IInventoryChangeTracker tracker() {
        return changeTracker;
    }

    private List<ItemStack> readStoredTypes() {
        List<ItemStack> stored = new ArrayList<>();
        frequency.forAllHashedStored((type, count) -> {
            var stack = type.getInternalStack().copy();
            stack.setCount(Math.toIntExact(Math.min(count, Integer.MAX_VALUE)));
            stored.add(stack);
        });
        return stored;
    }

    /**
     * One dedicated empty insert slot plus one slot per stored item type (for terminal display).
     */
    @Override
    public int getSlots() {
        return readStoredTypes().size() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == INSERT_SLOT) {
            return ItemStack.EMPTY;
        }

        var stored = readStoredTypes();
        if (slot < 0 || slot > stored.size()) {
            return ItemStack.EMPTY;
        }
        return stored.get(slot - 1);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot < 0 || slot >= getSlots()) {
            return stack;
        }

        long inserted = frequency.massInsert(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.EXECUTE);
        int remainder = stack.getCount() - (int) inserted;
        if (remainder <= 0) {
            return ItemStack.EMPTY;
        }
        var left = stack.copy();
        left.setCount(remainder);
        return left;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot <= INSERT_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }

        var stored = readStoredTypes();
        if (slot > stored.size()) {
            return ItemStack.EMPTY;
        }

        var prototype = stored.get(slot - 1);
        long extracted = frequency.massExtract(prototype, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }

        var result = prototype.copy();
        result.setCount((int) extracted);
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty() && slot >= 0 && slot < getSlots();
    }
}
