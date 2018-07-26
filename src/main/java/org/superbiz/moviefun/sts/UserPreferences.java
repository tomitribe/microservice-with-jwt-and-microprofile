package org.superbiz.moviefun.sts;

import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.StaticDESPasswordCipher;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserPreferences {

    private String language;
    private String jug;
    private String creditCard;

    private final PasswordCipher passwordCipher = new StaticDESPasswordCipher();

    public UserPreferences(final String language, final String jug, final String creditCard) {
        this.language = language;
        this.jug = jug;
        this.creditCard = new String(passwordCipher.encrypt(creditCard));
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getJug() {
        return jug;
    }

    public void setJug(String jug) {
        this.jug = jug;
    }
}
