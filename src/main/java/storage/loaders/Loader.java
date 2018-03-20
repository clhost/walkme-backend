package storage.loaders;

@FunctionalInterface
public interface Loader<T> {
    void load(T t);
}
