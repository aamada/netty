package io.netty.example.easynio.p3.server.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InBoundHandlerB extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.err.println(this.getClass().getSimpleName() + ":" + msg);
        super.channelRead(ctx, msg);
    }
}
