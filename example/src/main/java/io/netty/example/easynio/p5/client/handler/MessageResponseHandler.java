package io.netty.example.easynio.p5.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p5.protocol.response.MessageResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket msg) throws Exception {
        logger.info(LocalDateTime.now() + "：收到服务端的消息：" + msg.getMessage());
    }
}
