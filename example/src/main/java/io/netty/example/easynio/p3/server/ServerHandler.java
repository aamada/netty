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
 * 服务端处理器, 走到这里， 并不是NioServerSocketChannel的在的Selector来触发的， 而是一个新的请求过来时， 将NioSocketChannel注册到一个新的EvnetLoop上， 也就是SubSelector上， 这个时候是NioSocketChannel来触发到这里的
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
            // 将数据写出去, 看到没有， 这里的调用， 是在NioSocketChannel的pipeline调用的， 当前的线程， 还是NioEventLoop， 如果我们在这里进行了阻塞或者长时间的操作， 那么就会阻塞住EventLoop线程, 所以如果有耗时的操作， 应该开启一个线程来进行操作
            // 调用AbstractChannel调用write方法, 将数据写出去
            ctx.channel().writeAndFlush(responseByteBuf).addListener((future) -> {
                if (future.isSuccess()) {
                    System.err.println("回复客户端成功!");
                } else {
                    System.err.println("回复客户端失败!");
                }
            });
        }
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }
}
