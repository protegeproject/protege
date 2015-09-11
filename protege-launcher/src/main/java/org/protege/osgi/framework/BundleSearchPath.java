package org.protege.osgi.framework;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class BundleSearchPath {

    private Logger logger = Logger.getLogger(BundleSearchPath.class.getCanonicalName());

    public static final String USER_HOME = "user.home";

    public static final String USER_HOME_VAR = "${" + USER_HOME + "}/";


    private List<File> path = new ArrayList<>();

    private List<String> allowedBundles = new ArrayList<>();

    public BundleSearchPath() {
    }

    public void addSearchPath(String dir) {
        if (dir.startsWith(USER_HOME_VAR)) {
            String homeDirectory = System.getProperty(USER_HOME);
            dir = dir.substring(USER_HOME_VAR.length());
            path.add(new File(homeDirectory, dir));
        }
        else {
            path.add(new File(dir));
        }
    }


    public List<File> getPath() {
        return path;
    }

    public List<String> getAllowedBundles() {
        return allowedBundles;
    }

    public void addAllowedBundle(String bundle) {
        allowedBundles.add(bundle);
    }

    public Collection<File> search() {
        Map<SymbolicName, BundleInfo> nameToFileMap = new LinkedHashMap<>();
        for (File dir : path) {
            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }
            File[] contents = dir.listFiles();
            if (contents != null) {
                for (File jar : contents) {
                    String jarName = jar.getName();
                    if (jar.getName().endsWith(".jar") && isAllowedBundle(jarName)) {
                        Optional<BundleInfo> parseBundleInfo = toBundleInfo(jar);
                        if (parseBundleInfo.isPresent()) {
                            addJar(parseBundleInfo.get(), nameToFileMap);
                        }
                    }
                }
            }
        }
        return nameToFileMap.values().stream().map(BundleInfo::getBundleFile).collect(Collectors.toList());
    }

    private boolean isAllowedBundle(String jarName) {
        return allowedBundles.isEmpty() || allowedBundles.contains(jarName);
    }

    private void addJar(BundleInfo bundleInfo, Map<SymbolicName, BundleInfo> nameToFileMap) {
        SymbolicName symbolicName = bundleInfo.getSymbolicName();
        BundleInfo existingBundleInfo = nameToFileMap.get(symbolicName);
        if (existingBundleInfo == null) {
            nameToFileMap.put(symbolicName, bundleInfo);
            return;
        }

        if (bundleInfo.isNewerVersionThan(existingBundleInfo)) {
            nameToFileMap.put(symbolicName, bundleInfo);
            logger.warning(
                    String.format("Found duplicate plugin/bundle.  " +
                                    "Using the latest version, %s and ignoring the previous version, %s.",
                            bundleInfo.getBundleFile().getName(),
                            existingBundleInfo.getBundleFile().getName())
            );
        }
        else if(bundleInfo.isNewerTimestampThan(existingBundleInfo)) {
            nameToFileMap.put(symbolicName, bundleInfo);
            logger.warning(
                    String.format("Found duplicate plugin/bundle. " +
                                    "Using the most recent, %s (modified %tc) " +
                                    "and ignoring the older copy, %s (modified %tc).",
                            bundleInfo.getBundleFile().getName(),
                            bundleInfo.getBundleFile().lastModified(),
                            existingBundleInfo.getBundleFile().getName(),
                            existingBundleInfo.getBundleFile().lastModified()
                    )
            );
        }
        else {
            logger.warning(
                    String.format("Ignoring plugin/bundle (%s) because it is a duplicate of %s.",
                            existingBundleInfo.getBundleFile().getName(),
                            bundleInfo.getBundleFile().getName()
                    )
            );
        }

    }

    private Optional<BundleInfo> toBundleInfo(File file) {
        try (JarInputStream is = new JarInputStream(new FileInputStream(file))) {
            Manifest mf = is.getManifest();
            Attributes attributes = mf.getMainAttributes();
            String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
            if (symbolicName == null) {
                return Optional.empty();
            }
            String versionString = attributes.getValue(Constants.BUNDLE_VERSION);
            Optional<Version> version =
                    versionString == null ? Optional.<Version>empty() : Optional.of(new Version(versionString));
            return Optional.of(new BundleInfo(file, new SymbolicName(symbolicName), version));
        } catch (Exception e) {
            logger.warning("Could not parse " + file + " as plugin/bundle. Error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
