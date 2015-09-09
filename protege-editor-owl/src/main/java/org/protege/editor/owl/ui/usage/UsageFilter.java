package org.protege.editor.owl.ui.usage;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 23, 2008<br><br>
 */
public enum UsageFilter {

    filterSelf("filter.self"),
    filterNamedSubsSupers("filter.class.subs"),
    filterDisjoints("filter.class.disjoints"),
    filterDifferent("filter.individual.different");


    private String key;


    UsageFilter(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }
}
