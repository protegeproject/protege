package org.protege.editor.owl.ui;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 09-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLDescriptionComparator implements Comparator<OWLClassExpression> {

    private TypeVisitor typeVisitor;

    private OWLModelManager owlModelManager;

    private OWLClassExpression focusedDescription;


    public OWLDescriptionComparator(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        this.typeVisitor = new TypeVisitor();
    }


    public OWLClassExpression getFocusedDescription() {
        return focusedDescription;
    }


    public void setFocusedDescription(OWLClassExpression focusedDescription) {
        this.focusedDescription = focusedDescription;
    }


    public int compare(OWLClassExpression o1, OWLClassExpression o2) {
        if(focusedDescription != null) {
            if(o1.equals(focusedDescription)) {
                return -1;
            }
            else if(o2.equals(focusedDescription)) {
                return -1;
            }
        }
        o1.accept(typeVisitor);
        int type1 = typeVisitor.getType();
        o2.accept(typeVisitor);
        int type2 = typeVisitor.getType();
        int diff = type1 - type2;
        if (diff != 0) {
            return diff;
        }
        if (owlModelManager != null) {
            return owlModelManager.getRendering(o1).compareToIgnoreCase(owlModelManager.getRendering(o2));
        }
        return -1;
    }


    private class TypeVisitor implements OWLClassExpressionVisitor {

        private int type;


        public int getType() {
            return type;
        }


        public void visit(OWLClass owlClass) {
            type = 1000;
        }


        public void visit(OWLObjectIntersectionOf owlAnd) {
            type = 2000;
        }


        public void visit(OWLObjectComplementOf owlNot) {
            type = 2001;
        }


        public void visit(OWLObjectUnionOf owlOr) {
            type = 2002;
        }


        public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            type = 3001;
        }


        public void visit(OWLObjectHasValue owlObjectValueRestriction) {
            type = 3002;
        }


        public void visit(OWLObjectAllValuesFrom owlObjectAllRestriction) {
            type = 3003;
        }


        public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
            type = 3004;
        }


        public void visit(OWLDataHasValue owlDataValueRestriction) {
            type = 3005;
        }


        public void visit(OWLDataAllValuesFrom owlDataAllRestriction) {
            type = 3006;
        }


        public void visit(OWLObjectMinCardinality desc) {
            type = 3010;
        }


        public void visit(OWLObjectExactCardinality desc) {
            type = 3011;
        }


        public void visit(OWLObjectMaxCardinality desc) {
            type = 3012;
        }


        public void visit(OWLDataMinCardinality desc) {
            type = 3007;
        }


        public void visit(OWLDataExactCardinality desc) {
            type = 3008;
        }


        public void visit(OWLDataMaxCardinality desc) {
            type = 3009;
        }


        public void visit(OWLObjectOneOf owlEnumeration) {
            type = 4000;
        }


        public void visit(OWLObjectHasSelf desc) {
            type = 6000;
        }
    }
}
