package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchKeyword;

import com.google.common.collect.ImmutableList;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class OrSearch extends CompoundKeyword {

    private ImmutableList.Builder<SearchInput> builder = ImmutableList.builder();

    public OrSearch() {
        super();
    }

    public void add(SearchInput searchInput) {
        builder.add(searchInput);
        collectSearchKeyword(searchInput);
    }

    private void collectSearchKeyword(SearchInput searchInput) {
        if (searchInput instanceof SearchKeyword) {
            super.add((SearchKeyword) searchInput);
        } else if (searchInput instanceof CompoundKeyword) {
            for (SearchKeyword keyword : (CompoundKeyword) searchInput) {
                super.add(keyword);
            }
        }
    }

    public ImmutableList<SearchInput> getSearchGroup() {
        return builder.build();
    }

    @Override
    public String asSearchString() {
        StringBuilder sb = new StringBuilder();
        
        boolean needSeparator = false;
        for (SearchInput searchGroup : getSearchGroup()) {
            if (needSeparator) {
                sb.append(", ");
            }
            sb.append(searchGroup.toString());
            needSeparator = true;
        }
        return sb.toString();
    }
}
