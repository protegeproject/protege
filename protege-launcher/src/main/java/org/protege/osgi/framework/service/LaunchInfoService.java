package org.protege.osgi.framework.service;

import org.protege.osgi.framework.BundleSearchPath;

import java.util.List;

public interface LaunchInfoService {

	String[] getCommandLineArguments();
	List<BundleSearchPath> getSearchPath();
}
