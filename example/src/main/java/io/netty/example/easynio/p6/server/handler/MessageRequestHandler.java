package io.netty.example.easynio.p6.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p6.protocol.request.MessageRequestPacket;
import io.netty.example.easynio.p6.protocol.response.MessageResponsePacket;
import io.netty.example.easynio.p6.session.Session;
import io.netty.example.easynio.p6.util.LoginUtil;
import io.netty.example.easynio.p6.util.SessionUtil;

import java.time.LocalDateTime;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket) throws Exception {
        Session session = SessionUtil.getSession(ctx.channel());

        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setFromUserId(session.getUserId());
        messageResponsePacket.setFromUserName(session.getUserName());
        messageResponsePacket.setMessage(messageRequestPacket.getMessage());

        Channel toUserChannel = SessionUtil.getChannel(messageRequestPacket.getToUserId());

        if (toUserChannel != null && LoginUtil.hasLogin(toUserChannel)) {
            toUserChannel.writeAndFlush(messageRequestPacket);
        } else {
            System.err.println("[" + messageRequestPacket.getToUserId() + "]不在线， 发送失败");
        }
    }
}
