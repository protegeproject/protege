package org.protege.editor.owl.ui.search;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.*;
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
import java.util.regex.Pattern;

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
        this.searchString = searchString;
        searchOptionsPanel.refresh();
        doSearch();
    }

    private SearchRequest createSearchRequest() {
        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();
        int flags = Pattern.DOTALL | (prefs.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);

        ImmutableList.Builder<Pattern> builder = ImmutableList.builder();

        String preparedSearchString;
        for (String splitSearchString : searchString.split("\\s+")) {
            if (prefs.isUseRegularExpressions()) {
                preparedSearchString = splitSearchString;
                if (prefs.isIgnoreWhiteSpace()) {
                    preparedSearchString = preparedSearchString.replace(" ", "\\s+");
                }
            }
            else {
                if (prefs.isIgnoreWhiteSpace()) {
                    StringBuilder sb = new StringBuilder();
                    String[] split = splitSearchString.split("\\s+");
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        sb.append(Pattern.quote(s));
                        if (i < split.length - 1) {
                            sb.append("\\s+");
                        }
                    }
                    preparedSearchString = sb.toString();
                }
                else {
                    preparedSearchString = Pattern.quote(splitSearchString);
                }
            }
            if (prefs.isWholeWords()) {
                preparedSearchString = "\\b(:?" + preparedSearchString + ")\\b";
            }
            builder.add(Pattern.compile(preparedSearchString, flags));
        }
//        Pattern searchPattern = Pattern.compile(preparedSearchString, flags);
        return new SearchRequest(builder.build());
    }

    private void doSearch() {
        if (searchString.trim().isEmpty()) {
            searchResultsPanel.clearSearchResults();
            return;
        }
        SearchManager searchManager = editorKit.getSearchManager();
        SearchRequest searchRequest = createSearchRequest();
        searchManager.performSearch(searchRequest, new SearchResultHandler() {
            public void searchFinished(List<SearchResult> searchResults) {
                int categorySizeLimit = getCategoryLimit();
                searchResultsPanel.setSearchResults(new SearchResultSet(searchResults), categorySizeLimit);

            }
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
        List<OWLEntity> objects = new ArrayList<OWLEntity>();
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
