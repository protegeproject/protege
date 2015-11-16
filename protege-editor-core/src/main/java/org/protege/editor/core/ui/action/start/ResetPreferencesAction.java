package org.protege.editor.core.ui.action.start;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.JavaBackedPreferencesImpl;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.prefs.Preferences;

public class ResetPreferencesAction extends AltStartupAction {
	private static final long serialVersionUID = 697953371040653824L;

	public void initialise() throws Exception {

	}

	public void dispose() throws Exception {
	}

	public void actionPerformed(ActionEvent e) {
		try {
			JTextPane pane = new JTextPane();
			pane.setPreferredSize(new Dimension(400,400));
			pane.setEditable(false);
			URL u = getClass().getResource("ResetPreferencesExplanation.html");
			pane.setPage(u);
			Font font = UIManager.getFont("TextArea.font");
			if (font != null) {
				// modified font-size to match link label - alternatively use font.getSize();
				String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: 12pt; }";
				((HTMLDocument) pane.getDocument()).getStyleSheet().addRule(bodyRule);
			}
			JScrollPane scroller = new JScrollPane();
			scroller.getViewport().add(pane);
			Object reset = "Reset";
			int ret = JOptionPane.showOptionDialog(getParent(), scroller, "Reset Preferences?", 
													  JOptionPane.NO_OPTION, JOptionPane.INFORMATION_MESSAGE, 
													  null, 
													  new Object[] {reset, "Cancel"}, reset);
			if (ret == 0) {
				Preferences p = Preferences.userRoot();
				Preferences q = p.node(JavaBackedPreferencesImpl.PROTEGE_PREFS_KEY);
				q.removeNode();
				p.sync();
				p.flush();
				q.flush();
				JOptionPane.showMessageDialog(getParent(), "Preferences Reset");
				ProtegeApplication.handleQuit();
			}
		}
		catch (Exception ex) {
			LoggerFactory.getLogger(ResetPreferencesAction.class)
					.error("An error occurred whilst resetting the preferences: {}", ex);
		}
	}

}
