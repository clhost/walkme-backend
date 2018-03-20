package storage.validator;

import storage.mappers.WalkMeCategory;

@FunctionalInterface
public interface Repair<T> {
    T repair(T t, WalkMeCategory c);
}
