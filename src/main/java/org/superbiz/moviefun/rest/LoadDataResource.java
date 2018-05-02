/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun.rest;

import com.github.javafaker.Faker;
import org.superbiz.moviefun.Comment;
import org.superbiz.moviefun.Movie;
import org.superbiz.moviefun.MoviesBean;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Path("load")
public class LoadDataResource {
    @EJB
    private MoviesBean moviesBean;

    @POST
    public void load() {
        addComments(moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005)), 5);
        addComments(moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004)), 6);
        addComments(moviesBean.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003)), 4);
        addComments(moviesBean.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002)), 9);
        addComments(moviesBean.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001)), 2);
        addComments(moviesBean.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001)), 1);
        moviesBean.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));
    }

    private void addComments(final Movie movie, final int nbComments) {

        final Faker faker = new Faker(Locale.ENGLISH);

        for (int i = 0; i < nbComments; i++) {
            final Comment comment = new Comment();
            comment.setTimestamp(faker.date().past(300, TimeUnit.DAYS));
            comment.setAuthor(faker.name().fullName());
            comment.setEmail(faker.internet().emailAddress());
            comment.setComment(faker.chuckNorris().fact());

            moviesBean.addCommentToMovie(movie.getId(), comment);
        }
    }

}