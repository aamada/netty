package io.netty.example.easynio.p6.protocol.request;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p6.protocol.Packet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequestPacket extends Packet {
    private String toUserId;
    private String message;

    public MessageRequestPacket(String userId, String message) {
        this.toUserId = userId;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST;
    }
}
