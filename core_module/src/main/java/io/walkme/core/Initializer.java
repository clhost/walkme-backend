package io.walkme.core;

import auth.core.AuthService;
import io.netty.handler.ssl.SslContext;
import io.walkme.handlers.auth.AuthHandler;
import io.walkme.handlers.auth.LogoutHandler;
import io.walkme.handlers.auth.TokenHandler;
import io.walkme.handlers.categories.StartHandler;
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
import io.walkme.handlers.route.GetSavedRoutesHandler;
import io.walkme.handlers.route.IsPointAvailableHandler;
import io.walkme.handlers.route.SaveRouteHandler;
import io.walkme.http.RedirectHandler;
import io.walkme.handlers.statics.StaticHandler;
import route.core.RouteService;


public class Initializer extends ChannelInitializer<SocketChannel> {
    private final EventExecutorGroup route = new DefaultEventExecutorGroup(8);
    private final EventExecutorGroup auth = new DefaultEventExecutorGroup(4);
    private final EventExecutorGroup start = new DefaultEventExecutorGroup(4);
    private final EventExecutorGroup logout = new DefaultEventExecutorGroup(4);

    private final SslContext sslContext;
    private final AuthService authService;
    private final RouteService routeService;

    public Initializer(SslContext sslContext, AuthService authService, RouteService routeService) {
        this.sslContext = sslContext;
        this.authService = authService;
        this.routeService = routeService;
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
        pipeline.addLast("static", new StaticHandler());

        pipeline.addLast("token", new TokenHandler(authService));

        pipeline.addLast(auth, "auth", new AuthHandler(authService));
        pipeline.addLast(logout, "logout", new LogoutHandler(authService));

        pipeline.addLast(start, "start", new StartHandler(authService, routeService));
        pipeline.addLast(route, "save_favorite_route", new SaveRouteHandler(routeService, authService));
        pipeline.addLast(route, "is_point_available", new IsPointAvailableHandler(routeService));
        pipeline.addLast(route, "get_favorite_routes", new GetSavedRoutesHandler(routeService, authService));
        pipeline.addLast(route, "route", new GetRouteHandler(routeService));

        pipeline.addLast("invalid", new InvalidRequestHandler());
    }
}
