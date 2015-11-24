package org.protege.editor.owl.ui.search.nci;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.ui.search.SearchResultClickedListener;

import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.google.common.base.Optional;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class SearchDialogPanel extends JPanel {

    private static final long serialVersionUID = -3409675627052907430L;

    private final SearchFieldsPanel searchFieldsPanel;
    private final SearchPanel searchPanel;

    private final OWLEditorKit editorKit;

    public SearchDialogPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setLayout(new BorderLayout());
        
        searchFieldsPanel = new SearchFieldsPanel(this);
        searchPanel = new SearchPanel(editorKit);
        add(searchFieldsPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);

        searchPanel.setSearchResultClickedListener(new SearchResultClickedListener() {
            @Override
            public void handleSearchResultClicked(SearchResult searchResult, MouseEvent e) {
                if(e.getClickCount() == 2) {
                    selectEntity();
                }
            }
        });
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    private void selectEntity() {
        Optional<OWLEntity> selectedEntity = searchPanel.getSelectedEntity();
        if (selectedEntity.isPresent()) {
            editorKit.getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(selectedEntity.get());
            editorKit.getOWLWorkspace().displayOWLEntity(selectedEntity.get());
        }
    }

    @SuppressWarnings("serial")
    public static JDialog createDialog(JComponent parent, OWLEditorKit editorKit) {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
        final JDialog dialog = new JDialog(parentFrame, "Search", Dialog.ModalityType.MODELESS);

        final SearchDialogPanel searchDialogPanel = new SearchDialogPanel(editorKit);
        searchDialogPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        searchDialogPanel.setPreferredSize(new Dimension(800, 600));
        searchDialogPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE_DIALOG");
        searchDialogPanel.getActionMap().put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        dialog.setContentPane(searchDialogPanel);
        dialog.setResizable(true);
        dialog.pack();
        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dialog.setVisible(false);
            }
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // NO-OP
            }
        });
        return dialog;
    }
}