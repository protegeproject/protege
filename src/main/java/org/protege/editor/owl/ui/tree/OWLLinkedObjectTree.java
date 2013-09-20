package org.protege.editor.owl.ui.tree;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;/*
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2008<br><br>
 */
public class OWLLinkedObjectTree extends JTree implements LinkedObjectComponent {

    private OWLObject linkedObject;

    private boolean drawNodeSeperators = false;


    public OWLLinkedObjectTree(OWLEditorKit eKit) {
        setCellRenderer(new OWLObjectTreeCellRenderer(eKit));
        setRowHeight(-1); // gets the height from the renderer - used for wrapped objects        
        LinkedObjectComponentMediator mediator = new LinkedObjectComponentMediator(eKit, this);
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
        return linkedObject;
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
        linkedObject = object;
    }


    public void setDrawNodeSeperators(boolean drawNodeSeperators) {
        this.drawNodeSeperators = drawNodeSeperators;
    }
}
