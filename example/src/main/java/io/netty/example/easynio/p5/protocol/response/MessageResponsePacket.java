package io.netty.example.easynio.p5.protocol.response;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p5.protocol.Packet;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {
    private String message;
    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}
