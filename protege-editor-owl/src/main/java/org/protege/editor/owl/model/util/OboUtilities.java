package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.coode.owlapi.obo12.parser.OBOVocabulary.OBO_ID_PATTERN;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public class OboUtilities {

    /*
        PN_CHARS_BASE_OBO ::= [A-Z] | [a-z]
        IDSPACE ::= PN_CHARS_BASE_OBO+ ("_" PN_CHARS_BASE_OBO+ )
        LOCALID ::= [0-9]+
        OBO_IDENTIFIER ::= IDSPACE ":" LOCALID
     */
    private static final Pattern OBO_ID_PATTERN = Pattern.compile("(([A-Z]|[a-z])+(_([A-Z]|[a-z])+)?):(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final String IRI_BASE = "http://purl.obolibrary.org/obo/";

    private static final Pattern OBO_ID_IRI_PATTERN = Pattern.compile(Pattern.quote(IRI_BASE) + "(([A-Z]|[a-z])+(_([A-Z]|[a-z])+)?)_(\\d+)");

    private static final int ID_SPACE_GROUP = 1;

    private static final int LOCAL_ID_GROUP = 5;

    /**
     * Determines whether the specified string has the pattern of an OBO Id.
     * @param value The value to be tested.
     * @return true if the value to be tested matches the OBO Id pattern.
     */
    public static boolean isOboId(@Nonnull String value) {
        return OBO_ID_PATTERN.matcher(checkNotNull(value)).matches();
    }

    /**
     * Gets the Iri from the specified OBO Id.
     * @param oboId The OBO Id.  This MUST be a valid OBO Id according to the method {@link #isOboId(String)}
     *              otherwise a {@link RuntimeException} will be thrown.
     * @return The IRI that corresponds to the OBO Id.
     */
    @Nonnull
    public static IRI getIriFromOboId(@Nonnull String oboId) {
        Matcher matcher = OBO_ID_PATTERN.matcher(checkNotNull(oboId));
        if(!matcher.matches()) {
            throw new RuntimeException("Invalid OBO Id");
        }
        MatchResult matchResult = matcher.toMatchResult();
        return IRI.create(IRI_BASE + matchResult.group(ID_SPACE_GROUP) + "_" + matchResult.group(LOCAL_ID_GROUP));
    }

    @Nonnull
    public static Optional<String> getOboIdFromIri(@Nonnull IRI iri) {
        String iriString = iri.toString();
        if(!iriString.startsWith(IRI_BASE)) {
            return Optional.empty();
        }
        Matcher matcher = OBO_ID_IRI_PATTERN.matcher(iriString);
        if(!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(matcher.group(ID_SPACE_GROUP) + ":" + matcher.group(LOCAL_ID_GROUP));
    }
}
