package io.netty.example.easynio.p5.protocol.response;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p5.protocol.Packet;
import lombok.Data;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String reason;
    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}
