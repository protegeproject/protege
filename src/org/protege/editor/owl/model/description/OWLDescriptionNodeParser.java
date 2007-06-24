package org.protege.editor.owl.model.description;

import org.semanticweb.owl.model.OWLException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLDescriptionNodeParser extends OWLDescriptionParser {

    public boolean isWellFormedNode(String text) throws OWLExpressionParserException, OWLException;


    public OWLDescriptionNode createOWLDescriptionNode(String text) throws OWLExpressionParserException, OWLException;
}
