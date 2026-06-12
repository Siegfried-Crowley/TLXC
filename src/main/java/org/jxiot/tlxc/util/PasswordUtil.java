package org.jxiot.tlxc.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
