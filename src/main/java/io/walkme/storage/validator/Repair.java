package io.walkme.storage.validator;

import io.walkme.storage.entities.WalkMeCategory;

@FunctionalInterface
public interface Repair<T> {
    T repair(T t, WalkMeCategory c);
}
