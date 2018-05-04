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
import java.time.temporal.TemporalField;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Path("load")
public class LoadDataResource {

    @EJB
    private MoviesBean moviesBean;

    @POST
    public void load() {
        final Faker faker = new Faker(Locale.ENGLISH);
        final Random random = new Random(System.nanoTime());

        for (int i = 0 ; i < (5 + random.nextInt(20)) ; i++) {
            addComments(
                    moviesBean.addMovie(
                            new Movie(
                                    faker.book().title(),
                                    faker.book().author(),
                                    faker.book().genre(),
                                    random.nextInt(10),
                                    1960 + random.nextInt(50))),random.nextInt(100));
        }

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