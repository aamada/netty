package io.netty.example.easynio.p3.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.example.easynio.p3.protocol.Packet;
import io.netty.example.easynio.p3.protocol.PacketCodec;
import io.netty.example.easynio.p3.protocol.reponse.LoginResponsePacket;
import io.netty.example.easynio.p3.protocol.reponse.MessageResponsePacket;
import io.netty.example.easynio.p3.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p3.util.LoginUtil;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println(LocalDateTime.now() + "；客户端开始登录");

        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUsername("cjm");
        loginRequestPacket.setPassword("pwd");

        // 编码
        ByteBuf buf = PacketCodec.INSTANCE.encode(ctx.alloc(), loginRequestPacket);

        // 写数据
        ctx.channel().writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        Packet packet = PacketCodec.INSTANCE.decode(buf);
        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
            if (loginResponsePacket.isSuccess()) {
                System.err.println(LocalDateTime.now() + "客户登录成功");
                LoginUtil.markAsLogin(ctx.channel());
            }  else {
                System.err.println(LocalDateTime.now() + "；客户端登录失败， 原因：" + loginResponsePacket.getReason());
            }
        } else if (packet instanceof MessageResponsePacket) {
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
            System.err.println(LocalDateTime.now() + "；收到服务端消息：" + messageResponsePacket.getMessage());
        }
    }
}
