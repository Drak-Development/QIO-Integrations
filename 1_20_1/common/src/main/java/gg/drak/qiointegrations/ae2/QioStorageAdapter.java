/*
 * This file is adapted from Applied Mekanistics (LGPL-3.0).
 * https://github.com/AppliedEnergistics/Applied-Mekanistics
 * You may modify and redistribute this file under the terms of LGPL-3.0.
 */
package gg.drak.qiointegrations.ae2;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import gg.drak.qiointegrations.QIOText;
import gg.drak.qiointegrations.qio.QioFrequencyAccess;
import mekanism.api.Action;
import mekanism.api.inventory.IHashedItem;
import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.api.inventory.qio.IQIOFrequency;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;

/** Bridges a Mekanism QIO dashboard to AE2 {@link MEStorage}. */
public class QioStorageAdapter<DASHBOARD extends BlockEntity & IQIOComponent> implements MEStorage {
    private static final Map<IHashedItem, AEItemKey> CACHE = new WeakHashMap<>();

    private final DASHBOARD dashboard;
    private final @Nullable Direction queriedSide;
    private final @Nullable UUID owner;

    public QioStorageAdapter(DASHBOARD dashboard, @Nullable Direction queriedSide, @Nullable UUID owner) {
        this.dashboard = dashboard;
        this.queriedSide = queriedSide;
        this.owner = owner;
    }

    @Nullable
    public IQIOFrequency getFrequency() {
        return QioFrequencyAccess.resolve(dashboard, queriedSide, owner);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }
            return freq.massInsert(itemKey.toStack(), amount, Action.fromFluidAction(mode.getFluidAction()));
        }
        return 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }
            return freq.massExtract(itemKey.toStack(), amount, Action.fromFluidAction(mode.getFluidAction()));
        }
        return 0;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        var freq = getFrequency();
        if (freq == null) {
            return;
        }

        freq.forAllHashedStored((type, count) -> {
            out.add(CACHE.computeIfAbsent(type, it -> AEItemKey.of(it.getInternalStack())), count);
        });
    }

    @Override
    public Component getDescription() {
        var freq = getFrequency();
        if (freq == null) {
            throw new IllegalStateException("Unexpected null frequency!");
        }
        return QIOText.QIO_FREQUENCY.formatted(freq.getName());
    }
}
