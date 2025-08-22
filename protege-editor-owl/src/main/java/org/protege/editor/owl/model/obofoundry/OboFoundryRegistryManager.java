package org.protege.editor.owl.model.obofoundry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryRegistryManager {

    private static final Logger logger = LoggerFactory.getLogger(OboFoundryRegistryManager.class);

    private static OboFoundryRegistry registry = null;

    private static OboFoundryRegistry parse() {
        try {
            return OboFoundryRegistryParser.parseRegistryFromStandardLocation();
        } catch(IOException e) {
            try {
                logger.info("Could not parse OBO Foundry registry from {}.  Reason: {}", OboFoundryRegistryParser.getStandardRegistryLocation(), e.getMessage());
                return OboFoundryRegistryParser.parseRegistryFromLocalCopy();
            } catch(IOException e1) {
                logger.info("Could not parse OBO Foundry registry from local copy.  Reason: {}", OboFoundryRegistryParser.getStandardRegistryLocation(), e.getMessage());
                return OboFoundryRegistry.empty();
            }
        }
    }

    @Nonnull
    public static synchronized OboFoundryRegistry getRegistry() {
        if(registry == null) {
            registry = parse();
        }
        return registry;
    }
}
