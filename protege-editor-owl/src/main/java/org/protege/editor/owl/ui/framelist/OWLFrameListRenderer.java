package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.list.RendererWithInsets;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.renderer.*;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static org.protege.editor.owl.ui.renderer.InlineAnnotationRendering.DO_NOT_RENDER_COMPOUND_ANNOTATIONS_INLINE;
import static org.protege.editor.owl.ui.renderer.InlineAnnotationRendering.RENDER_COMPOUND_ANNOTATIONS_INLINE;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLFrameListRenderer implements ListCellRenderer, RendererWithInsets {

    private OWLEditorKit owlEditorKit;

    private OWLCellRenderer owlCellRenderer;

    private ListCellRenderer separatorRenderer;

    private OWLAnnotationCellRenderer2 annotationRenderer;

    private boolean highlightKeywords;

    private boolean highlightUnsatisfiableClasses;

    private boolean highlightUnsatisfiableProperties;

    private Set<OWLEntity> crossedOutEntities;

    private boolean annotationRendererEnabled;

    public OWLFrameListRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        owlCellRenderer = new OWLCellRenderer(owlEditorKit);
        separatorRenderer = new DefaultListCellRenderer();
        annotationRenderer = new OWLAnnotationCellRenderer2(owlEditorKit);
        highlightKeywords = true;
        highlightUnsatisfiableClasses = true;
        highlightUnsatisfiableProperties = true;
        annotationRendererEnabled = true;
        crossedOutEntities = new HashSet<>();
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void setHighlightKeywords(boolean highlightKeywords) {
        this.highlightKeywords = highlightKeywords;
    }


    public void setAnnotationRendererEnabled(boolean enabled) {
        this.annotationRendererEnabled = enabled;
    }


    public OWLCellRenderer getOWLCellRenderer() {
        return owlCellRenderer;
    }

    public void setHighlightUnsatisfiableClasses(boolean b) {
        this.highlightUnsatisfiableClasses = b;
    }


    public boolean isHighlightUnsatisfiableClasses() {
        return highlightUnsatisfiableClasses;
    }


    public boolean isHighlightUnsatisfiableProperties() {
        return highlightUnsatisfiableProperties;
    }


    public void setHighlightUnsatisfiableProperties(boolean highlightUnsatisfiableProperties) {
        this.highlightUnsatisfiableProperties = highlightUnsatisfiableProperties;
    }


    public void setCrossedOutEntities(Set<OWLEntity> entities) {
        this.crossedOutEntities.clear();
        this.crossedOutEntities.addAll(entities);
    }

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see javax.swing.JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {


        if (value instanceof OWLFrameSection) {
            JLabel label = (JLabel) separatorRenderer.getListCellRendererComponent(list,
                                                                                   " ",
                                                                                   index,
                                                                                   isSelected,
                                                                                   cellHasFocus);
            label.setVerticalAlignment(JLabel.TOP);
            return label;
        }
        else {
            final AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            final OWLAxiom axiom = row.getAxiom();
            if (axiom instanceof OWLAnnotationAssertionAxiom && annotationRendererEnabled) {
                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
                annotationRenderer.setReferenceOntology(row.getOntology());
                annotationRenderer.setInlineAnnotationRendering(
                        getRenderAnnotationAnnotationsInline());
                annotationRenderer.setInlineDatatypeRendering(
                        getAnnotationLiteralDatatypeRendering()
                );
                return annotationRenderer.getListCellRendererComponent(list,
                                                                       annotationAssertionAxiom,
                                                                       index,
                                                                       isSelected,
                                                                       cellHasFocus);
            }

            boolean commentedOut = false;
            owlCellRenderer.setCommentedOut(commentedOut);
            Object valueToRender = getValueToRender(list, value, index, isSelected, cellHasFocus);
            owlCellRenderer.setIconObject(getIconObject(list, value, index, isSelected, cellHasFocus));
            owlCellRenderer.setOntology(((OWLFrameSectionRow) value).getOntology());
            owlCellRenderer.setInferred(((OWLFrameSectionRow) value).isInferred());
            owlCellRenderer.setHighlightKeywords(highlightKeywords);
            owlCellRenderer.setHighlightUnsatisfiableClasses(highlightUnsatisfiableClasses);
            owlCellRenderer.setCrossedOutEntities(crossedOutEntities);
            return owlCellRenderer.getListCellRendererComponent(list,
                                                                valueToRender,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
        }
    }

    private InlineDatatypeRendering getAnnotationLiteralDatatypeRendering() {
        return OWLRendererPreferences.getInstance().isDisplayLiteralDatatypesInline() ? InlineDatatypeRendering.RENDER_DATATYPE_INLINE : InlineDatatypeRendering.DO_NOT_RENDER_DATATYPE_INLINE;
    }

    private InlineAnnotationRendering getRenderAnnotationAnnotationsInline() {
        return OWLRendererPreferences.getInstance().isDisplayAnnotationAnnotationsInline() ? RENDER_COMPOUND_ANNOTATIONS_INLINE : DO_NOT_RENDER_COMPOUND_ANNOTATIONS_INLINE;
    }

    public void setWrap(boolean b) {
        owlCellRenderer.setWrap(b);
    }


    protected OWLObject getIconObject(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof AbstractOWLFrameSectionRow) {
            AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            if (!row.getManipulatableObjects().isEmpty()) {
                Object firstObject = row.getManipulatableObjects().iterator().next();
                if (firstObject instanceof OWLObject) {
                    return (OWLObject) firstObject;
                }
            }
        }
        return null;
    }


    protected Object getValueToRender(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof AbstractOWLFrameSectionRow) {
            value = ((AbstractOWLFrameSectionRow) value).getRendering();
        }
        return value;
    }
}
