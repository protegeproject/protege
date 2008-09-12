package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import java.util.ArrayList;
import java.util.List;
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
 * Date: Sep 11, 2008<br><br>
 */
public class OWLObjectRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLObjectProperty, OWLClass> {

    public OWLObjectRestrictionCreatorPanel(OWLEditorKit eKit) {
        super(eKit);
    }


    protected List<RestrictionCreator<OWLObjectProperty, OWLClass>> createTypes() {
        List<RestrictionCreator<OWLObjectProperty, OWLClass>> types = new ArrayList<RestrictionCreator<OWLObjectProperty, OWLClass>>();

        types.add(new RestrictionCreator<OWLObjectProperty, OWLClass>("Some (existential)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLDescription> result) {
                for (OWLObjectProperty prop : properties) {
                    for (OWLClass filler : fillers) {
                        result.add(getDataFactory().getOWLObjectSomeRestriction(prop, filler));
                    }
                }
            }
        });
        types.add(new RestrictionCreator<OWLObjectProperty, OWLClass>("Only (universal)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLDescription> result) {
                for (OWLObjectProperty prop : properties) {
                    if (fillers.isEmpty()) {
                        return;
                    }
                    OWLDescription filler;
                    if (fillers.size() > 1) {
                        filler = getDataFactory().getOWLObjectUnionOf(fillers);
                    }
                    else {
                        filler = fillers.iterator().next();
                    }
                    result.add(getDataFactory().getOWLObjectAllRestriction(prop, filler));
                }
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Min (min cardinality)") {
            public OWLDescription createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMinCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Exactly (exact cardinality)") {
            public OWLDescription createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectExactCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Max (max cardinality)") {
            public OWLDescription createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMaxCardinalityRestriction(prop, card, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLClass> getFillerSelectorPanel() {
        return new OWLClassSelectorPanel(getOWLEditorKit());
    }


    protected AbstractHierarchySelectorPanel<OWLObjectProperty> getPropertySelectorPanel() {
        return new OWLObjectPropertySelectorPanel(getOWLEditorKit());
    }
}
