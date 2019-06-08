/*
 * Tomitribe Confidential
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package org.superbiz.moviefun;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import org.tomitribe.churchkey.Keys;

public class LoadBalancerRegisterService {

    private static final Logger LOGGER = Logger.getLogger(LoadBalancerRegisterService.class.getName());

    private String hostUrl;
    private Boolean acceptAllCertificates;

    // do we need to unregister at the end. Worse case, the TAG will figure out it's offline and yank it from the hosts list
    private Boolean unregisterOnShutdown;
    private String unregisterEndpoint;

    // URL where we gonna register
    private String registerEndpoint;

    // host details
    private Boolean active;
    private Integer weight;
    private String serverUrl;
    private String connectionId;

    // HTTP Signatures credentials
    private String signaturesKeyId;
    private String signaturesKey;
    private String signaturesAlgorithm;
    private String signaturesHeader;
    private String signaturesSignedHeaders;

    // Todo support for Bearer and Basic
    // we need an abstraction for credentials and get them injected so we can based on the type get the associated header


    @PostConstruct
    public void setup() {
        // sanity checks and adjustements
        Objects.requireNonNull(hostUrl,
                "Host URL required. For example: http://localhost:53302 " +
                        "One node of the Tribestream API Gateway cluster is enough. No need to register to all nodes.");
        Objects.requireNonNull(serverUrl,
                "Server URL required so the Tribestream API Gateway can forward traffic to us. For example: http://localhost:8080");
        Objects.requireNonNull(connectionId,
                "Connection ID required so we can add ourselves to the load balancer. For example: api-twitter");
        Objects.requireNonNull(signaturesKeyId, "Key ID for HTTP Signatures authentication is required");
        Objects.requireNonNull(signaturesKey, "Key value for HTTP Signatures authentication is required. Can be a plain String" +
                " if using an HMAC for example. But can also be a PEM if using RSA for instance");
        Objects.requireNonNull(signaturesAlgorithm, "Algorithm for HTTP Signatures authentication is required");

        if (unregisterOnShutdown == null) {
            LOGGER.info("unregisterOnShutdown not set. Adjusting to true.");
            unregisterOnShutdown = true;
        }
        if (active == null) {
            LOGGER.info("active not set. Adjusting to true so the current host is automatically active for the " +
                    "API Connection load balancer.");
            active = true;
        }
        if (acceptAllCertificates == null) {
            LOGGER.info("acceptAllCertificates not set. Adjusting to false. The remote certificate " +
                    "must be in your trust store");
            acceptAllCertificates = false;
        }
        if (weight == null) {
            LOGGER.info("weight not set. Adjusting to 1. If all registered hosts have a weight of 1, they" +
                    " will all receive the same traffic.");
            weight = 1;
        }
        if (signaturesSignedHeaders == null) {
            LOGGER.info("signaturesSignedHeaders not set. Adjusting to '(request-target) date'.");
            signaturesSignedHeaders = "(request-target) date";
        }
        if (signaturesHeader == null) {
            LOGGER.info("signaturesHeader not set. Adjusting to 'Authorization'.");
            signaturesHeader = "Authorization";
        }
        if (registerEndpoint == null) {
            LOGGER.info("registerEndpoint not set. Adjusting to '/tag/api/http/{connectionId}/hosts/register'.");
            registerEndpoint = "/tag/api/http/{connectionId}/hosts/register";
        }

        if (unregisterOnShutdown) {
            if (unregisterEndpoint == null) {
                LOGGER.info("unregisterOnShutdown not set. Adjusting to '/tag/api/http/{connectionId}/hosts/unregister'.");
                unregisterEndpoint = "/tag/api/http/{connectionId}/hosts/unregister";
            }
        }

        // only use plain tomee dependencies here (aka johnzon if really needed and a plain Http URL Connection so
        // we can extract this into a lib anyone can pull into a tomee to connect to TAG.

        final String payload = toJson();
        final Map<String, String> headers = new HashMap<String, String>() {{
            put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US) {{
                setTimeZone(TimeZone.getTimeZone("GMT"));
            }}.format(new Date()));
            put("Digest", toDigest(payload, "SHA-256"));
        }};
        final String signature = toSignature(headers);

        final WebClient webClient = clientFor().path(registerEndpoint, connectionId)
                .accept(APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(signaturesHeader, signature);

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            webClient.header(headerEntry.getKey(), headerEntry.getValue());
        }

        final Response response = webClient.post(payload);

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            // maybe also add the Entity string itself
            throw new IllegalStateException("Could not auto register host to the API Gateway. Status is " + response.getStatus());
        }

        LOGGER.info("Successfully registered into the load balancer group for API Connection " + connectionId);
    }

    @PreDestroy
    public void tearDown() {
        if (!unregisterOnShutdown) {
            return;
        }

        LOGGER.warning("Auto unregister on shutdown not yet implemented.");
    }

    private String toSignature(final Map<String, String> headers) {
        // open source library does not support extra scheme parameter
        final Signature signature = new Signature(signaturesKeyId, signaturesAlgorithm, null, signaturesSignedHeaders.split(" +"));
        final Signer signer = new Signer(toKey(), signature);
        try {
            final URI u = UriBuilder.fromUri(URI.create("http://tempuri")).path(registerEndpoint).buildFromEncoded(connectionId);
            return signer.sign("POST", u.getRawPath(), headers).toString();

        } catch (final IOException e) {
            throw new IllegalStateException("Can't sign register message with HTTP Signatures", e);
        }
    }

    private Key toKey() {
        final Algorithm algorithm = Algorithm.get(signaturesAlgorithm);
        if (algorithm == null) {
            throw new IllegalArgumentException("Unknown signature algorithm " + signaturesAlgorithm);
        }

        if (algorithm.getType() == Mac.class) {
            return new SecretKeySpec(signaturesKey.getBytes(StandardCharsets.UTF_8), signaturesAlgorithm);

        } else if (algorithm.getType() == java.security.Signature.class) {
            try {
                return Keys.decode(signaturesKey.getBytes(StandardCharsets.UTF_8)).getKey();

            } catch (final Exception e) {
                throw new IllegalStateException("Can't read key from PEM " + signaturesKey, e);
            }

        } else {
            throw new IllegalArgumentException("Unknown signature algorithm type " + algorithm.getType());
        }
    }

    private String toDigest(final String payload, final String algorithm) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        digest.update(payload.getBytes(StandardCharsets.UTF_8));
        return algorithm + '=' + new String(Base64.encodeBase64(digest.digest()), StandardCharsets.UTF_8);
    }

    private String toJson() {
        final HostAutoProvisionItem item = new HostAutoProvisionItem();
        item.setActive(active);
        item.setEndpoint(serverUrl);
        item.setWeight(weight);

        final Mapper mapper = new MapperBuilder().setPretty(false).build();
        return mapper.writeObjectAsString(item);
    }

    private WebClient clientFor() {
        final WebClient webClient = WebClient.create(this.hostUrl);

        if (acceptAllCertificates) {
            final HTTPConduit conduit = WebClient.getConfig(webClient).getHttpConduit();

            TLSClientParameters params = conduit.getTlsClientParameters();
            if (params == null) {
                params = new TLSClientParameters();
                conduit.setTlsClientParameters(params);
            }

            params.setTrustManagers(new TrustManager[]{new DumbX509TrustManager()});
            params.setDisableCNCheck(true);
        }

        return webClient;
    }

    public static class DumbX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    // this needs to be updated at the same time as the one in HttpResource. We do no want to create a dependency cause this part
    // will most likely be extracted into a separate library, hence the copy paste
    public static class HostAutoProvisionItem {
        private String endpoint;
        private Boolean active;
        private int weight;

        public HostAutoProvisionItem() {
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(final Boolean active) {
            this.active = active;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(final String endpoint) {
            this.endpoint = endpoint;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(final int weight) {
            this.weight = weight;
        }
    }

    public Boolean getAcceptAllCertificates() {
        return acceptAllCertificates;
    }

    public void setAcceptAllCertificates(final Boolean acceptAllCertificates) {
        this.acceptAllCertificates = acceptAllCertificates;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(final String connectionId) {
        this.connectionId = connectionId;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(final String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getRegisterEndpoint() {
        return registerEndpoint;
    }

    public void setRegisterEndpoint(final String registerEndpoint) {
        this.registerEndpoint = registerEndpoint;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getSignaturesAlgorithm() {
        return signaturesAlgorithm;
    }

    public void setSignaturesAlgorithm(final String signaturesAlgorithm) {
        this.signaturesAlgorithm = signaturesAlgorithm;
    }

    public String getSignaturesHeader() {
        return signaturesHeader;
    }

    public void setSignaturesHeader(final String signaturesHeader) {
        this.signaturesHeader = signaturesHeader;
    }

    public String getSignaturesKey() {
        return signaturesKey;
    }

    public void setSignaturesKey(final String signaturesKey) {
        this.signaturesKey = signaturesKey;
    }

    public String getSignaturesKeyId() {
        return signaturesKeyId;
    }

    public void setSignaturesKeyId(final String signaturesKeyId) {
        this.signaturesKeyId = signaturesKeyId;
    }

    public String getSignaturesSignedHeaders() {
        return signaturesSignedHeaders;
    }

    public void setSignaturesSignedHeaders(final String signaturesSignedHeaders) {
        this.signaturesSignedHeaders = signaturesSignedHeaders;
    }

    public String getUnregisterEndpoint() {
        return unregisterEndpoint;
    }

    public void setUnregisterEndpoint(final String unregisterEndpoint) {
        this.unregisterEndpoint = unregisterEndpoint;
    }

    public Boolean getUnregisterOnShutdown() {
        return unregisterOnShutdown;
    }

    public void setUnregisterOnShutdown(final Boolean unregisterOnShutdown) {
        this.unregisterOnShutdown = unregisterOnShutdown;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(final Integer weight) {
        this.weight = weight;
    }

}

