package org.protege.editor.owl.ui.search;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchResult;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/15
 */
public class SearchDialogPanel extends JPanel {

    private final JTextField searchField;

    private final SearchPanel searchPanel;

    private final OWLEditorKit editorKit;

    public SearchDialogPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setLayout(new BorderLayout());
        searchField = new AugmentedJTextField("Enter search string");
        searchPanel = new SearchPanel(editorKit);
        add(searchField, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectEntity();
                }
            }


            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    searchPanel.moveSelectionUp();
                    e.consume();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    searchPanel.moveSelectionDown();
                    e.consume();
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }


            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }


            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }
        });
        searchPanel.setSearchResultClickedListener((searchResult, e) -> {
            if(e.getClickCount() == 2) {
                selectEntity();
            }
        });

    }

    private void selectEntity() {
        Optional<OWLEntity> selectedEntity = searchPanel.getSelectedEntity();
        if (selectedEntity.isPresent()) {
            editorKit.getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(selectedEntity.get());
            editorKit.getOWLWorkspace().displayOWLEntity(selectedEntity.get());
        }
    }


    private void performSearch() {
        searchPanel.setSearchString(searchField.getText().trim());
    }




    public static JDialog createDialog(JComponent parent, OWLEditorKit editorKit) {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
        final JDialog dialog = new JDialog(parentFrame, "Search", Dialog.ModalityType.MODELESS);

        final SearchDialogPanel searchDialogPanel = new SearchDialogPanel(editorKit);
        searchDialogPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        searchDialogPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE_DIALOG");
        searchDialogPanel.getActionMap().put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        searchDialogPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "CLOSE_DIALOG_WITH_ENTER");
        searchDialogPanel.getActionMap().put("CLOSE_DIALOG_WITH_ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                searchDialogPanel.selectEntity();
            }
        });


        dialog.setContentPane(searchDialogPanel);
        dialog.setResizable(true);
        dialog.pack();
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                searchDialogPanel.searchField.requestFocusInWindow();
                searchDialogPanel.searchField.selectAll();
            }
        });
        return dialog;
    }

}
