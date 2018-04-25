package io.walkme.core;

import io.walkme.graph.Ways;
import io.walkme.helpers.ConfigHelper;
import io.walkme.helpers.OKHelper;
import io.walkme.helpers.VKHelper;
import io.walkme.services.CategoryService;
import io.walkme.services.PlaceService;
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

            if (port == null || port.equals("")) {
                throw new NullPointerException("server.port is missing.");
            }

            if (host == null || host.equals("")) {
                throw new NullPointerException("server.host is missing.");
            }

            if (url != null && !url.equals("")) {
                hibProps.setProperty("hibernate.connection.url", url);
            } else {
                throw new NullPointerException("db.url is missing.");
            }

            if (login != null && !login.equals("")) {
                hibProps.setProperty("hibernate.connection.username", login);
            } else {
                throw new NullPointerException("db.username is missing");
            }

            hibProps.setProperty("hibernate.connection.password", password);

            hibBuilder.save();
            checkServerMode();

            server = new Server(host, Integer.parseInt(port));
        } catch (ConfigurationException e) {
            logger.error(e.getCause().getMessage());
        }

        return server;
    }

    private static void checkServerMode() {
        try {
            PropertiesConfiguration locProps = locBuilder.getConfiguration();
            String stub = locProps.getString("server.stub.enable");
            switch (stub) {
                case "off":  // prod mode
                    initProd();
                    ServerMode.setProdMode();
                    ServerMode.setAuth(true);

                    Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown));
                    logger.info("Configured in prod mode.");
                    break;
                case "on":  // stub mode
                    initHibernate();

                    String dbReload = locProps.getString("server.stub.db_reload");
                    String auth = locProps.getString("server.stub.auth");

                    if (dbReload == null || dbReload.equals("")) {
                        throw new NullPointerException("server.stub.db_reload is missing.");
                    }

                    if (auth == null || auth.equals("")) {
                        throw new NullPointerException("server.stub.auth is missing.");
                    }

                    switch (dbReload) {
                        case "on": {
                            long a = ifExists();

                            if (a != 0) {
                                Dropper.drop();
                            }

                            Runtime.getRuntime().addShutdownHook(new Thread(Dropper::drop));
                            load();
                            break;
                        }
                        case "off": {
                            long a = ifExists();

                            if (a == 0) {
                                load();
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("server.stub.db_reload must be \"on\" or \"off\"");
                    }

                    if (auth.equals("on")) {
                        initAuth();
                        ServerMode.setAuth(true);
                    } else if (!auth.equals("off")) {
                        throw new IllegalStateException("server.stub.auth must be \"on\" or \"off\"");
                    }

                    initGraph();

                    logger.info("Configured in stub mode.");
                    break;
                default:
                    throw new IllegalStateException("server.stub.enable must be \"on\" or \"off\".");
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

            NativeQuery query = session.createNativeQuery("select count(*) from " + PlaceService.TABLE_NAME);
            BigInteger i = (BigInteger) query.getSingleResult();
            session.getTransaction().commit();

            return i.longValue();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static void load() {
        CategoryService.upload();

        Loader<File, WalkMeCategory> loader = new JsonLoader();
        loader.load(new File("nodejs-dataset/spb-1.json"), WalkMeCategory.BAR);
        loader.load(new File("nodejs-dataset/spb-2.json"), WalkMeCategory.EAT);
        loader.load(new File("nodejs-dataset/spb-3.json"), WalkMeCategory.FUN);
        loader.load(new File("nodejs-dataset/spb-4.json"), WalkMeCategory.PARKS);
        loader.load(new File("nodejs-dataset/spb-5.json"), WalkMeCategory.WALK);
    }

    private static void initHibernate() {
        // start hibernate
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
    }

    private static void initAuth() {
        // initialize all existing sessions
        SessionService.getInstance().loadFromDatabase();
        // initHibernate vk and ok helpers application info
        VKHelper.init();
        OKHelper.init();
    }

    private static void initGraph() {
        // load all places in RAM
        PlaceHolder.load();
        // start algorithm
        Ways.ghStart();
        Ways.initializePlaces(PlaceHolder.getAll());
    }

    private static void initProd() {
        initHibernate();
        initAuth();
        initGraph();
    }
}