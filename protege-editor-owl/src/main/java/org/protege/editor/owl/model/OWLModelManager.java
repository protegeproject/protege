package org.protege.editor.owl.model;

import org.protege.editor.core.ModelManager;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManager;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandler;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.protege.editor.owl.ui.ontology.authentication.BasicAuthenticationHandler;
import org.protege.xmlcatalog.XMLCatalog;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLModelManager extends ModelManager, HasActiveOntology {

    public static final String OWL_OBJECT_COMPARATOR_KEY = "OWL_OBJECT_COMPARATOR_KEY";


    void addListener(OWLModelManagerListener listener);

    void removeListener(OWLModelManagerListener listener);

    void fireEvent(EventType event);

    void addIOListener(IOListener listener);

    void removeIOListener(IOListener listener);

    /**
     * Creates a new, empty ontology that has the specified
     * ontology ID - i.e. the name of the ontology.
     */
    OWLOntology createNewOntology(OWLOntologyID ontologyID, URI physicalURI) throws OWLOntologyCreationException;


    OWLOntology reload(OWLOntology ont) throws OWLOntologyCreationException;


    /**
     * Remove the given ontology from the model manager.
     * Cannot remove the last ontology from the model manager.
     * Cannot remove the active ontology from the model manager.
     * @param ont the ontology to remove.
     * @return false if the ontology cannot be removed
     *         (eg if it does not exist or is the last open ontology)
     */
    boolean removeOntology(OWLOntology ont);


    /**
     * Performs a save operation.  The behaviour of this is implementation
     * specific.  For example, some implementations may choose to save the
     * active ontology, other implementations may choose to save all open
     * ontologies etc.
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     *          if a problem occurs during the save
     */
    void save() throws OWLOntologyStorageException;


    /**
     * Save only the ontology specified
     * @param ont the ontology to save
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     *          if a problem occurs during the save
     */
    void save(OWLOntology ont) throws OWLOntologyStorageException;

    /**
     * Checks if the ontology has been changed since it was loaded or last saved.
     * @param ontology
     */
    boolean isDirty(OWLOntology ontology);

    /**
     * This call is generally not recommended but there are occasions where
     * a plugin knows that an ontology has been saved even though it has not been
     * saved through the OWLModelManager save interface.
     * @param ontology
     */
    void setClean(OWLOntology ontology);

    /**
     * Gets the ontologies that are loaded into this model.
     * These are usually ontologies that are the imports closure
     * of some base ontology.  For example, if OntA imports OntB
     * and OntC then the collection of returned ontology will
     * typically contain OntA, OntB and OntC.
     * @return A <code>Set</code> of open <code>OWLOntology</code>
     *         objects.
     */
    Set<OWLOntology> getOntologies();


    /**
     * Gets the set of loaded ontologies that have been edited
     * but haven't been saved.
     * @return A <code>Set</code> of <code>OWLOntology</code>
     *         objects
     */
    Set<OWLOntology> getDirtyOntologies();


    /**
     * Forces the system to believe that an ontology
     * has been modified.
     * @param ontology The ontology to be made dirty.
     */
    void setDirty(OWLOntology ontology);


    /**
     * Gets the active ontology.  This is the ontology that
     * edits that cause information to be added to an ontology
     * usually take place in.
     */
    OWLOntology getActiveOntology();


    /**
     * Gets the ontologies that are in the imports
     * closure of the the active ontology.
     * @return A <code>Set</code> of <code>OWLOntologies</code>
     */
    Set<OWLOntology> getActiveOntologies();


    /**
     * A convenience method that determines if the active
     * ontology is mutable.
     * @return <code>true</code> if the active ontology is mutable
     *         or <code>false</code> if the active ontology isn't mutable.
     */
    boolean isActiveOntologyMutable();


    /**
     * Determines if the specified ontology is mutable.
     * @param ontology The ontology to be tested/
     * @return <code>true</code> if the ontology is mutable
     *         and can be edited, <code>false</code> if the ontology
     *         is not mutable i.e. can't be edited.
     */
    boolean isMutable(OWLOntology ontology);


    /**
     * Gets the ontology library manager which is an interface
     * for a repository of "standard"/frequently used ontologies (e.g. upper
     * ontologies).
     */
    OntologyCatalogManager getOntologyCatalogManager();

    ExplanationManager getExplanationManager();

    void setExplanationManager(ExplanationManager explanations);

    OWLHierarchyManager getOWLHierarchyManager();


    void setActiveOntology(OWLOntology activeOntology);


    void setActiveOntologiesStrategy(OntologySelectionStrategy strategy);


    void registerOntologySelectionStrategy(OntologySelectionStrategy strategy);


    OntologySelectionStrategy getActiveOntologiesStrategy();


    Set<OntologySelectionStrategy> getActiveOntologiesStrategies();


    ////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Change
    //
    ////////////////////////////////////////////////////////////////////////////////////////////


    void applyChange(OWLOntologyChange change);


    void applyChanges(List<? extends OWLOntologyChange> changes);


    boolean isChangedEntity(OWLEntity entity);


    /**
     * Gets the change history manager.  This tracks the changes that have
     * been made to various ontologies and has support for undo and redo.
     */
    public HistoryManager getHistoryManager();


    /**
     * Adds an ontology history listener.  The listener will be notified of
     * any changes to any of the ontologies that are managed by this model
     * manager.
     */
    void addOntologyChangeListener(OWLOntologyChangeListener listener);


    /**
     * Removes a previously added listener.
     */
    void removeOntologyChangeListener(OWLOntologyChangeListener listener);


    OWLModelManagerEntityRenderer getOWLEntityRenderer();

    void refreshRenderer();

    /**
     * @deprecated Use refreshRenderer instead.
     */
    @Deprecated
    void setOWLEntityRenderer(OWLModelManagerEntityRenderer renderer);


    OWLObjectRenderer getOWLObjectRenderer();


    OWLExpressionCheckerFactory getOWLExpressionCheckerFactory();


    OWLEntityFinder getOWLEntityFinder();


    Comparator<OWLObject> getOWLObjectComparator();


    OWLReasonerManager getOWLReasonerManager();


    OWLReasoner getReasoner();

    ReasonerPreferences getReasonerPreferences();

    /**
     * Gets the physical URI for the specified ontology.
     * @param ontology The ontology.
     * @return The physical URI.
     */
    URI getOntologyPhysicalURI(OWLOntology ontology);


    void setPhysicalURI(OWLOntology ontology, URI physicalURI);


    OWLEntityFactory getOWLEntityFactory();


    void setOWLEntityFactory(OWLEntityFactory owlEntityFactory);


    /*
     * Gets the OWLOntologyManager
     */
    OWLOntologyManager getOWLOntologyManager();


    /**
     * Gets the data factory for the active ontology
     */
    OWLDataFactory getOWLDataFactory();


    String getRendering(OWLObject object);


    void setMissingImportHandler(MissingImportHandler handler);

    /**
     * Do not use this to handle save errors
     * @param handler
     */
    @Deprecated
    default void setSaveErrorHandler(SaveErrorHandler handler) {

    }


    void setLoadErrorHandler(OntologyLoadErrorHandler handler);
    
    /**
     * Set the basic authentication value.
     * @param handler 
     */
    void setBasicAuthenticationHandler(BasicAuthenticationHandler handler);

    XMLCatalog addRootFolder(File dir);

    /**
     * Determines if the specified entity is deprecated by axioms in the active ontologies.
     * @param entity The entity.
     * @return true if the entity is deprecated by statements in the active ontologies otherwise false.
     */
    default boolean isDeprecated(@Nonnull OWLObject entity) {
        return false;
    }
}
