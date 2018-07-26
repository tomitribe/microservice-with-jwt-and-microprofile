package org.superbiz.moviefun.rest;

import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class Person {

    @Inject
    @Claim("username")
    private String username;
    @Inject
    @Claim("email")
    private String email;
    @Inject
    @Claim("creditCard")
    private String creditCard;
    @Inject
    @Claim("preferredGenre")
    private String preferredGenre;
    @Inject
    @Claim("age")
    private int age;

}
