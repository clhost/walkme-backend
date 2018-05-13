package auth.utils;

import org.hibernate.Session;

public class Dropper {
    public static void drop() {
        HibernateUtil.start();
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.createNativeQuery("drop table wm_fav_route").executeUpdate();
            session.createNativeQuery("drop table wm_session").executeUpdate();
            session.createNativeQuery("drop table wm_user").executeUpdate();

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
