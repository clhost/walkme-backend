package auth.services;

import java.util.List;

/**
 *
 * @param <T> Entity объект
 * @param <U> By параметр (delete by, get by)
 * @param <R> Type параметр (get by id or social id) -> depends on appropriate enum
 */
public interface EntityService<T, U, R extends Enum<R>> {
    T get(U byParameter, R columnType);

    List<T> getAll(List<U> byParametersList, R columnType);

    void save(T entity);

    void delete(U byParameter, R columnType);

    void update(T entity);
}