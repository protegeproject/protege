package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.entry.Entry;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class NewEntryPanel extends JPanel {
	private static final long serialVersionUID = -706694344158933046L;

	private static final Dimension defaultTextField = new JTextField("/some/unknown/path/dont/really/care/about-it.owl").getPreferredSize();
	
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

	public static Dimension getDefaultTextFieldDimension(){
		return defaultTextField;
	}
	
	protected void fireListeners() {
		for (Runnable r : listeners) {
			r.run();
		}
	}

	protected String getUniqueId(){
		return UUID.randomUUID().toString();
	}
}
