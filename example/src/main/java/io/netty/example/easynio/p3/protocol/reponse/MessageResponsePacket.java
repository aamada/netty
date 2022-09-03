package io.netty.example.easynio.p3.protocol.reponse;

import io.netty.example.easynio.p3.protocol.Packet;
import io.netty.example.easynio.p3.protocol.command.Command;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {
    private String message;
    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
