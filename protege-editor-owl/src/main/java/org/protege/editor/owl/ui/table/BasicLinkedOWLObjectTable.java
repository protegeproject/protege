package org.protege.editor.owl.ui.table;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class BasicLinkedOWLObjectTable extends BasicOWLTable implements LinkedObjectComponent {

    private OWLEditorKit editorKit;


    public BasicLinkedOWLObjectTable(TableModel model, OWLEditorKit owlEditorKit) {
        super(model);
        this.editorKit = owlEditorKit;
        defaultCursor = getCursor();
        LinkedObjectComponentMediator mediator = new LinkedObjectComponentMediator(owlEditorKit, this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Linked object component stuff
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }


    /**
     * Gets the location of the mouse relative to the
     * rendering cell that it is over.
     */
    public Point getMouseCellLocation() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int row = rowAtPoint(mouseLoc);
        int col = columnAtPoint(mouseLoc);
        Rectangle cellRect = getCellRect(row, col, true);
        return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
    }


    public Rectangle getMouseCellRect() {
        Point mousePos = getMousePosition();
        if (mousePos != null) {
            return getCellRect(rowAtPoint(mousePos), columnAtPoint(mousePos), true);
        }
        else {
            return null;
        }
    }


    /**
     * Gets the cell object that the mouse is over
     */
    public Object getCellObject() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int row = rowAtPoint(mouseLoc);
        int col = columnAtPoint(mouseLoc);
        if (row > -1 && col > -1) {
            return getModel().getValueAt(row, col);
        }
        else {
            return null;
        }
    }


    private OWLObject linkedObject;

    private Cursor defaultCursor;


    public void setLinkedObject(OWLObject object) {
        linkedObject = object;
        if (linkedObject != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            setCursor(defaultCursor);
        }
    }


    public OWLObject getLinkedObject() {
        return linkedObject;
    }


    public JComponent getComponent() {
        return this;
    }
}
