package org.protege.editor.core.ui.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/02/2012
 */
public class AugmentedJTextField extends JTextField {

    public static final Color DEFAULT_GHOST_TEXT_COLOR = Color.LIGHT_GRAY;

    private static final Color SEARCH_ICON_COLOR = new Color(200, 200, 200);

    private static final int SEARCH_RECT_WIDTH = 20;

    private String errorMessage = "";

    private int errorLocation = -1;

    private String ghostText = "";

    private final Border defaultBorder;

    private Optional<TextFieldSearchHandler> searchHandler = Optional.empty();

    private static final InlineFieldSearchIcon searchIcon = new InlineFieldSearchIcon(SEARCH_ICON_COLOR);


    /**
     * Constructs a new <code>TextField</code>.  A default model is created,
     * the initial string is <code>null</code>,
     * and the number of columns is set to 0.
     */
    public AugmentedJTextField(String ghostText) {
        this("", 40, ghostText);
    }

    /**
     * Constructs a new <code>TextField</code> initialized with the
     * specified text. A default model is created and the number of
     * columns is 0.
     * @param text the text to be displayed, or <code>null</code>
     */
    public AugmentedJTextField(String text, String ghostText) {
        this(text, 40, ghostText);
    }

    /**
     * Constructs a new empty <code>TextField</code> with the specified
     * number of columns.
     * A default model is created and the initial string is set to
     * <code>null</code>.
     * @param columns the number of columns to use to calculate
     * the preferred width; if columns is set to zero, the
     * preferred width will be whatever naturally results from
     * the component implementation
     */
    public AugmentedJTextField(int columns, String ghostText) {
        this("", columns, ghostText);
    }

    /**
     * Constructs a new <code>TextField</code> initialized with the
     * specified text and columns.  A default model is created.
     * @param text the text to be displayed, or <code>null</code>
     * @param columns the number of columns to use to calculate
     * the preferred width; if columns is set to zero, the
     * preferred width will be whatever naturally results from
     * the component implementation
     */
    public AugmentedJTextField(String text, int columns, String ghostText) {
        super(text, columns);
        this.ghostText = ghostText;
        defaultBorder = getBorder();
        installMouseListeners();
    }

    private void installMouseListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if(isInSearchRect(e.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(isInSearchRect(e.getPoint())) {
                    searchHandler.ifPresent(h -> {
                        Optional<String> result = h.handleSearch(AugmentedJTextField.this);
                        result.ifPresent(AugmentedJTextField.this::setText);
                    });
                }
            }
        });
    }


    public void setSearchHandler(TextFieldSearchHandler handler) {
        checkNotNull(handler);
        this.searchHandler = Optional.of(handler);
        setSearchIconVisible();


    }

    private boolean isInSearchRect(Point pt) {
        return pt.getX() > getWidth() - SEARCH_RECT_WIDTH && pt.getX() < getWidth();
    }

    private void setSearchIconVisible() {
        Border searchBorder = BorderFactory.createCompoundBorder(
                defaultBorder,
                BorderFactory.createEmptyBorder(0, 0, 0, SEARCH_RECT_WIDTH)
        );
        setBorder(searchBorder);
    }

    public int getErrorLocation() {
        return errorLocation;
    }

    public void setErrorLocation(int errorLocation) {
        if (this.errorLocation != errorLocation) {
            this.errorLocation = errorLocation;
            repaint();
        }
    }

    public void clearErrorLocation() {
        if (errorLocation != -1) {
            errorLocation = -1;
            repaint();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getGhostText() {
        return ghostText;
    }

    public void setGhostText(String ghostText) {
        if (!this.ghostText.equals(ghostText)) {
            this.ghostText = ghostText;
            repaint();
        }
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        repaint();
    }

    public void clearErrorMessage() {
        if (!this.errorMessage.isEmpty()) {
            this.errorMessage = "";
            repaint();
        }
    }

    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-<code>null</code>.  We pass the delegate a copy of the
     * <code>Graphics</code> object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, <code>Graphics.translate</code>).
     * <p>
     * If you override this in a subclass you should not make permanent
     * changes to the passed in <code>Graphics</code>. For example, you
     * should not alter the clip <code>Rectangle</code> or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new <code>Graphics</code> from the passed in
     * <code>Graphics</code> and manipulate it. Further, if you do not
     * invoker super's implementation you must honor the opaque property,
     * that is
     * if this component is opaque, you must completely fill in the background
     * in a non-opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     * <p>
     * The passed in <code>Graphics</code> object might
     * have a transform other than the identify transform
     * installed on it.  In this case, you might get
     * unexpected results if you cumulatively apply
     * another transform.
     * @param g the <code>Graphics</code> object to protect
     * @see #paint
     * @see javax.swing.plaf.ComponentUI
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        try {
            if(errorLocation != -1) {
                g.setColor(Color.PINK);
                Rectangle rectStart = modelToView(errorLocation);
                Rectangle rectEnd = modelToView(errorLocation + 1);
                g.fillRect(rectStart.x, rectStart.y, rectEnd.x - rectStart.x, rectStart.height);
            }

            if (getText().isEmpty()) {
                g.setColor(DEFAULT_GHOST_TEXT_COLOR);
                int baseLine = getBaseline(getWidth(), getHeight());
                Insets insets = getInsets();
                g.drawString(ghostText, insets.left, baseLine);

            }
            if (!errorMessage.isEmpty()) {
                int baseLine = getBaseline(getWidth(), getHeight());
                Rectangle rect = modelToView(getText().length());
                g.setColor(Color.PINK);
                g.drawString(errorMessage, rect.x + SEARCH_RECT_WIDTH, baseLine);
            }
        }
        catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }

        if(searchHandler.isPresent()) {
            g.setColor(SEARCH_ICON_COLOR);
            searchIcon.paintIcon(this, g, getWidth() - SEARCH_RECT_WIDTH, (getHeight() - searchIcon.getIconHeight()) / 2);
        }

        g.setColor(oldColor);
    }


}
