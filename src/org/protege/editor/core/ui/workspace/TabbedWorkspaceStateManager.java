package org.protege.editor.core.ui.workspace;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.log4j.Logger;
import org.protege.editor.core.FileManager;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabbedWorkspaceStateManager extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(TabbedWorkspaceStateManager.class);

    public static final String VISIBLE_TABS_PREFERENCE_KEY = "VisibleTabs";

    private TabbedWorkspace workspace;

    private List<String> tabs;


    public TabbedWorkspaceStateManager() {
        tabs = new ArrayList<String>();
        load();
    }


    public TabbedWorkspaceStateManager(TabbedWorkspace workspace) {
        this.workspace = workspace;
        tabs = new ArrayList<String>();
        for (WorkspaceTab tab : workspace.getWorkspaceTabs()) {
            tabs.add(tab.getId());
        }
    }


    public List<String> getTabs() {
        return new ArrayList<String>(tabs);
    }


    private static File getVisibleTabsFile() {
        return new File(FileManager.getViewConfigurationsFolder(), "VisibleTabs.xml");
    }


    private void load() {
        try {
            File file = getVisibleTabsFile();
            if (!file.exists()) {
                return;
            }
            InputStream is = new BufferedInputStream(new FileInputStream(getVisibleTabsFile()));
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, this);
            is.close();
        }
        catch (SAXException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
        catch (ParserConfigurationException e) {
            logger.error(e);
        }
    }


    public void save() {
        try {
            File file = getVisibleTabsFile();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element visibleTabsElement = doc.createElement("VisibleTabs");
            doc.appendChild(visibleTabsElement);
            for (String tabId : tabs) {
                Element tabElement = doc.createElement("Tab");
                tabElement.setAttribute("id", tabId);
                visibleTabsElement.appendChild(tabElement);
            }
            OutputFormat outputFormat = new OutputFormat();
            outputFormat.setIndent(4);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            XMLSerializer serializer = new XMLSerializer(os, outputFormat);
            serializer.serialize(doc);
            os.flush();
            os.close();
            for (WorkspaceTab tab : workspace.getWorkspaceTabs()) {
                tab.save();
            }
        }
        catch (ParserConfigurationException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
    }


    protected Preferences getPreferences() {
        return PreferencesManager.getInstance().getPreferencesForSet("Tabs", "VisibleTabs");
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Tab")) {
            tabs.add(attributes.getValue("id"));
        }
    }
}
