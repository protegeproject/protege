package org.protege.editor.owl.ui.action;

import javax.swing.JMenuItem;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public abstract class ProtegeOWLRadioButtonAction extends ProtegeOWLAction {

    private JMenuItem menuItem;

    /*
     * Sets the menu item associated with this action.  Not sure where this came from.  Seems dodgy.  Seems to be
     * called after initialize.
     */
    final public void setMenuItem(JMenuItem menuItem) {
        this.menuItem = menuItem;
        update();
    }

    protected abstract void update();

    public void setSelected(boolean selected) {
        if(menuItem == null) {
            return;
        }
        menuItem.setSelected(selected);
    }

    public boolean isSelected() {
        if(menuItem == null) {
            return false;
        }
        return menuItem.isSelected();
    }
}
