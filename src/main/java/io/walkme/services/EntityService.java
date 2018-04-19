package io.walkme.services;

import java.util.List;

/**
 *
 * @param <T> Entity объект
 * @param <U> By параметр (delete by, get by)
 * @param <R> Type параметр (get by id or social id) -> depends on appropriate enum
 */
public interface EntityService<T, U, R extends Enum<R>> {
    T get(U byParameter, R columnType) throws Exception;

    List<T> getAll(List<U> byParametersList, R columnType) throws Exception;

    void save(T entity) throws Exception;

    void delete(U byParameter, R columnType) throws Exception;

    void update(T entity) throws Exception;
}