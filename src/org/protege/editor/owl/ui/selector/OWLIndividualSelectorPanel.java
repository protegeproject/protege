package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.util.Set;

import javax.swing.ListSelectionModel;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.OWLIndividualListViewComponent;
import org.semanticweb.owl.model.OWLIndividual;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br>
 * <br>
 * <p/> matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br>
 * <br>
 */
public class OWLIndividualSelectorPanel extends AbstractSelectorPanel {
	private OWLIndividualListViewComponent viewComponent;
	private int selectionMode = ListSelectionModel.SINGLE_SELECTION;

	public OWLIndividualSelectorPanel(OWLEditorKit owlEditorKit) {
		super(owlEditorKit);
	}

	/**
	 * Builds an OWLIndividualSelectorPanel with the input selection mode. The
	 * valid values are the same described in the constants in
	 * javax.swing.ListSelectionModel (the default is
	 * ListSelectionModel.SINGLE_SELECTION)
	 * 
	 * @param owlEditorKit
	 * @param selectionMode
	 */
	public OWLIndividualSelectorPanel(OWLEditorKit owlEditorKit,
			int selectionMode) {
		super(owlEditorKit);
		this.viewComponent.setSelectionMode(selectionMode);
	}

	public void dispose() {
		this.viewComponent.dispose();
	}

	@Override
	protected ViewComponentPlugin getViewComponentPlugin() {
		return new ViewComponentPluginAdapter() {
			public String getLabel() {
				return "Individuals";
			}

			public Workspace getWorkspace() {
				return OWLIndividualSelectorPanel.this.getOWLEditorKit()
						.getOWLWorkspace();
			}

			public ViewComponent newInstance() throws ClassNotFoundException,
					IllegalAccessException, InstantiationException {
				OWLIndividualSelectorPanel.this.viewComponent = new OWLIndividualListViewComponent();
				OWLIndividualSelectorPanel.this.viewComponent.setup(this);
				return OWLIndividualSelectorPanel.this.viewComponent;
			}

			@Override
			public Color getBackgroundColor() {
				return OWLSystemColors.getOWLIndividualColor();
			}
		};
	}

	public OWLIndividual getSelectedIndividual() {
		return this.viewComponent.getSelectedIndividual();
	}

	public Set<OWLIndividual> getSelectedIndividuals() {
		return this.viewComponent.getSelectedIndividuals();
	}

	public void setSelectedIndividual(OWLIndividual ind) {
		if (this.viewComponent.getView() != null) {
			this.viewComponent.getView().setPinned(false);
		}
		this.viewComponent.setSelectedIndividual(ind);
	}
}
