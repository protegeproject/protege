package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;


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
public class ExpressionEditor<O> extends JTextPane {


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

    private int errorCheckDelay = 1000;

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
                timer.restart();
                clearError();
                performHighlighting();
            }


            public void removeUpdate(DocumentEvent e) {
                timer.restart();
                clearError();
                performHighlighting();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        };
        getDocument().addDocumentListener(docListener);
        timer = new Timer(errorCheckDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.debug("Timer fired");
                timer.stop();
                checkExpression();
            }
        });
        OWLDescriptionAutoCompleter completer = new OWLDescriptionAutoCompleter(owlEditorKit, this, checker);
        createStyles();
    }


    public void setExpressionObject(O desc) {
        if (desc == null) {
            setText("");
        }
        else if (desc instanceof OWLObject) {
            OWLModelManager man = getOWLEditorKit().getOWLModelManager();
            String rendering = man.getOWLObjectRenderer().render((OWLObject) desc, man.getOWLEntityRenderer());
            setText(rendering);
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


    public O createObject() throws OWLExpressionParserException, OWLException {
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

//    /**
//     * Called by the editor to check that the specified text is valid.  Override
//     * this method to alter the parse behaviour.
//     * @param text The text to check
//     * @throws OWLException If there was a problem parsing the text
//     * @throws OWLDescriptionParserException If the text did not parse correctly.  The
//     * description should contain details of why the text didn't parse correctly
//     */
//    protected void parse(String text), OWLDescriptionParserException {
//        OWLDescriptionParser parser = parserFactory.getParser();
//        parser.isWellFormed(text);
//    }


    private void setError(OWLExpressionParserException e) {
        logger.debug("Set error " + e);
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
            if (errorStartIndex != errorEndIndex && errorStartIndex > -1) {
                Rectangle start = modelToView(errorStartIndex);
                Rectangle end = modelToView(errorEndIndex);
                g.setColor(Color.RED);
                ((Graphics2D) g).setStroke(errorStroke);
                int y = end.y + end.height;
                g.drawLine(start.x, y, end.x + end.width, y);
            }
        }
        catch (BadLocationException e) {
            logger.error(e);
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
                            Color color = owlEditorKit.getOWLWorkspace().getKeyWordColorMap().get(curToken);
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
                    logger.error(e);
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
        for (Color color : owlEditorKit.getOWLWorkspace().getKeyWordColorMap().values()) {
            Style style = getStyledDocument().addStyle(color.toString(), null);
            StyleConstants.setForeground(style, color);
            StyleConstants.setBold(style, true);
        }
        StyleConstants.setForeground(getStyledDocument().addStyle(Color.BLACK.toString(), null), Color.BLACK);
    }
}
