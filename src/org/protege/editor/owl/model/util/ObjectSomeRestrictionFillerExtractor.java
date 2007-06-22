package org.protege.editor.owl.model.util;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

import java.util.Collections;
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
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that can be used to extract the fillers
 * of existential (OWLObjectSomeRestrictions) that restrict
 * a specified property.  This class is a visitor and should
 * be used to visit <code>OWLDescription</code>s - only existential
 * (and hasValue) restrictions are processed.
 * <p/>
 * <p/>
 * Visiting the following descriptions,
 * <code>
 * A
 * p some B
 * C
 * p some (C and D)
 * p only E
 * </code>
 * would return the set <code>{B, (C and D)}</code>.
 * <p/>
 */
public class ObjectSomeRestrictionFillerExtractor extends OWLDescriptionVisitorAdapter {

    private OWLDataFactory dataFactory;

    private OWLObjectProperty objectProperty;

    protected Set<OWLDescription> fillers;


    /**
     * Creates a filler extractor that will extract the fillers of existential
     * restrictions that restrict the specified object property.
     * @param objectProperty The object property.
     */
    public ObjectSomeRestrictionFillerExtractor(OWLDataFactory dataFactory, OWLObjectProperty objectProperty) {
        this.dataFactory = dataFactory;
        this.objectProperty = objectProperty;
        fillers = new HashSet<OWLDescription>();
    }


    public OWLObjectProperty getObjectProperty() {
        return objectProperty;
    }


    /**
     * Resets the extractor and clears the set of fillers that have been
     * accumulated over the course of a series of visits.
     */
    public void reset() {
        fillers.clear();
    }


    /**
     * Gets a set of descriptions that correspond to the fillers of existential
     * restrictions that this visitor has visited.
     * Note that the set returned also includes nominals (i.e. singleton enumerations)
     * containing the the individual used in an <code>OWLObjectValueRestriction</code>,
     * since these are really syntactic sugar for existential restrictions with
     * a nominal filler.
     * @return A <code>Set</code> of <code>OWLDescription</code>s that corresponds to the
     *         fillers of the existential restrictions that were processed.
     */
    public Set<OWLDescription> getFillers() {
        return new HashSet<OWLDescription>(fillers);
    }


    public void visit(OWLObjectSomeRestriction node) {
        if (node.getProperty().equals(objectProperty)) {
            NestedIntersectionFlattener flattener = new NestedIntersectionFlattener();
            node.getFiller().accept(flattener);
            fillers.addAll(flattener.getDescriptions());
        }
    }


    public void visit(OWLObjectValueRestriction node) {
        if (node.getProperty().equals(objectProperty)) {
            fillers.add(dataFactory.getOWLObjectOneOf(Collections.singleton(node.getValue())));
        }
    }
}
