package org.superbiz.moviefun.sts;

import org.superbiz.moviefun.utils.Cipher;

public class UserPreferences {

    private String language;
    private String jug;
    private String creditCard;
    private String preferredGenre;
    private int age;

    public UserPreferences() {

    }

    public UserPreferences(final String language, final String jug, final String creditCard) {
        this.language = language;
        this.jug = jug;
        this.creditCard = new String(Cipher.INSTANCE.getPasswordCipher().encrypt(creditCard));
    }

    public String getLanguage() {
        return language;
    }
}
