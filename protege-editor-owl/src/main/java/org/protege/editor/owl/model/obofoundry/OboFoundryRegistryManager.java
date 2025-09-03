package org.protege.editor.owl.model.obofoundry;

import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
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
        if (OWLRendererPreferences.getInstance().isUseOnlineLinkExtractors()) {
            try {
                return OboFoundryRegistryParser.parseRegistryFromStandardLocation();
            } catch (IOException e) {
                logger.warn("Could not parse OBO Foundry registry from {}.  Reason: {}",
                    OboFoundryRegistryParser.getStandardRegistryLocation(), e.getMessage());
            }
        }

        try {
            return OboFoundryRegistryParser.parseRegistryFromLocalCopy();
        } catch (IOException e) {
            logger.warn("Could not parse OBO Foundry registry from local copy.  Reason: {}",
                e.getMessage());
            return OboFoundryRegistry.empty();
        }
    }

    @Nonnull
    public static synchronized OboFoundryRegistry getRegistry() {
        if(registry == null) {
            registry = parse();
            int n = registry.getOntologies().size();
            if (n > 0) {
                logger.info("OBO Foundry registry loaded: {} entries", n);
            }
        }
        return registry;
    }
}
