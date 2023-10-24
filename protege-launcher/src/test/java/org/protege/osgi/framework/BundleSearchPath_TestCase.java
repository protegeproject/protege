package org.protege.osgi.framework;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Version;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-01-25
 */
@RunWith(MockitoJUnitRunner.class)
public class BundleSearchPath_TestCase {

    private BundleInfo t0BundleInfo, t1BundleInfo;

    @Mock
    private SymbolicName symbolicName;

    @Mock
    private File t0File, t1File;

    BundleSearchPath bundleSearchPath;

    @Before
    public void setUp() {
        bundleSearchPath = new BundleSearchPath();
        Version t0Version = new Version(1, 2, 3);
        Version t1Version = new Version(1, 2, 4, "SNAPSHOT");
        when(t0File.getName()).thenReturn("BundleT0.jar");
        when(t1File.getName()).thenReturn("BundleT1.jar");
        t0BundleInfo = new BundleInfo(t0File, symbolicName, Optional.of(t0Version));
        t1BundleInfo = new BundleInfo(t1File, symbolicName, Optional.of(t1Version));
    }

    @Test
    public void shouldNotAddDuplicate() {
        Map<SymbolicName, BundleInfo> map = new HashMap<>();
        bundleSearchPath.addJar(t0BundleInfo, map);
        bundleSearchPath.addJar(t0BundleInfo, map);
        assertThat(map.containsValue(t0BundleInfo), Matchers.is(true));
    }

    @Test
    public void shouldNotAddOlderVersion() {
        Map<SymbolicName, BundleInfo> map = new HashMap<>();
        bundleSearchPath.addJar(t1BundleInfo, map);
        bundleSearchPath.addJar(t0BundleInfo, map);
        assertThat(map.containsValue(t1BundleInfo), Matchers.is(true));
    }

    @Test
    public void shouldAddNewerVersion() {
        Map<SymbolicName, BundleInfo> map = new HashMap<>();
        bundleSearchPath.addJar(t0BundleInfo, map);
        bundleSearchPath.addJar(t1BundleInfo, map);
        assertThat(map.containsValue(t1BundleInfo), Matchers.is(true));
    }
}
