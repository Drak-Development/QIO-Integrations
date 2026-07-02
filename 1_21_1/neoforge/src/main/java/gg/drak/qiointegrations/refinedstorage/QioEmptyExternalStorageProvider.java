package gg.drak.qiointegrations.refinedstorage;

import java.util.Collections;
import java.util.Iterator;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;

/**
 * No-op provider returned whenever this factory does not handle the connected block. RS collects every factory
 * result into {@code CompositeExternalStorageProvider} without filtering nulls.
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
