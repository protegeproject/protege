package org.protege.editor.owl.ui.renderer;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface LinkedObjectComponent {

    /**
     * Gets the location of the mouse relative to the
     * rendering cell that it is over.
     */
    public Point getMouseCellLocation();


    public Rectangle getMouseCellRect();


    public void setLinkedObject(OWLObject object);


    public OWLObject getLinkedObject();


    public JComponent getComponent();
}
