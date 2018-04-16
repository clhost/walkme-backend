package io.walkme.services;

import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CategoryService {
    private static final List<Category> categories = new ArrayList<>();

    public static void upload() {
        prepare();

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

    private static void prepare() {
        EnumSet<WalkMeCategory> enumSet = EnumSet.of(
                WalkMeCategory.BAR,
                WalkMeCategory.PARKS,
                WalkMeCategory.EAT,
                WalkMeCategory.FUN,
                WalkMeCategory.WALK);

        for (WalkMeCategory w : enumSet) {
            categories.add(new Category(w.id(), w.description()));
        }
    }

    public static void main(String[] args) {
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
        CategoryService.upload();
    }
}
