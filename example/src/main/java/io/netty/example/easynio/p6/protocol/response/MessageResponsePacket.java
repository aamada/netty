package io.netty.example.easynio.p6.protocol.response;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p6.protocol.Packet;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {
    private String fromUserId;
    private String fromUserName;
    private String message;
    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
