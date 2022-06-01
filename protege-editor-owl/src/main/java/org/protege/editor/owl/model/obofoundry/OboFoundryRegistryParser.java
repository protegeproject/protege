package org.protege.editor.owl.model.obofoundry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryRegistryParser {

    private static final URI OBO_FOUNDRY_REGISTRY_LOCATION = URI.create("https://obofoundry.org/registry/ontologies.jsonld");

    @Nonnull
    public static URI getStandardRegistryLocation() {
        return OBO_FOUNDRY_REGISTRY_LOCATION;
    }

    @Nonnull
    public static OboFoundryRegistry parseRegistryFromLocalCopy() throws IOException {
        try(InputStream is = new BufferedInputStream(OboFoundryRegistry.class.getResourceAsStream("/obofoundry/obofoundry.registry.json"))) {
            return parseRegistry(is);
        }
    }

    @Nonnull
    public static OboFoundryRegistry parseRegistryFromStandardLocation() throws IOException {
        URL url = OBO_FOUNDRY_REGISTRY_LOCATION.toURL();
        try(BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
            return parseRegistry(bis);
        }
    }

    @Nonnull
    public static OboFoundryRegistry parseRegistry(@Nonnull InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new GuavaModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(inputStream, OboFoundryRegistry.class);
    }
}
