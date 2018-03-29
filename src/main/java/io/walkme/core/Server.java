package io.walkme.core;

import io.walkme.helpers.ConfigHelper;
import io.walkme.helpers.OKHelper;
import io.walkme.helpers.VKHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import io.walkme.services.SessionService;
import io.walkme.utils.HibernateUtil;


public class Server {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Class socketChannelClass;
    private final int port;
    private final String host;


    private Server(final String host, final int port) {
        if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup();
            socketChannelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            socketChannelClass = NioServerSocketChannel.class;
        }

        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(socketChannelClass)
                    .childHandler(new Initializer())
            .option(ChannelOption.SO_BACKLOG, 128);

            ChannelFuture cf = bootstrap.bind(host, port).syncUninterruptibly();

            cf.addListener(future -> {
                if (future.isDone()) {
                    System.out.println("Server has been started. \n* Host: " + host + "\n* Port: " + port +
                    "\n* Server socket channel: " + socketChannelClass.getSimpleName());
                }
            });

            cf.channel().closeFuture().syncUninterruptibly();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static void init() {
        // start hibernate
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();

        // initialize all existing sessions
        SessionService.getInstance().loadFromDatabase();

        // init vk and ok helpers application info
        VKHelper.init();
        OKHelper.init();
    }

    public static Server configure() {
        init();
        Server server = null;
        Configurations confs = new Configurations();

        FileBasedConfigurationBuilder<PropertiesConfiguration> locBuilder;
        FileBasedConfigurationBuilder<PropertiesConfiguration> hibBuilder;

        try {

            locBuilder = confs.propertiesBuilder(ConfigHelper.LOCAL_PROPERTIES);
            hibBuilder = confs.propertiesBuilder(ConfigHelper.HIBERNATE_PROPERTIES);

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
                hibProps.setProperty("db.login", login);
            }

            if (password != null && !password.equals("")) {
                hibProps.setProperty("db.password", password);
            }

            hibBuilder.save();

            server = new Server(host, Integer.parseInt(port));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return server;
    }

    public static void main(String[] args) {
        Server.configure().run();
    }
}
