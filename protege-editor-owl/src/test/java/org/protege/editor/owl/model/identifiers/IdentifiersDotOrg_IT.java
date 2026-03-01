package org.protege.editor.owl.model.identifiers;

import org.apache.http.HttpStatus;
import org.junit.Assume;
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
    public void shouldRetrieveNamespaces() {
        Optional<IdoNamespace> collection = identifiersDotOrg.getCollection("EFO:0008307");
        Assume.assumeTrue(identifiersDotOrg.getLastStatus() == HttpStatus.SC_OK);
        assertThat(collection.isPresent(), is(true));
    }

    @Test
    public void shouldNotRetriveCollection() {
        Optional<IdoNamespace> collection = identifiersDotOrg.getCollection("WRONG_PREFIX:1234");
        assertThat(collection.isPresent(), is(false));
    }

    @Test
    public void shouldResolveCompactId() {
        String compactId = "EFO:0008307";
        Optional<IdoResolvedResource> response = identifiersDotOrg.resolveCompactId(compactId);
        Assume.assumeTrue(identifiersDotOrg.getLastStatus() == HttpStatus.SC_OK);
        assertThat(response.isPresent(), is(true));
        IdoResolvedResource theResponse = response.orElseThrow(RuntimeException::new);
        assertThat(theResponse.getCompactIdentifierResolvedUrl(), is(not(isEmptyString())));
    }

}
