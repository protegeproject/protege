package org.protege.editor.owl.model.identifiers;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-17
 */
public class IdentifiersDotOrg_IT {

    private IdentifiersDotOrg identifiersDotOrg;

    @Before
    public void setUp() {
        identifiersDotOrg = IdentifiersDotOrg.create();
    }

    @Test
    public void shouldRetrieveCollections() {
        Optional<IdoCollection> collection = identifiersDotOrg.getCollection("EFO:0008307");
        assertThat(collection.isPresent(), is(true));
    }

    @Test
    public void shouldNotRetriveCollection() {
        Optional<IdoCollection> collection = identifiersDotOrg.getCollection("WRONG_PREFIX:1234");
        assertThat(collection.isPresent(), is(false));
    }

    @Test
    public void shouldResolveCompactId() {
        String compactId = "EFO:0008307";
        Optional<IdoValidateResponse> response = identifiersDotOrg.resolveCompactId(compactId);
        assertThat(response.isPresent(), is(true));
        IdoValidateResponse theResponse = response.orElseThrow(RuntimeException::new);
        assertThat(theResponse.getPrefix(), is("EFO"));
        assertThat(theResponse.getIdentifier(), is("0008307"));
        assertThat(theResponse.getUrl(), is(not(isEmptyString())));
    }

}
