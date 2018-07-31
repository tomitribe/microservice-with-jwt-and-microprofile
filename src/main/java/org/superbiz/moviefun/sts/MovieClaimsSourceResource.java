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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("claims")
public class MovieClaimsSourceResource {

    private static HashMap<String, UserPreferences> data = new HashMap<>();

    static {
        data.put("alex", new UserPreferences("SPANISH", "Guatemala JUG", "Mystery", "3211 1922 4433 1111"));
        data.put("john", new UserPreferences("ENGLISH", "Boston JUG", "Action", "2311 2345 8899 2222"));
        data.put("mark", new UserPreferences("ENGLISH", "Lodon JUG", "Drama", "1122 6543 5858 3333"));
        data.put("nick", new UserPreferences("SPANISH", "Mexico JUG", "Comedy", "7789 8765 1222 4444"));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserPreferences authenticate(final Map<String, String> payload) {
        String username = payload.get("username");
        UserPreferences userPreferences = data.get(username);
        return userPreferences;
    }

}