package org.protege.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(
	        javax.xml.parsers.SAXParserFactory.class.getName(),
	        javax.xml.parsers.SAXParserFactory.newInstance(), null);
		CommonProtegeProperties.getDataDirectory().mkdir();
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
