package org.protege.editor.owl.model.identifiers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
public class IdentifiersDotOrg {

    private static Logger logger = LoggerFactory.getLogger(IdentifiersDotOrg.class);

    private static final String REST_BASE = "http://identifiers.org/rest";

    @Nonnull
    private final HttpClient client;

    @Nonnull
    private final ObjectMapper objectMapper;

    private Multimap<String, IdoCollection> byPrefix = HashMultimap.create();

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
            c.getSynonyms().forEach(syn -> byPrefix.put(syn, c));
        });
    }


    @Nonnull
    public Optional<IdoCollection> getCollection(@Nonnull String compactId) {
        String[] split = compactId.split(":");
        if(split.length != 2) {
            return Optional.empty();
        }
        String prefix = split[0];
        String id = split[1];
        return byPrefix.get(prefix.toLowerCase())
                .stream()
                .filter(c ->
                        {
                            boolean match = id.matches(c.getPattern()) || compactId.matches(c.getPattern());
                            return match;
                        })
                .findFirst();
    }

    @Nonnull
    public Optional<IdoValidateResponse> resolveCompactId(@Nonnull String compactId) {
        HttpGet httpGet = new HttpGet(REST_BASE + "/identifiers/validate/" + compactId);
        try {
            HttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream contentInputStream = response.getEntity().getContent();
                IdoValidateResponse idoResponse = objectMapper.readValue(contentInputStream, IdoValidateResponse.class);
                return Optional.of(idoResponse);
            }
            else {
                return Optional.empty();
            }
        } catch(IOException e) {
            logger.warn("Error resolving compact id at identifiers.org", e);
            return Optional.empty();
        }
    }

    @Nonnull
    public List<IdoCollection> getCollections() {
        try {
            HttpGet httpGet = new HttpGet(REST_BASE + "/collections");
            HttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream contentInputStream = response.getEntity().getContent();
                return objectMapper.readValue(contentInputStream, new TypeReference<List<IdoCollection>>(){});
            }
            else {
                return ImmutableList.of();
            }
        } catch(IOException e) {
            logger.warn("Error retrieving identifiers.org collections", e);
            return ImmutableList.of();
        }
    }

    @Nonnull
    private static HttpClient createClient() {
        return HttpClientBuilder.create().build();


    }
}
