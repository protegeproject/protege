package org.protege.editor.owl.model.prefix;

import com.google.common.collect.ImmutableSortedMap;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.Namespaces;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static org.semanticweb.owlapi.vocab.Namespaces.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Oct 2017
 */
public final class PrefixedNameRenderer {

    private final ImmutableSortedMap<String, String> prefix2PrefixNameMap;

    /**
     * Constructs a PrefixedNameRenderer.  The specified map maps prefixes to prefix names.  Note that
     * this is the inverse of the map specified in the {@link #get(Map)} method!
     */
    private PrefixedNameRenderer(@Nonnull ImmutableSortedMap<String, String> prefix2PrefixNameMap) {
        this.prefix2PrefixNameMap = checkNotNull(prefix2PrefixNameMap);
    }

    /**
     * Creates a {@link PrefixedNameRenderer} using the specific map that maps prefixes to prefix names e.g.
     * "http://www.w3.org/2002/07/owl#" -> "owl:"
     * @param prefixName2PrefixMap The prefix to prefix name map.
     * @return A render that will render prefixed names based on the specified map.
     */
    public static PrefixedNameRenderer get(@Nonnull Map<String, String> prefixName2PrefixMap) {
        Comparator<String> stringLengthReversed = comparing(String::length).thenComparing(naturalOrder()).reversed();
        ImmutableSortedMap.Builder<String, String> builder = ImmutableSortedMap.orderedBy(stringLengthReversed);
        prefixName2PrefixMap.forEach(builder::put);
        return new PrefixedNameRenderer(builder.build());
    }

    /**
     * Gets the prefixed name for the specified IRI, or the IRI as a quoted IRI if there is no known prefix for
     * the specified IRI.
     * @param iri The IRI.
     * @return The prefixed name.
     */
    public String getPrefixedNameOrQuotedIri(@Nonnull IRI iri) {
        final String iriString = iri.toString();
        return prefix2PrefixNameMap.entrySet().stream()
                                   // Match prefix
                                   .filter(e -> iriString.startsWith(e.getKey()))
                                   // Concatenate prefix name with local name
                                   .map(e -> e.getValue() + iriString.substring(e.getKey().length()))
                                   .findFirst()
                                   // Return quoted IRI if we can't find a prefix match
                                   .orElse(iri.toQuotedString());
    }


    /**
     * Creates a new empty {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {

        private Map<String, String> prefix2PrefixNameMap = new HashMap<>();

        /**
         * Adds the specified prefix name prefix binding to this builder.
         * @param prefixName The prefix name e.g. "owl:"
         * @param prefix The prefix e.g. "http://www.w3.org/2002/07/owl#"
         */
        public Builder withPrefix(@Nonnull String prefixName, @Nonnull String prefix) {
            checkNotNull(prefixName);
            checkNotNull(prefix);
            if(!prefixName.endsWith(":")) {
                throw new IllegalArgumentException("Prefix name must end with a colon character");
            }
            prefix2PrefixNameMap.put(prefix, prefixName);
            return this;
        }

        /**
         * Adds the predefined OWL prefixes, owl:, rdfs:, rdf: and xsd:
         */
        public Builder withOwlPrefixes() {
            with(OWL);
            with(RDFS);
            with(RDF);
            with(XSD);
            return this;
        }

        /**
         * Adds well known prefixes (defined in the {@link Namespaces} enum in the OWL API) to this renderer.
         */
        public Builder withWellKnownPrefixes() {
            Arrays.stream(Namespaces.values())
                  .forEach(ns -> withPrefix(ns.getPrefixName() + ":", ns.getPrefixIRI()));
            return this;
        }

        /**
         * Builds the {@link PrefixedNameRenderer} from the information contained in this builder.
         */
        public PrefixedNameRenderer build() {
            return PrefixedNameRenderer.get(prefix2PrefixNameMap);
        }

        private void with(Namespaces ns) {
            withPrefix(ns.getPrefixName() + ":", ns.getPrefixIRI());
        }
    }
}
