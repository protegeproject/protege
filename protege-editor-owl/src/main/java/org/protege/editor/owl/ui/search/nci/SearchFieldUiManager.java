package org.protege.editor.owl.ui.search.nci;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.ui.OWLIcons;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 17/11/2015
 */
public class SearchFieldUiManager {

    private final SearchFieldsPanel container;

    private List<SearchField> searchFields = new ArrayList<>();

    public SearchFieldUiManager(SearchFieldsPanel container) {
        this.container = container;
        init();
    }

    private void init() {
        SearchField firstSearchField = new SearchField();
        firstSearchField.hideRemoveButton();
        fireSearchFieldAdd(firstSearchField);
    }

    public String getSearchString() {
        final List<String> linkedSearchStrings = new ArrayList<String>();
        final List<String> unionSearchStrings = new ArrayList<String>();
        collectSearchStrings(linkedSearchStrings, unionSearchStrings, searchFields);
        
        StringBuffer sb = new StringBuffer();
        boolean needSeparator = false;
        for (String searchString : linkedSearchStrings) {
            if (needSeparator) {
                sb.append(" & ");
            }
            sb.append(searchString);
            needSeparator = true;
        }
        for (String searchString : unionSearchStrings) {
            if (needSeparator) {
                sb.append(" | ");
            }
            sb.append(searchString);
            needSeparator = true;
        }
        return sb.toString();
    }

    private void collectSearchStrings(List<String> linkedSearchStrings, List<String> unionSearchStrings, List<SearchField> searchFields) {
        for (SearchField searchField : searchFields) {
            String searchString = searchField.getSearchString();
            if (searchString.isEmpty()) continue;
            if (searchField.isLinked()) {
                linkedSearchStrings.add(searchString);
            } else {
                unionSearchStrings.add(searchString);
            }
        }
    }

    private void fireSearchFieldLinked() {
        container.performSearch();
    }

    private void fireSearchFieldRemove(SearchField oldSearchField) {
        oldSearchField.reassignFocus();
        searchFields.remove(oldSearchField);
        container.removeSearchField(oldSearchField);
        container.performSearch();
    }

    private void fireSearchFieldAdd(SearchField newSearchField) {
        searchFields.add(newSearchField);
        container.addSearchField(newSearchField);
        newSearchField.assignFocus();
    }

    class SearchField extends JPanel {

        private static final long serialVersionUID = 5441250300176517185L;

        private final JTextField txtSearchField;
        private final JButton cmdLink;
        private final JButton cmdRemove;
        private final JButton cmdAdd;

        private boolean isLinked = true;

        public SearchField() {
            setLayout(new BorderLayout());
            txtSearchField = new AugmentedJTextField("Enter search string");
            add(txtSearchField, BorderLayout.CENTER);

            JPanel pnlButtonGroup = new JPanel();
            pnlButtonGroup.setLayout(new GridLayout(1, 3, 2, 0)); // row, col, hgap, vgap
            cmdLink = new JButton(OWLIcons.getIcon("link.png"));
            cmdLink.setFocusable(false);
            cmdRemove = new JButton("-");
            cmdRemove.setFocusable(false);
            cmdAdd = new JButton("+");
            cmdAdd.setFocusable(false);
            pnlButtonGroup.add(cmdLink);
            pnlButtonGroup.add(cmdAdd);
            pnlButtonGroup.add(cmdRemove);
            pnlButtonGroup.setPreferredSize(new Dimension(90, pnlButtonGroup.getHeight()));
            add(pnlButtonGroup, BorderLayout.EAST);

            txtSearchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        SearchField newSearchField = new SearchField();
                        fireSearchFieldAdd(newSearchField);
                    }
                }
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        container.moveSelectionUp();
                        e.consume();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        container.moveSelectionDown();
                        e.consume();
                    }
                }
            });
            txtSearchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    // NO-OP
                }
                @Override
                public void insertUpdate(DocumentEvent e) {
                    container.performSearch();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    container.performSearch();
                }
            });

            cmdLink.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isLinked = !isLinked;
                    JButton linkButton = (JButton) e.getSource();
                    if (isLinked) {
                        linkButton.setIcon(OWLIcons.getIcon("link.png"));
                    } else {
                        linkButton.setIcon(OWLIcons.getIcon("unlink.png"));
                    }
                    fireSearchFieldLinked();
                }
            });

            cmdRemove.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton removeButton = (JButton) e.getSource();
                    SearchField searchField = getParentPanel(removeButton);
                    fireSearchFieldRemove(searchField);
                }
                private SearchField getParentPanel(JComponent c) {
                    Container container = c.getParent();
                    while (!(container instanceof SearchField)) {
                        container = container.getParent();
                    }
                    return (SearchField) container;
                }
            });

            cmdAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SearchField newSearchField = new SearchField();
                    fireSearchFieldAdd(newSearchField);
                }
            });
        }

        public void hideLinkButton() {
            cmdLink.setVisible(false);
        }

        public void hideRemoveButton() {
            cmdRemove.setVisible(false);
        }

        public void hideAddButton() {
            cmdAdd.setVisible(false);
        }

        public String getSearchString() {
            return txtSearchField.getText();
        }

        public boolean isLinked() {
            return isLinked;
        }

        private void assignFocus() {
            txtSearchField.requestFocusInWindow();
        }

        private void reassignFocus() {
            int index = searchFields.indexOf(this);
            if (index == 0) return; // this is the last search field, no need to get the previous field
            SearchField previousSearchField = searchFields.get(index-1);
            previousSearchField.assignFocus();
        }
    }
}
