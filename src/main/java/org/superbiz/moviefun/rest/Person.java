package org.superbiz.moviefun.rest;

import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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

    @Produces
    @RequestScoped
    @DecryptedValue
    public String decryptedCreditCard(InjectionPoint injectionPoint) {
        final Claim annotation = injectionPoint.getAnnotated().getAnnotation(Claim.class);
    }

}
