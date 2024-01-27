package org.protege.editor.owl.ui.usage;

import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.tree.TreeModel;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 23, 2008<br><br>
 */
public interface UsageTreeModel extends TreeModel {

    void setOWLEntity(OWLEntity owlEntity);

    void addFilter(UsageFilter filter);

    void addFilters(Set<UsageFilter> filters);

    void removeFilter(UsageFilter filter);

    void refresh();
}
