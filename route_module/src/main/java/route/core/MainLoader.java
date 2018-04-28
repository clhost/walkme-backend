package route.core;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import route.services.CategoryService;
import route.services.PlaceService;
import route.storage.entities.WalkMeCategory;
import route.storage.loaders.JsonLoader;
import route.storage.loaders.Loader;
import route.utils.HibernateUtil;

import java.io.File;
import java.math.BigInteger;

public class MainLoader {
    private final CategoryService categoryService;

    public MainLoader() {
        this.categoryService = new CategoryService();
    }

    public static void main(String[] args) {
        MainLoader loader = new MainLoader();
        loader.initHibernate();
        if (!loader.isExists()) {
            loader.loadPlaces();
        }
    }

    private void loadPlaces() {
        Loader<File, WalkMeCategory> loader = new JsonLoader();
        categoryService.upload();
        loader.load(new File(
                        new File("nodejs-dataset/spb-1.json").getAbsolutePath()),
                WalkMeCategory.BAR);
        loader.load(new File(
                        new File("nodejs-dataset/spb-2.json").getAbsolutePath()),
                WalkMeCategory.EAT);
        loader.load(new File(
                        new File("nodejs-dataset/spb-3.json").getAbsolutePath()),
                WalkMeCategory.FUN);
        loader.load(new File(
                        new File("nodejs-dataset/spb-4.json").getAbsolutePath()),
                WalkMeCategory.PARKS);
        loader.load(new File(
                        new File("nodejs-dataset/spb-5.json").getAbsolutePath()),
                WalkMeCategory.WALK);
    }

    private boolean isExists() {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            NativeQuery query = session.createNativeQuery("select count(*) from " + PlaceService.TABLE_NAME);
            BigInteger i = (BigInteger) query.getSingleResult();

            session.getTransaction().commit();

            return i.longValue() != 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void initHibernate() {
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
    }
}
