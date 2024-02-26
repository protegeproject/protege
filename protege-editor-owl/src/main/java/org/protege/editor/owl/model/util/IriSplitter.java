package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Feb 2018
 */
public class IriSplitter {

    /**
     * Gets the index where an IRI short name should start from.
     * @param iriString The string representing the IRI.
     * @return The short name start index or -1 if there is no appropriate index.
     */
    private int getShortNameIndex(@Nonnull String iriString) {
        try {
            URI uri = new URI(iriString);
            String fragment = uri.getFragment();
            if(fragment != null) {
                int hashIndex = checkNotNull(iriString).lastIndexOf("#");
                if(hashIndex != -1 && hashIndex + 1 < iriString.length()) {
                    return hashIndex + 1;
                }
            }
            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                int slashIndex = iriString.lastIndexOf("/");
                if(slashIndex != -1 && slashIndex + 1 < iriString.length()) {
                    return slashIndex + 1;
                }
            }
            return -1;
        } catch (URISyntaxException e) {
            return -1;
        }
    }

    /**
     * Gets the suffix from an IRI.  This is either the fragment (the bit after the # character) or it is
     * the last path element (the bit after the last slash character).
     * @return The
     */
    public Optional<String> getShortName(@Nonnull IRI iri) {
        String iriString = checkNotNull(iri).toString();
        int shortNameIndex = getShortNameIndex(iriString);
        if(shortNameIndex == -1) {
            return Optional.empty();
        }
        return Optional.of(iriString.substring(shortNameIndex));
    }

    public Optional<String> getShortNamePrefix(@Nonnull IRI iri) {
        String iriString = checkNotNull(iri).toString();
        int shortNameIndex = getShortNameIndex(iriString);
        if(shortNameIndex == -1) {
            return Optional.empty();
        }
        return Optional.of(iriString.substring(0, shortNameIndex));
    }
}
