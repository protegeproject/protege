package org.protege.osgi.framework;

import java.io.File;
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
	public static void main(String[] args) throws Exception {
		Launcher.setArguments(args);
		new Launcher(new File("server.xml")).start();
	}

}
