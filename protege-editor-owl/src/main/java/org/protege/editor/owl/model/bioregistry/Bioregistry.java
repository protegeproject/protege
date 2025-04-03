package org.protege.editor.owl.model.bioregistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Author: Damien Goutte-Gattat<br>
 * University of Cambridge<br>
 * FlyBase Group<br>
 * Date: 03/04/2025
 * <p>
 * A client for the Bioregistry.io API. Not a full-featured client,
 * limited to transforming CURIEs into resolvable links.
 * </p>
 */
public class Bioregistry {

    private static final Logger logger = LoggerFactory.getLogger(Bioregistry.class);

    private static final String API_ENDPOINT = "http://bioregistry.io/api/registry/%s?format=json";

    private static Bioregistry instance;

    private Map<String,Resource> cache = new HashMap<>();
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public static synchronized Bioregistry getInstance() {
        if (instance == null) {
            instance = new Bioregistry();
        }
        return instance;
    }

    public Bioregistry() {
        client = HttpClientBuilder.create().useSystemProperties().build();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String resolve(@Nonnull String id) {
        String items[] = id.split(":", 2);
        if (items.length == 2) {
            return resolve(items[0], items[1]);
        }
        return null;
    }

    public String resolve(@Nonnull String prefix, @Nonnull String localIdentifier) {
        Resource resource = getResource(prefix);
        if (resource != null) {
            return resource.formatURI(localIdentifier);
        }
        return null;
    }

    private Resource getResource(@Nonnull String prefix) {
        if ( !cache.containsKey(prefix)) {
            Resource resource = null;
            HttpGet request = new HttpGet(String.format(API_ENDPOINT, prefix));
            request.addHeader("Accept", "application/json");
            try {
                HttpResponse response = client.execute(request);
                if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream stream = response.getEntity().getContent();
                    resource = objectMapper.readValue(stream, Resource.class);
                }
            } catch (IOException e ) {
                logger.warn("Error when querying the bioregistry for '{}': {}", prefix, e.getMessage());
            }
            cache.put(prefix, resource);
        }
        return cache.get(prefix);
    }
}
