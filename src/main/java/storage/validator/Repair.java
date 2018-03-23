package storage.validator;

import storage.entities.WalkMeCategory;

@FunctionalInterface
public interface Repair<T> {
    T repair(T t, WalkMeCategory c);
}
