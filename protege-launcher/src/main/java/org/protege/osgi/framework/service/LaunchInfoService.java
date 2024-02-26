package org.protege.osgi.framework.service;

import java.util.List;

import org.protege.osgi.framework.BundleSearchPath;

public interface LaunchInfoService {

	String[] getCommandLineArguments();
	List<BundleSearchPath> getSearchPath();
}
