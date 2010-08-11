package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
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
public class OWLObjectRestrictionCreatorPanel extends AbstractRestrictionCreatorPanel<OWLObjectProperty, OWLClass> {

    private RestrictionCreator<OWLObjectProperty, OWLClass> some;
    private RestrictionCreator<OWLObjectProperty, OWLClass> only;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> min;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> exactly;

    private CardinalityRestrictionCreator<OWLObjectProperty, OWLClass> max;


    protected List<RestrictionCreator<OWLObjectProperty, OWLClass>> createTypes() {
        List<RestrictionCreator<OWLObjectProperty, OWLClass>> types = new ArrayList<RestrictionCreator<OWLObjectProperty, OWLClass>>();

        types.add(some = new RestrictionCreator<OWLObjectProperty, OWLClass>("Some (existential)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLObjectProperty prop : properties) {
                    for (OWLClass filler : fillers) {
                        result.add(getDataFactory().getOWLObjectSomeValuesFrom(prop, filler));
                    }
                }
            }
        });
        types.add(only = new RestrictionCreator<OWLObjectProperty, OWLClass>("Only (universal)") {
            public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLClass> fillers,
                                           Set<OWLClassExpression> result) {
                for (OWLObjectProperty prop : properties) {
                    if (fillers.isEmpty()) {
                        return;
                    }
                    OWLClassExpression filler;
                    if (fillers.size() > 1) {
                        filler = getDataFactory().getOWLObjectUnionOf(fillers);
                    }
                    else {
                        filler = fillers.iterator().next();
                    }
                    result.add(getDataFactory().getOWLObjectAllValuesFrom(prop, filler));
                }
            }
        });
        types.add(min = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Min (min cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMinCardinality(card, prop, filler);
            }
        });
        types.add(exactly = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Exactly (exact cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectExactCardinality(card, prop, filler);
            }
        });
        types.add(max = new CardinalityRestrictionCreator<OWLObjectProperty, OWLClass>("Max (max cardinality)") {
            public OWLClassExpression createRestriction(OWLObjectProperty prop, OWLClass filler, int card) {
                return getDataFactory().getOWLObjectMaxCardinality(card, prop, filler);
            }
        });

        return types;
    }


    protected AbstractSelectorPanel<OWLClass> createFillerSelectorPanel() {
        return new OWLClassSelectorPanel(getOWLEditorKit());
    }


    protected AbstractHierarchySelectorPanel<OWLObjectProperty> createPropertySelectorPanel() {
        return new OWLObjectPropertySelectorPanel(getOWLEditorKit());
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
        private OWLObjectProperty p;
        private OWLClass f;
        private RestrictionCreator<OWLObjectProperty, OWLClass> t;
        private int cardinality = -1;

        private void handleRestriction(OWLQuantifiedRestriction<OWLClassExpression, OWLObjectPropertyExpression, OWLClassExpression> r) {
            if (!r.getProperty().isAnonymous() && !r.getFiller().isAnonymous()){
                p = r.getProperty().asOWLObjectProperty();
                f = r.getFiller().asOWLClass();
                isAcceptable = true;
            }
        }

        public void visit(OWLObjectSomeValuesFrom r) {
            t = some;
            handleRestriction(r);
        }

        public void visit(OWLObjectAllValuesFrom r) {
            t = only;
            handleRestriction(r);
        }

        public void visit(OWLObjectMinCardinality r) {
            t = min;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLObjectExactCardinality r) {
            t = exactly;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }

        public void visit(OWLObjectMaxCardinality r) {
            t = max;
            cardinality = r.getCardinality();
            handleRestriction(r);
        }
    }

}
