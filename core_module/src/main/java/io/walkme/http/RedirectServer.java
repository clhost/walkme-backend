package io.walkme.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.walkme.core.Configurator;
import io.walkme.helpers.ConfigHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class RedirectServer {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Class socketChannelClass;
    private final int port;
    private final String host;
    private ChannelFuture cf;
    private final Logger logger = LogManager.getLogger(RedirectServer.class);

    RedirectServer(final String host) {
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
        this.port = 8080;
    }

    @SuppressWarnings("unchecked")
    void run() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(socketChannelClass)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // HTTP decode handlers
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(Short.MAX_VALUE));

                            // redirect handler
                            pipeline.addLast("redirect", new RedirectHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128);

            cf = bootstrap.bind(host, port).syncUninterruptibly();

            cf.addListener(future -> {
                if (future.isDone()) {
                    logger.info("Redirect server has been started.");
                    System.out.println("Server has been started. \n" +
                            "* Host: " + host + "\n" +
                            "* Port: " + port + "\n" +
                            "* Server socket channel: " + socketChannelClass.getSimpleName() + "\n");
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

    public static RedirectServer build() throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(
                Configurator.class.getResourceAsStream("/" + ConfigHelper.LOCAL_PROPERTIES)));
        String host = properties.getProperty("server.host");
        if (host == null || host.equals("")) {
            throw new NullPointerException("server.host is missing.");
        }
        return new RedirectServer(host);
    }
}
