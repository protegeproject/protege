package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Jun-2006<br><br>
 * <p/>
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


    /**
     * Gets the cell object that the mouse is over
     */
//    public Object getCellObject();
    public void setLinkedObject(OWLObject object);


    public OWLObject getLinkedObject();


    public JComponent getComponent();
}
