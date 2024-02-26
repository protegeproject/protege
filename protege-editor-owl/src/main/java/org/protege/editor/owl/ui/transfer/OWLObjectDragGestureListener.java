package org.protege.editor.owl.ui.transfer;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 04-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLObjectDragGestureListener implements DragGestureListener {

    private final Cursor dragCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

    private final JComponent component;

    private final OWLEditorKit owlEditorKit;

    private static final Logger logger = LoggerFactory.getLogger(OWLObjectDragGestureListener.class);


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
        TransferableOWLObject transferable = new TransferableOWLObject(owlEditorKit.getModelManager(),
                                                                       getSelectedObjects());
        setupDragOriginator();
        try {
            dge.startDrag(dragCursor, transferable, new OWLDragSourceAdapter(component));
        } catch (InvalidDnDOperationException e) {
            logger.debug("Invalid drop operation");
        }
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


    private static class OWLDragSourceAdapter extends DragSourceAdapter {

        private Component component;

        public OWLDragSourceAdapter(Component component) {
            this.component = component;
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (component instanceof OWLObjectDragSource) {
                ((OWLObjectDragSource) component).setDragOriginater(false);
            }
        }
    }
}
