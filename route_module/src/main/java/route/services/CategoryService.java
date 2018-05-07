package route.services;

import org.hibernate.Session;
import route.storage.entities.Category;
import route.storage.entities.WalkMeCategory;
import route.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CategoryService {
    private final List<Category> categories;

    public CategoryService() {
        this.categories = new ArrayList<>();
        prepare();
    }

    public void upload() {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            for (Category category : categories) {
                session.save(category);
            }
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void prepare() {
        EnumSet<WalkMeCategory> enumSet = EnumSet.of(
                WalkMeCategory.ALCOHOL,
                WalkMeCategory.PARKS,
                WalkMeCategory.EAT,
                WalkMeCategory.AMUSEMENT,
                WalkMeCategory.CULTURE);
        for (WalkMeCategory w : enumSet) {
            categories.add(new Category(w.id(), w.description()));
        }
    }

    public List<Category> getCategories() {
        return categories;
    }
}
