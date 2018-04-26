package route.core;

import javax.annotation.Nullable;
import java.util.List;

public interface BaseRouteService {
    /**
     *
     * @return список категорий
     */
    @Nullable
    String getCategories();

    /**
     *
     * Начальная позиция, от которой требуется строить маршрут {@param lat - широта} {@param lng - долгота}
     * @param categories массив идентификаторов категорий
     * @return маршрут в формате json в виде строки, либо описание ошибок в формате json
     */
    String getRoute(double lat, double lng, int[] categories);

    /**
     *
     * @param userId идентификатор юзера, чью сохраненные маршруты требуется вернуть
     * @return список маршрутов в формате json в виде строки
     */
    @Nullable
    List<String> getSavedRoutes(String userId);

    /**
     * Стартует службу
     */
    void start();
}
