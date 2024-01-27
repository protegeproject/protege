package org.protege.editor.owl.ui.tree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTree;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2008<br><br>
 */
public class OWLLinkedObjectTree extends JTree implements LinkedObjectComponent {

    private boolean drawNodeSeperators = false;
    
    private final LinkedObjectComponentMediator mediator;

    public OWLLinkedObjectTree(OWLEditorKit eKit) {
    		mediator = new LinkedObjectComponentMediator(eKit, this);
        setCellRenderer(new OWLObjectTreeCellRenderer(eKit));
        setRowHeight(-1); // gets the height from the renderer - used for wrapped objects                
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawNodeSeperators){
            Color oldColor = g.getColor();
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < getRowCount(); i++) {
                Rectangle rowBounds = getRowBounds(i);
                if (g.getClipBounds().intersects(rowBounds)) {
                    if (getPathForRow(i).getPathCount() == 2) {
                        g.drawLine(0, rowBounds.y, getWidth(), rowBounds.y);
                    }
                }
            }
            g.setColor(oldColor);
        }
    }

    public JComponent getComponent() {
        return this;
    }


    public OWLObject getLinkedObject() {
    		return mediator.getLinkedObject();
    }


    public Point getMouseCellLocation() {
        Point mousePos = getMousePosition();
        if (mousePos == null) {
            return null;
        }
        Rectangle r = getMouseCellRect();
        if (r == null) {
            return null;
        }
        return new Point(mousePos.x - r.x, mousePos.y - r.y);
    }


    public Rectangle getMouseCellRect() {
        Point mousePos = getMousePosition();
        if (mousePos == null) {
            return null;
        }
        int row = getRowForLocation(mousePos.x, mousePos.y);
        if (row == -1) {
            return null;
        }
        Rectangle r = getRowBounds(row);
        return r;
    }


    //    public Object getCellObject();
    public void setLinkedObject(OWLObject object) {
    		mediator.setLinkedObject(object);
    }


    public void setDrawNodeSeperators(boolean drawNodeSeperators) {
        this.drawNodeSeperators = drawNodeSeperators;
    }
}
