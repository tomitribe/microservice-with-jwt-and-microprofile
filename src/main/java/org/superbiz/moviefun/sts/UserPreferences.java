/*
 *     Licensed to the Apache Software Foundation (ASF) under one or more
 *     contributor license agreements.  See the NOTICE file distributed with
 *     this work for additional information regarding copyright ownership.
 *     The ASF licenses this file to You under the Apache License, Version 2.0
 *     (the "License"); you may not use this file except in compliance with
 *     the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package org.superbiz.moviefun.sts;

import org.superbiz.moviefun.utils.Cipher;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "preferences")
public class UserPreferences {

    private String language;
    private String jug;
    private String creditCard;
    private String preferredGenre;
    private Integer age;
    private Double balance = 300.21;
    private Boolean active = true;

    public UserPreferences() {
    }

    public UserPreferences(final String language, final String jug, final String preferredGenre, final int age, final String creditCard) {
        this.language = language;
        this.jug = jug;
        this.creditCard = new String(Cipher.INSTANCE.getPasswordCipher().encrypt(creditCard));
        this.preferredGenre = preferredGenre;
        this.age = age;
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

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getPreferredGenre() {
        return preferredGenre;
    }

    public void setPreferredGenre(String preferredGenre) {
        this.preferredGenre = preferredGenre;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
