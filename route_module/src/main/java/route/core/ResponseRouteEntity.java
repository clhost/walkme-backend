package route.core;

import com.google.gson.annotations.Expose;
import route.storage.entities.Location;
import route.storage.entities.Place;

/**
 * ResponseRouteEntity представляет собой обёртку над ответом клиенту - "урезанная" версия класса {@link Place}.
 * Сериализуется в json.
 */
public class ResponseRouteEntity {
    @Expose
    @SuppressWarnings("unused")
    private Location point;

    @Expose
    @SuppressWarnings("unused")
    private String name;

    @Expose
    @SuppressWarnings("unused")
    private String category;

    @Expose
    @SuppressWarnings("unused")
    private int categoryId;

    @Expose
    @SuppressWarnings("unused")
    private String address;

    @Expose
    @SuppressWarnings("unused")
    private String addressAdditional;

    @Expose
    @SuppressWarnings("unused")
    private String workingTime;

    public ResponseRouteEntity(
            Location location,
            String name,
            String category,
            int categoryId,
            String address,
            String addressAdditional,
            String workingTime) {
        this.point = location;
        this.name = name;
        this.category = category;
        this.categoryId = categoryId;
        this.address = address;
        this.addressAdditional = addressAdditional;
        this.workingTime = workingTime;
    }
}