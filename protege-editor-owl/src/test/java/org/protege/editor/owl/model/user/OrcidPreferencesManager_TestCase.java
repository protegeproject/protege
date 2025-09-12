package org.protege.editor.owl.model.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.core.prefs.Preferences;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/01/16
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class OrcidPreferencesManager_TestCase {

    public static final String USER_ORCID_KEY = "user.orcid";

    @Mock
    private Preferences preferences;

    @Mock
    private Orcid orcid;

    private static final String ORCID_STRING = "1234-5678-1234-5678";

    private OrcidPreferencesManager manager;



    @Before
    public void setUp() throws Exception {
        manager = new OrcidPreferencesManager(preferences);
        when(orcid.getValue()).thenReturn(ORCID_STRING);
    }
    @Test
    public void shouldReturnOrcid() {
        when(preferences.getString(eq(USER_ORCID_KEY), anyObject())).thenReturn(ORCID_STRING);
        Optional<Orcid> value = manager.getOrcid();
        assertThat(value.isPresent(), is(true));
        assertThat(value.get().getValue(), is(ORCID_STRING));
    }

    @Test
    public void shouldReturnAbsentForNull() {
        when(preferences.getString(eq(USER_ORCID_KEY), anyObject())).thenReturn(null);
        Optional<Orcid> value = manager.getOrcid();
        assertThat(value.isPresent(), is(false));
    }

    @Test
    public void shouldReturnAbsentForEmptyString() {
        when(preferences.getString(eq(USER_ORCID_KEY), anyObject())).thenReturn("");
        Optional<Orcid> value = manager.getOrcid();
        assertThat(value.isPresent(), is(false));
    }

    @Test
    public void shouldClearOrcid() {
        manager.clearOrcid();
        verify(preferences, times(1)).putString(USER_ORCID_KEY, null);
    }

    @Test
    public void shouldSetOrcid() {
        manager.setOrcid(orcid);
        verify(preferences, times(1)).putString(USER_ORCID_KEY, ORCID_STRING);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionForNullOrcid() {
        manager.setOrcid(null);
    }

}
