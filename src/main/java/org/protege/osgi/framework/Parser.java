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
	public static final String DIRECTORY="dir";
	
	public static final String DEFAULT_PLUGIN_DIRECTORY = "plugins";
	
	private DocumentBuilderFactory factory;
	
	private String pluginDirectory;
    private Properties frameworkProperties = null;
    private Properties systemProperties = null;
    private List<DirectoryWithBundles> directories = new ArrayList<DirectoryWithBundles>();
    
    public Parser() {
        factory = DocumentBuilderFactory.newInstance();
    }
    
    public String getPluginDirectory() {
        return pluginDirectory;
    }
    
    public Properties getFrameworkProperties() {
        return frameworkProperties;
    }

    public Properties getSystemProperties() {
        return systemProperties;
    }

    public List<DirectoryWithBundles> getDirectories() {
        return directories;
    }

    public void reset() {
        pluginDirectory = null;
        frameworkProperties = null;
        systemProperties = null;
        directories.clear();
    }
    
    public void parse(File f) throws ParserConfigurationException, SAXException, IOException {
        reset();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        Node topNode = null;
        for (int j = 0; j < doc.getChildNodes().getLength(); j++) {
        	Node node = doc.getChildNodes().item(j);
        	if (node.getNodeName().equals("launch")) {
        		topNode = node;
        		break;
        	}
        }
        NodeList nodes = topNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node job = nodes.item(i);
            if (job instanceof Element && job.getNodeName().equals("pluginDirectory")) {
                setPluginDirectory(job);
            }
            if (job instanceof Element && job.getNodeName().equals("systemProperties")) {
                systemProperties = readProperties(job.getChildNodes());
            }
            else if (job instanceof Element && job.getNodeName().equals("frameworkProperties")) {
                frameworkProperties = readProperties(job.getChildNodes());
            }
            else if (job instanceof Element && job.getNodeName().equals("bundles")) {
                DirectoryWithBundles directory = readDirectories(job);
                if (directory != null) {
                    directories.add(directory);
                }
            }

        }
    }
    
    private void setPluginDirectory(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node dir = attributes.getNamedItem(DIRECTORY);
        if (dir != null) {
            String value = dir.getNodeValue();
            if (value != null) {
                pluginDirectory = value;
            }
        }
    }
    
    private Properties readProperties(NodeList nodes) {
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
    
    private DirectoryWithBundles readDirectories(Node node) {
        Node dirNameNode = node.getAttributes().getNamedItem(DIRECTORY);
        if (dirNameNode == null) {
            return null;
        }
        DirectoryWithBundles directories = new DirectoryWithBundles(dirNameNode.getNodeValue());
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element && child.getNodeName().equals("bundle")) {
                Node bundleNameNode = child.getAttributes().getNamedItem(NAME);
                if (bundleNameNode != null) {
                    directories.addBundle(bundleNameNode.getNodeValue());
                }
            }
        }
        return directories;
    }


	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
	    File example = new File("osgi/felix/launch.xml");
	    Parser parser = new Parser();
	    parser.parse(example);
	}

}
