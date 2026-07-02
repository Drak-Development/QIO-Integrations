package gg.drak.qiointegrations.toms;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;

import gg.drak.qiointegrations.qio.QioFrequencySnapshot;
import mekanism.api.Action;
import mekanism.api.inventory.qio.IQIOFrequency;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Mass-storage {@link IItemHandler} for Tom's Simple Storage.
 * <p>
 * Tom's uses {@link ItemHandlerHelper#insertItemStacked}, which only inserts into empty slots or
 * slots with a matching stack. Slot 0 is always empty so inserts succeed immediately without
 * waiting for Tom's {@code MultiItemHandler} to refresh its cached slot count.
 * <p>
 * Item lists are read from a shared per-frequency snapshot so Tom's connector scans do not walk the
 * entire QIO inventory once per slot.
 */
public class QioItemHandler implements IItemHandler {
    private static final int INSERT_SLOT = 0;

    private final IQIOFrequency frequency;

    public QioItemHandler(IQIOFrequency frequency) {
        this.frequency = frequency;
    }

    IQIOFrequency getFrequency() {
        return frequency;
    }

    private List<ItemStack> readStoredTypes() {
        return QioFrequencySnapshot.getStoredTypes(frequency);
    }

    /**
     * One dedicated empty insert slot plus one slot per stored item type (for terminal display).
     */
    @Override
    public int getSlots() {
        return readStoredTypes().size() + 1;
    }

    @Override
    @Nonnull
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
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot < 0 || slot >= getSlots()) {
            return stack;
        }

        long inserted = frequency.massInsert(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.EXECUTE);
        if (!simulate && inserted > 0) {
            QioFrequencySnapshot.markDirty(frequency);
        }
        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - (int) inserted);
    }

    @Override
    @Nonnull
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
        if (!simulate && extracted > 0) {
            QioFrequencySnapshot.markDirty(frequency);
        }
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
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return !stack.isEmpty() && slot >= 0 && slot < getSlots();
    }
}
