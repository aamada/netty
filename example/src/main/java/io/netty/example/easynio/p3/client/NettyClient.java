package io.netty.example.easynio.p3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.easynio.p3.protocol.PacketCodec;
import io.netty.example.easynio.p3.protocol.request.MessageRequestPacket;
import io.netty.example.easynio.p3.util.LoginUtil;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup wokerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(wokerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int maxRetry) {
        bootstrap.connect(host, port)
                .addListener(future -> {
                    // 连接成功后的第一个回调事件， 居然是这里
                   if (future.isSuccess()) {
                       System.err.println(LocalDateTime.now() + "连接成功");
                       Channel channel = ((ChannelFuture)future).channel();
                       startConsole(channel);
                   } else if (maxRetry == 0) {
                       System.err.println("连接失败");
                   } else {
                       int order = (MAX_RETRY - maxRetry) + 1;
                       int delay = 1 << order;
                       System.err.println(LocalDateTime.now() + "：连接失败， 第" + order + "次重连");
                       bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, maxRetry - 1), delay, TimeUnit.SECONDS);
                   }
                });
    }

    private static void startConsole(Channel channel) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (LoginUtil.hasLogin(channel)) {
                    System.err.println("输入消息发送至服务端：");
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();

                    MessageRequestPacket packet = new MessageRequestPacket();
                    packet.setMessage(line);
                    ByteBuf byteBuf = PacketCodec.INSTANCE.encode(channel.alloc(), packet);
                    channel.writeAndFlush(byteBuf);
                }
            }
        }).start();
    }
}
