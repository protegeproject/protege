package org.protege.osgi.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser {
	public static final String PROPERTY = "property";
	public static final String NAME = "name";
	public static final String VALUE = "value";

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		Properties launchProperties = null;
		List<DirectoryWithBundles> directories = new ArrayList<DirectoryWithBundles>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File("osgi/felix/launch.xml"));
		Node topNode = doc.getChildNodes().item(0);
		NodeList nodes = topNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node job = nodes.item(i);
			if (job instanceof Element && job.getNodeName().equals("systemProperties")) {
				for (Entry<Object, Object> entry : readProperties(job.getChildNodes()).entrySet()) {
					System.setProperty((String) entry.getKey(), (String) entry.getValue());
				}
			}
			else if (job instanceof Element && job.getNodeName().equals("frameworkProperties")) {
				launchProperties = readProperties(job.getChildNodes());
			}
			else if (job instanceof Element && job.getNodeName().equals("bundles")) {
				directories = readDirectories(job);
			}
			
		}
	}
	
	private static Properties readProperties(NodeList nodes) {
		Properties properties = new Properties();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node propertyNode = nodes.item(i);
			if (propertyNode instanceof Element && propertyNode.getNodeName().equals(PROPERTY)) {
				NamedNodeMap attributes = propertyNode.getAttributes();
				Node nameNode = attributes.getNamedItem(NAME);
				Node valueNode = attributes.getNamedItem(VALUE);
				if (nameNode != null && valueNode != null) {
					properties.put(nameNode.getNodeValue(), valueNode.getNodeValue());
				}
			}
		}
		return properties;
	}
	
	private static List<DirectoryWithBundles> readDirectories(Node node) {
		return null;
	}

}
