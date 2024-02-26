package org.protege.editor.owl.ui.view;

import java.util.List;

import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface Findable<E extends OWLEntity> {

    List<E> find(String match);


    void show(E owlEntity);
}
