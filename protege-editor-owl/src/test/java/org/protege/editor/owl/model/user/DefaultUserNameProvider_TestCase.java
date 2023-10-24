package org.protege.editor.owl.model.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/01/16
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultUserNameProvider_TestCase {

    public static final String USER_NAME = "Matthew";

    private DefaultUserNameProvider provider;


    @Mock
    private UserNameProvider gitRepoUserNameProvider;

    @Mock
    private UserNamePreferencesManager preferencesManager;

    @Mock
    private Properties properties;


    @Before
    public void setUp() throws Exception {
        provider = new DefaultUserNameProvider(gitRepoUserNameProvider, preferencesManager, properties);
    }

    @Test
    public void shouldReturnGitRepositoryUserName() {
        when(preferencesManager.isUseGitUserNameIfAvailable()).thenReturn(true);
        when(gitRepoUserNameProvider.getUserName()).thenReturn(Optional.of("git.repo.user.name"));
        assertThat(provider.getUserName(), is(Optional.of("git.repo.user.name")));
    }

    @Test
    public void shouldReturnPreferencesManagerUserName() {
        Optional<String> userName = Optional.of(USER_NAME);
        when(preferencesManager.getUserName()).thenReturn(userName);
        when(gitRepoUserNameProvider.getUserName()).thenReturn(Optional.empty());
        assertThat(provider.getUserName(), is(userName));
        verify(properties, never()).getProperty(anyString());
    }

    @Test
    public void shouldReturnPropertiesUserName() {
        when(gitRepoUserNameProvider.getUserName()).thenReturn(Optional.empty());
        when(preferencesManager.getUserName()).thenReturn(Optional.empty());
        when(properties.getProperty("user.name")).thenReturn(USER_NAME);
        assertThat(provider.getUserName(), is(Optional.of(USER_NAME)));
        verify(preferencesManager, times(1)).getUserName();
        verify(properties, times(1)).getProperty("user.name");
    }

    @Test
    public void shouldReturnEmpty() {
        when(gitRepoUserNameProvider.getUserName()).thenReturn(Optional.empty());
        Optional<String> empty = Optional.<String>empty();
        when(preferencesManager.getUserName()).thenReturn(empty);
        when(properties.getProperty(anyString())).thenReturn(null);
        assertThat(provider.getUserName().isPresent(), is(false));
    }
}
