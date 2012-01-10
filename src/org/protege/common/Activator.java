package org.protege.common;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;



public class Activator implements BundleActivator {
	public static final Logger LOGGER = Logger.getLogger(Activator.class);
	private ServiceListener listener;

	public void start(BundleContext context) throws Exception {
		context.registerService(
	        javax.xml.parsers.SAXParserFactory.class.getName(),
	        javax.xml.parsers.SAXParserFactory.newInstance(), null);
		CommonProtegeProperties.getDataDirectory().mkdir();
		if (LOGGER.isDebugEnabled()) {
			startDebug(context);
		}
	}
	
	public void startDebug(BundleContext context) {
		listener = new DebugServicesListener();
		context.addServiceListener(listener);
	}
	
	private class DebugServicesListener implements ServiceListener {
		@Override
		public void serviceChanged(ServiceEvent event) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				LOGGER.debug(event.getServiceReference().getBundle().getSymbolicName() + " registering service " + displayClass(event.getServiceReference()));
			}
			else if (event.getType() == ServiceEvent.UNREGISTERING) {
				LOGGER.debug(event.getServiceReference().getBundle().getSymbolicName() + " unregistering service " + displayClass(event.getServiceReference()));
			}
			else {
				LOGGER.debug("Modifying service " + event.getServiceReference());
			}
		}
		
		private String displayClass(ServiceReference ref) {
			Object classes = ref.getProperty(Constants.OBJECTCLASS);
			if (classes instanceof String[]) {
				String classArray[] = (String[]) classes;
				return classArray[0];
			}
			return classes.toString();
		}
	}

	public void stop(BundleContext context) throws Exception {
		if (listener != null) {
			context.removeServiceListener(listener);
			listener = null;
		}
	}

}
