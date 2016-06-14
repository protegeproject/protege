package org.protege.editor.owl.model.search;

import org.protege.editor.owl.ui.renderer.styledstring.StyledString;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ResultItem extends Comparable<ResultItem> {

    SearchCategory getCategory();

    String getGroupDescription();

    OWLObject getSubject();

    String getSubjectRendering();

    String getSearchString();

    StyledString getStyledSearchSearchString();
}
