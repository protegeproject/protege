package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owl.model.OWLAxiom;

import java.util.Comparator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrameSection<R, A extends OWLAxiom, E> extends OWLFrameObject<R, A, E>, MListSectionHeader {

    void dispose();


    OWLFrame<? extends R> getFrame();


    void setRootObject(R rootObject);


    String getLabel();


    String getRowLabel(OWLFrameSectionRow row);


    R getRootObject();


    /**
     * Gets the rows that this section contains.
     */
    List<OWLFrameSectionRow<R, A, E>> getRows();


    /**
     * Gets the index of the specified section row.
     * @param row The row whose index is to be obtained.
     * @return The index of the row, or -1 if the row is
     *         not contained within this section.
     */
    int getRowIndex(OWLFrameSectionRow row);


    OWLObjectEditor<E> getEditor();


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    Comparator<OWLFrameSectionRow<R, A, E>> getRowComparator();
}
