package org.protege.editor.owl.model.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.core.prefs.Preferences;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/01/16
 */
@RunWith(MockitoJUnitRunner.class)
public class UserNamePreferencesManager_TestCase {

    public static final String USER_NAME = "MatthewHorridge";

    public static final String USER_NAME_KEY = "user.name";

    @Mock
    private Preferences preferences;

    private UserNamePreferencesManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new UserNamePreferencesManager(preferences);
    }

    @Test
    public void shouldReturnUserName() {
        when(preferences.getString(eq(USER_NAME_KEY), anyObject())).thenReturn(USER_NAME);
        Optional<String> value = manager.getUserName();
        assertThat(value, is(Optional.of(USER_NAME)));
    }

    @Test
    public void shouldReturnAbsent() {
        when(preferences.getString(eq(USER_NAME_KEY), anyObject())).thenReturn(null);
        Optional<String> value = manager.getUserName();
        assertThat(value.isPresent(), is(false));
    }

    @Test
    public void shouldClearUserName() {
        manager.clearUserName();
        verify(preferences, times(1)).putString(USER_NAME_KEY, null);
    }

    @Test
    public void shouldSetUserName() {
        manager.setUserName(USER_NAME);
        verify(preferences, times(1)).putString(USER_NAME_KEY, USER_NAME);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionForNullUserName() {
        manager.setUserName(null);
    }
}
