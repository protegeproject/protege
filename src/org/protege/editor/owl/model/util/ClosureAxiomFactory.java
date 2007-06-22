package org.protege.editor.owl.model.util;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;

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
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClosureAxiomFactory extends ObjectSomeRestrictionFillerExtractor {

    protected OWLDataFactory owlDataFactory;


    public ClosureAxiomFactory(OWLDataFactory dataFactory, OWLObjectProperty objectProperty,
                               OWLDataFactory owlDataFactory) {
        super(dataFactory, objectProperty);
        this.owlDataFactory = owlDataFactory;
    }


    /**
     * Gets a universal restriction (<code>OWLObjectAllRestriction</code>) that
     * closes off the existential restrictions that have been visited by this
     * visitor.  For example, if the visitor had visited p some A, p some B, then
     * the restriction p only (A or B) would be returned.
     * @return A universal restriction that represents a closure axiom for visited
     *         restrictions, or <code>null</code> if no existential restrictions have been
     *         visited by this visitor and a universal closure axiom therefore doesn't make
     *         sense.
     */
    public OWLObjectAllRestriction getClosureAxiom() {
        Set<OWLDescription> descriptions = getFillers();
        if (descriptions.isEmpty()) {
            return null;
        }
        else {
            if (descriptions.size() == 1) {
                return owlDataFactory.getOWLObjectAllRestriction(getObjectProperty(), descriptions.iterator().next());
            }
            else {
                return owlDataFactory.getOWLObjectAllRestriction(getObjectProperty(),
                                                                 owlDataFactory.getOWLObjectUnionOf(descriptions));
            }
        }
    }
}
