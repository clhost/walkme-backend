package io.walkme.core;

import io.netty.handler.ssl.SslContext;
import io.walkme.handlers.auth.AuthHandler;
import io.walkme.handlers.auth.LogoutHandler;
import io.walkme.handlers.auth.TokenHandler;
import io.walkme.handlers.categories.GetCategoriesHandler;
import io.walkme.handlers.info.InvalidRequestHandler;
import io.walkme.handlers.route.GetRouteHandler;
import io.walkme.handlers.info.InfoHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;


public class Initializer extends ChannelInitializer<SocketChannel> {
    private final EventExecutorGroup route = new DefaultEventExecutorGroup(8);
    private final EventExecutorGroup auth = new DefaultEventExecutorGroup(4);
    private final EventExecutorGroup logout = new DefaultEventExecutorGroup(4);

    private final SslContext sslContext;

    public Initializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL (deploy only)
        if (ServerMode.getMode()) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }

        // HTTP decode handlers
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Short.MAX_VALUE));

        // AUTH handlers
        pipeline.addLast("info", new InfoHandler(true));
        pipeline.addLast("token", new TokenHandler());
        pipeline.addLast(auth, "auth", new AuthHandler());
        pipeline.addLast(logout, "logout", new LogoutHandler());
        pipeline.addLast("categories", new GetCategoriesHandler());
        pipeline.addLast(route, "route", new GetRouteHandler());
        pipeline.addLast("invalid", new InvalidRequestHandler());
    }
}
