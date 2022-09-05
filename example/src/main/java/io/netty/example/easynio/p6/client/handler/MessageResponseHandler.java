package io.netty.example.easynio.p6.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p6.protocol.response.MessageResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket messageResponsePacket) throws Exception {
        String fromUserId = messageResponsePacket.getFromUserId();
        String fromUserName = messageResponsePacket.getFromUserName();
        System.err.println(fromUserId + ":" + fromUserName + " -> " + messageResponsePacket.getMessage());
    }
}
