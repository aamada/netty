package io.netty.example.easynio.p2.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println(LocalDateTime.now() + "：客户端写出数据");
        // 得到返回的字符串
        ByteBuf buffer = getByteBuf(ctx);
        // 得到channel， 从channel中写数据出去
        ctx.channel().writeAndFlush(buffer);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 得到一个字符串的字节数组
        byte[] bytes = "你好！陈爷!".getBytes(StandardCharsets.UTF_8);
        // 拿到一个buffer容器
        ByteBuf buffer = ctx.alloc().buffer();
        // 将字节数组写入至buffer中去
        buffer.writeBytes(bytes);
        // 返回
        return buffer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.err.println(LocalDateTime.now() + "：客户端读取数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
    }
}
