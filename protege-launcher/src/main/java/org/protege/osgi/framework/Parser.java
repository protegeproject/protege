package org.protege.osgi.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.LoggerFactory;
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
    public static final String DIRECTORY = "dir";

    public static final String DEFAULT_PLUGIN_DIRECTORY = "plugins";

    private DocumentBuilderFactory factory;

    private Map<String, String> frameworkProperties;
    private Map<String, String> systemProperties;
    private List<BundleSearchPath> searchPaths = new ArrayList<>();

    public Parser() {
        factory = DocumentBuilderFactory.newInstance();
    }

    public Map<String, String> getFrameworkProperties() {
        return frameworkProperties;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public List<BundleSearchPath> getSearchPaths() {
        return searchPaths;
    }

    public void reset() {
        frameworkProperties = new TreeMap<>();
        systemProperties = new TreeMap<>();
        searchPaths.clear();
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
            if (job instanceof Element && job.getNodeName().equals("systemProperties")) {
                systemProperties = readProperties(job.getChildNodes());
            }
            else if (job instanceof Element && job.getNodeName().equals("frameworkProperties")) {
                frameworkProperties = readProperties(job.getChildNodes());
            }
            else if (job instanceof Element && job.getNodeName().equals("bundles")) {
                BundleSearchPath directory = readDirectories(job);
                if (directory != null) {
                    searchPaths.add(directory);
                    LoggerFactory.getLogger(Parser.class).debug("Added bundle search path: {}", directory);
                }
            }

        }
    }

    private Map<String, String> readProperties(NodeList nodes) {
        Map<String, String> properties = new TreeMap<>();
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

    private BundleSearchPath readDirectories(Node node) {
        BundleSearchPath directories = new BundleSearchPath();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element && child.getNodeName().equals("bundle")) {
                Node bundleNameNode = child.getAttributes().getNamedItem(NAME);
                if (bundleNameNode != null) {
                    directories.addAllowedBundle(bundleNameNode.getNodeValue());
                }
            }
            else if (child instanceof Element && child.getNodeName().equals("search")) {
                Node searchPathNode = child.getAttributes().getNamedItem("path");
                if (searchPathNode != null) {
                    directories.addSearchPath(searchPathNode.getNodeValue());
                }
            }
        }
        return directories;
    }
}
