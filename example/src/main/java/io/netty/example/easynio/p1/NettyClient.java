package io.netty.example.easynio.p1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;

    public static void main(String[] args) {
        NioEventLoopGroup wokerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(wokerGroup).channel(NioSocketChannel.class)
                .attr(AttributeKey.newInstance("clientName"), "nettyClient")
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });

        connect(bootstrap, "127.0.0.1", 8000,MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int maxRetry) {
        bootstrap.connect(host, port).addListener(future -> {
           if (future.isSuccess()) {
               System.err.println("连接成功");
           } else  if (maxRetry == 0) {
               System.err.println("game over !");
           } else {
               int order = (MAX_RETRY - maxRetry) + 1;
               int delay = 1 << order;
               System.err.println("下一次开始");
               bootstrap.config().group().schedule(
                       () -> connect(bootstrap, host, port, maxRetry - 1),
                       delay,
                       TimeUnit.SECONDS);
           }
        });
    }
}
