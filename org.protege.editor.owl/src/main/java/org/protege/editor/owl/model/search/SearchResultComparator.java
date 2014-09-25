package org.protege.editor.owl.model.search;

import org.protege.editor.owl.OWLEditorKit;

import java.util.Comparator;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/09/2012
 */
public class SearchResultComparator implements Comparator<SearchResult> {


    public SearchResultComparator(OWLEditorKit editorKit) {
    }

    public int compare(SearchResult result1, SearchResult result2) {
        int catDiff = result1.getCategory().ordinal() - result2.getCategory().ordinal();
        if (catDiff != 0) {
            return catDiff;
        }

        int typeDiff = result1.getGroupDescription().compareTo(result2.getGroupDescription());
        if (typeDiff != 0) {
            return typeDiff;
        }

        if (isStartOfRenderingMatch(result1)) {
            if (isStartOfRenderingMatch(result2)) {
                return compareOWLEntities(result1, result2);
            }
            else {
                return -1;
            }
        }
        else {
            if (isStartOfRenderingMatch(result2)) {
                return 1;
            }
            else {
                return compareOWLEntities(result1, result2);
            }
        }
    }

    private int compareOWLEntities(SearchResult result1, SearchResult result2) {
        int diff = result1.compareTo(result2);
        if(diff != 0) {
            return diff;
        }
        String subjectRendering1 = result1.getSubjectRendering();
        String subjectRendering2 = result2.getSubjectRendering();
        String strippedRendering1 = subjectRendering1.startsWith("'") ? subjectRendering1.substring(1) : subjectRendering1;
        String strippedRendering2 = subjectRendering2.startsWith("'") ? subjectRendering2.substring(1) : subjectRendering2;
        return strippedRendering1.compareToIgnoreCase(strippedRendering2);
    }


    private boolean isStartOfRenderingMatch(SearchResult searchResult) {
        if (searchResult.getCategory() != SearchCategory.DISPLAY_NAME) {
            return false;
        }
        String rendering = searchResult.getSearchString();
        SearchResultMatch firstMatch = searchResult.getMatches().get(0);
        if (rendering.startsWith("'")) {
            return firstMatch.getStart() == 1;
        }
        else {
            return firstMatch.getStart() == 0;
        }
    }


}
