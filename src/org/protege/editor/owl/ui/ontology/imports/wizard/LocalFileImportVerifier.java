package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFileImportVerifier implements ImportVerifier {

    private File file;

    private OWLEditorKit owlEditorKit;


    public LocalFileImportVerifier(OWLEditorKit owlEditorKit, File file) {
        this.file = file;
        this.owlEditorKit = owlEditorKit;
    }


    public ImportParameters checkImports() {
        MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
        extractor.setPhysicalAddress(file.toURI());
        final OWLOntologyID id = extractor.getOntologyId();
        if (id == null) {
            String msg = "The ontology contained in " + file.toString() + " could " + "not be imported. ";
            throw new OWLRuntimeException(msg);
        }
        else {
            return new ImportParameters() {
                public Set<IRI> getOntologiesToBeImported() {
                    return Collections.singleton(id.getOntologyIRI());
                }


                public String getOntologyLocationDescription(IRI iri) {
                    return file.toString();
                }


                public void performImportSetup(OWLEditorKit editorKit) {
                    final OWLOntologyManager mngr = owlEditorKit.getModelManager().getOWLOntologyManager();

                    // We need to copy the file to the root ontology folder
                    mngr.addIRIMapper(new SimpleIRIMapper(id.getOntologyIRI(), file.toURI()));
                    // If this fails then we can only add a direct mapping
                }
            };
        }
    }
}
