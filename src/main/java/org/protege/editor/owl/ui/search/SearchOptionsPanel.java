package org.protege.editor.owl.ui.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.SearchCategory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 */
public class SearchOptionsPanel extends JPanel {

    private final JCheckBox useRegexCheckBox;

    private final JCheckBox caseSensitive;

    private final JCheckBox wholeWordsCheckbox;

    private final JCheckBox ignoreWhiteSpaceCheckbox;


    private final JCheckBox showAllResultsCheckBox;


    private final List<SearchOptionsChangedListener> listeners = new ArrayList<SearchOptionsChangedListener>();

    private final JCheckBox searchInAnnotationValues;

    private final JCheckBox searchInLogicalAxioms;

    private final OWLEditorKit editorKit;

    private final JCheckBox searchInIRIs;

    private final JProgressBar searchProgressBar;

    private final JLabel searchProgressLabel = new JLabel();

    private final Timer visibilityTimer;

    public SearchOptionsPanel(OWLEditorKit editorKit) {
        visibilityTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProgressBar.setVisible(true);
                searchProgressLabel.setVisible(true);
            }
        });
        this.editorKit = editorKit;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        Box box = new Box(BoxLayout.Y_AXIS);
        add(box, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        box.add(topPanel);

        caseSensitive = new JCheckBox(new AbstractAction("Case sensitive") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setCaseSensitive(caseSensitive.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        topPanel.add(caseSensitive);

        wholeWordsCheckbox = new JCheckBox(new AbstractAction("Whole words") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setWholeWords(wholeWordsCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        topPanel.add(wholeWordsCheckbox);

        ignoreWhiteSpaceCheckbox = new JCheckBox(new AbstractAction("Ignore white space") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setIgnoreWhiteSpace(ignoreWhiteSpaceCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        topPanel.add(ignoreWhiteSpaceCheckbox);


        useRegexCheckBox = new JCheckBox(new AbstractAction("Regular expression") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setUseRegularExpressions(useRegexCheckBox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        topPanel.add(useRegexCheckBox);


        showAllResultsCheckBox = new JCheckBox(new AbstractAction("Show all results") {
            public void actionPerformed(ActionEvent e) {
                fireSearchResultsPresentationOptionChanged();
            }
        });
        topPanel.add(showAllResultsCheckBox);

        box.add(Box.createVerticalStrut(5));


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        box.add(bottomPanel);

        searchInIRIs = new JCheckBox(new AbstractAction("Search in IRIs") {
            public void actionPerformed(ActionEvent e) {
                handleSearchTypeChanged();
            }
        });
        searchInIRIs.setSelected(editorKit.getSearchManager().isSearchType(SearchCategory.IRI));
        bottomPanel.add(searchInIRIs);
        searchInAnnotationValues = new JCheckBox(new AbstractAction("Search in annotation values") {
            public void actionPerformed(ActionEvent e) {
                handleSearchTypeChanged();
            }
        });
        searchInAnnotationValues.setSelected(editorKit.getSearchManager().isSearchType(SearchCategory.ANNOTATION_VALUE));
        bottomPanel.add(searchInAnnotationValues);
        searchInLogicalAxioms = new JCheckBox(new AbstractAction("Search in logical axioms") {
            public void actionPerformed(ActionEvent e) {
                handleSearchTypeChanged();
            }
        });
        searchInLogicalAxioms.setSelected(editorKit.getSearchManager().isSearchType(SearchCategory.LOGICAL_AXIOM));
        bottomPanel.add(searchInLogicalAxioms);

        bottomPanel.add(Box.createHorizontalStrut(10));
        searchProgressBar = new JProgressBar();
        searchProgressBar.putClientProperty("JComponent.sizeVariant", "small");

        searchProgressLabel.setFont(new Font("verdana", Font.PLAIN, 9));
        searchProgressLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        bottomPanel.add(searchProgressBar);
        bottomPanel.add(searchProgressLabel);
        editorKit.getSearchManager().addProgressMonitor(new org.semanticweb.owlapi.util.ProgressMonitor() {
            public void setStarted() {
                searchProgressBar.setValue(0);
                visibilityTimer.restart();
            }

            public void setSize(long l) {
                searchProgressBar.setMinimum(0);
                searchProgressBar.setMaximum((int) l);
            }

            public void setProgress(long l) {
                searchProgressBar.setValue((int) l);
            }

            public void setMessage(String s) {
                searchProgressBar.setToolTipText(s);
                searchProgressLabel.setText(s);
            }

            public void setIndeterminate(boolean b) {
                searchProgressBar.setIndeterminate(b);
            }

            public void setFinished() {
                visibilityTimer.stop();
                searchProgressBar.setVisible(false);
                searchProgressLabel.setVisible(false);
            }

            public boolean isCancelled() {
                return false;
            }
        });

        refresh();

    }

    private void handleSearchTypeChanged() {
        System.out.println("Search type changed");
        editorKit.getSearchManager().setCategories(getSearchTypes());
        fireSearchRequestOptionChanged();
    }

    public Collection<SearchCategory> getSearchTypes() {
        Set<SearchCategory> result = new HashSet<SearchCategory>();
        result.add(SearchCategory.DISPLAY_NAME);
        if (searchInIRIs.isSelected()) {
            result.add(SearchCategory.IRI);
        }
        if (searchInAnnotationValues.isSelected()) {
            result.add(SearchCategory.ANNOTATION_VALUE);
        }
        if (searchInLogicalAxioms.isSelected()) {
            result.add(SearchCategory.LOGICAL_AXIOM);
        }
        return result;
    }

    public void addListener(SearchOptionsChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SearchOptionsChangedListener listener) {
        listeners.remove(listener);
    }

    public boolean isShowAllResults() {
        return showAllResultsCheckBox.isSelected();
    }

    private void fireSearchRequestOptionChanged() {
        for (SearchOptionsChangedListener listener : new ArrayList<SearchOptionsChangedListener>(listeners)) {
            listener.searchRequestOptionChanged();
        }
    }

    private void fireSearchResultsPresentationOptionChanged() {
        for (SearchOptionsChangedListener listener : new ArrayList<SearchOptionsChangedListener>(listeners)) {
            listener.searchResultsPresentationOptionChanged();
        }
    }


    public void refresh() {
        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();

        caseSensitive.setSelected(prefs.isCaseSensitive());
        useRegexCheckBox.setSelected(prefs.isUseRegularExpressions());
        wholeWordsCheckbox.setSelected(prefs.isWholeWords());
        ignoreWhiteSpaceCheckbox.setSelected(prefs.isIgnoreWhiteSpace());

    }
}
