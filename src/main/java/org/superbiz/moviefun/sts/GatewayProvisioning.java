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

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.WebClient;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Startup
@Singleton
public class GatewayProvisioning {
    private WebClient webClient;

    @PostConstruct
    private void config() throws Exception {
        System.out.println("Setting TAG Config");
        final Config config = ConfigProvider.getConfig();
        final String tagHost = config.getOptionalValue("TAG_HOST", String.class).orElse("http://localhost:8080");

        final LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        webClient = WebClient.create(tagHost + "/tag/api", emptyList(), singletonList(loggingFeature), null);

        waitForTag(0);

        oauth2();

        account("john");
        account("mark");
        account("alex");
        account("nick");

        route();
    }

    private void waitForTag(final int count) throws Exception {
        try {
            final Response response =
                    webClient.reset()
                             .path("environment")
                             .header(HttpHeaders.AUTHORIZATION, generateBasicAuth())
                             .get();

            if (response.getStatus() != 200) {
                if (count + count > 600) {
                    System.exit(0);
                }
                TimeUnit.SECONDS.sleep(10);
                waitForTag(count + 10);
            }
        } catch (Exception e) {
            if (count + count > 600) {
//                System.exit(0);
                System.out.println(">>>> Can't provision data because the Gateway is unavailable.");
            }
            TimeUnit.SECONDS.sleep(10);
            waitForTag(count + 10);
        }
    }

    private void oauth2() throws Exception {
        final URL resource = GatewayProvisioning.class.getClassLoader().getResource("profile-oauth2.json");
        final JsonReader reader = Json.createReader(resource.openStream());
        final JsonObject object = reader.readObject();

        webClient.reset()
                 .path("/profile/oauth2/")
                 .header(HttpHeaders.AUTHORIZATION, generateBasicAuth())
                 .type(MediaType.APPLICATION_JSON_TYPE)
                 .accept(MediaType.APPLICATION_JSON_TYPE)
                 .post(object);

        // make sure this is the default profile
        webClient.reset()
                .path("/settings/bulk/write")
                .header(HttpHeaders.AUTHORIZATION, generateBasicAuth())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post("{\n" +
                        "  \"settings\": [\n" +
                        "    {\n" +
                        "      \"key\": \"com.tomitribe.tribestream.container.oauth2.OAuth2Configuration.defaultProfile\",\n" +
                        "      \"value\": \"movies\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
    }

    private void account(final String account) throws Exception {
        final URL resource = GatewayProvisioning.class.getClassLoader().getResource("account-" + account + ".json");
        final JsonReader reader = Json.createReader(resource.openStream());
        final JsonObject object = reader.readObject();

        webClient.reset()
                 .path("/account/")
                 .header(HttpHeaders.AUTHORIZATION, generateBasicAuth())
                 .type(MediaType.APPLICATION_JSON_TYPE)
                 .accept(MediaType.APPLICATION_JSON_TYPE)
                 .post(object);
    }

    private void route() throws Exception {
        final URL resource = GatewayProvisioning.class.getClassLoader().getResource("route.json");
        final JsonReader reader = Json.createReader(resource.openStream());
        final JsonObject object = reader.readObject();

        webClient.reset()
                 .path("/route/")
                 .header(HttpHeaders.AUTHORIZATION, generateBasicAuth())
                 .type(MediaType.APPLICATION_JSON_TYPE)
                 .accept(MediaType.APPLICATION_JSON_TYPE)
                 .post(object);
    }

    private String generateBasicAuth() {
        return "Basic " + new String(Base64.getEncoder().encode(("admin:admin").getBytes()));
    }
}