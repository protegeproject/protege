package org.protege.editor.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.BookMarkedURIManager;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 12-May-2007<br><br>
 */
public class OpenFromURIPanel extends JPanel {

    private JTextField uriField;

    private MList bookmarksList;


    public OpenFromURIPanel() {
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        uriField = new JTextField(45);
        JPanel uriFieldHolder = new JPanel(new BorderLayout());
        uriFieldHolder.setBorder(ComponentFactory.createTitledBorder("URI"));
        uriFieldHolder.add(uriField, BorderLayout.NORTH);
        add(uriFieldHolder, BorderLayout.NORTH);
        JPanel bookmarksHolder = new JPanel(new BorderLayout());
        bookmarksHolder.setBorder(ComponentFactory.createTitledBorder("Bookmarks"));
        add(bookmarksHolder);
        bookmarksList = new MList() {
            protected void handleAdd() {
                addURI();
            }


            protected void handleDelete() {
                deleteSelectedBookmark();
            }
        };

        bookmarksList.setCellRenderer(new BookmarkedItemListRenderer());
        bookmarksHolder.add(new JScrollPane(bookmarksList));
        fillList();
        bookmarksList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateTextField();
                }
            }
        });
    }


    public URI getURI() {
        try {
            return new URI(uriField.getText().trim());
        }
        catch (URISyntaxException e) {
            showURIErrorMessage(e);
        }
        return null;
    }


    private void updateTextField() {
        URIListItem item = getSelUriListItem();
        if (item != null) {
            uriField.setText(item.uri.toString());
        }
    }


    private void addURI() {
        String uriString = JOptionPane.showInputDialog(this, "Please enter a URI", "URI", JOptionPane.PLAIN_MESSAGE);
        if (uriString != null) {
            try {
                URI uri = new URI(uriString);
                BookMarkedURIManager.getInstance().add(uri);
            }
            catch (URISyntaxException e) {
                showURIErrorMessage(e);
            }
            fillList();
        }
    }


    private void showURIErrorMessage(URISyntaxException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid URI", JOptionPane.ERROR_MESSAGE);
    }


    private void fillList() {
        BookMarkedURIManager man = BookMarkedURIManager.getInstance();
        Set<URI> ts = new TreeSet<URI>();
        ts.addAll(man.getBookMarkedURIs());
        ArrayList<Object> data = new ArrayList<Object>();

        data.add(new AddURIItem());
        for (URI uri : ts) {
            data.add(new URIListItem(uri));
        }
        bookmarksList.setListData(data.toArray());
    }


    private void deleteSelectedBookmark() {
        Object selObj = bookmarksList.getSelectedValue();
        if (!(selObj instanceof URIListItem)) {
            return;
        }
        URIListItem item = (URIListItem) selObj;
        BookMarkedURIManager.getInstance().remove(item.uri);
        fillList();
    }


    private URIListItem getSelUriListItem() {
        if (bookmarksList.getSelectedValue() instanceof URIListItem) {
            return (URIListItem) bookmarksList.getSelectedValue();
        }
        return null;
    }


    private class BookmarkedItemListRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof URIListItem) {
                URIListItem item = (URIListItem) value;
                label.setText(item.uri.toString());
            }
            return label;
        }
    }


    private class AddURIItem implements MListSectionHeader {

        public String getName() {
            return "Bookmared URIs";
        }


        public boolean canAdd() {
            return true;
        }
    }


    private class URIListItem implements MListItem {

        private URI uri;


        public URIListItem(URI uri) {
            this.uri = uri;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
            System.out.println("DEL!");
            return true;
        }


        public String getTooltip() {
            return uri.toString();
        }
    }


    public static URI showDialog() {
        OpenFromURIPanel panel = new OpenFromURIPanel();
        int ret = JOptionPaneEx.showConfirmDialog(null,
                                                  "Enter or select a URI",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel.uriField);
        if (ret == JOptionPane.OK_OPTION) {
            return panel.getURI();
        }
        return null;
    }
}
