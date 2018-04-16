package io.walkme.services;

import java.util.List;

/**
 *
 * @param <T> Entity объект
 * @param <E> By параметр (delete by, get by)
 */
public interface EntityService<T, E> {
    T get(E e, String column) throws Exception;

    List<T> getAll(List<E> e, String column) throws Exception;

    void save(T t) throws Exception;

    void delete(E e, String column) throws Exception;

    void update(T t) throws Exception;
}