package org.superbiz.moviefun.sts;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserPreferences {

    private String language;
    private String jug;
    private String creditCard;

    public UserPreferences(String language, String jug, String creditCard) {
        this.language = language;
        this.jug = jug;
        this.creditCard = jug;
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
