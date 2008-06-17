package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.semanticweb.owl.model.OWLAxiom;

import java.util.Comparator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrameSection<R extends Object, A extends OWLAxiom, E> extends OWLFrameObject<R, A, E>, MListSectionHeader {

    void dispose();


    OWLFrame<? extends R> getFrame();


    void setRootObject(R rootObject);


    String getLabel();


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


    /**
     * @deprecated use <code>canAdd</code> instead
     * Determines if rows can be added to this section.
     * @return <code>true</code> if rows can be added to this section,
     *         or <code>false</code> if rows cannot be added to this section.
     */
    boolean canAddRows();


    OWLFrameSectionRowObjectEditor<E> getEditor();


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    Comparator<OWLFrameSectionRow<R, A, E>> getRowComparator();
}
