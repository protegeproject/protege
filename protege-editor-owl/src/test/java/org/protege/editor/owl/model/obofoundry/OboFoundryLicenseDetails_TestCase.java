package org.protege.editor.owl.model.obofoundry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryLicenseDetails_TestCase {

    private static final String THE_LABEL = "The label";

    private static final String THE_LOGO = "The logo";

    private static final String THE_URL = "The Url";

    private OboFoundryLicenseDetails details;

    @Before
    public void setUp() {
        details = OboFoundryLicenseDetails.get(THE_LABEL, THE_LOGO, THE_URL);
    }

    @Test
    public void shouldGetLabel() {
        assertThat(details.getLabel(), Matchers.is(THE_LABEL));
    }

    @Test
    public void shouldGetLogo() {
        assertThat(details.getLogo(), Matchers.is(THE_LOGO));
    }

    @Test
    public void shouldGetUrl() {
        assertThat(details.getUrl(), Matchers.is(THE_URL));
    }
}
