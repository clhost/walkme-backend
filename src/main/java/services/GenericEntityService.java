package services;

import java.util.List;

public interface GenericEntityService<T, E> {
    T get(E e, String column) throws Exception;
    List<T> getAll(List<E> e, String column) throws Exception;
    void save(T t) throws Exception;
    void delete(E e, String column) throws Exception;
    void update(T t) throws Exception;
}