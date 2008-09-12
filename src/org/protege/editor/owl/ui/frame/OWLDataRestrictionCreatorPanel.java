package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;

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
public class OWLDataRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLDataProperty, OWLDataType> {

    public OWLDataRestrictionCreatorPanel(OWLEditorKit eKit) {
        super(eKit);
    }


    protected List<RestrictionCreator<OWLDataProperty, OWLDataType>> createTypes() {
        List<RestrictionCreator<OWLDataProperty, OWLDataType>> types = new ArrayList<RestrictionCreator<OWLDataProperty, OWLDataType>>();

        types.add(new RestrictionCreator<OWLDataProperty, OWLDataType>("Some (existential)") {
            public void createRestrictions(Set<OWLDataProperty> properties, Set<OWLDataType> fillers,
                                           Set<OWLDescription> result) {
                for (OWLDataProperty prop : properties) {
                    for (OWLDataType filler : fillers) {
                        result.add(getDataFactory().getOWLDataSomeRestriction(prop, filler));
                    }
                }
            }
        });
        types.add(new RestrictionCreator<OWLDataProperty, OWLDataType>("Only (universal)") {
            public void createRestrictions(Set<OWLDataProperty> properties, Set<OWLDataType> fillers,
                                           Set<OWLDescription> result) {
                for (OWLDataProperty prop : properties) {
                    if (fillers.isEmpty()) {
                        return;
                    }
                        OWLDataType filler = fillers.iterator().next();
                    result.add(getDataFactory().getOWLDataAllRestriction(prop, filler));
                }
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Min (min cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataMinCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Exactly (exact cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataExactCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Max (max cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataMaxCardinalityRestriction(prop, card, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLDataType> getFillerSelectorPanel() {
        return new OWLDataTypeSelectorPanel(getOWLEditorKit());
    }


    protected AbstractHierarchySelectorPanel<OWLDataProperty> getPropertySelectorPanel() {
        return new OWLDataPropertySelectorPanel(getOWLEditorKit());
    }
}
