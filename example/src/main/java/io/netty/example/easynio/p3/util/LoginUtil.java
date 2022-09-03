package io.netty.example.easynio.p3.util;

import io.netty.channel.Channel;
import io.netty.example.easynio.p3.attribute.Attributes;
import io.netty.util.Attribute;

public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttribute = channel.attr(Attributes.LOGIN);
        return loginAttribute.get() != null;
    }
}
