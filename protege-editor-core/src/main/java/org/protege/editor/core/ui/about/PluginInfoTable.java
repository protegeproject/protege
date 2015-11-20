package org.protege.editor.core.ui.about;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoTable extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = -6344747415695178548L;

    public PluginInfoTable() {
        super(new PluginInfoTableModel());
        setRowHeight(getRowHeight() + 10);
        getColumnModel().setColumnMargin(6);
        setGridColor(Color.LIGHT_GRAY);
        setShowHorizontalLines(true);
    }
}
