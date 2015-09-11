
package org.protege.osgi.framework;

import java.io.File;
import java.lang.NullPointerException;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BundleInfo_TestCase {

    private BundleInfo bundleInfo;

    @Mock
    private File bundleFile;

    @Mock
    private SymbolicName symbolicName;

    private Optional<Version> version;

    @Mock
    private Version theVersion;
    private Version otherVersion;

    @Before
    public void setUp() throws Exception {
        version = Optional.of(theVersion);
        bundleInfo = new BundleInfo(bundleFile, symbolicName, version);
        otherVersion = mock(Version.class);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_bundleFile_IsNull() {
        new BundleInfo(null, symbolicName, version);
    }

    @Test
    public void shouldReturnSupplied_bundleFile() {
        assertThat(bundleInfo.getBundleFile(), is(this.bundleFile));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_symbolicName_IsNull() {
        new BundleInfo(bundleFile, null, version);
    }

    @Test
    public void shouldReturnSupplied_symbolicName() {
        assertThat(bundleInfo.getSymbolicName(), is(this.symbolicName));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_version_IsNull() {
        new BundleInfo(bundleFile, symbolicName, null);
    }

    @Test
    public void shouldReturnSupplied_version() {
        assertThat(bundleInfo.getVersion(), is(this.version));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(bundleInfo, is(bundleInfo));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(bundleInfo.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(bundleInfo, is(new BundleInfo(bundleFile, symbolicName, version)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_bundleFile() {
        assertThat(bundleInfo, is(not(new BundleInfo(mock(File.class), symbolicName, version))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_symbolicName() {
        assertThat(bundleInfo, is(not(new BundleInfo(bundleFile, mock(SymbolicName.class), version))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_version() {
        assertThat(bundleInfo, is(not(new BundleInfo(bundleFile, symbolicName, Optional.of(mock(Version.class))))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(bundleInfo.hashCode(), is(new BundleInfo(bundleFile, symbolicName, version).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(bundleInfo.toString(), Matchers.startsWith("BundleInfo"));
    }

    @Test
    public void should_compareByVersion() {
        int result = -6;
        when(theVersion.compareTo(otherVersion)).thenReturn(result);
        BundleInfo otherBundleInfo = new BundleInfo(bundleFile, symbolicName, Optional.of(otherVersion));
        assertThat(bundleInfo.compareByVersion(otherBundleInfo), is(result));
    }

    @Test
    public void shouldReturn_true_For_isNewerVersionThan() {
        int result = 5;
        when(theVersion.compareTo(otherVersion)).thenReturn(result);
        BundleInfo otherBundleInfo = new BundleInfo(bundleFile, symbolicName, Optional.of(otherVersion));
        assertThat(bundleInfo.isNewerVersionThan(otherBundleInfo), is(true));
    }

    @Test
    public void shouldReturn_false_For_isNewerVersionThan() {
        int result = 0;
        when(theVersion.compareTo(otherVersion)).thenReturn(result);
        BundleInfo otherBundleInfo = new BundleInfo(bundleFile, symbolicName, Optional.of(otherVersion));
        assertThat(bundleInfo.isNewerVersionThan(otherBundleInfo), is(false));
    }

    @Test
    public void shouldReturn_true_For_isNewerTimeStampThan() {
        when(bundleFile.lastModified()).thenReturn(4l);
        File otherFile = mock(File.class);
        when(otherFile.lastModified()).thenReturn(2l);
        BundleInfo otherBundleInfo = new BundleInfo(otherFile, symbolicName, version);
        assertThat(bundleInfo.isNewerTimestampThan(otherBundleInfo), is(true));
    }

    @Test
    public void shouldReturn_false_For_isNewerTimeStampThan() {
        when(bundleFile.lastModified()).thenReturn(4l);
        File otherFile = mock(File.class);
        when(otherFile.lastModified()).thenReturn(10l);
        BundleInfo otherBundleInfo = new BundleInfo(otherFile, symbolicName, version);
        assertThat(bundleInfo.isNewerTimestampThan(otherBundleInfo), is(false));
    }

}
