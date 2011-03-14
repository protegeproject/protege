package org.protege.osgi.framework;

import java.io.IOException;

import org.osgi.framework.BundleException;

public class Server {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BundleException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, IOException {
		Launcher.setArguments(args);
		new Launcher(new ServerLaunchConfiguration()).start();
	}

}
