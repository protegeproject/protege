package org.protege.editor.owl.model.obofoundry;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryContact_TestCase {

    private static final String THE_LABEL = "The label";

    private static final String THE_EMAIL = "The email";

    private static final String THE_GITHUB_ID = "The github Id";

    private OboFoundryOntologyContact contact;

    @Before
    public void setUp() {
        contact = OboFoundryOntologyContact.get(THE_LABEL, THE_EMAIL, THE_GITHUB_ID);
    }

    @Test
    public void shouldRetrieveLabel() {
        assertThat(contact.getLabel(), Matchers.is(THE_LABEL));
    }

    @Test
    public void shouldRetrieveEmail() {
        assertThat(contact.getEmail(), Matchers.is(THE_EMAIL));
    }

    @Test
    public void shouldRetrieveGithubId() {
        assertThat(contact.getGithubId(), Matchers.is(THE_GITHUB_ID));
    }
}
