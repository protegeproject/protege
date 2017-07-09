package org.protege.editor.owl.model.search;

import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 * <p>
 * A SearchMetadata object contains a (partial) string representation of some object that can be searched.  There
 * are various categories of SearchMetadata objects as defined by the {@link SearchCategory} enum.  Further
 * more, each SearchMetadata object can belong to a subgroup.
 * </p>
 */
public class SearchMetadata implements Comparable<SearchMetadata> {

    private SearchCategory category;

    private String groupDescription;


    private OWLObject subject;

    private String subjectRendering;

    private String searchString;

    /**
     * Records search metadata for a given object.
     * @param category The category which the search metadata falls into.
     * @param groupDescription The description (human readable name) of the subgroup which the metadata falls into.
     * @param subject The subject to which the search string pertains to.  This is usually an entity or an ontology
     * i.e.
     * something which can be selected in Protege.
     * @param subjectRendering A rendering of the subject.  This rendering is used to compare search metadata objects.
     * @param searchString The string that should be searched.
     */
    public SearchMetadata(SearchCategory category, String groupDescription, OWLObject subject, String subjectRendering, String searchString) {
        this.category = category;
        this.groupDescription = groupDescription;
        this.subject = subject;
        this.subjectRendering = subjectRendering;
        this.searchString = searchString;
    }


    /**
     * Gets the category which this SearchMetadata belongs to.
     * @return The SearchMetadataCategory.  Not <code>null</code>.
     */
    public SearchCategory getCategory() {
        return category;
    }

    /**
     * Gets the group description of this SearchMetadata object.
     * @return The group description.  Not <code>null</code>.
     */
    public String getGroupDescription() {
        return groupDescription;
    }

    /**
     * Gets the subject of this SearchMetadata object.  The subject is the main object that can be selected in protege
     * to which the search string pertains.  For example, the subject of a data property assertion would be the subject
     * individual.
     * @return The subject.  Not <code>null</code>.
     */
    public OWLObject getSubject() {
        return subject;
    }

    /**
     * Gets the rendering of the subject of this SearchMetadata.
     * @return The subject rendering.  Not <code>null</code>.
     */
    public String getSubjectRendering() {
        return subjectRendering;
    }

    /**
     * Determines whether the search string provided by this SearchMetadata object is in fact the display name
     * rendering
     * of the subject of this SearchMetadata object.
     * @return <code>true</code> if the search string is a display name, otherwise <code>false</code>.
     */
    public boolean isSearchStringEntityRendering() {
        return category == SearchCategory.DISPLAY_NAME;
    }

    /**
     * Gets the search string which this SearchMetadata object records.
     * @return The search string.  Not <code>null</code>.
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Gets a stylised version of the search string.  This stylised version contains exactly the same underlying string
     * as returned by {@link #getSearchString()}.
     * @return A {@link StyledString} rendering of the search string (for display in a UI).  Not <code>null</code>.
     */
    public StyledString getStyledSearchSearchString() {
        return new StyledString(searchString);
    }

    public int compareTo(SearchMetadata o) {
        int catDiff = this.category.compareTo(o.category);
        if (catDiff != 0) {
            return catDiff;
        }
        int typeDiff = this.groupDescription.compareTo(o.groupDescription);
        if (typeDiff != 0) {
            return typeDiff;
        }
        int subjectRenderingDiff = this.subjectRendering.compareTo(o.subjectRendering);
        if (subjectRenderingDiff != 0) {
            return subjectRenderingDiff;
        }
        int subjectDiff = this.subject.compareTo(o.subject);
        if (subjectDiff != 0) {
            return subjectDiff;
        }
        return searchString.compareTo(o.searchString);
    }

    @Override
    public int hashCode() {
        return "SearchMetadata".hashCode()
                + category.hashCode()
                + groupDescription.hashCode()
                + subject.hashCode()
                + subjectRendering.hashCode()
                + searchString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof SearchMetadata)) {
            return false;
        }
        SearchMetadata other = (SearchMetadata) o;
        return this.category.equals(other.category)
                && this.groupDescription.equals(other.groupDescription)
                && this.subject.equals(other.subject)
                && this.subjectRendering.equals(other.subjectRendering)
                && this.searchString.equals(other.searchString);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("category: ").append(category).append(", ")
                 .append("groupDescription: ").append(groupDescription).append(", ")
                 .append("subject: ").append(subject).append(", ")
                 .append("subjectRendering").append(subjectRendering).append(", ")
                 .append("searchString").append(searchString)
                 .toString();
    }
}
