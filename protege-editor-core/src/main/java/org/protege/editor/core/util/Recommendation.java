package org.protege.editor.core.util;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16 Sep 16
 *
 * A flag for whether something (e.g. an edit to an ontology) is recommended or not.
 */
public enum Recommendation {

    RECOMMENDED("Recommended"),
    NOT_RECOMMENDED("Not recommended");

    private String printName;

    Recommendation(String printName) {
        this.printName = printName;
    }

    public String getPrintName() {
        return printName;
    }
}
