package org.protege.editor.owl.ui.library;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.protege.xmlcatalog.entry.Entry;

public abstract class NewEntryPanel extends JPanel {
	private static final long serialVersionUID = -706694344158933046L;
	
	private List<Runnable> listeners = new ArrayList<>();

	public abstract Entry getEntry();
	
	public void addListener(Runnable r) {
		listeners.add(r);
	}
	
	public void removeListener(Runnable r) {
	    listeners.remove(r);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	protected void fireListeners() {
		for (Runnable r : listeners) {
			r.run();
		}
	}
}
