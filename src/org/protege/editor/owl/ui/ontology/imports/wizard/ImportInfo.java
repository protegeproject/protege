package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportInfo {
	private OWLOntologyID ontologyID;
	private URI physicalLocation;
	private IRI importLocation;
	
	public OWLOntologyID getOntologyID() {
		return ontologyID;
	}
	public void setOntologyID(OWLOntologyID ontologyID) {
		this.ontologyID = ontologyID;
	}
	public URI getPhysicalLocation() {
		return physicalLocation;
	}
	public void setPhysicalLocation(URI physicalLocation) {
		this.physicalLocation = physicalLocation;
	}
	public IRI getImportLocation() {
		return importLocation;
	}
	public void setImportLocation(IRI importLocation) {
		this.importLocation = importLocation;
	}
	
	public boolean isReady() {
		return importLocation != null && ontologyID != null & physicalLocation != null;
	}
}
