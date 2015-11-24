package org.protege.editor.owl.model.search;

import org.protege.editor.owl.ui.renderer.styledstring.StyledString;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public interface ResultItem extends Comparable<ResultItem> {

    SearchCategory getCategory();

    String getGroupDescription();

    OWLObject getSubject();

    String getSubjectRendering();

    String getSearchString();

    StyledString getStyledSearchSearchString();
}
