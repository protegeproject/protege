package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchKeyword;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/11/2015
 */
public class NestedSearch extends CompoundKeyword {

    public NestedSearch() {
        super();
    }

    @Override
    public String asSearchString() {
        StringBuilder sb = new StringBuilder();

        boolean needSeparator = false;
        for (SearchKeyword keyword : this) {
            if (needSeparator) {
                sb.append(" NESTED ");
            }
            sb.append(keyword.toString());
            needSeparator = true;
        }
        return sb.toString();
    }
}
