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

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("token")
@Produces({"application/json"})
/**
 * This is a mock implementation for delivering JWT. It is meant to be only for testing purpose and should in reality be
 * an actual STS IDP such as Tribestream API Gateway or other identity providers capable of producing JWT
 */
public class STSResource {

    @POST
    @Consumes("application/json")
    public void authenticate(
            @HeaderParam("Authorization") final String authorization,
            final MultivaluedMap<String, String> formParameters,
            @Context final UriInfo uriInfo) {

        final String clientId = nullSafeGetFormParameter("client_id", formParameters);
        final String clientSecret = nullSafeGetFormParameter("client_secret", formParameters);
        final String code = nullSafeGetFormParameter("code", formParameters);
        final String grantType = nullSafeGetFormParameter("grant_type", formParameters);
        final String redirectUri = nullSafeGetFormParameter("redirect_uri", formParameters);
        final String refreshToken = nullSafeGetFormParameter("refresh_token", formParameters);
        final String username = nullSafeGetFormParameter("username", formParameters);
        final String password = nullSafeGetFormParameter("password", formParameters);
        final String scope = nullSafeGetFormParameter("scope", formParameters);



    }

    private static String nullSafeGetFormParameter(String parameterName, MultivaluedMap<String, String> formParameters) {
        List<String> params = formParameters.get(parameterName);
        return params == null || params.isEmpty() ? null : params.get(0);
    }

}