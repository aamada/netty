package io.netty.example.easynio.p3.protocol.reponse;

import io.netty.example.easynio.p3.Packet;
import lombok.Data;

import static io.netty.example.easynio.p3.protocol.command.Command.LOGIN_RESPONSE;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String reason;
    @Override
    public Byte getCommand() {
        return LOGIN_RESPONSE;
    }
}
