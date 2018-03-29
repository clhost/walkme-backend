package io.walkme.mappers;

@FunctionalInterface
public interface Mapper<E, T> {
    E map(T t);
}
