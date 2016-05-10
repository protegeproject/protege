package org.protege.editor.owl.ui.table;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class BasicOWLTable extends JTable {

    public BasicOWLTable(TableModel model) {
        super(model);
        setRowHeight(getFontMetrics(getFont()).getHeight() + 3);
        setRowMargin(1);
        if (!isHeaderVisible()) {
            setTableHeader(null);
        }
        setShowGrid(false);
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setGridColor(Color.LIGHT_GRAY);
        getColumnModel().setColumnSelectionAllowed(false);
    }


    /**
     * By default, the table header isn't visible.  This method
     * can be overriden to return true.
     */
    protected boolean isHeaderVisible() {
        return false;
    }
}
