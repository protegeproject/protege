package org.protege.editor.owl.ui.search;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchRequest;
import org.protege.editor.owl.model.search.SearchResultSet;
import org.protege.editor.owl.model.util.OboUtilities;
import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public class SearchPanel extends JPanel {

    private static final String WHITE_SPACE_PATTERN = "\\s+";

    private final SearchOptionsPanel searchOptionsPanel;

    private final SearchResultsPanel searchResultsPanel;

    private final Logger logger = LoggerFactory.getLogger(SearchPanel.class);

    private final OWLEditorKit editorKit;

    private String searchString = "";

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

    private String getSearchString() {
        return searchString;
    }

    public void setSearchResultClickedListener(SearchResultClickedListener searchResultClickedListener) {
        searchResultsPanel.setSearchResultClickedListener(searchResultClickedListener);
    }

    public void setSearchString(String searchString) {
        this.searchString = checkNotNull(searchString);
        searchOptionsPanel.refresh();
        doSearch();
    }

    private SearchRequest createSearchRequest() throws PatternSyntaxException {
            OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();
            int flags = Pattern.DOTALL | (prefs.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);

            ImmutableList.Builder<Pattern> builder = ImmutableList.builder();

            String preparedSearchString;
            for (final String splitSearchString : getSearchString().split(WHITE_SPACE_PATTERN)) {
                if(OboUtilities.isOboId(splitSearchString)) {
                    preparedSearchString = splitSearchString.replace(":", "(?::|_)");
                }
                else if (prefs.isUseRegularExpressions()) {
                    preparedSearchString = splitSearchString;
                    if (prefs.isIgnoreWhiteSpace()) {
                        preparedSearchString = preparedSearchString.replace(" ", WHITE_SPACE_PATTERN);
                    }
                }
                else {
                    if (prefs.isIgnoreWhiteSpace()) {
                        StringBuilder sb = new StringBuilder();
                        String[] split = splitSearchString.split(WHITE_SPACE_PATTERN);
                        for (int i = 0; i < split.length; i++) {
                            String s = split[i];
                            sb.append(Pattern.quote(s));
                            if (i < split.length - 1) {
                                sb.append(WHITE_SPACE_PATTERN);
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
            return new SearchRequest(builder.build());
    }

    private void doSearch() {
        String searchStr = getSearchString();
        if (searchStr.trim().isEmpty()) {
            searchResultsPanel.clearSearchResults();
            return;
        }
        try {
            SearchManager searchManager = editorKit.getSearchManager();
            SearchRequest searchRequest = createSearchRequest();
            searchManager.performSearch(searchRequest, searchResults -> {
                int categorySizeLimit = getCategoryLimit();
                SearchResultSet searchResultSet = new SearchResultSet(searchResults);
                SwingUtilities.invokeLater(() -> searchResultsPanel.setSearchResults(searchResultSet, categorySizeLimit));

            });
        } catch (PatternSyntaxException e) {
            logger.info("Invalid regular expression in search pattern: {}", e.getPattern());
        }
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
