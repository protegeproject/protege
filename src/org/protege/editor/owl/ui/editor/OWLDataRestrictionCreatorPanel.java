package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

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
 * Date: Dec 23, 2008<br><br>
 */
public class OWLDataRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLDataProperty, OWLDataType> {

    private RestrictionCreator<OWLDataProperty, OWLDataType> some;
    private RestrictionCreator<OWLDataProperty, OWLDataType> only;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDataType> min;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDataType> exactly;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDataType> max;
    

    protected List<RestrictionCreator<OWLDataProperty, OWLDataType>> createTypes() {
        List<RestrictionCreator<OWLDataProperty, OWLDataType>> types = new ArrayList<RestrictionCreator<OWLDataProperty, OWLDataType>>();

        types.add(some = new RestrictionCreator<OWLDataProperty, OWLDataType>("Some (existential)") {
            public void createRestrictions(Set<OWLDataProperty> properties, Set<OWLDataType> fillers,
                                           Set<OWLDescription> result) {
                for (OWLDataProperty prop : properties) {
                    for (OWLDataType filler : fillers) {
                        result.add(getDataFactory().getOWLDataSomeRestriction(prop, filler));
                    }
                }
            }
        });
        types.add(only = new RestrictionCreator<OWLDataProperty, OWLDataType>("Only (universal)") {
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
        types.add(min = new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Min (min cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataMinCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(exactly = new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Exactly (exact cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataExactCardinalityRestriction(prop, card, filler);
            }
        });
        types.add(max = new CardinalityRestrictionCreator<OWLDataProperty, OWLDataType>("Max (max cardinality)") {
            public OWLDescription createRestriction(OWLDataProperty prop, OWLDataType filler, int card) {
                return getDataFactory().getOWLDataMaxCardinalityRestriction(prop, card, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLDataType> createFillerSelectorPanel() {
        return new OWLDataTypeSelectorPanel(getOWLEditorKit());
    }


    protected AbstractHierarchySelectorPanel<OWLDataProperty> createPropertySelectorPanel() {
        return new OWLDataPropertySelectorPanel(getOWLEditorKit());
    }


    public boolean setDescription(OWLDescription description) {
        if (description == null){
            return true;
        }
        final AcceptableExpressionFilter filter = new AcceptableExpressionFilter();
        description.accept(filter);
        if (filter.isAcceptable){
            setProperty(filter.p);
            setFiller(filter.f);
            setType(filter.t);
            if (filter.cardinality >= 0){
                setCardinality(filter.cardinality);
            }
            return true;
        }
        return false;
    }


    class AcceptableExpressionFilter extends OWLDescriptionVisitorAdapter {
        private boolean isAcceptable = false;
        private OWLDataProperty p;
        private OWLDataType f;
        private RestrictionCreator t;
        private int cardinality = -1;

        private void handleRestriction(OWLQuantifiedRestriction<OWLDataPropertyExpression, OWLDataRange> r) {
            if (!r.getProperty().isAnonymous() && r.getFiller().isDataType()){
                p = r.getProperty().asOWLDataProperty();
                f = r.getFiller().asOWLDataType();
                isAcceptable = true;
            }
        }

        public void visit(OWLDataSomeRestriction r) {
            t = some;
            handleRestriction(r);
        }

        public void visit(OWLDataAllRestriction r) {
            t = only;
            handleRestriction(r);
        }

        public void visit(OWLDataMinCardinalityRestriction r) {
            t = min;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLDataExactCardinalityRestriction r) {
            t = exactly;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLDataMaxCardinalityRestriction r) {
            t = max;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }
    }
}