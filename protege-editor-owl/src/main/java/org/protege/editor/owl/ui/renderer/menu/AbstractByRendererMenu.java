package org.protege.editor.owl.ui.renderer.menu;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AbstractByRendererMenu extends ProtegeOWLAction {
	private static final long serialVersionUID = -143138190636320921L;
	private JCheckBoxMenuItem menuItem;
	private OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
	private OWLModelManagerListener listener;

	protected abstract boolean isMyRendererPlugin(RendererPlugin plugin);
	protected abstract boolean isConfigured(RendererPlugin plugin);
	protected abstract void configure(RendererPlugin plugin);
	
	public void setMenuItem(JMenuItem menuItem) {
		this.menuItem = (JCheckBoxMenuItem) menuItem;
		updateCheckedStatus();
	}

	public void initialise() throws Exception {
		listener = event -> {
            if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                updateCheckedStatus();
            }
        };
		getOWLModelManager().addListener(listener);
	}
	
	private void updateCheckedStatus() {
		if (menuItem != null) {
			RendererPlugin plugin = preferences.getRendererPlugin();
			menuItem.setSelected(isMyRendererPlugin(plugin) && isConfigured(plugin));
		}
	}
	


	public void dispose() throws Exception {
		getOWLModelManager().removeListener(listener);
	}

	public void actionPerformed(ActionEvent e) {
		OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
		for (RendererPlugin plugin : preferences.getRendererPlugins()) {
			if (isMyRendererPlugin(plugin)) {
				preferences.setRendererPlugin(plugin);
				configure(plugin);
				getOWLModelManager().refreshRenderer();
				menuItem.setSelected(true);
				break;
			}
		}
	}
}
