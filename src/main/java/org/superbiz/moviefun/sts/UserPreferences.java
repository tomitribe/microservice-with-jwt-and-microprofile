package org.superbiz.moviefun.sts;

import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.StaticDESPasswordCipher;

public class UserPreferences {

    private String language;
    private String jug;
    private String creditCard;
    private String preferredGenre;
    private int age;

    private final PasswordCipher passwordCipher = new StaticDESPasswordCipher();

    public UserPreferences(){
    }

    public UserPreferences(final String language, final String jug, final String preferredGenre, final int age, final String creditCard) {
        this.language = language;
        this.jug = jug;
        this.creditCard = new String(passwordCipher.encrypt(creditCard));
    }
}
