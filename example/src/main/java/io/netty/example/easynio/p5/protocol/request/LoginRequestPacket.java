package io.netty.example.easynio.p5.protocol.request;

import io.netty.example.easynio.p3.protocol.command.Command;
import io.netty.example.easynio.p5.protocol.Packet;
import lombok.Data;

@Data
public class LoginRequestPacket extends Packet {
    private String userId;
    private String username;
    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
