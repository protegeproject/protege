package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

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
public class OWLDataRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLDataProperty, OWLDatatype> {

    private RestrictionCreator<OWLDataProperty, OWLDatatype> some;
    private RestrictionCreator<OWLDataProperty, OWLDatatype> only;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype> min;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype> exactly;
    private CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype> max;
    

    protected List<RestrictionCreator<OWLDataProperty, OWLDatatype>> createTypes() {
        List<RestrictionCreator<OWLDataProperty, OWLDatatype>> types = new ArrayList<RestrictionCreator<OWLDataProperty, OWLDatatype>>();

        types.add(some = new RestrictionCreator<OWLDataProperty, OWLDatatype>("Some (existential)") {
            public void createRestrictions(Set<OWLDataProperty> properties, Set<OWLDatatype> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLDataProperty prop : properties) {
                    for (OWLDatatype filler : fillers) {
                        result.add(getDataFactory().getOWLDataSomeValuesFrom(prop, filler));
                    }
                }
            }
        });
        types.add(only = new RestrictionCreator<OWLDataProperty, OWLDatatype>("Only (universal)") {
            public void createRestrictions(Set<OWLDataProperty> properties, Set<OWLDatatype> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLDataProperty prop : properties) {
                    if (fillers.isEmpty()) {
                        return;
                    }
                    OWLDatatype filler = fillers.iterator().next();
                    result.add(getDataFactory().getOWLDataAllValuesFrom(prop, filler));
                }
            }
        });
        types.add(min = new CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype>("Min (min cardinality)") {
            public OWLClassExpression createRestriction(OWLDataProperty prop, OWLDatatype filler, int card) {
                return getDataFactory().getOWLDataMinCardinality(card, prop, filler);
            }
        });
        types.add(exactly = new CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype>("Exactly (exact cardinality)") {
            public OWLClassExpression createRestriction(OWLDataProperty prop, OWLDatatype filler, int card) {
                return getDataFactory().getOWLDataExactCardinality(card, prop, filler);
            }
        });
        types.add(max = new CardinalityRestrictionCreator<OWLDataProperty, OWLDatatype>("Max (max cardinality)") {
            public OWLClassExpression createRestriction(OWLDataProperty prop, OWLDatatype filler, int card) {
                return getDataFactory().getOWLDataMaxCardinality(card, prop, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLDatatype> createFillerSelectorPanel() {
        return new OWLDataTypeSelectorPanel(getOWLEditorKit());
    }


    protected AbstractHierarchySelectorPanel<OWLDataProperty> createPropertySelectorPanel() {
        return new OWLDataPropertySelectorPanel(getOWLEditorKit());
    }


    public boolean setDescription(OWLClassExpression description) {
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


    class AcceptableExpressionFilter extends OWLClassExpressionVisitorAdapter {
        private boolean isAcceptable = false;
        private OWLDataProperty p;
        private OWLDatatype f;
        private RestrictionCreator t;
        private int cardinality = -1;

        private void handleRestriction(OWLQuantifiedRestriction<OWLDataRange, OWLDataPropertyExpression, OWLDataRange>  r) {
            if (!r.getProperty().isAnonymous() && r.getFiller().isDatatype()){
                p = r.getProperty().asOWLDataProperty();
                f = r.getFiller().asOWLDatatype();
                isAcceptable = true;
            }
        }

        public void visit(OWLDataSomeValuesFrom r) {
            t = some;
            handleRestriction(r);
        }

        public void visit(OWLDataAllValuesFrom r) {
            t = only;
            handleRestriction(r);
        }

        public void visit(OWLDataMinCardinality r) {
            t = min;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLDataExactCardinality r) {
            t = exactly;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLDataMaxCardinality r) {
            t = max;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }
    }
}