package gg.drak.qiointegrations.refinedstorage;

import java.util.Collections;
import java.util.Iterator;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;

/**
 * No-op provider used when a QIO dashboard is connected on the wrong face so RS never receives a null factory result
 * (CompositeExternalStorageProvider does not tolerate null providers).
 */
enum QioEmptyExternalStorageProvider implements ExternalStorageProvider {
    INSTANCE;

    @Override
    public long insert(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        return 0;
    }

    @Override
    public long extract(ResourceKey resource, long amount, com.refinedmods.refinedstorage.api.core.Action action,
            Actor actor) {
        return 0;
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        return Collections.emptyIterator();
    }
}
