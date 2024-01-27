package org.protege.editor.owl.model.classexpression;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLExpressionParserException extends OWLException {

    private int startIndex;

    private int endIndex;

    private boolean owlClassExpected;

    private boolean owlObjectPropertyExpected;

    private boolean owlDataPropertyExpected;

    private boolean owlIndividualExpected;

    private boolean owlDatatypeExpected;

    private boolean owlAnnotationPropertyExpected;

    private Set<String> expectedKeyWords;


    public OWLExpressionParserException(String string, int startIndex, int endIndex,
                                        boolean owlClassExpected,
                                        boolean owlObjectPropertyExpected,
                                        boolean owlDataPropertyExpected,
                                        boolean owlIndividualExpected,
                                        boolean owlDatatypeExpected,
                                        boolean owlAnnotationPropertyExpected,
                                        Set<String> expectedKeyWords) {
        super(string);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.owlClassExpected = owlClassExpected;
        this.owlObjectPropertyExpected = owlObjectPropertyExpected;
        this.owlDataPropertyExpected = owlDataPropertyExpected;
        this.owlIndividualExpected = owlIndividualExpected;
        this.owlDatatypeExpected = owlDatatypeExpected;
        this.owlAnnotationPropertyExpected = owlAnnotationPropertyExpected;
        this.expectedKeyWords = expectedKeyWords;
    }


    public OWLExpressionParserException(Throwable throwable) {
        super(throwable);
    }


    public int getStartIndex() {
        return startIndex;
    }


    public int getEndIndex() {
        return endIndex;
    }


    public boolean isOWLClassExpected() {
        return owlClassExpected;
    }


    public boolean isOWLObjectPropertyExpected() {
        return owlObjectPropertyExpected;
    }


    public boolean isOWLDataPropertyExpected() {
        return owlDataPropertyExpected;
    }


    public boolean isOWLIndividualExpected() {
        return owlIndividualExpected;
    }


    public boolean isDatatypeExpected() {
        return owlDatatypeExpected;
    }


    public boolean isAnnotationPropertyExpected() {
        return owlAnnotationPropertyExpected;
    }


    public Set<String> getExpectedKeyWords() {
        return expectedKeyWords;
    }
}
