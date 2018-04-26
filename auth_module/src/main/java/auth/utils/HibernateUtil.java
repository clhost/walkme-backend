package auth.utils;

import auth.entities.User;
import auth.helpers.ConfigHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

public class HibernateUtil {
    private static SessionFactory factory;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    public static void start() {
        lock.lock();
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    HibernateUtil.class.getResourceAsStream("/" + ConfigHelper.HIBERNATE_PROPERTIES)));
            properties.setProperty("hibernate.dialect", "auth.utils.UTFMySQLSupportDialect");

            factory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(auth.entities.Session.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static Session getSession() {
        return factory.openSession();
    }

    public static void shutdown() {
        factory.close();
    }

    public static void setNamesUTF8() {
        Session session = null;
        try {
            session = factory.openSession();
            session.beginTransaction();

            session.createNativeQuery("SET NAMES UTF8").executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
