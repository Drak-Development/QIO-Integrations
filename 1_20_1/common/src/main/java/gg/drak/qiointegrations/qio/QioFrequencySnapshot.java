package gg.drak.qiointegrations.qio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.ObjLongConsumer;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;

import mekanism.api.inventory.IHashedItem;
import mekanism.api.inventory.qio.IQIOFrequency;

/**
 * Shared, per-frequency inventory snapshot for QIO bridges.
 * <p>
 * Multiple integrations (Tom's, AE2, RS) can probe the same frequency during grid or connector init.
 * Without caching, each slot query triggers another full {@code forAllHashedStored} walk, which becomes
 * quadratic for large frequencies. Snapshots are reused for the rest of the game tick and invalidated when
 * a bridge mutates the frequency or a new tick begins.
 */
public final class QioFrequencySnapshot {
    private static final Map<IQIOFrequency, Entry> CACHE = new WeakHashMap<>();
    private static final Map<IQIOFrequency, Long> MUTATION_VERSION = new WeakHashMap<>();

    private record StoredEntry(IHashedItem type, long count) {}

    private record Entry(long gameTime, long mutationVersion, List<StoredEntry> stored) {}

    private static volatile long currentGameTime = Long.MIN_VALUE;

    private QioFrequencySnapshot() {
    }

    public static void beginTick(long gameTime) {
        currentGameTime = gameTime;
    }

    public static void markDirty(IQIOFrequency frequency) {
        synchronized (QioFrequencySnapshot.class) {
            MUTATION_VERSION.merge(frequency, 1L, Long::sum);
            CACHE.remove(frequency);
        }
    }

    public static boolean needsRescan(IQIOFrequency frequency, long gameTime) {
        synchronized (QioFrequencySnapshot.class) {
            long version = MUTATION_VERSION.getOrDefault(frequency, 0L);
            Entry entry = CACHE.get(frequency);
            return entry == null || entry.gameTime() != gameTime || entry.mutationVersion() != version;
        }
    }

    public static boolean anyNeedsRescan(long gameTime) {
        synchronized (QioFrequencySnapshot.class) {
            for (IQIOFrequency frequency : MUTATION_VERSION.keySet()) {
                if (needsRescan(frequency, gameTime)) {
                    return true;
                }
            }

            for (var entry : CACHE.entrySet()) {
                if (entry.getValue().gameTime() != gameTime) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean anyMutationDirty() {
        synchronized (QioFrequencySnapshot.class) {
            for (var mutation : MUTATION_VERSION.entrySet()) {
                Entry entry = CACHE.get(mutation.getKey());
                if (entry == null || entry.mutationVersion() != mutation.getValue()) {
                    return true;
                }
            }
            return false;
        }
    }

    public static List<ItemStack> getStoredTypes(IQIOFrequency frequency) {
        return getStoredTypes(frequency, null);
    }

    public static List<ItemStack> getStoredTypes(IQIOFrequency frequency, @Nullable Long gameTime) {
        return snapshot(frequency, gameTime).stream()
                .map(stored -> {
                    var stack = stored.type().getInternalStack().copy();
                    stack.setCount(Math.toIntExact(Math.min(stored.count(), Integer.MAX_VALUE)));
                    return stack;
                })
                .toList();
    }

    public static void forAllStored(IQIOFrequency frequency, @Nullable Long gameTime, ObjLongConsumer<IHashedItem> consumer) {
        for (var stored : snapshot(frequency, gameTime)) {
            if (stored.count() > 0) {
                consumer.accept(stored.type(), stored.count());
            }
        }
    }

    private static List<StoredEntry> snapshot(IQIOFrequency frequency, @Nullable Long gameTime) {
        synchronized (QioFrequencySnapshot.class) {
            long version = MUTATION_VERSION.getOrDefault(frequency, 0L);
            long tickKey = gameTime != null ? gameTime : currentGameTime;
            Entry entry = CACHE.get(frequency);
            if (entry != null && entry.mutationVersion() == version && entry.gameTime() == tickKey) {
                return entry.stored();
            }

            List<StoredEntry> stored = new ArrayList<>();
            frequency.forAllHashedStored((type, count) -> {
                if (count > 0 && !type.getInternalStack().isEmpty()) {
                    stored.add(new StoredEntry(type, count));
                }
            });
            List<StoredEntry> cached = List.copyOf(stored);
            CACHE.put(frequency, new Entry(tickKey, version, cached));
            return cached;
        }
    }
}
