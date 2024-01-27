package org.protege.osgi.framework;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Optional;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.osgi.framework.Version;

import com.google.common.base.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10/09/15
 */
public class BundleInfo {


    private static final int BEFORE_OTHER = -1;

    private static final int AFTER_OTHER = 1;

    private static final int SAME = 0;

    private final File bundleFile;

    private final SymbolicName symbolicName;

    private final Optional<Version> version;

    /**
     * Construct an instance of BundleInfo.
     * @param bundleFile The file that contains the bundle.  Not {@code null}.
     * @param symbolicName The symbolic name of the bundle.  Not {@code null}.
     * @param version The version of the bundle.  Not {@code null}.
     * @throws java.lang.NullPointerException if any parameters are {@code null}.
     */
    public BundleInfo(File bundleFile, SymbolicName symbolicName, Optional<Version> version) {
        this.bundleFile = checkNotNull(bundleFile);
        this.symbolicName = checkNotNull(symbolicName);
        this.version = checkNotNull(version);
    }

    public File getBundleFile() {
        return bundleFile;
    }

    public SymbolicName getSymbolicName() {
        return symbolicName;
    }

    public Optional<Version> getVersion() {
        return version;
    }

    /**
     * Compare this bundle version to an other.
     * @param other The other.
     * @return an integer representing the comparison, as defined in the {@link java.lang.Comparable} interface.
     */
    public int compareByVersion(BundleInfo other) {
        if(version.isPresent()) {
            if(other.version.isPresent()) {
                ComparableVersion thisVersion = new ComparableVersion(version.get().toString());
                ComparableVersion otherVersion = new ComparableVersion(other.version.get().toString());
                return thisVersion.compareTo(otherVersion);
            }
            else {
                return BEFORE_OTHER;
            }
        }
        else {
            if(other.version.isPresent()) {
                return AFTER_OTHER;
            }
            else {
                return SAME;
            }
        }
    }

    /**
     * Determines if this bundle has a newer version than the other bundle.
     * @param other The other bundle.  Not {@code null}.
     * @return <code>true</code> if this bundle is newer than the other bundle, otherwise, <code>false</code>.
     */
    public boolean isNewerVersionThan(BundleInfo other) {
        return this.compareByVersion(other) > 0;
    }

    /**
     * Determines if this bundle is newer than the other bundle.
     * @param other The other bundle.  Not {@code null}.
     * @return <code>true</code> if this bundle is newer than the other bundle, otherwise, <code>false</code>.
     */
    public boolean isNewerTimestampThan(BundleInfo other) {
        return this.bundleFile.lastModified() > other.bundleFile.lastModified();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bundleFile, symbolicName, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BundleInfo)) {
            return false;
        }
        BundleInfo other = (BundleInfo) obj;
        return this.bundleFile.equals(other.bundleFile)
                && this.symbolicName.equals(other.symbolicName)
                && this.version.equals(other.version);
    }


    @Override
    public String toString() {
        return toStringHelper("BundleInfo")
                .addValue(bundleFile)
                .addValue(symbolicName)
                .addValue(version)
                .toString();
    }
}
