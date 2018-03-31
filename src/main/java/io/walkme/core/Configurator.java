package io.walkme.core;

import io.walkme.graph.stub.RouteFinder;
import io.walkme.helpers.ConfigHelper;
import io.walkme.helpers.OKHelper;
import io.walkme.helpers.VKHelper;
import io.walkme.services.CategoryService;
import io.walkme.services.SessionService;
import io.walkme.services.fields.PlaceFields;
import io.walkme.storage.Dropper;
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.storage.loaders.JsonLoader;
import io.walkme.storage.loaders.Loader;
import io.walkme.utils.HibernateUtil;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.io.File;
import java.math.BigInteger;

public class Configurator {
    private static Configurations confs = new Configurations();
    private static FileBasedConfigurationBuilder<PropertiesConfiguration> locBuilder;
    private static FileBasedConfigurationBuilder<PropertiesConfiguration> hibBuilder;
    private static final Logger logger = LogManager.getLogger(Configurator.class);

    static {
        StartInfo.start = System.currentTimeMillis();
        locBuilder = confs.propertiesBuilder(ConfigHelper.LOCAL_PROPERTIES);
        hibBuilder = confs.propertiesBuilder(ConfigHelper.HIBERNATE_PROPERTIES);
    }

    public static Server configure() {
        Server server = null;

        try {
            PropertiesConfiguration locProps = locBuilder.getConfiguration();
            PropertiesConfiguration hibProps = hibBuilder.getConfiguration();

            String port = locProps.getString("server.port");
            String host = locProps.getString("server.host");
            String url = locProps.getString("db.url");
            String login = locProps.getString("db.login");
            String password = locProps.getString("db.password");

            if (port == null) {
                throw new NullPointerException("Port is missing.");
            }

            if (host == null) {
                throw new NullPointerException("Host is missing.");
            }

            if (port.equals("")) {
                port = "8080";
            }

            if (host.equals("")) {
                host = "localhost";
            }

            if (url != null && !url.equals("")) {
                hibProps.setProperty("hibernate.connection.url", url);
            }

            if (login != null && !login.equals("")) {
                hibProps.setProperty("hibernate.connection.username", login);
            }

            if (password != null && !password.equals("")) {
                hibProps.setProperty("hibernate.connection.password", password);
            }

            hibBuilder.save();
            init();
            isStub();
            server = new Server(host, Integer.parseInt(port));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return server;
    }


    private static void isStub() {
        try {
            PropertiesConfiguration locProps = locBuilder.getConfiguration();
            String stub = locProps.getString("server.stub.enable");
            String dbReload = locProps.getString("server.stub.db_reload");

            if (stub != null && stub.equals("on")) {
                RouteFinder.init();

                if (dbReload != null && dbReload.equals("true")) {
                    long a = ifExists();

                    if (a != 0) {
                        Dropper.drop();
                        HibernateUtil.start();
                    }

                    Runtime.getRuntime().addShutdownHook(new Thread(Dropper::drop));
                    load();
                } else if (dbReload != null && dbReload.equals("false")) {
                    long a = ifExists();

                    if (a == 0) {
                        load();
                    }
                }

                GlobalProps.setIsStub(true);
            }

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

    }

    private static long ifExists() {
        Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            NativeQuery query = session.createNativeQuery("select count(*) from " + PlaceFields.TABLE_NAME);
            BigInteger i = (BigInteger) query.getSingleResult();
            session.getTransaction().commit();

            return i.longValue();
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    static void load() {
        CategoryService.upload();

        Loader<File> loader = new JsonLoader();
        loader.load(new File("nodejs-dataset/spb-1.json"), WalkMeCategory.BAR);
        loader.load(new File("nodejs-dataset/spb-2.json"), WalkMeCategory.EAT);
        loader.load(new File("nodejs-dataset/spb-3.json"), WalkMeCategory.FUN);
        loader.load(new File("nodejs-dataset/spb-4.json"), WalkMeCategory.PARKS);
        loader.load(new File("nodejs-dataset/spb-5.json"), WalkMeCategory.WALK);
    }

    static void init() {
        // start hibernate
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();

        // initialize all existing sessions
        SessionService.getInstance().loadFromDatabase();

        // init vk and ok helpers application info
        VKHelper.init();
        OKHelper.init();
    }
}
