package org.protege.editor.owl.ui.transfer;

import java.awt.Point;
import java.util.List;

import javax.swing.JComponent;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLObject;


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
