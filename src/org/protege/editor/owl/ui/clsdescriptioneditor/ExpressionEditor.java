package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An editor that can be used to edit text containing class
 * expressions.  The editor is backed by a parser that checks
 * that the text is well formed and provides feedback if the
 * text is not well formed.
 */
public class ExpressionEditor<O> extends JTextPane
        implements RefreshableComponent, VerifiedInputEditor {


    private static Logger logger = Logger.getLogger(ExpressionEditor.class);

    private Border outerBorder;

    private OWLEditorKit owlEditorKit;

    private Border defaultBorder;

    private Border stateBorder;

    private Border errorBorder;

    private Stroke errorStroke;

    private DocumentListener docListener;

    private int errorStartIndex;

    private int errorEndIndex;

    private Timer timer;


    private static final int DEFAULT_TOOL_TIP_INITIAL_DELAY = ToolTipManager.sharedInstance().getInitialDelay();

    private static final int DEFAULT_TOOL_TIP_DISMISS_DELAY = ToolTipManager.sharedInstance().getDismissDelay();

    private static final int ERROR_TOOL_TIP_INITIAL_DELAY = 100;

    private static final int ERROR_TOOL_TIP_DISMISS_DELAY = 9000;

    private OWLExpressionChecker<O> expressionChecker;


    public ExpressionEditor(OWLEditorKit owlEditorKit, OWLExpressionChecker<O> checker) {
        this.owlEditorKit = owlEditorKit;
        this.outerBorder = null;
        this.expressionChecker = checker;
        defaultBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY);
        setStateBorder(defaultBorder);
        errorBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED);
        errorStroke = new BasicStroke(3.0f,
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_ROUND,
                                      3.0f,
                                      new float []{4.0f, 2.0f},
                                      0.0f);
        
        docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                handleDocumentUpdated();
            }

            public void removeUpdate(DocumentEvent e) {
                handleDocumentUpdated();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        };
        getDocument().addDocumentListener(docListener);

        timer = new Timer(ExpressionEditorPreferences.getInstance().getCheckDelay(), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleTimer();
            }
        });

        refreshComponent();

        new OWLAutoCompleter(owlEditorKit, this, checker);

        new OWLExpressionHistoryCompleter(owlEditorKit, this);

        createStyles();
    }


    public void refreshComponent() {
        setFont(OWLRendererPreferences.getInstance().getFont());
    }


    public void setExpressionObject(O desc) {
        if (desc == null) {
            setText("");
        }
        else if (desc instanceof OWLObject) {
            OWLModelManager mngr = getOWLEditorKit().getModelManager();
            String rendering = mngr.getRendering((OWLObject) desc);
            setText(rendering);
        }
        else if (desc instanceof Collection){
            OWLModelManager mngr = getOWLEditorKit().getModelManager();
            StringBuffer sb = new StringBuffer();
            for (Object obj : (Collection)desc){
                if (obj instanceof OWLObject){
                    if (sb.length() > 0){
                        sb.append(", ");
                    }
                    sb.append(mngr.getRendering((OWLObject)obj));
                }
            }
            setText(sb.toString());
        }
        else {
            setText(desc.toString());
        }
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public OWLExpressionChecker<O> getExpressionChecker() {
        return expressionChecker;
    }


    public O createObject() throws OWLException {
        return expressionChecker.createObject(getText());
    }


    public void setStateBorder(Border border) {
        stateBorder = border;
        super.setBorder(BorderFactory.createCompoundBorder(outerBorder, stateBorder));
    }


    public void setBorder(Border border) {
        outerBorder = border;
        // Override to set the outer border
        super.setBorder(BorderFactory.createCompoundBorder(outerBorder, stateBorder));
    }


    private void clearError() {
        setToolTipText(null);
        setStateBorder(defaultBorder);
        setErrorRange(0, 0);
        ToolTipManager.sharedInstance().setInitialDelay(DEFAULT_TOOL_TIP_INITIAL_DELAY);
        ToolTipManager.sharedInstance().setDismissDelay(DEFAULT_TOOL_TIP_DISMISS_DELAY);
    }


    public boolean isWellFormed() {
        try {
            expressionChecker.check(getText());
            return true;
        }
        catch (OWLException e) {
            return false;
        }
    }


    protected void checkExpression() {
        try {
            // Parse the text in the editor.  If no parse
            // exception is thrown, clear the error, otherwise
            // set the error
            expressionChecker.check(getText());
            setError(null);
        }
        catch (OWLExpressionParserException e) {
            setError(e);
        }
        catch (OWLException e) {
            logger.error(e);
        }
    }


    private void handleTimer() {
        timer.stop();
        checkExpression();
    }


    private void handleDocumentUpdated() {
        timer.restart();
        clearError();
        performHighlighting();
        notifyValidationChanged(false); // updates always disable until parsed
    }


    private void setError(OWLExpressionParserException e) {
        logger.debug("Set error " + e);
        notifyValidationChanged(e == null); // if no error, then content is valid

        if (e != null) {
            ToolTipManager.sharedInstance().setInitialDelay(ERROR_TOOL_TIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(ERROR_TOOL_TIP_DISMISS_DELAY);
            setToolTipText(getHTMLErrorMessage(e.getMessage()));
            setStateBorder(errorBorder);
            setErrorRange(e.getStartIndex(), e.getEndIndex());
        }
        else {
            clearError();
        }
    }


    private void setErrorRange(int startIndex, int endIndex) {
        errorStartIndex = startIndex;
        errorEndIndex = endIndex;
        repaint();
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        try {
            // Paint in error range if there is one
            if (errorStartIndex != errorEndIndex && errorStartIndex > -1 && errorStartIndex < getDocument().getLength()) {
                Rectangle start = modelToView(errorStartIndex);
                Rectangle end = modelToView(errorEndIndex);
                g.setColor(Color.RED);
                ((Graphics2D) g).setStroke(errorStroke);
                int y = end.y + end.height;
                g.drawLine(start.x, y, end.x + end.width, y);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        g.setColor(oldColor);
    }


    private void performHighlighting() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    final int lineStartIndex = 0;
                    final int lineEndIndex = getDocument().getLength();
                    if (lineEndIndex - lineStartIndex < 0) {
                        return;
                    }
                    StringTokenizer tokenizer = new StringTokenizer(getDocument().getText(lineStartIndex,
                                                                                          lineEndIndex - lineStartIndex),
                                                                    " ()[]{},\n\t.'",
                                                                    true);
                    int index = lineStartIndex;
                    boolean inEscapedName = false;
                    while (tokenizer.hasMoreTokens()) {
                        String curToken = tokenizer.nextToken();
                        if (curToken.equals("'")) {
                            if (inEscapedName) {
                                inEscapedName = false;
                            }
                            else {
                                inEscapedName = true;
                            }
                        }
                        if (!inEscapedName) {
                            Color color = owlEditorKit.getWorkspace().getKeyWordColorMap().get(curToken);
                            if (color == null) {
                                color = Color.BLACK;
                            }
                            getStyledDocument().setCharacterAttributes(index,
                                                                       curToken.length(),
                                                                       getStyledDocument().getStyle(color.toString()),
                                                                       true);
                        }
                        index += curToken.length();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    private static String getHTMLErrorMessage(String msg) {
        String html = "<html><body>";
        msg = msg.replace("\n", "<br>");
        msg = msg.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        html += msg;
        html += "</body></html>";
        return html;
    }


    private void createStyles() {
        for (Color color : owlEditorKit.getWorkspace().getKeyWordColorMap().values()) {
            Style style = getStyledDocument().addStyle(color.toString(), null);
            StyleConstants.setForeground(style, color);
            StyleConstants.setBold(style, true);
        }
        StyleConstants.setForeground(getStyledDocument().addStyle(Color.BLACK.toString(), null), Color.BLACK);
    }


///////////////////////// content verification


    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

    private boolean previousValue = true;

    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
    }

    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyValidationChanged(boolean valid){
        if (valid != previousValue){ // only report changes
            previousValue = valid;
            for (InputVerificationStatusChangedListener l : listeners){
                l.verifiedStatusChanged(valid);
            }
        }
    }
}
