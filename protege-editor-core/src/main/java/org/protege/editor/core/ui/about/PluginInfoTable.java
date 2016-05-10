package org.protege.editor.core.ui.about;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoTable extends JTable {

    public PluginInfoTable() {
        super(new PluginInfoTableModel());
        setRowHeight(getRowHeight() + 10);
        getColumnModel().setColumnMargin(6);
        setGridColor(Color.LIGHT_GRAY);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        getColumnModel().getColumn(1).setMaxWidth(80);
        getColumnModel().getColumn(2).setMaxWidth(80);
    }
}
