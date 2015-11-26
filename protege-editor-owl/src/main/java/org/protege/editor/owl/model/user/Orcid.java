package org.protege.editor.owl.model.user;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 */
public class Orcid {

    private static final Pattern PATTERN = Pattern.compile("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[0-9X]");

    public static final String ORCID_URI_PREFIX = "http://orcid.org/";

    private final String value;

    public Orcid(String value) {
        this.value = checkNotNull(value);
        if(!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid ORCID");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Orcid)) {
            return false;
        }
        Orcid other = (Orcid) obj;
        return this.value.equals(other.value);
    }


    @Override
    public String toString() {
        return toStringHelper("Orcid")
                .addValue(value)
                .toString();
    }

    public URI toUri() {
        return URI.create(ORCID_URI_PREFIX + value);
    }
}
