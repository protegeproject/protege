package org.protege.editor.owl.model.hierarchy.property;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLRuntimeException;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public class InferredObjectPropertyHierarchyProvider extends OWLObjectPropertyHierarchyProvider {

    private OWLModelManager mngr;

    public static final String ID = "inferredObjectPropertyHierarchyProvider";


    public InferredObjectPropertyHierarchyProvider(OWLModelManager mngr) {
        super(mngr.getOWLOntologyManager());
        this.mngr = mngr;
    }

    protected OWLReasoner getReasoner() {
        return mngr.getOWLReasonerManager().getCurrentReasoner();
    }

    public Set<OWLObjectProperty> getChildren(OWLObjectProperty objectProperty) {
        try {
            Set<OWLObjectProperty> subs = OWLReasonerAdapter.flattenSetOfSets(getReasoner().getSubProperties(objectProperty));
            subs.remove(objectProperty);
            return subs;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLObjectProperty> getParents(OWLObjectProperty objectProperty) {
        try {
            Set<OWLObjectProperty> supers = OWLReasonerAdapter.flattenSetOfSets(getReasoner().getSuperProperties(objectProperty));
            supers.remove(objectProperty);
            return supers;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLObjectProperty> getEquivalents(OWLObjectProperty objectProperty) {
        try {
            Set<OWLObjectProperty> equivs = getReasoner().getEquivalentProperties(objectProperty);
            equivs.remove(objectProperty);
            return equivs;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }
}
