/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun.sts;

import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Path("token")
@Produces(MediaType.APPLICATION_JSON)
/**
 * This is a mock implementation for delivering JWT. It is meant to be only for testing purpose and should in reality be
 * an actual STS IDP such as Tribestream API Gateway or other identity providers capable of producing JWT
 */
public class STSResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticate(final MultivaluedMap<String, String> formParameters) {

        final String clientId = nullSafeGetFormParameter("client_id", formParameters);
        final String clientSecret = nullSafeGetFormParameter("client_secret", formParameters);
        final String code = nullSafeGetFormParameter("code", formParameters);
        final String grantType = nullSafeGetFormParameter("grant_type", formParameters);
        final String redirectUri = nullSafeGetFormParameter("redirect_uri", formParameters);
        final String refreshToken = nullSafeGetFormParameter("refresh_token", formParameters);
        final String username = nullSafeGetFormParameter("username", formParameters);
        final String password = nullSafeGetFormParameter("password", formParameters);
        final String scope = nullSafeGetFormParameter("scope", formParameters);

        // validate incoming parameters
        {
            final TokenErrorResponse tokenErrorResponse = validateGrantType(grantType);
            if (tokenErrorResponse != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(tokenErrorResponse).build();
            }
        }

        {
            final TokenErrorResponse tokenErrorResponse = validateAttributes(grantType, refreshToken,
                    username, password, clientId, clientSecret, code, scope, redirectUri);
            if (tokenErrorResponse != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(tokenErrorResponse).build();
            }
        }

        final List<String> scopes = new ArrayList<>();

        // validate grants (especially the supported grants)
        if ("password".equalsIgnoreCase(grantType)) {

            // validate the user and client credentials
            if ("hacker".equals(username)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new TokenErrorResponse(
                        "bad_credentials",
                        "Invalid provided credentials"
                )).build();

            } else if ("admin".equals(username)) {
                scopes.add("create");
                scopes.add("update");
                scopes.add("delete");

            } else {
                scopes.add(username);

            }

            final JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .claim("groups", scopes)
                    .claim("username", username)
                    .claim("email", username + "@superbiz.org")
                    .subject(username)
                    .audience("mp-jwt-moviefun")
                    .build();

            final String accessToken;
            try {
                accessToken = "";

                return Response.ok().entity(new TokenResponse(
                        accessToken,
                        "bearer",
                        Math.round(300),
                        null, // refresh not supported
                        StringUtils.join(scopes, ' '))).build();

            } catch (final Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new TokenErrorResponse(
                        "invalid_grant", "An issue occurred while creating the token." // should we disclose the actual error?
                )).build();
            }


        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(new TokenErrorResponse(
               "unsupported_grant_type", grantType + " is not a supported grant type. Only password is supported."
            )).build();
        }

    }

    private TokenErrorResponse validateAttributes(
            final String grantType, final String refreshToken,
            final String username, final String password,
            final String clientId, final String clientSecret,
            final String code, final String scope,
            final String redirectUri) {

        if ("password".equals(grantType)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                new TokenErrorResponse(
                        "missing_credentials",
                        "username and password are required to authenticate a user with the password grant type.");
            }
        }

        // more to do if needed

        return null;
    }

    private TokenErrorResponse validateGrantType(final String grantType) {
        final List<String> grants = Arrays.asList("password", "client_credentials", "refresh_token", "authorization_code");
        if (StringUtils.isEmpty(grantType)) {
            new TokenErrorResponse("grant_type_required", "grant_type is a required parameter");
        }
        if (!grants.contains(grantType)) {
            new TokenErrorResponse(
                    "unsupported_grant_type",
                    grantType + " is not supported. Only supporting " + StringUtils.join(grants, ", "));
        }
        return null;
    }

    private static String nullSafeGetFormParameter(String parameterName, MultivaluedMap<String, String> formParameters) {
        List<String> params = formParameters.get(parameterName);
        return params == null || params.isEmpty() ? null : params.get(0);
    }

}