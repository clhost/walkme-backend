package route.storage;

import org.hibernate.Session;
import route.utils.HibernateUtil;

public class Dropper {
    public static void drop() {
        HibernateUtil.start();
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.createNativeQuery("drop table wm_session").executeUpdate();
            session.createNativeQuery("drop table wm_place").executeUpdate();
            session.createNativeQuery("drop table wm_user").executeUpdate();
            session.createNativeQuery("drop table wm_category").executeUpdate();

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
            HibernateUtil.shutdown();
        }
    }

    public static void main(String[] args) {
        Dropper.drop();
    }
}
