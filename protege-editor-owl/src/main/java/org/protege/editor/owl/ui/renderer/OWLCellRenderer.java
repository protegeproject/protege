package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.model.util.OWLUtilities;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static org.protege.editor.owl.ui.renderer.RenderingEscapeUtils.RenderingEscapeSetting.UNESCAPED_RENDERING;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-May-2006<br><br>
 * <p>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLCellRenderer implements TableCellRenderer, TreeCellRenderer, ListCellRenderer {

    private final Logger logger = LoggerFactory.getLogger(OWLCellRenderer.class);

    private boolean forceReadOnlyRendering;

    private OWLEditorKit owlEditorKit;

    private boolean renderIcon;

    private boolean renderExpression;

    private boolean strikeThrough;

    private OWLOntology ontology;

    private Set<OWLObject> equivalentObjects;

    private LinkedObjectComponent linkedObjectComponent;

    private Font plainFont;

    private Font boldFont;

    public static final Color SELECTION_BACKGROUND = UIManager.getDefaults().getColor("List.selectionBackground");

    public static final Color SELECTION_FOREGROUND = UIManager.getDefaults().getColor("List.selectionForeground");

    public static final Color FOREGROUND = UIManager.getDefaults().getColor("List.foreground");

    private boolean ignoreLinks;
    
    /**
     * to ensure that the renderer is not used recursively accidently
     */
    private boolean isLocked = false;
    
    /**
     * used to copy the settings when running recursively
     */
    private final OWLCellRenderer tmp;

    private List<OWLEntityColorProvider> entityColorProviders;

    // The object that determines which icon should be displayed.
    private OWLObject iconObject;

    private int leftMargin = 0;

    private int rightMargin = 40;

    private JComponent componentBeingRendered;

    private JPanel renderingComponent;

    private final IconComponent iconComponent = new IconComponent();

    private JTextPane textPane;

    private int preferredWidth;

    private int minTextHeight;

    private OWLEntity focusedEntity;

    private boolean commentedOut;

    private boolean feint;

    private boolean highlightKeywords;

    private boolean wrap = true;

    private boolean highlightUnsatisfiableClasses = true;

    private boolean highlightUnsatisfiableProperties = true;

    private Set<OWLEntity> crossedOutEntities;

    private Set<String> unsatisfiableNames;

    private Set<String> boxedNames;

    private boolean opaque = false;


    private class OWLCellRendererPanel extends JPanel {

        private OWLCellRendererPanel(LayoutManager layout) {
            super(layout);
        }
    }

    public OWLCellRenderer(OWLEditorKit owlEditorKit) {
        this(owlEditorKit, true, true);
    }

    
    public OWLCellRenderer(OWLEditorKit owlEditorKit, boolean renderExpression, boolean renderIcon) {
		this(owlEditorKit, renderExpression, renderIcon, new OWLCellRenderer(
				owlEditorKit, renderExpression, renderIcon, null));
    }

    OWLCellRenderer(OWLEditorKit owlEditorKit, boolean renderExpression, boolean renderIcon, OWLCellRenderer tmp) {
        this.owlEditorKit = owlEditorKit;
        this.renderExpression = renderExpression;
        this.renderIcon = renderIcon;
        this.equivalentObjects = new HashSet<>();

        iconComponent.setOpaque(false);

        textPane = new JTextPane();
        textPane.setOpaque(false);

        renderingComponent = new OWLCellRendererPanel(new OWLCellRendererLayoutManager());
        renderingComponent.add(iconComponent);
        renderingComponent.add(textPane);

        entityColorProviders = new ArrayList<>();
        OWLEntityColorProviderPluginLoader loader = new OWLEntityColorProviderPluginLoader(getOWLModelManager());
        for (OWLEntityColorProviderPlugin plugin : loader.getPlugins()) {
            try {
                OWLEntityColorProvider prov = plugin.newInstance();
                prov.initialise();
                entityColorProviders.add(prov);
            } catch (Exception e) {
                logger.error("An error occurred whilst trying to load an OWLEntityColorProviderPlugin", e);
            }
        }
        crossedOutEntities = new HashSet<>();
        unsatisfiableNames = new HashSet<>();
        boxedNames = new HashSet<>();
        prepareStyles();
        setupFont();
        this.tmp = tmp;
    }


    public void setForceReadOnlyRendering(boolean forceReadOnlyRendering) {
        this.forceReadOnlyRendering = forceReadOnlyRendering;
    }


    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }


    public void setUnsatisfiableNames(Set<String> unsatisfiableNames) {
        this.unsatisfiableNames.clear();
        this.unsatisfiableNames.addAll(unsatisfiableNames);
    }


    public void setHighlightKeywords(boolean hightlighKeywords) {
        this.highlightKeywords = hightlighKeywords;
    }


    public void setHighlightUnsatisfiableClasses(boolean highlightUnsatisfiableClasses) {
        this.highlightUnsatisfiableClasses = highlightUnsatisfiableClasses;
    }


    public void setHighlightUnsatisfiableProperties(boolean highlightUnsatisfiableProperties) {
        this.highlightUnsatisfiableProperties = highlightUnsatisfiableProperties;
    }


    public void setOntology(OWLOntology ont) {
        forceReadOnlyRendering = false;
        this.ontology = ont;
    }


    public void setIconObject(OWLObject object) {
        iconObject = object;
    }

    public void setCrossedOutEntities(Set<OWLEntity> entities) {
        crossedOutEntities.addAll(entities);
    }

    public void addBoxedName(String name) {
        boxedNames.add(name);
    }

    public boolean isBoxedName(String name) {
        return boxedNames.contains(name);
    }

    public void reset() {
        iconObject = null;
        rightMargin = 0;
        ontology = null;
        focusedEntity = null;
        commentedOut = false;
        feint = false;
        strikeThrough = false;
        highlightUnsatisfiableClasses = true;
        highlightUnsatisfiableProperties = true;
        crossedOutEntities.clear();
        unsatisfiableNames.clear();
        equivalentObjects.clear();
        boxedNames.clear();
    }
    
	public void save() {
		tmp.iconObject = iconObject;
		tmp.rightMargin = rightMargin;
		tmp.ontology = ontology;
		tmp.focusedEntity = focusedEntity;
		tmp.commentedOut = commentedOut;
		tmp.feint = feint;
		tmp.strikeThrough = strikeThrough;
		tmp.highlightUnsatisfiableClasses = highlightUnsatisfiableClasses;
		tmp.highlightUnsatisfiableProperties = highlightUnsatisfiableProperties;
		tmp.crossedOutEntities.clear();
		tmp.crossedOutEntities.addAll(crossedOutEntities);
		tmp.unsatisfiableNames.clear();
		tmp.unsatisfiableNames.addAll(unsatisfiableNames);
		tmp.equivalentObjects.clear();
		tmp.equivalentObjects.addAll(equivalentObjects);
		tmp.boxedNames.clear();
		tmp.boxedNames.addAll(boxedNames);
		unlock();
	}

	public void restore() {
		lock();
		iconObject = tmp.iconObject;
		rightMargin = tmp.rightMargin;
		ontology = tmp.ontology;
		focusedEntity = tmp.focusedEntity;
		commentedOut= tmp.commentedOut;
		feint = tmp.feint;
		strikeThrough = tmp.strikeThrough;
		highlightUnsatisfiableClasses = tmp.highlightUnsatisfiableClasses;
		highlightUnsatisfiableProperties = tmp.highlightUnsatisfiableProperties;
		crossedOutEntities.clear();
		crossedOutEntities.addAll(tmp.crossedOutEntities);
		unsatisfiableNames.clear();
		unsatisfiableNames.addAll(tmp.unsatisfiableNames);
		equivalentObjects.clear();
		equivalentObjects.addAll(tmp.equivalentObjects);
		boxedNames.clear();
		boxedNames.addAll(tmp.boxedNames);
	}

    public void setFocusedEntity(OWLEntity entity) {
        focusedEntity = entity;
    }


    /**
     * Sets equivalent objects for the object being rendered.  For example,
     * if the object being rendered is A, and B and C are equivalent to A, then
     * setting the equivalent objects to {B, C} will cause the rendering to
     * have (= B = C) appended to it
     *
     * @param objects The objects that are equivalent to the object
     *                being rendered
     */
    public void setEquivalentObjects(Set<OWLObject> objects) {
        equivalentObjects.clear();
        equivalentObjects.addAll(objects);
    }


    /**
     * Specifies whether or not this row displays inferred information (the
     * default value is false)
     */
    public void setInferred(boolean inferred) {
        /*
         * Currently doesn't do anything.  Inferred defaults to false.
    	 */
    }


    public void setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
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


    private void setupFont() {
        plainFont = OWLRendererPreferences.getInstance().getFont();
        boldFont = plainFont.deriveFont(Font.BOLD);
        textPane.setFont(plainFont);
    }

    protected int getFontSize() {
        return OWLRendererPreferences.getInstance().getFontSize();
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

    public void setFeint(boolean feint) {
        this.feint = feint;
    }

    public void setRelationshipsDisplayed(boolean relationshipsDisplayed) {
        iconComponent.setRelationshipsDisplayed(relationshipsDisplayed);
    }

    public void setRelationship(Object relationship) {
        iconComponent.setRelationShip(relationship);
    }

    public void clearRelationship() {
        iconComponent.clearRelationship();
    }


    public boolean isWrap() {
        return wrap;
    }


    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

	void lock() {
		if (isLocked) {
			throw new RuntimeException(
					"Renderer used recursively without saving the settings!");
		}
		isLocked = true;
	}

	void unlock() {
		if (!isLocked) {
			throw new RuntimeException(
					"Renderer was not locked!");
		}
		reset();
		isLocked = false;
	}
    
    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of renderer interfaces
    //
    ////////////////////////////////////////////////////////////////////////////////////////

    private boolean renderLinks;


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
		lock();
		try {
			setupLinkedObjectComponent(table,
					table.getCellRect(row, column, true));
			preferredWidth = table.getParent().getWidth();
			componentBeingRendered = table;
			// Set the size of the table cell
			return prepareRenderer(value, isSelected, hasFocus);
		} finally {
			unlock();
		}
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
		lock();
		try {
			componentBeingRendered = tree;
			Rectangle cellBounds = new Rectangle();
			if (!ignoreLinks) {
				ignoreLinks = true;
				save();
				try {
					cellBounds = tree.getRowBounds(row);
				} finally {
					restore();
				}
				ignoreLinks = false;
			}
			setupLinkedObjectComponent(tree, cellBounds);
			preferredWidth = -1;
			minTextHeight = 12;
			return prepareRenderer(value, selected, hasFocus);
		} finally {
			unlock();
		}
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
		lock();
		try {
			componentBeingRendered = list;
			Rectangle cellBounds = new Rectangle();
			// We need to prevent infinite recursion here!
			if (!ignoreLinks) {
				ignoreLinks = true;
				save();
				try {
					cellBounds = list.getCellBounds(index, index);
				} finally {	
					restore();
				}
				ignoreLinks = false;
			}
			minTextHeight = 12;
			if (list.getParent() != null) {
				preferredWidth = list.getParent().getWidth();
			}
			// preferredWidth = -1;
			// textPane.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2 +
			// rightMargin));
			setupLinkedObjectComponent(list, cellBounds);
			return prepareRenderer(value, isSelected, cellHasFocus);
		} finally {
			unlock();
		}
    }


    private void setupLinkedObjectComponent(JComponent component, Rectangle cellRect) {
        renderLinks = false;
        linkedObjectComponent = null;
        if (cellRect == null) {
            return;
        }
        if (component instanceof LinkedObjectComponent && OWLRendererPreferences.getInstance().isRenderHyperlinks()) {
            linkedObjectComponent = (LinkedObjectComponent) component;
            Point mouseLoc = component.getMousePosition();
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


        public void visit(OWLDatatype dataType) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(dataType).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }


        public void visit(OWLNamedIndividual individual) {
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


        public void visit(OWLAnnotationProperty property) {
            if (!getOWLModelManager().getActiveOntology().getAxioms(property).isEmpty()) {
                ontology = getOWLModelManager().getActiveOntology();
            }
        }
    }


    private ActiveEntityVisitor activeEntityVisitor = new ActiveEntityVisitor();


    private Component prepareRenderer(Object value, boolean isSelected, boolean hasFocus) {
        renderingComponent.setOpaque(isSelected || opaque);

        if (value instanceof OWLEntity) {
            OWLEntity entity = (OWLEntity) value;
            OWLDeclarationAxiom declAx = getOWLModelManager().getOWLDataFactory().getOWLDeclarationAxiom(entity);
            if (getOWLModelManager().getActiveOntology().containsAxiom(declAx)) {
                ontology = getOWLModelManager().getActiveOntology();
            }
            entity.accept(activeEntityVisitor);
            if (getOWLModelManager().isDeprecated(entity)) {
                setStrikeThrough(true);
            }
            else {
                setStrikeThrough(false);
            }
        }


        prepareTextPane(getRendering(value), isSelected);

        if (isSelected) {
            renderingComponent.setBackground(SELECTION_BACKGROUND);
            textPane.setForeground(SELECTION_FOREGROUND);
        }
        else {
            renderingComponent.setBackground(componentBeingRendered.getBackground());
            textPane.setForeground(componentBeingRendered.getForeground());
        }

        final Icon icon = getIcon(value);
        iconComponent.setIcon(icon);
        renderingComponent.revalidate();
        return renderingComponent;
    }


    protected String getRendering(Object object) {
        if (object instanceof OWLObject) {
            String rendering = getOWLModelManager().getDisabmiguatedRendering((OWLObject) object, UNESCAPED_RENDERING);
            for (OWLObject eqObj : equivalentObjects) {
                // Add in the equivalent class symbol
                rendering += " \u2261 " + getOWLModelManager().getRendering(eqObj);
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
        if (!renderIcon) {
            return null;
        }
        if (iconObject != null) {
            return owlEditorKit.getWorkspace().getOWLIconProvider().getIcon(iconObject);
        }
        if (object instanceof OWLObject) {
            return owlEditorKit.getWorkspace().getOWLIconProvider().getIcon((OWLObject) object);
        }
        else {
            return null;
        }
    }


    private Composite disabledComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);


    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
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

    private Style boldStyle;

    private Style nonBoldStyle;

    private Style selectionForeground;

    private Style foreground;

    private Style linkStyle;

    private Style inconsistentClassStyle;

    private Style focusedEntityStyle;

    private Style ontologyURIStyle;

    private Style commentedOutStyle;

    private Style feintStyle;

    private Style strikeOutStyle;

    private Style fontSizeStyle;

    private void prepareStyles() {
        StyledDocument doc = textPane.getStyledDocument();
        Map<String, Color> keyWordColorMap = owlEditorKit.getWorkspace().getKeyWordColorMap();
        for (String keyWord : keyWordColorMap.keySet()) {
            Style s = doc.addStyle(keyWord, null);
            Color color = keyWordColorMap.get(keyWord);
            StyleConstants.setForeground(s, color);
            StyleConstants.setBold(s, true);
        }
        plainStyle = doc.addStyle("PLAIN_STYLE", null);
        StyleConstants.setItalic(plainStyle, false);
        StyleConstants.setSpaceAbove(plainStyle, 0);
        boldStyle = doc.addStyle("BOLD_STYLE", null);
        StyleConstants.setBold(boldStyle, true);


        nonBoldStyle = doc.addStyle("NON_BOLD_STYLE", null);
        StyleConstants.setBold(nonBoldStyle, false);

        selectionForeground = doc.addStyle("SEL_FG_STYPE", null);
        // we know that it is possible for SELECTION_FOREGROUND to be null 
        // and an exception here means that Protege doesn't start
        if (selectionForeground != null && SELECTION_FOREGROUND != null) {
            StyleConstants.setForeground(selectionForeground, SELECTION_FOREGROUND);
        }

        foreground = doc.addStyle("FG_STYLE", null);
        if (foreground != null && FOREGROUND != null) {
            StyleConstants.setForeground(foreground, FOREGROUND);
        }

        linkStyle = doc.addStyle("LINK_STYLE", null);
        StyleConstants.setForeground(linkStyle, Color.BLUE);
        StyleConstants.setUnderline(linkStyle, true);

        inconsistentClassStyle = doc.addStyle("INCONSISTENT_CLASS_STYLE", null);
        StyleConstants.setForeground(inconsistentClassStyle, Color.RED);

        focusedEntityStyle = doc.addStyle("FOCUSED_ENTITY_STYLE", null);
        StyleConstants.setForeground(focusedEntityStyle, Color.BLACK);
        StyleConstants.setBackground(focusedEntityStyle, new Color(220, 220, 250));

        ontologyURIStyle = doc.addStyle("ONTOLOGY_URI_STYLE", null);
        StyleConstants.setForeground(ontologyURIStyle, Color.GRAY);

        commentedOutStyle = doc.addStyle("COMMENTED_OUT_STYLE", null);
        StyleConstants.setForeground(commentedOutStyle, Color.GRAY);
        StyleConstants.setItalic(commentedOutStyle, true);

        feintStyle = doc.addStyle("FEINT_STYLE", null);
        StyleConstants.setForeground(feintStyle, Color.GRAY);

        strikeOutStyle = doc.addStyle("STRIKE_OUT", null);
        StyleConstants.setStrikeThrough(strikeOutStyle, true);
        StyleConstants.setBold(strikeOutStyle, false);

        fontSizeStyle = doc.addStyle("FONT_SIZE", null);
        StyleConstants.setFontSize(fontSizeStyle, 40);
    }


    private void prepareTextPane(Object value, boolean selected) {

        textPane.setBorder(null);
        String theVal = value.toString();
        if (!wrap) {
            theVal = theVal.replace('\n', ' ');
            theVal = theVal.replaceAll(" [ ]+", " ");
        }
        
		TheValueStyler theStyler = new TheValueStyler();
		theVal = theStyler.processTheValue(theVal);
        
        textPane.setText(theVal);
        if (commentedOut) {
            textPane.setText(textPane.getText());
        }
//        textPane.setSize(textPane.getPreferredSize());
        StyledDocument doc = textPane.getStyledDocument();
//        doc.setParagraphAttributes(0, doc.getLength(), linespacingStyle, false);
        resetStyles(doc);

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

        if (strikeThrough) {
            doc.setParagraphAttributes(0, doc.getLength(), strikeOutStyle, false);
        }

        if (ontology != null) {
            if (OWLRendererPreferences.getInstance().isHighlightActiveOntologyStatements() &&
                    getOWLModelManager().getActiveOntology().equals(ontology)) {
                doc.setParagraphAttributes(0, doc.getLength(), boldStyle, false);
            }
            else {
                doc.setParagraphAttributes(0, doc.getLength(), nonBoldStyle, false);
            }
        }
        else {
            textPane.setFont(plainFont);
        }

        if (feint) {
            doc.setParagraphAttributes(0, doc.getLength(), feintStyle, false);
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

        highlightText(doc, selected);
        if (selected) {
            if (selectionForeground != null) {
                doc.setCharacterAttributes(0, doc.getLength(), selectionForeground, false);
            }
        }
        
        theStyler.applyTheValueStyles(doc);
    }


    protected void highlightText(StyledDocument doc, boolean selected) {
        // Highlight text
        StringTokenizer tokenizer = new StringTokenizer(textPane.getText(), " []{}(),\n\t'", true);
        linkRendered = false;
        annotURIRendered = false;
        int tokenStartIndex = 0;
        while (tokenizer.hasMoreTokens()) {
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
            renderToken(curToken, tokenStartIndex, doc, selected);

            tokenStartIndex += curToken.length();
        }
        if (renderLinks && !linkRendered) {
            linkedObjectComponent.setLinkedObject(null);
        }
    }


    private boolean annotURIRendered = false;

    private boolean linkRendered = false;

    private boolean parenthesisRendered = false;

    protected void renderToken(final String curToken,
                               final int tokenStartIndex,
                               final StyledDocument doc,
                               boolean selected) {

        boolean enclosedByBracket = false;
        if (parenthesisRendered) {
            parenthesisRendered = false;
            enclosedByBracket = true;
        }

        OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();

        final int tokenLength = curToken.length();
        Color c = owlEditorKit.getWorkspace().getKeyWordColorMap().get(curToken);
        if (c != null && prefs.isHighlightKeyWords() && highlightKeywords) {
            Style s = doc.getStyle(curToken);
            doc.setCharacterAttributes(tokenStartIndex, tokenLength, s, true);
        }
        else {
            // Not a keyword, so might be an entity (or delim)
            final OWLEntity curEntity = getOWLModelManager().getOWLEntityFinder().getOWLEntity(curToken);
            if (curEntity != null) {
                if (focusedEntity != null && !selected) {
                    if (curEntity.equals(focusedEntity)) {
                        doc.setCharacterAttributes(tokenStartIndex, tokenLength, focusedEntityStyle, true);
                    }
                }
                else if (highlightUnsatisfiableClasses && curEntity instanceof OWLClass) {
                    // If it is a class then paint the word red if the class
                    // is inconsistent
                    try {
                        getOWLModelManager().getReasonerPreferences()
                                            .executeTask(OptionalInferenceTask.SHOW_CLASS_UNSATISFIABILITY,
                                                         () -> {
                                                             OWLReasoner reasoner = getOWLModelManager().getReasoner();
                                                             boolean consistent = reasoner.isConsistent();
                                                             if (!consistent || !getOWLModelManager().getReasoner()
                                                                                                     .isSatisfiable((OWLClass) curEntity)) {
                                                                 // Paint red because of inconsistency
                                                                 doc.setCharacterAttributes(tokenStartIndex,
                                                                                            tokenLength,
                                                                                            inconsistentClassStyle,
                                                                                            true);
                                                             }
                                                         });
                    } catch (Exception e) {
                        logger.error("An error occurred whilst rendering a token. " +
                                             "Token: {}; " +
                                             "Token start index: {}",
                                     curToken,
                                     tokenStartIndex,
                                     e);
                    }

                }
                else if (highlightUnsatisfiableProperties && curEntity instanceof OWLObjectProperty) {
                    highlightPropertyIfUnsatisfiable(curEntity, doc, tokenStartIndex, tokenLength);
                }
                if (getOWLModelManager().isDeprecated(curEntity)) {
                    setStrikeThrough(true);
                }
                else {
                    setStrikeThrough(false);
                }
                strikeoutEntityIfCrossedOut(curEntity, doc, tokenStartIndex, tokenLength);

                if (renderLinks) {
                    renderHyperlink(curEntity, tokenStartIndex, tokenLength, doc);
                }
            }
            else {
                if (highlightUnsatisfiableClasses && unsatisfiableNames.contains(curToken)) {
                    // Paint red because of inconsistency
                    doc.setCharacterAttributes(tokenStartIndex, tokenLength, inconsistentClassStyle, true);
                }
				else {
                    switch (curToken) {
                        case " ":
                            break;
                        case "(":
                            parenthesisRendered = true;
                            break;
                        case ")":
                            break;
                        case "[":
                            break;
                        case "]":
                            break;
                        case "{":
                            break;
                        case "}":
                            break;
                        case ",":
                            break;
                        case "\n":
                            break;
                        case "\t":
                            break;
                        case "'":
                            break;
                        default:
                            // expensive test!
                            if (isOntologyURI(curToken)) {
                                fadeOntologyURI(doc, tokenStartIndex, tokenLength,
                                                enclosedByBracket);
                            }
                    }
                }
            }
        }
    }


    private void renderHyperlink(OWLEntity curEntity, int tokenStartIndex, int tokenLength, StyledDocument doc) {
        try {
            Rectangle startRect = textPane.modelToView(tokenStartIndex);
            Rectangle endRect = textPane.modelToView(tokenStartIndex + tokenLength);
            if (startRect != null && endRect != null) {
                int width = endRect.x - startRect.x;
                int heght = startRect.height;

                Rectangle tokenRect = new Rectangle(startRect.x, startRect.y, width, heght);
                tokenRect.grow(0, -2);
                Point mouseCellLocation = linkedObjectComponent.getMouseCellLocation();
                if (mouseCellLocation != null) {
                    mouseCellLocation = SwingUtilities.convertPoint(renderingComponent,
                                                                    mouseCellLocation,
                                                                    textPane);
                    if (tokenRect.contains(mouseCellLocation)) {
                        doc.setCharacterAttributes(tokenStartIndex, tokenLength, linkStyle, false);
                        linkedObjectComponent.setLinkedObject(curEntity);
                        linkRendered = true;
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private boolean isOntologyURI(String token) {
        try {
            final URI uri = new URI(token);
            if (uri.isAbsolute()) {
                IRI iri = IRI.create(uri);
                OWLOntology ont = getOWLModelManager().getOWLOntologyManager().getOntology(iri);
                if (getOWLModelManager().getActiveOntologies().contains(ont)) {
                    return true;
                }
            }
        } catch (URISyntaxException e) {
            // just dropthough
        }
        return false;
    }


    private void fadeOntologyURI(StyledDocument doc, int tokenStartIndex, int tokenLength, boolean enclosedByBracket) {
        // if surrounded by brackets, also render them in grey
        int start = tokenStartIndex;
        int length = tokenLength;
        if (enclosedByBracket) {
            start--;
            length = length + 2;
        }
        doc.setCharacterAttributes(start, length, ontologyURIStyle, true);
    }


    private void strikeoutEntityIfCrossedOut(OWLEntity entity, StyledDocument doc, int tokenStartIndex,
                                             int tokenLength) {
        if (crossedOutEntities.contains(entity) || strikeThrough) {
            doc.setCharacterAttributes(tokenStartIndex, tokenLength, strikeOutStyle, false);
        }
    }


    private void highlightPropertyIfUnsatisfiable(final OWLEntity entity,
                                                  final StyledDocument doc,
                                                  final int tokenStartIndex,
                                                  final int tokenLength) {
        try {
            getOWLModelManager().getReasonerPreferences()
                                .executeTask(OptionalInferenceTask.SHOW_OBJECT_PROPERTY_UNSATISFIABILITY,
                                             () -> {
                                                 OWLObjectProperty prop = (OWLObjectProperty) entity;
                                                 OWLReasoner reasoner = getOWLModelManager().getReasoner();
                                                 boolean consistent = reasoner.isConsistent();
                                                 if (!consistent || reasoner.getBottomObjectPropertyNode()
                                                                            .contains(prop)) {
                                                     doc.setCharacterAttributes(tokenStartIndex,
                                                                                tokenLength,
                                                                                inconsistentClassStyle,
                                                                                true);
                                                 }
                                             });
        } catch (Exception e) {
            logger.warn("An error occurred whilst highlighting an unsatisfiable property: {}", e);
        }
    }


    private void resetStyles(StyledDocument doc) {
        doc.setParagraphAttributes(0, doc.getLength(), plainStyle, true);
        StyleConstants.setFontSize(fontSizeStyle, getFontSize());
        Font f = OWLRendererPreferences.getInstance().getFont();
        StyleConstants.setFontFamily(fontSizeStyle, f.getFamily());
        doc.setParagraphAttributes(0, doc.getLength(), fontSizeStyle, false);
        setupFont();
    }


    private class OWLCellRendererLayoutManager implements LayoutManager2 {


        /**
         * Adds the specified component to the layout, using the specified
         * constraint object.
         *
         * @param comp        the component to be added
         * @param constraints where/how the component is added to the layout.
         */
        public void addLayoutComponent(Component comp, Object constraints) {
            // We only have three components the label that holds the icon
            // the text area
        }


        /**
         * Calculates the maximum size dimensions for the specified container,
         * given the components it contains.
         *
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
         *
         * @param name the string to be associated with the component
         * @param comp the component to be added
         */
        public void addLayoutComponent(String name, Component comp) {
        }


        /**
         * Removes the specified component from the layout.
         *
         * @param comp the component to be removed
         */
        public void removeLayoutComponent(Component comp) {
        }


        /**
         * Calculates the preferred size dimensions for the specified
         * container, given the components it contains.
         *
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
            Dimension preferredSize = iconComponent.getPreferredSize();
            iconWidth = preferredSize.width + 2;
            iconHeight = preferredSize.height;
            Insets rcInsets = renderingComponent.getInsets();

            if (preferredWidth != -1) {
                textWidth = preferredWidth - iconWidth - rcInsets.left - rcInsets.right;
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
            int totalWidth = width + rcInsets.left + rcInsets.right;
            int totalHeight = height + rcInsets.top + rcInsets.bottom;
            return new Dimension(totalWidth + 10, totalHeight);
        }

        /**
         * Lays out the specified container.
         *
         * @param parent the container to be laid out
         */
        public void layoutContainer(Container parent) {
            int iconWidth;
            int iconHeight;
            int textWidth;
            int textHeight;
            int deprecatedWidth;
            int deprecatedHeight;
            Insets rcInsets = renderingComponent.getInsets();

            Dimension iconPreferredSize = iconComponent.getPreferredSize();
            if (preferredWidth != -1) {
                textWidth = preferredWidth - iconPreferredSize.width - rcInsets.left - rcInsets.right;
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
            int leftOffset = rcInsets.left;
            int topOffset = rcInsets.top;
            int textPaneY = topOffset;
            iconComponent.setBounds(leftOffset, topOffset, iconPreferredSize.width, iconPreferredSize.height);
            textPane.setBounds(leftOffset + iconPreferredSize.width + 2, textPaneY, textWidth, textHeight);
        }

        /**
         * Calculates the minimum size dimensions for the specified
         * container, given the components it contains.
         *
         * @param parent the component to be laid out
         * @see #preferredLayoutSize
         */
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }
    }


    private static final class IconComponent extends JPanel {

        private static final BasicStroke RELATIONSHIP_STROKE = new BasicStroke(1.5f);

        private static final Color UNSPECIFIED_RELATIONSHIP_COLOR = new Color(190, 190, 190);

        @Nullable
        private Icon icon;

        private Dimension preferredSize = new Dimension();

        @Nullable
        private Object relationship = null;

        private boolean relationshipsDisplayed;

        public void setRelationshipsDisplayed(boolean relationshipsDisplayed) {
            this.relationshipsDisplayed = relationshipsDisplayed;
            updatePreferredSize();
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
            updatePreferredSize();
        }

        private void updatePreferredSize() {
            int prefWidth = 0;
            int prefHeight = 0;
            if (icon != null) {
                prefHeight = icon.getIconHeight();
                prefWidth = icon.getIconWidth();
            }
            if (relationshipsDisplayed) {
                prefWidth += 18;
            }
            preferredSize.width = prefWidth;
            preferredSize.height = prefHeight;
        }

        public void setRelationShip(Object relationShip) {
            this.relationship = relationShip;
            updatePreferredSize();
        }

        public void clearRelationship() {
            this.relationship = null;
            updatePreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            return preferredSize;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int xOffset = 1;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            if (relationshipsDisplayed) {
                g2.setStroke(RELATIONSHIP_STROKE);
                if (relationship != null) {
                    g.setColor(OWLSystemColors.getOWLObjectPropertyColor());
                }
                else {
                    g.setColor(UNSPECIFIED_RELATIONSHIP_COLOR);
                }
                // Paint a left pointing arrow
                int lineY = preferredSize.height / 2;
                g.drawLine(xOffset + 5, lineY, xOffset + 14, lineY);
                g.drawLine(xOffset + 4, lineY, xOffset + 6, lineY - 2);
                g.drawLine(xOffset + 4, lineY, xOffset + 6, lineY + 2);
                xOffset += 16;
            }

            if (icon != null) {
                icon.paintIcon(this, g, xOffset, 0);
            }
        }
    }
    
    private class TheValueStyler {

		private String[] tagNames = { "demph" };
		private Style[] styles = { feintStyle };

		private List<Integer> startIdxs = new ArrayList<>();
		private List<Integer> endIdxs = new ArrayList<>();
		private List<Style> foundStyles = new ArrayList<>();

		public String processTheValue(String theVal) {
			for (int i = 0; i < tagNames.length; i++) {
				String tagName = tagNames[i];
				String startTag = "<" + tagName + ">";
				String endTag = "</" + tagName + ">";

				while (theVal.contains(startTag)) {
					int startIdx = theVal.indexOf(startTag);
					int endIdx = theVal.indexOf(endTag, startIdx);

					theVal = theVal.substring(0, startIdx)
							+ theVal.substring(startIdx + startTag.length(), endIdx)
							+ theVal.substring(endIdx + endTag.length());
					
					startIdxs.add(startIdx);
					endIdxs.add(endIdx - startTag.length());
					foundStyles.add(styles[i]);
				}
			}

			return theVal;
		}

		public void applyTheValueStyles(StyledDocument doc) {
			for (int i = 0; i < startIdxs.size(); i++) {
				int startIdx = startIdxs.get(i);
				int endIdx = endIdxs.get(i);
				Style style = foundStyles.get(i);

				doc.setCharacterAttributes(startIdx, endIdx - startIdx, style, false);
			}
		}
	}
}
