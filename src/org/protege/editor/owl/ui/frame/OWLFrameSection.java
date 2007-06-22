package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;

import java.util.Comparator;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrameSection<R extends OWLObject, A extends OWLAxiom, E> extends OWLFrameObject<R, A, E>, MListSectionHeader {

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
