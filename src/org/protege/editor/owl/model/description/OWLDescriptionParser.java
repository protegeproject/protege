package org.protege.editor.owl.model.description;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A parser that can parse class and axiom descriptions.
 */
public interface OWLDescriptionParser {

    public void setOWLModelManager(OWLModelManager owlModelManager);


    /**
     * Checks to see if the specified expression is well formed.
     * @return <code>true</code> if the specified expression is well formed. <code>false</code>
     *         if the expression is not well formed
     */
    public boolean isWellFormed(String expression) throws OWLExpressionParserException;


    /**
     * Parses the specified expression and creates the corresponding class description.
     * @param expression The expression to be parsed.
     * @return The OWLDescription that is represented by the expression.
     * @throws OWLExpressionParserException if the expression cannot be parsed.
     */
    public OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException;


    /**
     * Checks to see if the specified class axiom expression is well formed.
     * @return <code>true</code> if the specified expression is well formed. <code>false</code>
     *         if the expression is not well formed
     */
    public boolean isClassAxiomWellFormed(String expression) throws OWLExpressionParserException;


    /**
     * Parses the specified expression and creates the corresponding class axiom.
     * @param expression The expression to be parsed.
     * @return The OWLDescription that is represented by the expression.
     * @throws OWLExpressionParserException if the expression cannot be parsed.
     */
    public OWLClassAxiom createOWLClassAxiom(String expression) throws OWLExpressionParserException;
}

