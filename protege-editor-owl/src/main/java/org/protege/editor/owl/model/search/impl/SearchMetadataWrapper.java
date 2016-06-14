package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.ResultItem;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class SearchMetadataWrapper implements ResultItem {

    private SearchMetadata searchMetadata;

    public SearchMetadataWrapper(SearchMetadata searchMetadata) {
        this.searchMetadata = searchMetadata;
    }

    @Override
    public SearchCategory getCategory() {
        return searchMetadata.getCategory();
    }

    @Override
    public String getGroupDescription() {
        return searchMetadata.getGroupDescription();
    }

    @Override
    public OWLObject getSubject() {
        return searchMetadata.getSubject();
    }

    @Override
    public String getSubjectRendering() {
        return searchMetadata.getSubjectRendering();
    }

    @Override
    public String getSearchString() {
        return searchMetadata.getSearchString();
    }

    @Override
    public StyledString getStyledSearchSearchString() {
        return searchMetadata.getStyledSearchSearchString();
    }

    @Override
    public int compareTo(ResultItem other) {
        int catDiff = this.getCategory().compareTo(other.getCategory());
        if (catDiff != 0) {
            return catDiff;
        }
        int typeDiff = this.getSubjectRendering().compareTo(other.getSubjectRendering());
        if (typeDiff != 0) {
            return typeDiff;
        }
        int subjectRenderingDiff = this.getSubjectRendering().compareTo(other.getSubjectRendering());
        if (subjectRenderingDiff != 0) {
            return subjectRenderingDiff;
        }
        int subjectDiff = this.getSubject().compareTo(other.getSubject());
        if (subjectDiff != 0) {
            return subjectDiff;
        }
        return getSearchString().compareTo(other.getSearchString());
    }
}
