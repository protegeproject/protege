package org.protege.editor.owl.model.hierarchy.tabbed;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class Edge {

    private final String child;

    private final String parent;

    public Edge(@Nonnull String child, @Nullable String parent) {
        this.child = checkNotNull(child);
        this.parent = parent;
    }

    @Nonnull
    public String getChild() {
        return child;
    }

    public String getChildName() {
        return child;
    }

    /**
     * Gets the parent.
     * @return The parent, or null if there is no parent.
     */
    @Nullable
    public String getParent() {
        return parent;
    }

    public Optional<String> getParentName() {
        return Optional.ofNullable(parent);
    }

    /**
     * Determines if this is an edge to the root node.
     * @return true if the parent is null, which indicates that this is the root node.
     */
    public boolean isRoot() {
        return parent == null;
    }


    public String toString() {
        return MoreObjects.toStringHelper("Edge")
                .add("child", child)
                .add("parent", parent)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(child, parent);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) obj;
        return Objects.equal(this.child, other.child)
                && Objects.equal(this.parent, other.parent);
    }
}
