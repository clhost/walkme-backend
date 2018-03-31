package io.walkme.storage.loaders;

import io.walkme.storage.entities.WalkMeCategory;

@FunctionalInterface
public interface Loader<T> {
    void load(T t, WalkMeCategory c);
}
