package io.netty.example.easynio.p5.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.easynio.p5.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p5.protocol.response.LoginResponsePacket;

import java.time.LocalDateTime;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
        System.err.println(LocalDateTime.now() + "：收到客户端登录请求。。。。。。");
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        loginResponsePacket.setVersion(msg.getVersion());
        if (valid(msg)) {
            loginResponsePacket.setSuccess(true);
            System.out.println(LocalDateTime.now() + ":登录成功");
        } else {
            loginResponsePacket.setReason("账号密码校验失败.");
            loginResponsePacket.setSuccess(false);
            System.out.println(LocalDateTime.now() + ":登陆失败.");
        }

        ctx.channel().writeAndFlush(loginResponsePacket);
    }

    private boolean valid(LoginRequestPacket loginResponsePacket) {
        return true;
    }
}
