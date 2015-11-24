package org.protege.editor.owl.model.search.lucene;

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
public class ResultDocument implements ResultItem {

    private SearchCategory category;

    private String groupDescription;

    private OWLObject subject;

    private String subjectRendering;

    private String searchString;

    public ResultDocument(SearchCategory category, String groupDescription, OWLObject subject, String subjectRendering, String searchString) {
        this.category = category;
        this.groupDescription = groupDescription;
        this.subject = subject;
        this.subjectRendering = subjectRendering;
        this.searchString = searchString;
    }

    @Override
    public SearchCategory getCategory() {
        return category;
    }

    @Override
    public String getGroupDescription() {
        return groupDescription;
    }

    @Override
    public OWLObject getSubject() {
        return subject;
    }

    @Override
    public String getSubjectRendering() {
        return subjectRendering;
    }

    @Override
    public String getSearchString() {
        return searchString;
    }

    @Override
    public StyledString getStyledSearchSearchString() {
        return new StyledString(searchString);
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

    @Override
    public int hashCode() {
        return ResultDocument.class.getSimpleName().hashCode() + category.hashCode()
             + groupDescription.hashCode() + subject.hashCode()
             + subjectRendering.hashCode() + searchString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResultDocument)) {
            return false;
        }
        ResultDocument other = (ResultDocument) obj;
        return category.equals(other.category) && groupDescription.equals(other.groupDescription)
                && subject.equals(other.subject) && subjectRendering.equals(other.subjectRendering)
                && searchString.equals(other.searchString);
    }

    @Override
    public String toString() {
        return category.name() + " " + subjectRendering + " " + searchString;
    }
}
