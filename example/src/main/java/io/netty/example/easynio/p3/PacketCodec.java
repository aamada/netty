package io.netty.example.easynio.p3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.example.easynio.p3.protocol.reponse.LoginResponsePacket;
import io.netty.example.easynio.p3.protocol.request.LoginRequestPacket;
import io.netty.example.easynio.p3.serialize.Serializer;
import io.netty.example.easynio.p3.serialize.impl.JSONSerializer;

import java.util.HashMap;
import java.util.Map;

import static io.netty.example.easynio.p3.protocol.command.Command.LOGIN_REQUEST;
import static io.netty.example.easynio.p3.protocol.command.Command.LOGIN_RESPONSE;

public class PacketCodec {
    private static final int MAGIC_NUMBER = 0x12345678;
    public static final PacketCodec INSTANCE = new PacketCodec();
    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializableMap;

    public PacketCodec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(LOGIN_RESPONSE, LoginResponsePacket.class);

        serializableMap = new HashMap<>();
        Serializer serializable = new JSONSerializer();
        serializableMap.put(serializable.getSerializerAlogrithm(), serializable);
    }

    public ByteBuf encode(ByteBufAllocator byteBufAllocator, Packet packet) {
        // 得到一个ByteBuf
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        // 将对象进行序列化
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 写一个魔数, 一个标记
        byteBuf.writeInt(MAGIC_NUMBER);
        // 版本
        byteBuf.writeByte(packet.getVersion());
        // 算法
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlogrithm());
        // 命令
        byteBuf.writeByte(packet.getCommand());
        // 长度
        byteBuf.writeInt(bytes.length);
        // 实体
        byteBuf.writeBytes(bytes);
        // 返回容器
        return byteBuf;
    }
    public Packet decode(ByteBuf byteBuf) {
        // 跳过魔数
        byteBuf.skipBytes(4);
        // 跳过版本号
        byteBuf.skipBytes(1);
        // 序列化算法
        byte serializeAlgorithm = byteBuf.readByte();
        // 命令
        byte command = byteBuf.readByte();
        // 数据包长度
        int length = byteBuf.readInt();
        // 实体长度的一个容器
        byte[] bytes = new byte[length];
        // 将数据从ByteBuf中读取数据到bytes中
        byteBuf.readBytes(bytes);

        // 命令的类型
        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }
    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializableMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {
        return packetTypeMap.get(command);
    }
}
