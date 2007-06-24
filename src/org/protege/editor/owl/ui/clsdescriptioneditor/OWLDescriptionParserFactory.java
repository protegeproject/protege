package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.description.OWLDescriptionParser;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLDescriptionParserFactory {

    OWLDescriptionParser getParser();
}
