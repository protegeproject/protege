package org.protege.editor.core.ui.menu;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 05/03/16
 */
public class PopupMenuId {

    private final String id;

    public PopupMenuId(String id) {
        this.id = checkNotNull(id);
        checkArgument(id.startsWith("["), "PopupMenuIds must start with '['");
        checkArgument(id.endsWith("]"), "PopupMenuIds must end with ']'");
    }

    /**
     * Tests whether an id is a popup menu Id.  Popup menu ids start with '[' and end with ']'
     * @param id The id to test.  Not {@code null}.
     * @return true if the id is a popup menu Id, otherwise false.
     */
    public static boolean isPopupMenuId(String id) {
        return id.startsWith("[") && id.endsWith("]");
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PopupMenuId)) {
            return false;
        }
        PopupMenuId other = (PopupMenuId) obj;
        return this.id.equals(other.id);
    }


    @Override
    public String toString() {
        return toStringHelper("PopupMenuId")
                .addValue(id)
                .toString();
    }
}
