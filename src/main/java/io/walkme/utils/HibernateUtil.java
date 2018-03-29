package utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import storage.entities.Category;
import storage.entities.Location;
import storage.entities.Place;
import storage.entities.User;

import java.io.FileInputStream;
import java.util.Properties;


public class HibernateUtil {
    private static SessionFactory factory;


    public static void start() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("hibernate.properties"));

            factory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Place.class)
                    .addAnnotatedClass(Location.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(storage.entities.Session.class)
                    .addAnnotatedClass(Category.class)
                    .buildSessionFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Session getSession() {
        return factory.openSession();
    }

    public static void shutdown() {
        factory.close();
    }

    public static void setNamesUTF8() {
        Session session = factory.openSession();
        session.beginTransaction();

        session.createNativeQuery("SET NAMES UTF8").executeUpdate();

        session.getTransaction().commit();
        session.close();
    }
}
