package io.netty.example.easynio.p1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.cjm.utils.PrintUitls;

public class NettyServer {

    private static final int BEGIN_PORT = 8000;

    public static void main(String[] args) {
        PrintUitls.printToConsole("创建bossGroup");
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        PrintUitls.printToConsole("创建workerGroup");
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        PrintUitls.printToConsole("创建ServerBootstrap");
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                .childAttr(clientKey, "clientValue")
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        PrintUitls.printToConsole("触发客户自己写的初始化pipeline事件， 往这里加入一些自定义的handler");
                    }
                });


        bind(serverBootstrap, BEGIN_PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        PrintUitls.printToConsole("绑定去喽");
        serverBootstrap.bind(port).addListener(future -> {
            PrintUitls.printToConsole("绑定完成后， 调用的一个监听器");
            if (future.isSuccess()) {
                PrintUitls.printToConsole("端口[" + port + "]绑定成功!");
            } else {
                PrintUitls.printToConsole("端口[" + port + "]绑定失败!");
                bind(serverBootstrap, port + 1);
            }
        });
    }
}