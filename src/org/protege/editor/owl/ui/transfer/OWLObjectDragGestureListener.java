package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.image.BufferedImage;
import java.util.List;
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
 * Date: 04-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLObjectDragGestureListener implements DragGestureListener {

    private Cursor dragCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

    private JComponent component;

    private OWLEditorKit owlEditorKit;


    protected OWLObjectDragGestureListener(OWLEditorKit owlEditorKit, JComponent component) {
        this.component = component;
        this.owlEditorKit = owlEditorKit;
    }


    public void dragGestureRecognized(DragGestureEvent dge) {
        if (!canPerformDrag()) {
            return;
        }
        if (getSelectedObjects().isEmpty()) {
            return;
        }
        // TODO: !!!
        TransferableOWLObject transferable = new TransferableOWLObject(owlEditorKit.getOWLModelManager(),
                                                                       getSelectedObjects());
//        if (DragSource.isDragImageSupported()) {
//            setupDragOriginator();
//            dge.startDrag(dragCursor, createImage(), getImageOffset(), transferable, new OWLDragSourceAdapter());
//        } else {
        setupDragOriginator();
        dge.startDrag(dragCursor, transferable, new OWLDragSourceAdapter());
//        }
    }


    protected boolean canPerformDrag() {
        return true;
    }


    protected abstract List<OWLObject> getSelectedObjects();


    protected abstract JComponent getRendererComponent();


    protected abstract Dimension getRendererComponentSize();


    protected abstract Point getImageOffset();


    protected Image createImage() {
        JComponent component = getRendererComponent();
        component.setSize(getRendererComponentSize());
        // component.setOpaque(false);
        BufferedImage img = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
        component.paint(g2);
        return img;
    }


    private void setupDragOriginator() {
        if (component instanceof OWLObjectDragSource) {
            ((OWLObjectDragSource) component).setDragOriginater(true);
        }
    }


    private class OWLDragSourceAdapter extends DragSourceAdapter {


        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (component instanceof OWLObjectDragSource) {
                ((OWLObjectDragSource) component).setDragOriginater(false);
            }
        }
    }
}
