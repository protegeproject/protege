package org.protege.editor.owl.ui.ontology.wizard.move;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/09/2012
 */
public enum MoveType {

    COPY("copied"),

    MOVE("moved"),

    DELETE("deleted");


    private String completedName;

    private MoveType(String completedName) {
        this.completedName = completedName;
    }

    public String getCompletedName() {
        return completedName;
    }
}
