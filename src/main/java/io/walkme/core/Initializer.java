package core;

import handlers.distance.GetRouteHandler;
import handlers.auth.AuthHandler;
import handlers.test.InfoHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;


public class Initializer extends ChannelInitializer<SocketChannel> {
    private final EventExecutorGroup route = new DefaultEventExecutorGroup(8);
    private final EventExecutorGroup auth = new DefaultEventExecutorGroup(8);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // HTTP decode io.walkme.handlers
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Short.MAX_VALUE));

        // AUTH io.walkme.handlers
        pipeline.addLast(new InfoHandler(true));
        pipeline.addLast(auth, "auth", new AuthHandler());
        pipeline.addLast(route, "route", new GetRouteHandler());

        // REST API io.walkme.handlers
        //pipeline.addLast("post_distance", new PostDistanceHandler());
        //pipeline.addLast("rest_create", new CreateEventHandler());
        //pipeline.addLast("rest_get", new GetEventHandler());у
        //pipeline.addLast("rest_update", new UpdateEventHandler());
        //pipeline.addLast("rest_delete", new DeleteEventHandler());
    }
}
