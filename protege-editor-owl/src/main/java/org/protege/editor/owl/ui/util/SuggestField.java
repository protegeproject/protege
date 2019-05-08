package org.protege.editor.owl.ui.util;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.lang.LangCode;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-05-08
 */
public class SuggestField<T> extends JComponent {


    private static final int POPUP_WIDTH = 300;

    private static final int POPUP_HEIGHT = 150;

    private final JTextField textField;

    private final JList<T> suggestList = new JList<>();

    @Nonnull
    private SuggestOracle<T> suggestOracle = query -> Stream.empty();

    @Nonnull
    private SuggestionToString<T> suggestionToString = Object::toString;

    private JWindow window;

    private boolean completing = false;

    public SuggestField(@Nonnull String placeholderText) {
        this.textField = new AugmentedJTextField(placeholderText);
        setLayout(new BorderLayout());
        add(textField, BorderLayout.NORTH);
        KeyListener keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                processKeyPressed(e);
            }
        };
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hidePopupWindow();
            }
        });
        suggestList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    completeWithPopupSelection();
                }
            }
        });
        textField.addKeyListener(keyListener);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    @Nonnull
    public String getText() {
        return textField.getText();
    }

    public void setSuggestOracle(@Nonnull SuggestOracle<T> suggestOracle) {
        this.suggestOracle = checkNotNull(suggestOracle);
    }

    public void setSuggestionToString(@Nonnull SuggestionToString<T> suggestionToString) {
        this.suggestionToString = checkNotNull(suggestionToString);
    }

    public void setRenderer(@Nonnull ListCellRenderer<T> renderer) {
        suggestList.setCellRenderer(checkNotNull(renderer));
    }

    private void handleDocumentChanged(DocumentEvent e) {
        if(completing) {
            return;
        }
        String enteredText = textField.getText();
        if(enteredText.isEmpty()) {
            hidePopupWindow();
            return;
        }
        Vector<T> matchedLangCodes = suggestOracle.getMatches(enteredText)
                .collect(Collectors.toCollection(Vector::new));
        suggestList.setListData(matchedLangCodes);
        if(!matchedLangCodes.isEmpty()) {
            showPopupWindow();
        }
    }

    private void hidePopupWindow() {
        getPopupWindow().ifPresent(w -> w.setVisible(false));
    }

    private void showPopupWindow() {
        Optional<JWindow> popupWindow = getPopupWindow();
        popupWindow.ifPresent(w -> {
            Point location = new Point(0, 0);
            SwingUtilities.convertPointToScreen(location, this);
            location.y = location.y + getHeight() + 2;
            w.setLocation(location);
            w.setVisible(true);
        });
    }

    private void processKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            e.consume();
            completeWithPopupSelection();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            getPopupWindow().ifPresent(w -> {
                e.consume();
                completeWithPopupSelection();
            });
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            getPopupWindow().ifPresent(w -> {
                e.consume();
                incrementSelection();
            });
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            getPopupWindow().ifPresent(w -> {
                e.consume();
                decrementSelection();
            });
        }
    }

    private void completeWithPopupSelection() {
        try {
            completing = true;
            hidePopupWindow();
            T sel = suggestList.getSelectedValue();
            if(sel == null) {
                return;
            }
            textField.setText(suggestionToString.toString(sel));
        } finally {
            completing = false;
        }
    }

    @Nonnull
    private Optional<JWindow> getPopupWindow() {
        if(window != null) {
            return Optional.of(window);
        }

        Window topLevelAncestor = (Window) this.textField.getTopLevelAncestor();
        if(topLevelAncestor == null) {
            return Optional.empty();
        }
        window = new JWindow(topLevelAncestor);
        window.setFocusableWindowState(false);
        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(ComponentFactory.createScrollPane(suggestList));
        window.setSize(new Dimension(POPUP_WIDTH, POPUP_HEIGHT));
        return Optional.of(window);
    }

    private void incrementSelection() {
        int size = suggestList.getModel().getSize();
        if(size == 0) {
            return;
        }
        int selIndex = suggestList.getSelectedIndex() + 1;
        if(selIndex >= size) {
            selIndex = size - 1;
        }
        suggestList.setSelectedIndex(selIndex);
        suggestList.scrollRectToVisible(suggestList.getCellBounds(selIndex, selIndex));
    }

    private void decrementSelection() {
        int size = suggestList.getModel().getSize();
        if(size == 0) {
            return;
        }
        int selIndex = suggestList.getSelectedIndex() - 1;
        if(selIndex < 0) {
            selIndex = 0;
        }
        suggestList.setSelectedIndex(selIndex);
        suggestList.scrollRectToVisible(suggestList.getCellBounds(selIndex, selIndex));
    }

    public void clear() {
        textField.setText("");
        hidePopupWindow();
    }

    public void setText(@Nonnull String langCode) {
        textField.setText(langCode);
    }
}
