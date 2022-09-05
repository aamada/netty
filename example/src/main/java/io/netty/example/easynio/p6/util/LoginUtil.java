package io.netty.example.easynio.p6.util;

import io.netty.channel.Channel;
import io.netty.example.easynio.p6.attribute.Attributes;
import io.netty.util.Attribute;

public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }
    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }
}
