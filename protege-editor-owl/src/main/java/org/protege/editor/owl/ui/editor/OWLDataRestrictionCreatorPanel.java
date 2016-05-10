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
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

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
        List<RestrictionCreator<OWLDataProperty, OWLDatatype>> types = new ArrayList<>();

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

        private void handleRestriction(OWLQuantifiedRestriction<OWLDataRange>  r) {
            if (!r.getProperty().isAnonymous() && r.getFiller().isDatatype()){
                p = (OWLDataProperty) r.getProperty();
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
