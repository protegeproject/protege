package org.protege.editor.core.ui.view;

import com.google.common.base.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26/01/16
 */
public class ViewMode {

    public static final ViewMode ASSERTED = new ViewMode("Asserted");

    public static final ViewMode INFERRED = new ViewMode("Inferred");

    private final String name;

    public ViewMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ViewMode)) {
            return false;
        }
        ViewMode other = (ViewMode) obj;
        return this.name.equals(other.name);
    }


    @Override
    public String toString() {
        return name;
    }
}
