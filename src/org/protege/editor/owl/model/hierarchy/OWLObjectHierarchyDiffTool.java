package org.protege.editor.owl.model.hierarchy;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLObject;


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
