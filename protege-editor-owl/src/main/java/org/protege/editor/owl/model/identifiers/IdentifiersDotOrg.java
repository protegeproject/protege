package org.protege.editor.owl.model.identifiers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
public class IdentifiersDotOrg {

    private static Logger logger = LoggerFactory.getLogger(IdentifiersDotOrg.class);

    @Nonnull
    private final HttpClient client;

    @Nonnull
    private final ObjectMapper objectMapper;

    private Multimap<String, IdoNamespace> byPrefix = HashMultimap.create();

    private IdentifiersDotOrg(@Nonnull HttpClient client,
                              @Nonnull ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }


    public static IdentifiersDotOrg create() {
        HttpClient client = createClient();
        ObjectMapper objectMapper = createObjectMapper();
        IdentifiersDotOrg identifiersDotOrg = new IdentifiersDotOrg(client, objectMapper);
        identifiersDotOrg.buildCache();
        return identifiersDotOrg;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new GuavaModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private void buildCache() {
        getCollections().forEach(c -> {
            String lowerCasePrefix = c.getPrefix().toLowerCase();
            byPrefix.put(lowerCasePrefix, c);
        });
    }


    @Nonnull
    public Optional<IdoNamespace> getCollection(@Nonnull String compactId) {
        String[] split = compactId.split(":");
        if(split.length != 2) {
            return Optional.empty();
        }
        String prefix = split[0];
        String id = split[1];
        return byPrefix.get(prefix.toLowerCase())
                .stream()
                .filter(c -> id.matches(c.getPattern()) || compactId.matches(c.getPattern()))
                .findFirst();
    }

    @Nonnull
    public Optional<IdoResolvedResource> resolveCompactId(@Nonnull String compactId) {
        String url = String.format("https://resolver.api.identifiers.org/%s", compactId);
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream contentInputStream = response.getEntity().getContent();
                IdoResponse idoResponse = objectMapper.readValue(contentInputStream, IdoResponse.class);
                JsonNode payload = idoResponse.getPayload();
                JsonNode resolvedResourcesNode = payload.path("resolvedResources");
                List<IdoResolvedResource> resolvedResources = objectMapper.convertValue(resolvedResourcesNode, new TypeReference<List<IdoResolvedResource>>(){});
                return resolvedResources.stream().findFirst();
            }
            else {
                logger.debug("[IdentifiersDotOrg] Error code returned by identifiers.org {}", response.getStatusLine());
                return Optional.empty();
            }
        } catch(IOException e) {
            logger.warn("Error resolving compact id at identifiers.org", e);
            return Optional.empty();
        }
    }

    @Nonnull
    public List<IdoNamespace> getCollections() {
        try {
            ImmutableList.Builder<IdoNamespace> builder = ImmutableList.builder();
            // Retrieve the first page
            int pageSize = 1000;
            String url = String.format("https://registry.api.identifiers.org/restApi/namespaces?page=0&size=%s", pageSize);
            while(url != null) {
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream contentInputStream = response.getEntity().getContent();
                    JsonNode idoResponse = objectMapper.readValue(contentInputStream, JsonNode.class);
                    // The namespaces list is an embedded resource that is paged
                    JsonNode namespaces = idoResponse.path("_embedded").path("namespaces");
                    List<IdoNamespace> collections = objectMapper.convertValue(namespaces, new TypeReference<List<IdoNamespace>>(){});
                    builder.addAll(collections);
                    // Get the next page, if there is one
                    url = objectMapper.convertValue(idoResponse.path("_links").path("next").path("href"), String.class);
                }
                else {
                    break;
                }
            }
            return builder.build();
        } catch(IOException e) {
            logger.warn("[IdentifiersDotOrg] Error retrieving identifiers.org namespaces: {}", e.getMessage());
            return ImmutableList.of();
        }
    }

    @Nonnull
    private static HttpClient createClient() {
        return HttpClientBuilder.create().build();


    }
}
