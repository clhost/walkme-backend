package io.walkme.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class Server {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Class socketChannelClass;
    private final int port;
    private final String host;


    Server(final String host, final int port) {
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
    void run() {
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

    public static void main(String[] args) {
        Configurator.configure().run();
    }
}
