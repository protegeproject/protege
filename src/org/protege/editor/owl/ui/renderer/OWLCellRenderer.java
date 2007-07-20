package org.protege.editor.owl.ui.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.tree.TreeCellRenderer;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLCellRenderer implements TableCellRenderer, TreeCellRenderer, ListCellRenderer {

    private static final Logger logger = Logger.getLogger(OWLCellRenderer.class);

    private boolean forceReadOnlyRendering;

    private OWLEditorKit owlEditorKit;

    private boolean renderIcon;

    private boolean renderExpression;

    private int indentation;

    private OWLOntology ontology;

    private Set<OWLObject> equivalentObjects;

    private LinkedObjectComponent linkedObjectComponent;

    private Font plainFont;

    private Font boldFont;

    public static final Color SELECTION_BACKGROUND = UIManager.getDefaults().getColor("List.selectionBackground");

    public static final Color SELECTION_FOREGROUND = UIManager.getDefaults().getColor("List.selectionForeground");

    public static final Color FOREGROUND = UIManager.getDefaults().getColor("List.foreground");

    private boolean trasparent;

    private List<OWLEntityColorProvider> entityColorProviders;

    // The object that determines which icon should be displayed.
    private OWLObject iconObject;

    private int leftMargin = 0;

    private int rightMargin = 40;

    private JComponent componentBeingRendered;

    private JPanel renderingComponent;

    private JLabel iconLabel;

    private boolean stylesApplied;

    private JTextPane textPane = new JTextPane() {
        protected void paintComponent(Graphics g) {
            if (!stylesApplied) {
                applyStyles((StyledDocument) getDocument());
            }
            super.paintComponent(g);
        }
    };

    private int preferredWidth;

    private int minTextHeight;

    private OWLEntity focusedEntity;

    private boolean commentedOut;

    private boolean inferred;

    private boolean highlightKeywords;


    public OWLCellRenderer(OWLEditorKit owlEditorKit) {
        this(owlEditorKit, true, true);
    }


    public OWLCellRenderer(OWLEditorKit owlEditorKit, boolean renderExpression, boolean renderIcon) {
        this(owlEditorKit, renderExpression, renderIcon, 0);
    }


    public void setForceReadOnlyRendering(boolean forceReadOnlyRendering) {
        this.forceReadOnlyRendering = forceReadOnlyRendering;
    }


    public void setTransparent() {
        trasparent = true;
    }


    public void setHighlightKeywords(boolean hightlighKeywords) {
        this.highlightKeywords = hightlighKeywords;
    }


    public void setOntology(OWLOntology ont) {
        forceReadOnlyRendering = false;
        this.ontology = ont;
    }


    public void setIconObject(OWLObject object) {
        iconObject = object;
    }


    public void reset() {
        iconObject = null;
        trasparent = false;
        rightMargin = 0;
        ontology = null;
        focusedEntity = null;
        commentedOut = false;
        inferred = false;
//        highlightKeywords = true;
    }


    public void setFocusedEntity(OWLEntity entity) {
        focusedEntity = entity;
    }


    public void setEquivalentObjects(Set<OWLObject> objects) {
        equivalentObjects.clear();
        equivalentObjects.addAll(objects);
    }


    public void setInferred(boolean inferred) {
        this.inferred = inferred;
    }


    public int getPreferredWidth() {
        return preferredWidth;
    }


    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }


    public int getRightMargin() {
        return rightMargin;
    }


    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }


    public OWLCellRenderer(OWLEditorKit owlEditorKit, boolean renderExpression, boolean renderIcon, int indentation) {
        this.owlEditorKit = owlEditorKit;
        this.renderExpression = renderExpression;
        this.renderIcon = renderIcon;
        this.equivalentObjects = new HashSet<OWLObject>();
        setFontSize(owlEditorKit.getWorkspace().getFontSize());
        renderingComponent = new JPanel(new OWLCellRendererLayoutManager());
        renderingComponent.setOpaque(false);
        iconLabel = new JLabel("");
        iconLabel.setVerticalAlignment(JLabel.TOP);
        renderingComponent.add(iconLabel);
        renderingComponent.add(textPane);
        iconLabel.setOpaque(false);
        renderingComponent.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        entityColorProviders = new ArrayList<OWLEntityColorProvider>();
        OWLEntityColorProviderPluginLoader loader = new OWLEntityColorProviderPluginLoader(getOWLModelManager());
        for (OWLEntityColorProviderPlugin plugin : loader.getPlugins()) {
            try {
                OWLEntityColorProvider prov = plugin.newInstance();
                prov.initialise();
                entityColorProviders.add(prov);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
        this.indentation = indentation;
        prepareStyles();
    }


    private void setFontSize(int fontSize) {
        plainFont = new Font("lucida grande", Font.PLAIN, fontSize);
        boldFont = plainFont.deriveFont(Font.BOLD);
    }


    public boolean isRenderExpression() {
        return renderExpression;
    }


    public boolean isRenderIcon() {
        return renderIcon;
    }


    public void setCommentedOut(boolean commentedOut) {
        this.commentedOut = commentedOut;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of renderer interfaces
    //
    ////////////////////////////////////////////////////////////////////////////////////////

    private boolean renderLinks;


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setupLinkedObjectComponent(table, table.getCellRect(row, column, true));
        preferredWidth = table.getParent().getWidth();
        componentBeingRendered = table;
        // Set the size of the table cell
//        setPreferredWidth(table.getColumnModel().getColumn(column).getWidth());
        return prepareRenderer(value, isSelected, hasFocus);

//        // This is a bit messy - the row height doesn't get reset if it is larger than the
//        // desired row height.
//        // Reset the row height if the text has been wrapped
//        int desiredRowHeight = getPrefSize(table, table.getGraphics(), c.getText()).height;
//        if (desiredRowHeight < table.getRowHeight()) {
//            desiredRowHeight = table.getRowHeight();
//        }
//        else if (desiredRowHeight > table.getRowHeight(row)) {
//            // Add a bit of a margin, because wrapped lines
//            // tend to merge with adjacent lines too much
//            desiredRowHeight += 4;
//        }
//        if (table.getEditingRow() != row) {
//            if (table.getRowHeight(row) < desiredRowHeight) {
//                table.setRowHeight(row, desiredRowHeight);
//            }
//        }
//        reset();
//        return c;
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        componentBeingRendered = tree;
        Rectangle cellBounds = new Rectangle();
        if (!gettingCellBounds) {
            gettingCellBounds = true;
            cellBounds = tree.getRowBounds(row);
            gettingCellBounds = false;
        }
        setupLinkedObjectComponent(tree, cellBounds);
        preferredWidth = -1;
        minTextHeight = 12;
//        textPane.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2 + rightMargin));
        tree.setToolTipText(value != null ? value.toString() : "");
        Component c = prepareRenderer(value, selected, hasFocus);
        textPane.setOpaque(true);
        if (trasparent) {
            textPane.setOpaque(false);
        }
        reset();
        return c;
    }


    private boolean gettingCellBounds;


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        componentBeingRendered = list;
        Rectangle cellBounds = new Rectangle();
        // We need to prevent infinite recursion here!
        if (!gettingCellBounds) {
            gettingCellBounds = true;
            cellBounds = list.getCellBounds(index, index);
            gettingCellBounds = false;
        }
        minTextHeight = 12;
        if (list.getParent() != null) {
            preferredWidth = list.getParent().getWidth();
        }
//        preferredWidth = -1;
//        textPane.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2 + rightMargin));
        setupLinkedObjectComponent(list, cellBounds);
        Component c = prepareRenderer(value, isSelected, cellHasFocus);
        textPane.setOpaque(true);
        renderingComponent.setOpaque(true);
        if (trasparent) {
            textPane.setOpaque(false);
        }
        reset();
        return c;
    }


    private void setupLinkedObjectComponent(JComponent component, Rectangle cellRect) {
        renderLinks = false;
        linkedObjectComponent = null;
        if (cellRect == null) {
            return;
        }
        if (component instanceof LinkedObjectComponent && OWLRendererPreferences.getInstance().isRenderHyperlinks()) {
            linkedObjectComponent = (LinkedObjectComponent) component;
            Point mouseLoc = component.getMousePosition(true);
            if (mouseLoc == null) {
                linkedObjectComponent.setLinkedObject(null);
                return;
            }
            renderLinks = cellRect.contains(mouseLoc);
        }
    }


    private class ActiveEntityVisitor implements OWLEntityVisitor {

        public void visit(OWLClass cls) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(cls).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }


        public void visit(OWLDataType dataType) {
        }


        public void visit(OWLIndividual individual) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(individual).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }


        public void visit(OWLDataProperty property) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(property).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }


        public void visit(OWLObjectProperty property) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(property).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }
    }


    private ActiveEntityVisitor activeEntityVisitor = new ActiveEntityVisitor();


    private Component prepareRenderer(Object value, boolean isSelected, boolean hasFocus) {
        if (value instanceof OWLEntity) {
            OWLEntity entity = (OWLEntity) value;
            OWLDeclarationAxiom declAx = getOWLModelManager().getOWLDataFactory().getOWLDeclarationAxiom(entity);
            if (getOWLModelManager().getActiveOntology().containsAxiom(declAx)) {
                ontology = getOWLModelManager().getActiveOntology();
            }
            entity.accept(activeEntityVisitor);
        }
        String rendering = "";
        if (value instanceof OWLObject) {
            rendering = getOWLModelManager().getOWLObjectRenderer().render((OWLObject) value,
                                                                           getOWLModelManager().getOWLEntityRenderer());
        }
        else if (value != null) {
            rendering = value.toString();
        }
        for (OWLObject equivObj : equivalentObjects) {
            rendering += " \u2261 ";
            rendering += getOWLModelManager().getOWLObjectRenderer().render(equivObj,
                                                                            getOWLModelManager().getOWLEntityRenderer());
        }

        prepareTextPane(rendering, isSelected);
        if (isSelected) {
            textPane.setBackground(SELECTION_BACKGROUND);
            textPane.setForeground(SELECTION_FOREGROUND);
            renderingComponent.setBackground(SELECTION_BACKGROUND);
        }
        else {
            textPane.setBackground(componentBeingRendered.getBackground());
            textPane.setForeground(componentBeingRendered.getForeground());
            renderingComponent.setBackground(componentBeingRendered.getBackground());
        }
        iconLabel.setIcon(getIcon(value));
        renderingComponent.revalidate();
        return renderingComponent;
    }


    protected String getRendering(Object object) {
        if (object instanceof OWLObject) {
            String rendering = getOWLModelManager().getOWLObjectRenderer().render(((OWLObject) object),
                                                                                  getOWLModelManager().getOWLEntityRenderer());
            for (OWLObject eqObj : equivalentObjects) {
                // Add in the equivalent class symbol
                rendering += " \u2261 " + getOWLModelManager().getOWLObjectRenderer().render(eqObj,
                                                                                             getOWLModelManager().getOWLEntityRenderer());
            }
            return rendering;
        }
        else {
            if (object != null) {
                return object.toString();
            }
            else {
                return "";
            }
        }
    }


    protected Icon getIcon(Object object) {
        if (iconObject != null) {
            return owlEditorKit.getOWLWorkspace().getOWLIconProvider().getIcon(iconObject);
        }
        if (object instanceof OWLObject) {
            return owlEditorKit.getOWLWorkspace().getOWLIconProvider().getIcon((OWLObject) object);
        }
        else {
            return null;
        }
    }


    private Composite disabledComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);


    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getOWLModelManager();
    }


    protected Color getColor(OWLEntity entity, Color defaultColor) {
        for (OWLEntityColorProvider prov : entityColorProviders) {
            Color c = prov.getColor(entity);
            if (c != null) {
                return c;
            }
        }
        return defaultColor;
    }


    protected boolean activeOntologyContainsAxioms(OWLEntity owlEntity) {
        return !getOWLModelManager().getActiveOntology().getReferencingAxioms(owlEntity).isEmpty();
    }


    private Style plainStyle;

    private Style selectionForeground;

    private Style foreground;

    private Style linkStyle;

    private Style inconsistentClassStyle;

    private Style focusedEntityStyle;

//    private Style linespacingStyle;

    private Style commentedOutStyle;

    private Style inferredInformationOutOfDate;


    private void prepareStyles() {
        StyledDocument doc = textPane.getStyledDocument();
        Map<String, Color> keyWordColorMap = owlEditorKit.getOWLWorkspace().getKeyWordColorMap();
        for (String keyWord : keyWordColorMap.keySet()) {
            Style s = doc.addStyle(keyWord, null);
            Color color = keyWordColorMap.get(keyWord);
            StyleConstants.setForeground(s, color);
            StyleConstants.setBold(s, true);
        }
        plainStyle = doc.addStyle("PLAIN_STYLE", null);
//        StyleConstants.setForeground(plainStyle, Color.BLACK);
        StyleConstants.setItalic(plainStyle, false);
        StyleConstants.setSpaceAbove(plainStyle, 0);

        selectionForeground = doc.addStyle("SEL_FG_STYPE", null);
        StyleConstants.setForeground(selectionForeground, SELECTION_FOREGROUND);

        foreground = doc.addStyle("FG_STYLE", null);
        StyleConstants.setForeground(foreground, FOREGROUND);

        linkStyle = doc.addStyle("LINK_STYLE", null);
        StyleConstants.setForeground(linkStyle, Color.BLUE);
        StyleConstants.setUnderline(linkStyle, true);

        inconsistentClassStyle = doc.addStyle("INCONSISTENT_CLASS_STYLE", null);
        StyleConstants.setForeground(inconsistentClassStyle, Color.RED);

        focusedEntityStyle = doc.addStyle("FOCUSED_ENTITY_STYLE", null);
        StyleConstants.setForeground(focusedEntityStyle, Color.BLACK);
        StyleConstants.setBackground(focusedEntityStyle, new Color(220, 220, 250));

//        linespacingStyle = doc.addStyle("LINE_SPACING_STYLE", null);
//        StyleConstants.setLineSpacing(linespacingStyle, 0.0f);

        commentedOutStyle = doc.addStyle("COMMENTED_OUT_STYLE", null);
        StyleConstants.setForeground(commentedOutStyle, Color.GRAY);
        StyleConstants.setItalic(commentedOutStyle, true);

        inferredInformationOutOfDate = doc.addStyle("INFERRED_INFO_OUT_OF_DATE", null);
        StyleConstants.setStrikeThrough(inferredInformationOutOfDate, true);
    }


    private void prepareTextPane(Object value, boolean selected) {
        textPane.setBorder(null);
        textPane.setText(value.toString());
        stylesApplied = false;
        if (commentedOut) {
            textPane.setText("// " + textPane.getText());
        }
//        textPane.setSize(textPane.getPreferredSize());
        StyledDocument doc = textPane.getStyledDocument();
//        doc.setParagraphAttributes(0, doc.getLength(), linespacingStyle, false);
        doc.setParagraphAttributes(0, doc.getLength(), plainStyle, false);
        if (selected) {
            doc.setParagraphAttributes(0, doc.getLength(), selectionForeground, false);
        }
        else {
            doc.setParagraphAttributes(0, doc.getLength(), foreground, false);
        }

        if (commentedOut) {
            doc.setParagraphAttributes(0, doc.getLength(), commentedOutStyle, false);
            return;
        }
        else if (inferred) {
            try {
                if (!getOWLModelManager().getReasoner().isClassified()) {
                    doc.setParagraphAttributes(0, doc.getLength(), inferredInformationOutOfDate, false);
                }
                else {
                    doc.setParagraphAttributes(0, doc.getLength(), plainStyle, false);
                }
            }
            catch (OWLReasonerException e) {
                e.printStackTrace();
            }
        }
        else {
            doc.setParagraphAttributes(0, doc.getLength(), plainStyle, false);
        }
        if (ontology != null) {
            if (OWLRendererPreferences.getInstance().isHighlightActiveOntologyStatements() && getOWLModelManager().getActiveOntology().equals(
                    ontology)) {
                textPane.setFont(boldFont);
            }
            else {
                textPane.setFont(plainFont);
            }
        }
        else {
            textPane.setFont(plainFont);
        }

        // Set the writable status
        if (ontology != null) {
            if (getOWLModelManager().isMutable(ontology)) {
                textPane.setEnabled(!forceReadOnlyRendering);
            }
            else {
                // Not editable - set readonly
                textPane.setEnabled(false);
            }
        }
        else {
            // Ontology is null.  If the object is an entity then the font
            // should be bold if there are statements about it
            if (value instanceof OWLEntity) {
                if (activeOntologyContainsAxioms((OWLEntity) value)) {
                    textPane.setFont(boldFont);
                }
            }
        }
    }


    private void applyStyles(StyledDocument doc) {
        stylesApplied = true;
        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
        // Highlight text
        StringTokenizer tokenizer = new StringTokenizer(textPane.getText(), " []{}(),.\n'", true);
        OWLEntity curEntity = null;
        boolean linkRendered = false;
        int tokenStartIndex = 0;
        while (tokenizer.hasMoreTokens()) {
            curEntity = null;
            // Get the token and determine if it is a keyword or
            // entity (or delimeter)
            String curToken = tokenizer.nextToken();
            if (curToken.equals("'")) {
                while (tokenizer.hasMoreTokens()) {
                    String s = tokenizer.nextToken();
                    curToken += s;
                    if (s.equals("'")) {
                        break;
                    }
                }
            }
            int tokenLength = curToken.length();
            Color c = owlEditorKit.getOWLWorkspace().getKeyWordColorMap().get(curToken);
            if (c != null && prefs.isHighlightKeyWords() && highlightKeywords) {
                Style s = doc.getStyle(curToken);
                doc.setCharacterAttributes(tokenStartIndex, tokenLength, s, true);
            }
            else {
                // Not a keyword, so might be an entity (or delim)
                OWLEntity entity = getOWLModelManager().getOWLEntity(curToken);
                curEntity = entity;
                if (entity != null) {
                    boolean styleSet = false;
                    if (focusedEntity != null) {
                        if (entity.equals(focusedEntity)) {
                            doc.setCharacterAttributes(tokenStartIndex, tokenLength, focusedEntityStyle, true);
                        }
                    }
                    else if (entity instanceof OWLClass) {
                        // If it is a class then paint the word red if the class
                        // is inconsistent
                        try {
                            if (!getOWLModelManager().getReasoner().isSatisfiable((OWLClass) entity)) {
                                // Paint red because of inconsistency
                                doc.setCharacterAttributes(tokenStartIndex, tokenLength, inconsistentClassStyle, true);
                                styleSet = true;
                            }
                        }
                        catch (OWLReasonerException e) {
                            e.printStackTrace();
                            //                            throw new OWLRuntimeException(e);
                        }
                    }
                    // We did have a check here for changed entities
                    if (!styleSet) {
                        //                        C = getColor(entity, textPane.getForeground());
                    }
                }
            }
            try {
                boolean isLink = false;
                if (curEntity != null && renderLinks) {
                    Rectangle startRect = textPane.modelToView(tokenStartIndex);
                    Rectangle endRect = textPane.modelToView(tokenStartIndex + tokenLength);
                    if (startRect != null && endRect != null) {
                        int width = endRect.x - startRect.x;
                        int heght = startRect.height;

                        Rectangle tokenRect = new Rectangle(startRect.x, startRect.y, width, heght);
                        tokenRect.grow(0, -2);
                        if (linkedObjectComponent.getMouseCellLocation() != null) {
                            Point mouseCellLocation = linkedObjectComponent.getMouseCellLocation();
                            if (mouseCellLocation != null) {
                                mouseCellLocation = SwingUtilities.convertPoint(renderingComponent,
                                                                                mouseCellLocation,
                                                                                textPane);
                                if (tokenRect.contains(mouseCellLocation)) {
                                    doc.setCharacterAttributes(tokenStartIndex, tokenLength, linkStyle, false);
                                    linkedObjectComponent.setLinkedObject(curEntity);
                                    isLink = true;
                                    linkRendered = true;
                                }
                            }
                        }
                    }
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            ;

            tokenStartIndex += tokenLength;
        }
        if (renderLinks && !linkRendered) {
            linkedObjectComponent.setLinkedObject(null);
        }
    }


    private class OWLCellRendererLayoutManager implements LayoutManager2 {


        /**
         * Adds the specified component to the layout, using the specified
         * constraint object.
         * @param comp        the component to be added
         * @param constraints where/how the component is added to the layout.
         */
        public void addLayoutComponent(Component comp, Object constraints) {
            // We only have two components the label that holds the icon
            // and the text area
        }


        /**
         * Calculates the maximum size dimensions for the specified container,
         * given the components it contains.
         * @see java.awt.Component#getMaximumSize
         * @see java.awt.LayoutManager
         */
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }


        /**
         * Returns the alignment along the x axis.  This specifies how
         * the component would like to be aligned relative to other
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentX(Container target) {
            return 0;
        }


        /**
         * Returns the alignment along the y axis.  This specifies how
         * the component would like to be aligned relative to other
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentY(Container target) {
            return 0;
        }


        /**
         * Invalidates the layout, indicating that if the layout manager
         * has cached information it should be discarded.
         */
        public void invalidateLayout(Container target) {
        }


        /**
         * If the layout manager uses a per-component string,
         * adds the component <code>comp</code> to the layout,
         * associating it
         * with the string specified by <code>name</code>.
         * @param name the string to be associated with the component
         * @param comp the component to be added
         */
        public void addLayoutComponent(String name, Component comp) {
        }


        /**
         * Removes the specified component from the layout.
         * @param comp the component to be removed
         */
        public void removeLayoutComponent(Component comp) {
        }


        /**
         * Calculates the preferred size dimensions for the specified
         * container, given the components it contains.
         * @param parent the container to be laid out
         * @see #minimumLayoutSize
         */
        public Dimension preferredLayoutSize(Container parent) {
            if (componentBeingRendered instanceof JList) {
                JList list = (JList) componentBeingRendered;
                if (list.getFixedCellHeight() != -1) {
                    return new Dimension(list.getWidth(), list.getHeight());
                }
            }
            int iconWidth;
            int iconHeight;
            int textWidth;
            int textHeight;
            int width;
            int height;
            iconWidth = iconLabel.getPreferredSize().width;
            iconHeight = iconLabel.getPreferredSize().height;
            Insets insets = parent.getInsets();
            if (preferredWidth != -1) {
                textWidth = preferredWidth - iconWidth;
                View v = textPane.getUI().getRootView(textPane);
                v.setSize(textWidth, Integer.MAX_VALUE);
                textHeight = (int) v.getMinimumSpan(View.Y_AXIS);
                width = preferredWidth;
            }
            else {
                textWidth = textPane.getPreferredSize().width;
                textHeight = textPane.getPreferredSize().height;
                width = textWidth + iconWidth;
            }
            if (textHeight < iconHeight) {
                height = iconHeight;
            }
            else {
                height = textHeight;
            }
            int minHeight = minTextHeight;
            if (height < minHeight) {
                height = minHeight;
            }
            return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
        }


        /**
         * Calculates the minimum size dimensions for the specified
         * container, given the components it contains.
         * @param parent the component to be laid out
         * @see #preferredLayoutSize
         */
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }


        /**
         * Lays out the specified container.
         * @param parent the container to be laid out
         */
        public void layoutContainer(Container parent) {
            int iconWidth;
            int iconHeight;
            int textWidth;
            int textHeight;
            Insets insets = parent.getInsets();
            iconWidth = iconLabel.getPreferredSize().width;
            iconHeight = iconLabel.getPreferredSize().height;
            if (preferredWidth != -1) {
                textWidth = preferredWidth - iconWidth;
                View v = textPane.getUI().getRootView(textPane);
                v.setSize(textWidth, Integer.MAX_VALUE);
                textHeight = (int) v.getMinimumSpan(View.Y_AXIS);
            }
            else {
                textWidth = textPane.getPreferredSize().width;
                textHeight = textPane.getPreferredSize().height;
                if (textHeight < minTextHeight) {
                    textHeight = minTextHeight;
                }
            }
            iconLabel.setBounds(insets.left, insets.top, iconWidth, iconHeight);
            textPane.setBounds(insets.left + iconWidth, insets.top, textWidth, textHeight);
        }
    }
}
