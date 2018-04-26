package route.storage.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

/**
 * Путь, который клиент добавляет в "сохраненные".
 * Для простоты хранения весь путь хранится в json строке.
 */
@Entity
@Table(name = "wm_fav_route")
public class SavedRoute {
    @ManyToOne
    @JoinColumn(name = "id")
    @Expose
    private User user;

    //Нуждается в отдельном парсинге.
    @Column(name = "json_route")
    private String jsonRoute;

    public SavedRoute() {
        // The explicit constructor for ORM
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJsonRoute() {
        return jsonRoute;
    }

    public void setJsonRoute(String jsonRoute) {
        this.jsonRoute = jsonRoute;
    }
}
