package org.protege.editor.owl.ui.find;

import javax.swing.table.TableCellRenderer;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public abstract class AbstractSearchResultsRow {

    public abstract Object getValueAtColumnIndex(int columnIndex);

    public abstract TableCellRenderer getRendererAtColumnIndex(int columnIndex);


}
