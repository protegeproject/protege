package org.protege.editor.owl.ui;

import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

import java.util.Comparator;

/**
 * Compares rows of type {@link OWLPropertyAssertionAxiom}.
 * Comparison priority is as follows:
 * inference > property > object
 * <p>
 * Author: Michael Opitz <br>
 * https://github.com/mmopitz<br>
 * Date: 30-Sep-2024<br><br>
 */
public class OWLPropertyAssertionRowComparator<E extends OWLPropertyAssertionAxiom<?, ?>, PAIR> implements
        Comparator<OWLFrameSectionRow<OWLIndividual, E, PAIR>> {
    private final Comparator<OWLObject> delegate;

    public OWLPropertyAssertionRowComparator(Comparator<OWLObject> delegate) {
        this.delegate = delegate;
    }


    @Override
    public int compare(OWLFrameSectionRow<OWLIndividual, E, PAIR> o1, OWLFrameSectionRow<OWLIndividual, E, PAIR> o2) {
        if (o1.isInferred() && !o2.isInferred()) {
            return 1;
        }
        else if (o2.isInferred() && !o1.isInferred()) {
            return -1;
        }
        int comparison = delegate.compare(o1.getAxiom().getProperty(), o2.getAxiom().getProperty());
        if (comparison != 0) {
            return comparison;
        }
        return delegate.compare(o1.getAxiom().getObject(), o2.getAxiom().getObject());
    }
}
