package org.protege.editor.owl.ui.renderer;

import javax.swing.Icon;

import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLIconProvider {

    public Icon getIcon(OWLObject object);
}
