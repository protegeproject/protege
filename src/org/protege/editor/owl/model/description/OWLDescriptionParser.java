package org.protege.editor.owl.model.description;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.SWRLRule;

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


    public boolean isSWRLRuleWellFormed(String expression) throws OWLExpressionParserException;


    public SWRLRule createSWRLRule(String expression) throws OWLExpressionParserException;
}

