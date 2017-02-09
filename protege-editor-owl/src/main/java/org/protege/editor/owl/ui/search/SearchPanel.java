package org.protege.editor.owl.ui.search;

import com.google.common.base.Optional;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchResultSet;
import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public class SearchPanel extends JPanel {

    private SearchOptionsPanel searchOptionsPanel;

    private SearchResultsPanel searchResultsPanel;

    private String searchString;

    private OWLEditorKit editorKit;

    public SearchPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setLayout(new BorderLayout(4, 4));
        searchOptionsPanel = new SearchOptionsPanel(editorKit);
        add(searchOptionsPanel, BorderLayout.NORTH);
        searchResultsPanel = new SearchResultsPanel(editorKit);
        add(searchResultsPanel);

        searchOptionsPanel.addListener(new SearchOptionsChangedListener() {
            public void searchRequestOptionChanged() {
                doSearch();
            }

            public void searchResultsPresentationOptionChanged() {
                updateSearchResultsPresentation();
            }
        });

        JPanel searchActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchActionsPanel.add(new JButton(new AbstractAction("Copy selected entities") {
            public void actionPerformed(ActionEvent e) {
                copySelectedEntities();
            }
        }));
        add(searchActionsPanel, BorderLayout.SOUTH);
    }

    public void setSearchResultClickedListener(SearchResultClickedListener searchResultClickedListener) {
        searchResultsPanel.setSearchResultClickedListener(searchResultClickedListener);
    }

    public void setSearchString(String searchString) {
        checkNotNull(searchString);
        this.searchString = toLowerCase(searchString);
        searchOptionsPanel.refresh();
        doSearch();
    }

    private String toLowerCase(String str) {
        return str.toLowerCase();
    }

    private void doSearch() {
        if (searchString.trim().isEmpty()) {
            searchResultsPanel.clearSearchResults();
            return;
        }
        editorKit.getSearchManager().performSearch(searchString, searchResults -> {
            int categorySizeLimit = getCategoryLimit();
            SearchResultSet searchResultSet = new SearchResultSet(searchResults);
            SwingUtilities.invokeLater(() -> searchResultsPanel.setSearchResults(searchResultSet, categorySizeLimit));
        });
    }

    private int getCategoryLimit() {
        int categorySizeLimit = 10;
        if (searchOptionsPanel.isShowAllResults()) {
            categorySizeLimit = Integer.MAX_VALUE;
        }
        return categorySizeLimit;
    }


    private void updateSearchResultsPresentation() {
        int categorySizeLimit = getCategoryLimit();
        searchResultsPanel.setCategorySizeLimit(categorySizeLimit);
    }


    public void moveSelectionDown() {
        searchResultsPanel.moveSelectionDown();
    }

    public void moveSelectionUp() {
        searchResultsPanel.moveSelectionUp();
    }

    private void copySelectedEntities() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        List<OWLEntity> objects = new ArrayList<>();
        objects.addAll(searchResultsPanel.getSelectedEntities());
        ViewClipboard clipboard = ViewClipboard.getInstance();
        TransferableOWLObject contents = new TransferableOWLObject(editorKit.getOWLModelManager(), objects);
        clipboard.getClipboard().setContents(contents, null);

        StringBuilder buffer = new StringBuilder();
        for (OWLEntity owlObject : objects) {
            buffer.append(editorKit.getOWLModelManager().getRendering(owlObject));
            buffer.append(" ");
            buffer.append(owlObject.getIRI().toQuotedString());
            buffer.append("\n");
        }
        StringSelection stringSelection = new StringSelection(buffer.toString().trim());
        systemClipboard.setContents(stringSelection, null);
    }

    public Optional<OWLEntity> getSelectedEntity() {
        return searchResultsPanel.getSelectedEntity();
    }
}
