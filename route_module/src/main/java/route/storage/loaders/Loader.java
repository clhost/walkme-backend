package route.storage.loaders;


/**
 * Интерфейс предназначен для загрузки данных из T (файл, сеть, бд и т.д.), определяя данные к некоторой категории Е.
 * @param <T> объект данных
 * @param <E> объект категории
 */
@FunctionalInterface
public interface Loader<T, E> {
    void load(T t, E e);
}
