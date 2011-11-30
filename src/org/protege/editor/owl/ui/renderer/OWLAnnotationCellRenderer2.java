package org.protege.editor.owl.ui.renderer;

import org.coode.string.EscapeUtils;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.list.AbstractAnnotationsList;
import org.protege.editor.owl.ui.renderer.layout.*;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final Color ANNOTATION_PROPERTY_FOREGROUND = Color.BLUE.darker();

    private OWLEditorKit editorKit;

    private Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\b");

    public OWLAnnotationCellRenderer2(OWLEditorKit editorKit) {
        super();
        this.editorKit = editorKit;
    }

    @Override
    protected Object getValueKey(Object value) {
        return extractOWLAnnotationFromCellValue(value);
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
        return list.getWidth() - list.getInsets().left - list.getInsets().right;
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
        }
        page.setMargin(2);

    }

    /**
     * Extracts an OWLAnnotation from the actual value held in a cell in a list or table.
     * @param value The list or table cell value.
     * @return The OWLAnnotation contained within the value.
     */
    protected OWLAnnotation extractOWLAnnotationFromCellValue(Object value) {
        OWLAnnotation annotation = null;
        if (value instanceof AbstractAnnotationsList.AnnotationsListItem) {
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
            paragraph.setTabCount(1);
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
    private void renderAnnotationProperty(Page page, OWLAnnotation annotation, Color defaultForeground, Color defaultBackground, boolean isSelected) {
        OWLAnnotationProperty property = annotation.getProperty();
        String rendering = editorKit.getOWLModelManager().getRendering(property);
        Paragraph paragraph = page.addParagraph(rendering);
        Color foreground = getAnnotationPropertyForeground(defaultForeground, isSelected);
        paragraph.setForeground(foreground);
        paragraph.setBold(true);
        paragraph.setMarginBottom(4);
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
        String rendering = editorKit.getOWLModelManager().getRendering(literal);
        rendering = EscapeUtils.unescapeString(rendering);
        List<LinkSpan> linkSpans = extractLinks(rendering);
        AttributedString attributedRendering = new AttributedString(rendering);
        attributedRendering.addAttribute(TextAttribute.FOREGROUND, foreground);
        Paragraph paragraph = new Paragraph(attributedRendering, linkSpans);
        page.add(paragraph);
        return Arrays.asList(paragraph);
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
