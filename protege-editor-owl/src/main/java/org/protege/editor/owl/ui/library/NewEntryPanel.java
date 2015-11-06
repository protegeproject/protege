package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.entry.Entry;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class NewEntryPanel extends JPanel {
	private static final long serialVersionUID = -706694344158933046L;
	
	private List<Runnable> listeners = new ArrayList<Runnable>();

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
