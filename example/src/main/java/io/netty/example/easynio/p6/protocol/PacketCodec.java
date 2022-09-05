package io.netty.example.easynio.p6.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p6.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p6.protocol.request.MessageRequestPacket;
import io.netty.example.easynio.p6.protocol.response.LoginResponsePacket;
import io.netty.example.easynio.p6.protocol.response.MessageResponsePacket;
import io.netty.example.easynio.p6.serialize.Serializer;
import io.netty.example.easynio.p6.serialize.impl.JSONSerializer;

import java.util.HashMap;
import java.util.Map;

public class PacketCodec {
    public static final int MAGIC_NUMBER = 0x12345678;
    public static final PacketCodec INSTANCE = new PacketCodec();
    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    public PacketCodec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);
        packetTypeMap.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(Command.MESSAGE_RESPONSE, MessageResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlogrithm(), serializer);
    }

    public void encode(ByteBuf byteBuf, Packet packet) {
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlogrithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public Packet decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        byteBuf.skipBytes(1);
        byte serializeAlgorithm = byteBuf.readByte();
        byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Class<? extends Packet> requestType = packetTypeMap.get(command);
        Serializer serializer = serializerMap.get(serializeAlgorithm);
        if (requestType!=null&&serializer!=null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }
}