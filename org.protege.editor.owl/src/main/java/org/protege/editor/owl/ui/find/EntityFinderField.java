package org.protege.editor.owl.ui.find;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.ui.search.SearchPanel;

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

    private OWLEditorKit editorKit;

//    private JList resultsList;

//    EntityFinderResultsList resultsList;

    private JWindow window;

    private JComponent parent;

    private SearchPanel searchPanel;


    public EntityFinderField(JComponent parent, OWLEditorKit editorKit) {
        super(20, "Search for entity");
        putClientProperty("JTextField.variant", "search");
        this.parent = parent;
        this.editorKit = editorKit;
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
        // tooltip and fixed cell size code at svn revision 23344
//        resultsList = new EntityFinderResultsList(editorKit);
//        resultsList.setCellRenderer(editorKit.getWorkspace().createOWLCellRenderer());
//        resultsList.getMainComponent().addMouseListener(new MouseAdapter() {
//            public void mouseReleased(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    selectEntity();
//                }
//            }
//        });

    }


    private void selectEntity() {
//        OWLEntity selEntity = resultsList.getSelectedEntity();
//        if (selEntity != null) {
//            closeResults();
//            EntityFinderField.this.editorKit.getWorkspace().getOWLSelectionModel().setSelectedEntity(selEntity);
//            editorKit.getWorkspace().displayOWLEntity(selEntity);
//        }
    }


    private void incrementListSelection() {
        searchPanel.moveSelectionDown();
    }


    private void decrementListSelection() {
        searchPanel.moveSelectionUp();
    }


    private void closeResults() {
        getWindow().setVisible(false);
//        searchPanel.clearData();
    }


    private Timer timer = new Timer(400, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            executeFind();
            timer.stop();
        }
    });


    private void executeFind() {
        showResults();
//        String trimmedText = getText().trim();
//        if(trimmedText.isEmpty()) {
//            closeResults();
//            return;
//        }
//
//        boolean useRegularExpressions = OWLEntityFinderPreferences.getInstance().isUseRegularExpressions();
//        String searchString;
//        if(useRegularExpressions) {
//            searchString = trimmedText;
//        }
//        else {
//            searchString = Pattern.quote(trimmedText);
//        }
//
//        Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//
//
//        SearchRequest searchRequest = new SearchRequest(pattern);
//        SearchManager searchManager = editorKit.getSearchManager();
//        searchManager.performSearch(searchRequest, new SearchResultHandler() {
//            public void searchFinished(List<SearchResult> searchResults) {
//                showResults(searchResults);
//            }
//        });
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
//        if (results.size() > 0) {
        Point pt = new Point(0, 0);
        SwingUtilities.convertPointToScreen(pt, this);
        window.setLocation(pt.x + (getWidth() - WINDOW_WIDTH), pt.y + getHeight() + 2);

        Container parent = window.getParent();
        int height = 400;
        if (parent != null) {
            height = (parent.getHeight() * 3) / 4;
        }
        window.setSize(WINDOW_WIDTH, height);

//            searchOptionsPanel.refresh();

        searchPanel.setSearchString(getText().trim());

        window.setVisible(true);
        window.validate();
//        }
//        else {
//            closeResults();
//        }
    }

    /*
    * This is an opportunity to add some code to deal with large result sets.  The problem
    * is that if you give a big set of results to a JList then it will will take a very long time
    * (35 seconds on my work desktop to display 29000 entities beginning with an 'a').  A sort already
    * involves calculating the browser text for each item (and this is not the bottleneck).  So a potential
    * solution is to use the setProtypicalItem method of the jlist to set the width and height to the max.
    * But for some reason this only worked the first time when I tried it and the results would probably not be
    * optimal.
    *
    * The More... could perhaps be instrumented to completely fill the list when clicked.
    */
    private List<SearchResult> getSortedResults(List<SearchResult> results) {
        TreeSet<SearchResult> ts = new TreeSet<SearchResult>();
        ts.addAll(results);


        List<SearchResult> resultsList = new ArrayList<SearchResult>();
        int i = 0;
        for (SearchResult e : ts) {
            resultsList.add(e);
        }
//        if (tooMany) {
//        	arrayResults[maxSize - 1] = "More...";
//        }
        return resultsList;
    }
}
