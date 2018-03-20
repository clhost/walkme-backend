package storage.mappers;

@FunctionalInterface
public interface Mapper<E, T> {
    E map(T t);
}
