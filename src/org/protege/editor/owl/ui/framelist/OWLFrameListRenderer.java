package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.renderer.OWLAnnotationRenderer;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLFrameListRenderer implements ListCellRenderer {

    private OWLEditorKit owlEditorKit;

    private OWLCellRenderer owlCellRenderer;

    private ListCellRenderer separatorRenderer;

    private OWLAnnotationRenderer annotationRenderer;

    private boolean highlightKeywords;

    private boolean highlightUnsatisfiableClasses;

    private boolean highlightUnsatisfiableProperties;

    private Set<OWLEntity> crossedOutEntities;

    private boolean annotationRendererEnabled;

    public OWLFrameListRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        owlCellRenderer = new OWLCellRenderer(owlEditorKit);
        separatorRenderer = new DefaultListCellRenderer();
        annotationRenderer = new OWLAnnotationRenderer(owlEditorKit);
        highlightKeywords = true;
        annotationRendererEnabled = true;
        crossedOutEntities = new HashSet<OWLEntity>();
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

            if (((AbstractOWLFrameSectionRow) value).getAxiom() instanceof OWLAnnotationAxiom &&
                    annotationRendererEnabled) {
                OWLAnnotationAxiom ax = (OWLAnnotationAxiom) ((AbstractOWLFrameSectionRow) value).getAxiom();
                return annotationRenderer.getListCellRendererComponent(list,
                                                                       ax.getAnnotation(),
                                                                       index,
                                                                       isSelected,
                                                                       cellHasFocus);
            }

            boolean commentedOut = false;
            OWLFrameSectionRow row = ((OWLFrameSectionRow) value);
            owlCellRenderer.setCommentedOut(commentedOut);
            Object valueToRender = getValueToRender(list, value, index, isSelected, cellHasFocus);
            owlCellRenderer.setIconObject(getIconObject(list, value, index, isSelected, cellHasFocus));
            owlCellRenderer.setOntology(((OWLFrameSectionRow) value).getOntology());
            owlCellRenderer.setTransparent();
            owlCellRenderer.setInferred(((OWLFrameSectionRow) value).isInferred());
            owlCellRenderer.setHighlightKeywords(highlightKeywords);
            owlCellRenderer.setHighlightUnsatisfiableClasses(highlightUnsatisfiableClasses);
            owlCellRenderer.setCrossedOutEntities(crossedOutEntities);
//            if(row.getOntology() != null) {
//                if(!row.getOntology().containsAxiom(row.getAxiom())) {
//                    owlCellRenderer.setStrikeThrough(true);
//                }
//            }
            return owlCellRenderer.getListCellRendererComponent(list,
                                                                valueToRender,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
        }
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
            ((AbstractOWLFrameSectionRow) value).getRendering();
        }
        return value;
    }
}
