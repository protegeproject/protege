package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 04-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLObjectDropTarget {

    public JComponent getComponent();


    public boolean dropOWLObjects(List<OWLObject> owlObjects, Point pt, int type);


    public OWLModelManager getOWLModelManager();
}
