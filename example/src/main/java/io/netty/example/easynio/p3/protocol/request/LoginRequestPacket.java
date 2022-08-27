package io.netty.example.easynio.p3.protocol.request;

import io.netty.example.easynio.p3.Packet;
import lombok.Data;

import static io.netty.example.easynio.p3.protocol.command.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {
    private String userId;
    private String username;
    private String password;
    @Override
    public Byte getCommand() {
        return LOGIN_REQUEST;
    }
}
