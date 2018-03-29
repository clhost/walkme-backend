package io.walkme.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.Location;
import io.walkme.storage.entities.Place;
import io.walkme.storage.entities.User;

import java.io.FileInputStream;
import java.util.Properties;


public class HibernateUtil {
    private static SessionFactory factory;
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);


    public static void start() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("hibernate.properties"));

            factory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Place.class)
                    .addAnnotatedClass(Location.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(io.walkme.storage.entities.Session.class)
                    .addAnnotatedClass(Category.class)
                    .buildSessionFactory();

        } catch (Exception e) {
            logger.error(e.getMessage());
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
