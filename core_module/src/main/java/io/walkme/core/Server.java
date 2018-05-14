package io.walkme.core;

import auth.core.AuthService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.core.RouteService;

import javax.net.ssl.SSLException;


public class Server {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Class socketChannelClass;
    private final int port;
    private final String host;
    private ChannelFuture cf;
    private final Logger logger = LogManager.getLogger(Server.class);
    private SslContext sslContext;
    private AuthService authService;
    private RouteService routeService;

    Server(final String host, final int port, AuthService authService, RouteService routeService) {
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
        this.authService = authService;
        this.routeService = routeService;

        try {
            if (ServerMode.getMode()) {
                sslContext = SslContextBuilder.forServer(
                        Server.class.getResourceAsStream("/fullchain.pem"),
                        Server.class.getResourceAsStream("/privkey.pem")).build();
            }
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    void run() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(socketChannelClass)
                    .childHandler(new Initializer(sslContext, authService, routeService))
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

            cf = bootstrap.bind(host, port).syncUninterruptibly();

            cf.addListener(future -> {
                if (future.isDone()) {
                    logger.info("Server has been started.");
                    System.out.println("Server has been started. \n" +
                                            "* Host: " + host + "\n" +
                                            "* Port: " + port + "\n" +
                                            "* Server socket channel: " + socketChannelClass.getSimpleName() + "\n" +
                                            "* Time: " + (System.currentTimeMillis() - StartInfo.start));
                }
            });

            cf.channel().closeFuture().syncUninterruptibly();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        cf.channel().closeFuture().syncUninterruptibly();
        logger.info("Server has been shutdown.");
    }

    public void start() {
        run();
    }

    public void shutdown() {
        close();
    }

    public static Server configuredServer() {
        Server server = Configurator.configure();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        return server;
    }

    public static void main(String[] args) {
        Server.configuredServer().start();
    }
}
