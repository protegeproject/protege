package org.protege.osgi.framework;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
* Matthew Horridge
* Stanford Center for Biomedical Informatics Research
* 10/09/15
*/
public class SymbolicName {

    private final String name;

    public SymbolicName(String name) {
        this.name = checkNotNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SymbolicName)) {
            return false;
        }
        SymbolicName other = (SymbolicName) obj;
        return this.name.equals(other.name);
    }


    @Override
    public String toString() {
        return toStringHelper("SymbolicName")
                .addValue(name)
                .toString();
    }
}
