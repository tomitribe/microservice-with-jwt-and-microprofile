package org.superbiz.moviefun.utils;

import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.StaticDESPasswordCipher;

public enum Cipher {
    INSTANCE;

    final PasswordCipher passwordCipher = new StaticDESPasswordCipher();

    public PasswordCipher getPasswordCipher() {
        return passwordCipher;
    }
}