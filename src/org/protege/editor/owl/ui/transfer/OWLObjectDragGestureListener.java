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
        TransferableOWLObject transferable = new TransferableOWLObject(owlEditorKit.getModelManager(),
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
