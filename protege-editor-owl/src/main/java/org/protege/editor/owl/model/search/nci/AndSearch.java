package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchKeyword;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class AndSearch extends CompoundKeyword {

    public AndSearch() {
        super();
    }

    @Override
    public String asSearchString() {
        StringBuilder sb = new StringBuilder();
        
        boolean needSeparator = false;
        for (SearchKeyword keyword : this) {
            if (needSeparator) {
                sb.append(" AND ");
            }
            sb.append(keyword.toString());
            needSeparator = true;
        }
        return sb.toString();
    }
}
