package org.protege.editor.owl.model.obofoundry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
@RunWith(MockitoJUnitRunner.class)
public class OboFoundryEntry_TestCase {

    private static final String THE_ID = "The Id";

    private static final String THE_TITLE = "The title";

    private static final String THE_PURL = "The purl";

    private static final String THE_DEPICTED_BY = "The depicted by";

    private static final String THE_TRACKER = "The tracker";

    private static final String THE_DOMAIN = "The domain";

    private static final String THE_ACTIVITY_STATUS = "The activity status";

    private static final String THE_DESCRIPTION = "The description";

    private static final String THE_HOMEPAGE = "The homepage";

    private static final boolean IS_OBSOLETE = true;

    @Mock
    private OboFoundryOntologyContact contact;

    @Mock
    private OboFoundryLicenseDetails license;

    private OboFoundryEntry entry;

    @Before
    public void setUp() {
        entry = OboFoundryEntry.get(THE_ID, THE_TITLE, THE_PURL, THE_DEPICTED_BY, contact, license, IS_OBSOLETE, THE_TRACKER, THE_DOMAIN, THE_ACTIVITY_STATUS, THE_DESCRIPTION, THE_HOMEPAGE);
    }

    @Test
    public void shouldGetId() {
        assertThat(entry.getId(), is(THE_ID));
    }

    @Test
    public void shouldGetTitle() {
        assertThat(entry.getTitle(), is(THE_TITLE));
    }

    @Test
    public void shouldGetPurl() {
        assertThat(entry.getOntologyPurl(), is(THE_PURL));
    }

    @Test
    public void shouldGetDepictedBy() {
        assertThat(entry.getDepictedBy(), is(THE_DEPICTED_BY));
    }

    @Test
    public void shouldGetContact() {
        assertThat(entry.getContact(), is(contact));
    }

    @Test
    public void shouldGetLicense() {
        assertThat(entry.getLicense(), is(license));
    }

    @Test
    public void shouldGetObsolete() {
        assertThat(entry.isObsolete(), is(IS_OBSOLETE));
    }

    @Test
    public void shouldGetTracker() {
        assertThat(entry.getTracker(), is(THE_TRACKER));
    }

    @Test
    public void shouldGetDomain() {
        assertThat(entry.getDomain(), is(THE_DOMAIN));
    }

    @Test
    public void shouldGetActivityStatus() {
        assertThat(entry.getActivityStatus(), is(THE_ACTIVITY_STATUS));
    }

    @Test
    public void shouldGetDescription() {
        assertThat(entry.getDescription(), is(THE_DESCRIPTION));
    }

    @Test
    public void shouldGetHomepage() {
        assertThat(entry.getHomepage(), is(THE_HOMEPAGE));
    }
}
