package org.protege.editor.owl.model.obofoundry;

import org.protege.editor.owl.model.util.OboUtilities;
import org.protege.editor.owl.ui.renderer.LinkExtractor;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryLinkExtractor implements LinkExtractor {

    @Nonnull
    private final OboFoundryRegistry registry;

    public OboFoundryLinkExtractor(@Nonnull OboFoundryRegistry registry) {
        this.registry = checkNotNull(registry);
    }

    public static OboFoundryLinkExtractor createLinkExtractor() {
        return new OboFoundryLinkExtractor(OboFoundryRegistryManager.getRegistry());
    }

    @Override
    public Optional<Link> extractLink(String s) {
        String trimmed = s.trim();
        if(!OboUtilities.isOboId(s)) {
            return Optional.empty();
        }
        String [] parts = trimmed.split(":");
        if(parts.length != 2) {
            return Optional.empty();
        }
        String lowerCasePrefix = parts[0].toLowerCase();
        return registry.getOntology(lowerCasePrefix)
                .map(e -> new OboFoundryLink(s, OboUtilities.getOboLibraryIriFromOboId(trimmed), e));
    }
}
