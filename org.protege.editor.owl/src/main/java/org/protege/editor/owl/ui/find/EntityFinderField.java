package org.protege.editor.owl.ui.find;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.ui.search.SearchPanel;
import org.protege.editor.owl.ui.search.SearchResultClickedListener;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EntityFinderField extends AugmentedJTextField {

    private static final long serialVersionUID = -5383341925424297227L;


    public static final int WINDOW_WIDTH = 800;

    private JWindow window;

    private JComponent parent;

    private SearchPanel searchPanel;

    private OWLEditorKit editorKit;

    public EntityFinderField(JComponent parent, OWLEditorKit editorKit) {
        super(20, "Search for entity");
        this.editorKit = editorKit;
        putClientProperty("JTextField.variant", "search");
        this.parent = parent;
        searchPanel = new SearchPanel(editorKit);
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeResults();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectEntity();
                }
            }


            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    decrementListSelection();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    incrementListSelection();
                }
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    handleBackSpaceDown();
                }
            }
        });
        getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }


            public void insertUpdate(DocumentEvent e) {
                performFind();
            }


            public void removeUpdate(DocumentEvent e) {
                performFind();
            }
        });
        searchPanel.setSearchResultClickedListener(new SearchResultClickedListener() {
            @Override
            public void handleSearchResultClicked(SearchResult searchResult, MouseEvent e) {
                if(e.getClickCount() == 2) {
                    selectEntity();
                }
            }
        });
    }

    private void selectEntity() {
        Optional<OWLEntity> selectedEntity = searchPanel.getSelectedEntity();
        if (selectedEntity.isPresent()) {
            editorKit.getWorkspace().getOWLSelectionModel().setSelectedEntity(selectedEntity.get());
            editorKit.getWorkspace().displayOWLEntity(selectedEntity.get());
        }
        closeResults();
    }

    private void handleBackSpaceDown() {
        if(getText().isEmpty()) {
            closeResults();
        }
    }


    private void incrementListSelection() {
        searchPanel.moveSelectionDown();
    }


    private void decrementListSelection() {
        searchPanel.moveSelectionUp();
    }


    private void closeResults() {
        getWindow().setVisible(false);
    }


    private Timer timer = new Timer(400, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            executeFind();
            timer.stop();
        }
    });


    private void executeFind() {
        showResults();
    }


    private void performFind() {
        timer.setDelay((int) OWLEntityFinderPreferences.getInstance().getSearchDelay());
        timer.restart();
    }


    private JWindow getWindow() {
        if (window == null) {
            Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
            window = new JWindow(w);
            window.setFocusableWindowState(false);
            JPanel popupContent = new JPanel(new BorderLayout(3, 3));
            popupContent.add(searchPanel);
            window.setContentPane(popupContent);
            addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    window.setVisible(false);
                }
            });
            SwingUtilities.getRoot(this).addComponentListener(new ComponentAdapter() {
                public void componentMoved(ComponentEvent e) {
                    closeResults();
                }
            });
        }
        return window;
    }


    private void showResults() {
        JWindow window = getWindow();
        Point pt = new Point(0, 0);
        SwingUtilities.convertPointToScreen(pt, this);
        window.setLocation(pt.x + (getWidth() - WINDOW_WIDTH), pt.y + getHeight() + 2);

        Container parent = window.getParent();
        int height = 400;
        if (parent != null) {
            height = (parent.getHeight() * 3) / 4;
        }
        window.setSize(WINDOW_WIDTH, height);
        searchPanel.setSearchString(getText().trim());

        window.setVisible(true);
        window.validate();
    }

}
