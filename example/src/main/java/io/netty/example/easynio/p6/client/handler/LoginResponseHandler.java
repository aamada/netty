package io.netty.example.easynio.p6.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p6.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p6.protocol.response.LoginResponsePacket;
import io.netty.example.easynio.p6.session.Session;
import io.netty.example.easynio.p6.util.LoginUtil;
import io.netty.example.easynio.p6.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
        String userId = loginResponsePacket.getUserId();
        String userName = loginResponsePacket.getUserName();

        if (loginResponsePacket.isSuccess()) {
            System.err.println("["+userName+"]登陆成功， userId=" + loginResponsePacket.getUserId());
            SessionUtil.bindSesssion(new Session(userId, userName), ctx.channel());
        } else {
            System.err.println("["+userName+"]登陆失败， reason=" + loginResponsePacket.getReason());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("客户端连接被关闭");
    }
}
