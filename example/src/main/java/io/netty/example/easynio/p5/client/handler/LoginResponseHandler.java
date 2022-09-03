package io.netty.example.easynio.p5.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p5.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p5.protocol.response.LoginResponsePacket;
import io.netty.example.easynio.p5.util.LoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUsername("cjm");
        loginRequestPacket.setPassword("pwd");

        ctx.channel().writeAndFlush(loginRequestPacket);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket msg) throws Exception {
        if (msg.isSuccess()) {
            logger.info(LocalDateTime.now() + "：客户端登陆成功");
            LoginUtil.markAsLogin(ctx.channel());
        } else {
            logger.info(LocalDateTime.now() + "：客户端登陆失败， 原因：" + msg.getReason());
        }
    }
}
