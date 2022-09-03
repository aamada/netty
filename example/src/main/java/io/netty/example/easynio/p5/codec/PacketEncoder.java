package io.netty.example.easynio.p5.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.easynio.p5.protocol.Packet;
import io.netty.example.easynio.p5.protocol.PacketCodec;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        PacketCodec.INSTANCE.encode(out, packet);
    }
}
