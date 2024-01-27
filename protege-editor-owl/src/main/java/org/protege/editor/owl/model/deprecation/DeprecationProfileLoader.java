package org.protege.editor.owl.model.deprecation;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class DeprecationProfileLoader {

    private static final Logger logger = LoggerFactory.getLogger(DeprecationProfileLoader.class);

    public DeprecationProfileLoader() {

    }

    public List<DeprecationProfile> loadProfiles() throws IOException {
            Path deprecationConfDirectory = Paths.get("conf", "deprecation");
            logger.info("Loading deprecation profiles from {}", deprecationConfDirectory.toAbsolutePath());
            return Files.list(deprecationConfDirectory)
                        .filter(file -> Files.isRegularFile(file))
                        .map(Path::toFile)
                        .filter(file -> file.getName().endsWith(".yaml"))
                        .map(file -> {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                                return objectMapper.readValue(file, DeprecationProfile.class);
                            } catch (IOException e) {
                                logger.warn("Could not load deprecation profile at " + file + ".  Cause: {}",
                                            e.getMessage(),
                                            e);
                                return null;
                            }
                        })
                        .filter(profile -> profile != null)
                        .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                        .collect(toList());
    }
}
