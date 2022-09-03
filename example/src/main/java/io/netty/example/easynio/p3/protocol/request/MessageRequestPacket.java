package io.netty.example.easynio.p3.protocol.request;

import io.netty.example.easynio.p3.protocol.Packet;
import io.netty.example.easynio.p3.protocol.command.Command;
import lombok.Data;

@Data
public class MessageRequestPacket extends Packet {
    private String message;
    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST;
    }
}
