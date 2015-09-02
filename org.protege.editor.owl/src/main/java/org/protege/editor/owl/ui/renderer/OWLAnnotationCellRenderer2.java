package org.protege.editor.owl.ui.renderer;

import org.coode.string.EscapeUtils;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.AbstractAnnotationsList;
import org.protege.editor.owl.ui.renderer.layout.*;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.protege.editor.owl.ui.renderer.InlineAnnotationRendering.*;
import static org.protege.editor.owl.ui.renderer.InlineDatatypeRendering.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 14/11/2011
 * <p>
 * A replacement for {@link OWLAnnotationCellRenderer}.  This version renderes annotations using {@link Page}
 * objects and helps to avoid the problem of wrapping and overflow errors that arose with the old renderer.
 * </p>
 */
public class OWLAnnotationCellRenderer2 extends PageCellRenderer {

    public static final Color ANNOTATION_PROPERTY_FOREGROUND = new Color(65, 108, 226);

    private OWLEditorKit editorKit;

    private Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\b");

    private OWLOntology ontology;

    private InlineAnnotationRendering inlineAnnotationRendering = RENDER_COMPOUND_ANNOTATIONS_INLINE;

    private InlineDatatypeRendering datatypeRendering = RENDER_DATATYPE_INLINE;

    private AnnotationRenderingStyle annotationRenderingStyle = AnnotationRenderingStyle.COMFORTABLE;

    public OWLAnnotationCellRenderer2(OWLEditorKit editorKit) {
        super();
        this.editorKit = editorKit;
    }

    public void setInlineAnnotationRendering(InlineAnnotationRendering inlineAnnotationRendering) {
        if (this.inlineAnnotationRendering != inlineAnnotationRendering) {
            this.inlineAnnotationRendering = inlineAnnotationRendering;
            invalidateCache();
        }
    }

    public void setInlineDatatypeRendering(InlineDatatypeRendering datatypeRendering) {
        if (this.datatypeRendering != datatypeRendering) {
            this.datatypeRendering = datatypeRendering;
            invalidateCache();
        }
    }

    public void setAnnotationRenderingStyle(AnnotationRenderingStyle annotationRenderingStyle) {
        if (this.annotationRenderingStyle != annotationRenderingStyle) {
            this.annotationRenderingStyle = annotationRenderingStyle;
            invalidateCache();
        }
    }

    /**
     * Sets a reference ontology to provide a context for the rendering.  The renderer may render certain things differently
     * depending on whether this is equal to the active ontology or not.
     * @param ontology The ontology.
     */
    public void setReferenceOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    /**
     * Clears the reference ontology.
     * @see #setReferenceOntology(OWLOntology)
     */
    public void clearReferenceOntology() {
        ontology = null;
    }

    /**
     * Determines if the reference ontology (if set) is equal to the active ontology.
     * @return <code>true</code> if the reference ontology is equal to the active ontology, otherwise <code>false</code>.
     */
    public boolean isReferenceOntologyActive() {
        return ontology != null && ontology.equals(editorKit.getOWLModelManager().getActiveOntology());
    }

    @Override
    protected Object getValueKey(Object value) {
        OWLAnnotation annotation = null;
        if(value instanceof OWLAnnotationAssertionAxiom) {
            OWLAnnotationAssertionAxiom axiom = (OWLAnnotationAssertionAxiom) value;
            if(axiom.getAnnotations().isEmpty()) {
                return axiom.getAnnotations();
            }
            else {
                return axiom;
            }
        }
        else if (value instanceof AbstractAnnotationsList.AnnotationsListItem) {
            annotation = ((AbstractAnnotationsList.AnnotationsListItem) value).getAnnotation();
        }
        else if (value instanceof OWLAnnotation) {
            annotation = (OWLAnnotation) value;
        }
        return annotation;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  JTable Cell Rendering
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void fillPage(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color foreground = isSelected ? table.getSelectionForeground() : table.getForeground();
        Color background = isSelected ? table.getSelectionBackground() : table.getBackground();
        renderCellValue(page, value, foreground, background, isSelected);
    }

    @Override
    protected int getMaxAvailablePageWidth(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return table.getColumnModel().getColumn(column).getWidth();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  JList Cell Rendering
    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void fillPage(final Page page, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();
        Color background = isSelected ? list.getSelectionBackground() : list.getBackground();
        renderCellValue(page, value, foreground, background, isSelected);
    }

    @Override
    protected int getMaxAvailablePageWidth(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Insets insets = list.getInsets();//OWLFrameList.ITEM_BORDER.getBorderInsets();
        int componentWidth = list.getWidth();
        JViewport vp = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, list);
        if(vp != null) {
            componentWidth = vp.getViewRect().width;
        }

        return componentWidth - list.getInsets().left - list.getInsets().right - insets.left + insets.right - 20;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Renderes a list or table cell value if the value contains an OWLAnnotation.
     * @param page The page that the value will be rendered into.
     * @param value The value that may or may not contain an OWLAnnotation.  The annotation will be extracted from
     * this value.
     * @param foreground The default foreground color.
     * @param background The default background color.
     * @param isSelected Whether or not the cell containing the value is selected.
     */
    private void renderCellValue(Page page, Object value, Color foreground, Color background, boolean isSelected) {
        OWLAnnotation annotation = extractOWLAnnotationFromCellValue(value);
        if (annotation != null) {
            renderAnnotationProperty(page, annotation, foreground, background, isSelected);
            renderAnnotationValue(page, annotation, foreground, background, isSelected);
            if (inlineAnnotationRendering == RENDER_COMPOUND_ANNOTATIONS_INLINE &&  value instanceof HasAnnotations) {
                Page subAnnotationPage = new Page();
                for(OWLAnnotation anno : ((HasAnnotations) value).getAnnotations()) {
                    renderCellValue(subAnnotationPage, anno, foreground, background, isSelected);
                }
                subAnnotationPage.setMarginLeft(40);
                subAnnotationPage.setOpacity(0.6);
                page.add(subAnnotationPage);
            }
        }
        switch (annotationRenderingStyle){
            case COMFORTABLE:
                page.setMargin(2);
                page.setMarginBottom(6);
                break;
            case COSY:
                page.setMargin(1);
                page.setMarginBottom(3);
                break;
            case COMPACT:
                page.setMargin(0);
                page.setMarginBottom(1);
        }

    }

    /**
     * Extracts an OWLAnnotation from the actual value held in a cell in a list or table.
     * @param value The list or table cell value.
     * @return The OWLAnnotation contained within the value.
     */
    protected OWLAnnotation extractOWLAnnotationFromCellValue(Object value) {
        OWLAnnotation annotation = null;
        if(value instanceof OWLAnnotationAssertionAxiom) {
            annotation = ((OWLAnnotationAssertionAxiom) value).getAnnotation();
        }
        else if (value instanceof AbstractAnnotationsList.AnnotationsListItem) {
            annotation = ((AbstractAnnotationsList.AnnotationsListItem) value).getAnnotation();
        }
        else if (value instanceof OWLAnnotation) {
            annotation = (OWLAnnotation) value;
        }
        return annotation;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Formats paragraphs that were generated as a result of rendering an annotation value with global formatting such
     * as tab count and margins.  This ensures that all paragraphs representing annotation values have the correct
     * indentation etc.
     * @param valueRenderingParagraphs The paragraphs to be formatted.
     */
    private void applyGlobalFormattingToAnnotationValueParagraphs(List<Paragraph> valueRenderingParagraphs) {
        for (Paragraph paragraph : valueRenderingParagraphs) {
            paragraph.setTabCount(0);
            paragraph.setMarginBottom(2);
        }
    }

    /**
     * Renders the annotation property into a paragraph in the page.
     * @param page The page to insert the paragraph into.
     * @param annotation The annotation containing the property to be rendered.
     * @param defaultForeground The default foreground color.
     * @param defaultBackground The default background color.
     * @param isSelected Specifies whether the associated cell is selected or not.
     */
    private Paragraph renderAnnotationProperty(Page page, OWLAnnotation annotation, Color defaultForeground, Color defaultBackground, boolean isSelected) {
        OWLAnnotationProperty property = annotation.getProperty();
        String rendering = editorKit.getOWLModelManager().getRendering(property);
        Paragraph paragraph = page.addParagraph(rendering);
        Color foreground = getAnnotationPropertyForeground(defaultForeground, isSelected);
        paragraph.setForeground(foreground);
//        if (isReferenceOntologyActive()) {
//            paragraph.setBold(true);
//        }
        if (annotation.getValue() instanceof OWLLiteral) {
            OWLLiteral literalValue = (OWLLiteral) annotation.getValue();
            paragraph.append("    ", foreground);
            appendTag(paragraph, literalValue, foreground, isSelected);
        }
        switch (annotationRenderingStyle) {
            case COMFORTABLE:
                paragraph.setMarginBottom(4);
                break;
            case COSY:
                paragraph.setMarginBottom(2);
                break;
            case COMPACT:
                paragraph.setMarginBottom(1);
                break;
        }
        return paragraph;
    }

    private Color getAnnotationPropertyForeground(Color defaultForeground, boolean isSelected) {
        return isSelected ? defaultForeground : ANNOTATION_PROPERTY_FOREGROUND;
    }

    /**
     * Renders an annotation value into a {@link Page}.
     * @param page The page that the value should be rendered into.
     * @param annotation The annotation that contains the value to be rendered.
     * @param defaultForeground The default foreground color.
     * @param defaultBackground The default background color.
     * @param isSelected Whether or not the cell containing the annotation is selected.
     * @return A list of paragraphs that represent the rendering of the annotation value.  These paragraphs will have
     * been added to the Page specified by the page argument.
     */
    private List<Paragraph> renderAnnotationValue(final Page page, OWLAnnotation annotation, final Color defaultForeground, final Color defaultBackground, final boolean isSelected) {
        OWLAnnotationValue annotationValue = annotation.getValue();
        List<Paragraph> paragraphs = annotationValue.accept(new OWLAnnotationValueVisitorEx<List<Paragraph>>() {
            public List<Paragraph> visit(IRI iri) {
                return renderIRI(page, iri, defaultForeground, defaultBackground, isSelected, hasFocus());
            }

            public List<Paragraph> visit(OWLAnonymousIndividual individual) {
                return renderAnonymousIndividual(page, individual);
            }

            public List<Paragraph> visit(OWLLiteral literal) {
                return renderLiteral(page, literal, defaultForeground, defaultBackground, isSelected);
            }
        });
        applyGlobalFormattingToAnnotationValueParagraphs(paragraphs);
        return paragraphs;
    }

    /**
     * Renderes an annotation value that is an IRI
     * @param page The page that the value will be rendered into.
     * @param iri The IRI that is the annotation value.
     * @param defaultForeground The default foreground color.
     * @param defaultBackgound The default background color.
     * @param isSelected Whether or not the cell containing the annotation is selected.
     * @param hasFocus Whether or not the cell containing the annotation has the focus.
     * @return A list of paragraphs that represent the rendering of the annotation value.
     */
    private List<Paragraph> renderIRI(Page page, IRI iri, Color defaultForeground, Color defaultBackgound, boolean isSelected, boolean hasFocus) {
        OWLModelManager modelManager = editorKit.getOWLModelManager();
        Set<OWLEntity> entities = modelManager.getOWLEntityFinder().getEntities(iri);
        List<Paragraph> paragraphs;
        if (entities.isEmpty()) {
            paragraphs = renderExternalIRI(page, iri);
        }
        else {
            paragraphs = renderEntities(page, entities);
        }
        return paragraphs;
    }

    /**
     * Determines whether an IRI that represents an annotation value can be opened in a web browser. i.e. wherther or
     * not the IRI represents a web link.
     * @param iri The iri to be tested.
     * @return <code>true</code> if the IRI represents a web link, other wise <code>false</code>.
     */
    private boolean isLinkableAddress(IRI iri) {
        String scheme = iri.getScheme();
        return scheme != null && scheme.startsWith("http");
    }

    /**
     * Renders an IRI as a full IRI rather than as an IRI that represents an entity in the signature of the imports
     * closure of the active ontology.
     * @param page The page that the IRI should be rendered into.
     * @param iri The IRI to be rendered.
     * @return A list of paragraphs that represent the rendering of the annotation value.
     */
    private List<Paragraph> renderExternalIRI(Page page, IRI iri) {
        Paragraph paragraph;
        if (isLinkableAddress(iri)) {
            paragraph = page.addParagraph(iri.toString(), new HTTPLink(iri.toURI()));
        }
        else {
            paragraph = page.addParagraph(iri.toString());
        }
        return Arrays.asList(paragraph);
    }

    /**
     * Renderes a set of entities as an annotation value.  The idea is that the annotation value is an IRI that
     * corresponds to the IRI of entities in the imports closure of the active ontology.
     * @param page The page that the entities will be rendered into.
     * @param entities The entities.
     * @return A list of paragraphs that represents the rendering of the entities.
     */
    private List<Paragraph> renderEntities(Page page, Set<OWLEntity> entities) {
        List<Paragraph> paragraphs = new ArrayList<Paragraph>();
        for (OWLEntity entity : entities) {
            Icon icon = getIcon(entity);
            OWLModelManager modelManager = editorKit.getOWLModelManager();
            Paragraph paragraph = new Paragraph(modelManager.getRendering(entity), new OWLEntityLink(editorKit, entity));
            paragraph.setIcon(icon);
            page.add(paragraph);
            paragraphs.add(paragraph);
        }
        return paragraphs;
    }

    /**
     * Gets the icon for an entity.
     * @param entity The entity.
     * @return The icon or null if the entity does not have an icon.
     */
    private Icon getIcon(OWLObject entity) {
        return editorKit.getOWLWorkspace().getOWLIconProvider().getIcon(entity);
    }

    /**
     * Renders an annotation value that is an OWLLiteral.
     * @param page The page that the value will be rendered into.
     * @param literal The literal to be rendered.
     * @param foreground The default foreground.
     * @param background The default background.
     * @param isSelected Whether or not the cell containing the annotation value is selected.
     * @return A list of paragraphs that represent the rendering of the literal.
     */
    private List<Paragraph> renderLiteral(Page page, OWLLiteral literal, Color foreground, Color background, boolean isSelected) {
        String rendering = EscapeUtils.unescapeString(literal.getLiteral()).trim();
        List<Paragraph> result = new ArrayList<Paragraph>();
        if (rendering.length() > 0) {
            List<LinkSpan> linkSpans = extractLinks(rendering);
            Paragraph literalParagraph = new Paragraph(rendering, linkSpans);
            literalParagraph.setForeground(foreground);
            page.add(literalParagraph);
            result.add(literalParagraph);
            Paragraph tagParagraph = literalParagraph;//new Paragraph("");
            tagParagraph.append("    ", foreground);
            page.add(tagParagraph);
            result.add(tagParagraph);
            tagParagraph.setMarginTop(2);
            tagParagraph.setTabCount(2);
//            appendTag(tagParagraph, literal, foreground, isSelected);
        }
        return result;
    }

    private void appendTag(Paragraph tagParagraph, OWLLiteral literal, Color foreground, boolean isSelected) {
        Color tagColor = isSelected ? foreground : Color.GRAY;
        Color tagValueColor = isSelected ? foreground : Color.GRAY;
        if (literal.hasLang()) {
            tagParagraph.append("[language: ", tagColor);
            tagParagraph.append(literal.getLang(), tagValueColor);
            tagParagraph.append("]", tagColor);
        }
        else if(datatypeRendering == RENDER_DATATYPE_INLINE && !literal.isRDFPlainLiteral()) {
            tagParagraph.append("[type: ", tagColor);
            tagParagraph.append(editorKit.getOWLModelManager().getRendering(literal.getDatatype()), tagValueColor);
            tagParagraph.append("]", tagColor);
        }
//        if (ontology != null) {
//            tagParagraph.append("    ", foreground);
//            tagParagraph.append("[in: ", tagColor);
//            String ontologyRendering = editorKit.getOWLModelManager().getRendering(ontology);
//            tagParagraph.append(ontologyRendering, tagColor);
//            tagParagraph.append("]", tagColor);
//        }
    }


    /**
     * Extracts links from a piece of text.
     * @param s The string that represents the piece of text.
     * @return A (possibly empty) list of links.
     */
    private List<LinkSpan> extractLinks(String s) {
        Matcher matcher = URL_PATTERN.matcher(s);
        List<LinkSpan> result = new ArrayList<LinkSpan>();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String url = s.substring(start, end);
            try {
                result.add(new LinkSpan(new HTTPLink(new URI(url)), new Span(start, end)));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Renders an annotation value that is an anonymous individual.
     * @param page The page that the individual should be rendered into.
     * @param individual The individual.
     * @return A list of paragraphs that represent the rendering of the individual.
     */
    private List<Paragraph> renderAnonymousIndividual(Page page, OWLAnonymousIndividual individual) {
        String rendering = editorKit.getOWLModelManager().getRendering(individual);
        Paragraph paragraph = page.addParagraph(rendering);
        paragraph.setIcon(getIcon(individual));
        return Arrays.asList(paragraph);
    }




}
