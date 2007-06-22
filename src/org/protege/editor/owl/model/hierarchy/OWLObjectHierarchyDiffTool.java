package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owl.model.OWLObject;

import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectHierarchyDiffTool<N extends OWLObject> {

    private OWLObjectHierarchyProvider<N> fromProv;

    private OWLObjectHierarchyProvider<N> toProv;


    public OWLObjectHierarchyDiffTool(OWLObjectHierarchyProvider<N> fromProv, OWLObjectHierarchyProvider<N> toProv) {
        this.fromProv = fromProv;
        this.toProv = toProv;
    }


    public Set<N> getAddedParents(N child) {
        Set<N> parents = new HashSet<N>(toProv.getParents(child));
        parents.removeAll(new HashSet<N>(fromProv.getParents(child)));
        return parents;
    }


    public boolean isChanged(N child) {
        return !getAddedParents(child).isEmpty() || !getRemovedParents(child).isEmpty();
    }


    public Set<N> getRemovedParents(N child) {
        Set<N> parents = new HashSet<N>(fromProv.getParents(child));
        parents.removeAll(new HashSet<N>(toProv.getParents(child)));
        return parents;
    }
}
