package route.storage.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wm_category")
public class Category {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @Expose
    private int id;

    @Column(name = "category_name", unique = true, nullable = false)
    @Expose
    private String name;

    public Category(int id, String categoryName) {
        this.id = id;
        this.name = categoryName;
    }

    public Category() {
        // The explicit constructor for ORM
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
