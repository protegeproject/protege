package org.protege.editor.owl.ui.search.nci;

import org.protege.editor.owl.ui.search.nci.SearchFieldUiManager.SearchField;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 17/11/2015
 */
public class SearchFieldsPanel extends JPanel {

    private static final long serialVersionUID = 9088795993515821318L;

    private SearchDialogPanel container;

    private SearchFieldUiManager searchFieldManager = new SearchFieldUiManager(this);

    public SearchFieldsPanel(SearchDialogPanel container) {
        this.container = container;
        setLayout(new GridLayout(0, 1, 1, 1)); // row, col, hgap, vgap
    }

    public void performSearch() {
        String searchString = searchFieldManager.getSearchString();
        container.getSearchPanel().setSearchString(searchString);
    }

    public void addSearchField(SearchField newSearchField) {
        add(newSearchField);
        revalidate();
        repaint();
    }

    public void removeSearchField(SearchField oldSearchField) {
        remove(oldSearchField);
        revalidate();
        repaint();
    }

    public void moveSelectionUp() {
        container.getSearchPanel().moveSelectionUp();
    }

    public void moveSelectionDown() {
        container.getSearchPanel().moveSelectionDown();
    }
}
