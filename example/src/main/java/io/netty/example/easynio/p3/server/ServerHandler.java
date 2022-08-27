package io.netty.example.easynio.p3.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.example.easynio.p3.Packet;
import io.netty.example.easynio.p3.PacketCodec;
import io.netty.example.easynio.p3.protocol.reponse.LoginResponsePacket;
import io.netty.example.easynio.p3.protocol.request.LoginRequestPacket;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 服务端处理器
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 进行登陆处理器
        System.err.println(LocalDateTime.now() + "；客户端开始登陆");
        // 得到信息
        ByteBuf requestByteBuf = (ByteBuf) msg;
        // 序列化
        Packet packet = PacketCodec.INSTANCE.decode(requestByteBuf);

        // 如果是请求包， 那么就进行处理
        if (packet instanceof LoginRequestPacket) {
            // 请求
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            // 新建一个回复
            LoginResponsePacket responsePacket = new LoginResponsePacket();

            // 设置版本号
            responsePacket.setVersion(packet.getVersion());
            // 校验请求是否合法
            if (valid(loginRequestPacket)) {
                // 设置成功
                responsePacket.setSuccess(true);
                System.err.println(LocalDateTime.now() + "；登录成功!");
            } else {
                responsePacket.setReason("账号密码校验失败");
                responsePacket.setSuccess(false);
                System.err.println(LocalDateTime.now() + "；登录失败!");
            }
            // 序列化
            ByteBuf responseByteBuf = PacketCodec.INSTANCE.encode(ctx.alloc(), responsePacket);
            // 将数据写出去
            ctx.channel().writeAndFlush(responseByteBuf);
        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }
}
