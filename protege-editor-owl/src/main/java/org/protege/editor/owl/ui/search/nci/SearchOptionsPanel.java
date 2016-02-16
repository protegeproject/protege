package org.protege.editor.owl.ui.search.nci;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.ui.search.SearchOptionsChangedListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class SearchOptionsPanel extends JPanel {

    private static final long serialVersionUID = -8251725471986798465L;

    private final JCheckBox showAllResultsCheckBox;

    private final List<SearchOptionsChangedListener> listeners = new ArrayList<SearchOptionsChangedListener>();

    private final JProgressBar searchProgressBar;

    private final JLabel searchProgressLabel = new JLabel();

    private final Timer visibilityTimer;

    @SuppressWarnings("serial")
    public SearchOptionsPanel(OWLEditorKit editorKit) {
        visibilityTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProgressBar.setVisible(true);
                searchProgressLabel.setVisible(true);
            }
        });

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        Box box = new Box(BoxLayout.Y_AXIS);
        add(box, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        box.add(topPanel);

        showAllResultsCheckBox = new JCheckBox(new AbstractAction("Show all results") {
            public void actionPerformed(ActionEvent e) {
                fireSearchResultsPresentationOptionChanged();
            }
        });
        topPanel.add(showAllResultsCheckBox);

        box.add(Box.createVerticalStrut(5));

        topPanel.add(Box.createHorizontalStrut(10));
        searchProgressBar = new JProgressBar();
        searchProgressBar.putClientProperty("JComponent.sizeVariant", "small");
        searchProgressBar.setVisible(false);
        
        searchProgressLabel.setFont(new Font("verdana", Font.PLAIN, 9));
        searchProgressLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        searchProgressLabel.setVisible(false);

        topPanel.add(searchProgressBar);
        topPanel.add(searchProgressLabel);
        editorKit.getSearchManager().addProgressMonitor(new org.semanticweb.owlapi.util.ProgressMonitor() {
            @Override
            public void setStarted() {
                searchProgressBar.setValue(0);
                visibilityTimer.restart();
            }
            @Override
            public void setSize(long l) {
                searchProgressBar.setMinimum(0);
                searchProgressBar.setMaximum((int) l);
            }
            @Override
            public void setProgress(long l) {
                searchProgressBar.setValue((int) l);
            }
            @Override
            public void setMessage(String s) {
                searchProgressBar.setToolTipText(s);
                searchProgressLabel.setText(s);
            }
            @Override
            public void setIndeterminate(boolean b) {
                searchProgressBar.setIndeterminate(b);
            }
            @Override
            public void setFinished() {
                visibilityTimer.stop();
                searchProgressBar.setVisible(false);
                searchProgressLabel.setVisible(false);
            }
            @Override
            public boolean isCancelled() {
                return false;
            }
        });

        refresh();

    }

    public Collection<SearchCategory> getSearchTypes() {
        Set<SearchCategory> result = new HashSet<SearchCategory>();
        result.add(SearchCategory.DISPLAY_NAME);
        result.add(SearchCategory.IRI);
        result.add(SearchCategory.ANNOTATION_VALUE);
        result.add(SearchCategory.LOGICAL_AXIOM);
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

    private void fireSearchResultsPresentationOptionChanged() {
        for (SearchOptionsChangedListener listener : new ArrayList<SearchOptionsChangedListener>(listeners)) {
            listener.searchResultsPresentationOptionChanged();
        }
    }

    public void refresh() {
        // NO-OP
    }
}
