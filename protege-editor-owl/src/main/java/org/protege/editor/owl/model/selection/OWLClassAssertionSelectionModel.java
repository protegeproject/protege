package org.protege.editor.owl.model.selection;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OWLClassAssertionSelectionModel implements Disposable {
	public static Logger logger = LoggerFactory.getLogger(OWLClassAssertionSelectionModel.class);
	
	private OWLEditorKit editorKit;
	private OWLSelectionModel mainSelectionModel;
	private OWLClass owlClass;
	private OWLClass inferredOwlClass;
	private OWLIndividual individual;
	private boolean inferredOwlClassNeedsRecalculation = true;
	private List<OWLSelectionModelListener> listeners = new ArrayList<OWLSelectionModelListener>();
	
	private OWLSelectionModelListener mainSelectionListener = new OWLSelectionModelListener() {
		public void selectionChanged() throws Exception {
			mainSelectionChanged();
		}
	};
	
	public static OWLClassAssertionSelectionModel get(OWLEditorKit editorKit) {
		OWLClassAssertionSelectionModel selectionModel = (OWLClassAssertionSelectionModel) editorKit.get(OWLClassAssertionSelectionModel.class);
		if (selectionModel == null) {
			selectionModel = new OWLClassAssertionSelectionModel(editorKit);
			selectionModel.initialise();
			editorKit.put(OWLClassAssertionSelectionModel.class, selectionModel);
		}
		return selectionModel;
	}
	
	private OWLClassAssertionSelectionModel(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
	}
	
	private void initialise() {
		mainSelectionModel = editorKit.getOWLWorkspace().getOWLSelectionModel();
	}
	
	private void mainSelectionChanged() {
		OWLEntity e = mainSelectionModel.getSelectedEntity();
		if (e instanceof OWLClass) {
			owlClass = (OWLClass) e;
			inferredOwlClass = (OWLClass) e;
			individual = null;
			fireSelectionChanged();
		}
		else if (e instanceof OWLIndividual) {
			individual = (OWLIndividual) individual;
			inferredOwlClassNeedsRecalculation = true;
			OWLModelManager modelManager = editorKit.getOWLModelManager();
			Collection<OWLClassExpression> types = EntitySearcher.getTypes(individual, modelManager.getActiveOntologies());
			if (!types.contains(owlClass)) {
				owlClass = null;
				for (OWLClassExpression type : types) {
					if (!type.isAnonymous()) {
						owlClass = type.asOWLClass();
					}
				}
			}
			fireSelectionChanged();
		}
	}
	
	public OWLClass getOwlClass() {
		return owlClass;
	}

	// lazy update and don't change it if you don't need to.
	public OWLClass getInferredOwlClass() {
		OWLReasoner reasoner = editorKit.getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
		if (individual == null || individual.isAnonymous() || reasoner instanceof NoOpReasoner) {
			return owlClass;
		}
		if (inferredOwlClassNeedsRecalculation) {
			recalculateInferredOwlClass(reasoner);
			inferredOwlClassNeedsRecalculation = false;
		}

		return inferredOwlClass;
	}
	
	private void recalculateInferredOwlClass(OWLReasoner reasoner) {
		if (inferredOwlClass == null || !reasoner.getInstances(owlClass, true).containsEntity(individual.asOWLNamedIndividual())) {
			inferredOwlClass = null;
			for (OWLClass cls : reasoner.getTypes(individual.asOWLNamedIndividual(), true).getFlattened()) {
				inferredOwlClass = cls;
				break;
			}
		}
	}

	public OWLIndividual getIndividual() {
		return individual;
	}
	
	public void addListener(OWLSelectionModelListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(OWLSelectionModelListener listener) {
		listeners.remove(listener);
	}
	
	private void fireSelectionChanged() {
		for (OWLSelectionModelListener listener : listeners) {
			try {
				listener.selectionChanged();
			}
			catch (Exception e) {
                logger.warn("BAD LISTENER: (" + listener.getClass().getSimpleName() + ") ");
			}
		}
	}

	public void dispose() throws Exception {
		mainSelectionModel.removeListener(mainSelectionListener);
	}
}
