package org.protege.editor.core.ui;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.BookMarkedURIManager;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.FormLabel;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 12-May-2007<br><br>
 */
public class OpenFromURLPanel extends JPanel implements VerifiedInputEditor {

    private static final int PREF_WIDTH = 500;

    private static final int PREF_HEIGHT = 300;

    private static final String TITLE = "Enter or select a URL";

    private static final String URL_FIELD_PLACEHOLDER = "Enter URL to open from";

    private static final String URL_FIELD_LABEL = "URL";

    private static final String BOOKMARKED_URLS_LABEL = "Bookmarked URLs";

    private JTextField uriField;

    private MList bookmarksList;

    private List<InputVerificationStatusChangedListener> listeners =
            new ArrayList<>();


    public OpenFromURLPanel() {
        createUI();
    }


    private void createUI() {
        uriField = new AugmentedJTextField("", 45, URL_FIELD_PLACEHOLDER);
        uriField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                handleValueChanged();
            }
            public void removeUpdate(DocumentEvent event) {
                handleValueChanged();
            }
            public void changedUpdate(DocumentEvent event) {
                handleValueChanged();
            }
        });

        JPanel upperGroup = new JPanel(new BorderLayout());
        upperGroup.add(new FormLabel(URL_FIELD_LABEL), BorderLayout.NORTH);
        upperGroup.add(uriField, BorderLayout.SOUTH);

        JPanel lowerGroup = new JPanel(new BorderLayout());
        lowerGroup.add(new FormLabel(BOOKMARKED_URLS_LABEL), BorderLayout.NORTH);
        bookmarksList = new MList() {
            protected void handleAdd() {
                addURI();
            }
            protected void handleDelete() {
                deleteSelectedBookmark();
            }
        };
        bookmarksList.setCellRenderer(new BookmarkedItemListRenderer());
        JScrollPane scrollPane = new JScrollPane(bookmarksList);
        lowerGroup.add(scrollPane);
        fillList();
        bookmarksList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateTextField();
            }
        });
        setLayout(new BorderLayout(7, 7));
        add(upperGroup, BorderLayout.NORTH);
        add(lowerGroup, BorderLayout.CENTER);
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
    }


    private void handleValueChanged() {
        final boolean validURI = isValidURI();
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(validURI);
        }
    }

    protected boolean isValidURI(){
        final URI uri = getURI(false);
        return uri != null && uri.isAbsolute() && uri.getScheme() != null;        
    }

    public URI getURI() {
        return getURI(true);
    }

    private URI getURI(boolean showMessage) {
        try {
            return new URI(uriField.getText().trim());
        }
        catch (URISyntaxException e) {
            if (showMessage){
                showURIErrorMessage(e);
            }
        }
        return null;
    }

    private void updateTextField() {
        UrlListItem item = getSelUriListItem();
        if (item != null) {
            uriField.setText(item.uri.toString());
        }
    }


    private void addURI() {
        String uriString = JOptionPane.showInputDialog(this, "Please enter a URL", "URL", PLAIN_MESSAGE);
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
        JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid URL", ERROR_MESSAGE);
    }


    private void fillList() {
        List<Object> listData = new ArrayList<>();
        listData.add(new AddUrlItem());
        getBookmarkedUrls()
                .sorted()
                .map(UrlListItem::new)
                .forEach(listData::add);
        bookmarksList.setListData(listData.toArray());
    }

    private static Stream<URI> getBookmarkedUrls() {
        BookMarkedURIManager man = BookMarkedURIManager.getInstance();
        return man.getBookMarkedURIs().stream();
    }


    private void deleteSelectedBookmark() {
        Object selObj = bookmarksList.getSelectedValue();
        if (!(selObj instanceof UrlListItem)) {
            return;
        }
        UrlListItem item = (UrlListItem) selObj;
        BookMarkedURIManager.getInstance().remove(item.uri);
        fillList();
    }


    private UrlListItem getSelUriListItem() {
        if (bookmarksList.getSelectedValue() instanceof UrlListItem) {
            return (UrlListItem) bookmarksList.getSelectedValue();
        }
        return null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        listener.verifiedStatusChanged(isValidURI());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    private static class BookmarkedItemListRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof UrlListItem) {
                UrlListItem item = (UrlListItem) value;
                label.setText(item.uri.toString());
            }
            label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            return label;
        }
    }


    private static class AddUrlItem implements MListSectionHeader {

        public String getName() {
            return "Bookmarked URLs";
        }


        public boolean canAdd() {
            return true;
        }
    }


    private static class UrlListItem implements MListItem {

        private final URI uri;

        public UrlListItem(URI uri) {
            this.uri = uri;
        }

        public boolean isEditable() {
            return false;
        }

        public void handleEdit() {}

        public boolean isDeleteable() {
            return true;
        }

        public boolean handleDelete() {
            return true;
        }

        public String getTooltip() {
            return uri.toString();
        }
    }


    public static URI showDialog() {
        OpenFromURLPanel panel = new OpenFromURLPanel();
        int ret = JOptionPaneEx.showValidatingConfirmDialog(null, TITLE,
                                                            panel,
                                                            PLAIN_MESSAGE,
                                                            OK_CANCEL_OPTION,
                                                            panel.uriField);
        if (ret == JOptionPane.OK_OPTION) {
            return panel.getURI();
        }
        return null;
    }

    public static void main(String[] args) {
        OpenFromURLPanel.showDialog();
    }
}
