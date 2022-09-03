package io.netty.example.easynio.p3.server.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class OutBoundHandlerA extends ChannelOutboundHandlerAdapter {
    protected final InternalLogger logger = InternalLoggerFactory.getInstance(getClass());
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(this.getClass().getSimpleName() + ":" + msg);
        logger.info("abc");
        super.write(ctx, msg, promise);
    }
}
