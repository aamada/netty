package io.netty.example.easynio.p5.protocol.request;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p5.protocol.Packet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequestPacket extends Packet {
    private String message;

    public MessageRequestPacket(String message) {
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST;
    }
}
