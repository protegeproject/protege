package org.protege.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Activator implements BundleActivator {

	private final Logger logger = LoggerFactory.getLogger(Activator.class);

	private ServiceListener listener;

	public void start(BundleContext context) throws Exception {
		logger.debug("Starting org.protege.common bundle");
		context.registerService(
	        javax.xml.parsers.SAXParserFactory.class.getName(),
	        javax.xml.parsers.SAXParserFactory.newInstance(), null);
//		CommonProtegeProperties.getDataDirectory().mkdir();
		if (logger.isDebugEnabled()) {
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
				logger.debug(event.getServiceReference().getBundle().getSymbolicName() + " registering service " + displayClass(event.getServiceReference()));
			}
			else if (event.getType() == ServiceEvent.UNREGISTERING) {
				logger.debug(event.getServiceReference().getBundle().getSymbolicName() + " unregistering service " + displayClass(event.getServiceReference()));
			}
			else {
				logger.debug("Modifying service " + event.getServiceReference());
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
