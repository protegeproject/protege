package org.protege.editor.owl.ui.framelist;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.renderer.OWLAnnotationRenderer;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLObject;


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


    public OWLFrameListRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        owlCellRenderer = new OWLCellRenderer(owlEditorKit);
        separatorRenderer = new DefaultListCellRenderer();
        annotationRenderer = new OWLAnnotationRenderer();
        highlightKeywords = true;
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void setHighlightKeywords(boolean highlightKeywords) {
        this.highlightKeywords = highlightKeywords;
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

            if (((AbstractOWLFrameSectionRow) value).getAxiom() instanceof OWLAnnotationAxiom) {
                OWLAnnotationAxiom ax = (OWLAnnotationAxiom) ((AbstractOWLFrameSectionRow) value).getAxiom();
                return (JComponent) annotationRenderer.getListCellRendererComponent(list,
                                                                                    ax.getAnnotation(),
                                                                                    index,
                                                                                    isSelected,
                                                                                    cellHasFocus);
            }
//            OWLAxiom ax = ((OWLFrameSectionRow) value).getAxiom();
//            boolean commentedOut = false;
//            for (OWLOntology ont : getOWLEditorKit().getOWLModelManager().getActiveOntologies()) {
//                for(OWLAxiomAnnotationAxiom annoAx : ax.getAnnotationAxioms(ont)) {
//                    if(annoAx.getAnnotation().getAnnotationURI().equals(COMMENTED_OUT_URI)) {
//                        owlCellRenderer.setCommentedOut(true);
//                        commentedOut = true;
//                        break;
//                    }
//                }
//                if(commentedOut) {
//                    break;
//                }
//            }
            boolean commentedOut = false;
            OWLFrameSectionRow row = ((OWLFrameSectionRow) value);
            if (row.getOntology() != null && row.getAxiom() != null) {
                commentedOut = getOWLEditorKit().getOWLModelManager().isCommentedOut(row.getOntology(), row.getAxiom());
            }
            owlCellRenderer.setCommentedOut(commentedOut);
            Object valueToRender = getValueToRender(list, value, index, isSelected, cellHasFocus);
            owlCellRenderer.setIconObject(getIconObject(list, value, index, isSelected, cellHasFocus));
            owlCellRenderer.setOntology(((OWLFrameSectionRow) value).getOntology());
            owlCellRenderer.setTransparent();
            owlCellRenderer.setInferred(((OWLFrameSectionRow) value).isInferred());
            owlCellRenderer.setHighlightKeywords(highlightKeywords);
            return (JComponent) owlCellRenderer.getListCellRendererComponent(list,
                                                                             valueToRender,
                                                                             index,
                                                                             isSelected,
                                                                             cellHasFocus);
        }
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
            AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            StringBuilder sb = new StringBuilder();
            sb.append(row.getPrefix());
            for (Iterator<? extends OWLObject> it = row.getManipulatableObjects().iterator(); it.hasNext();) {
                Object curObj = it.next();
                OWLObjectRenderer ren = getOWLEditorKit().getOWLModelManager().getOWLObjectRenderer();
                OWLEntityRenderer entRen = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();
                sb.append(ren.render(((OWLObject) curObj), entRen));
                if (it.hasNext()) {
                    sb.append(row.getDelimeter());
                }
            }
            sb.append(row.getSuffix());
            return sb.toString();
        }
        return value;
    }
}
