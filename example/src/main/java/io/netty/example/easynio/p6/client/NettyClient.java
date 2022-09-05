package io.netty.example.easynio.p6.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.easynio.p6.client.handler.LoginResponseHandler;
import io.netty.example.easynio.p6.client.handler.MessageResponseHandler;
import io.netty.example.easynio.p6.codec.PacketDecoder;
import io.netty.example.easynio.p6.codec.PacketEncoder;
import io.netty.example.easynio.p6.codec.Spliter;
import io.netty.example.easynio.p6.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p6.protocol.request.MessageRequestPacket;
import io.netty.example.easynio.p6.util.LoginUtil;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int retry = 5;
    private static final String host = "127.0.0.1";
    private static final int port = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new LoginResponseHandler());
                        ch.pipeline().addLast(new MessageResponseHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        conect(bootstrap, host, port, retry);
    }

    private static void conect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(LocalDateTime.now() + "：连接成功， 启动控制台线程!!!");
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.out.println("重试次数用完， 放弃连接！");
            } else {
                int order = (NettyClient.retry - retry) + 1;
                int delay = 1 << order;
                System.out.println(LocalDateTime.now() + "：连接失败， 第" + order + "次重连.....");
                bootstrap.config().group().schedule(() -> conect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                Scanner sc = new Scanner(System.in);
                if (!LoginUtil.hasLogin(channel)) {
                    System.out.println("输入用户名登陆：");
                    String username = sc.nextLine();
                    LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
                    loginRequestPacket.setUsername(username);
                    loginRequestPacket.setPassword("pwd");
                    channel.writeAndFlush(loginRequestPacket);
                    waitForLoginResponse();
                } else {
                    String toUserId = sc.nextLine();
                    String message = sc.nextLine();
                    channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
                }
            }
        }).start();
    }

    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}
