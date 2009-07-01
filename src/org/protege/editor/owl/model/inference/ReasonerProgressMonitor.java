package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.util.ProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 10-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An interface to monitor the progress of classification.
 * Note that the methods on this implementation might be called
 * from a thread other than the event dispatch thread.
 */
public interface ReasonerProgressMonitor extends ProgressMonitor {

}
