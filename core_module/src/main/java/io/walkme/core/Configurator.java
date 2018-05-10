package io.walkme.core;

import auth.core.AuthService;
import io.walkme.helpers.ConfigHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.core.RouteService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Configurator {
    private static final Logger logger = LogManager.getLogger(Configurator.class);
    private static final Properties locProps;
    private static final AuthService authService;
    private static final RouteService routeService;

    static {
        StartInfo.start = System.currentTimeMillis();

        authService = new AuthService();
        routeService = new RouteService();

        locProps = new Properties();
    }

    public static Server configure() {
        Server server = null;
        try {
            locProps.load(new InputStreamReader(
                    Configurator.class.getResourceAsStream("/" + ConfigHelper.LOCAL_PROPERTIES)));
            String port = locProps.getProperty("server.port");
            String host = locProps.getProperty("server.host");
            String fullDomain = locProps.getProperty("server.full_domain");

            if (port == null || port.equals("")) {
                throw new NullPointerException("server.port is missing.");
            }

            if (host == null || host.equals("")) {
                throw new NullPointerException("server.host is missing.");
            }

            if (fullDomain == null || fullDomain.equals("")) {
                throw new NullPointerException("server.full_domain is missing.");
            }

            checkServerMode();
            startServices();
            ConfigHelper.setFullDomain(fullDomain);
            server = new Server(host, Integer.parseInt(port), authService, routeService);
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }

        return server;
    }

    private static void checkServerMode() {
        String stub = locProps.getProperty("server.stub.enable");
        switch (stub) {
            case "off":  // prod mode
                ServerMode.setProdMode();
                logger.info("Configured in prod mode.");
                break;
            case "on":  // stub mode
                ServerMode.setStubMode();
                logger.info("Configured in stub mode.");
                break;
            default:
                throw new IllegalStateException("server.stub.enable must be \"on\" or \"off\".");
        }
    }

    private static void startServices() {
        authService.start();
        routeService.start();
    }
}
