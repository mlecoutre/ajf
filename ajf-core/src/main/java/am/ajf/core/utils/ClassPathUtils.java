package am.ajf.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathUtils {

	private ClassPathUtils() {
		super();
	}

	public static File getResourceDir(Class<?> clazz) {

		String resourceName = clazz.getName().replace('.', '/')
				.concat(".class");
		return getResourceDir(resourceName);

	}

	public static File getResourceDir(String resourceName) {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource(resourceName);
		if (null == url)
			return null;

		String urlPath = url.getFile();
		String urlDirPath = urlPath.substring(0, urlPath.length()
				- (resourceName.length() + 1));

		File dir = new File(urlDirPath);
		return dir;

	}

	public static List<Class<?>> listClassesForPackage(String pkgName) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// Get a File object for the package
		File directory = null;
		String fullPath;
		String relPath = pkgName.replace('.', '/');
		System.out.println("ClassDiscovery: Package: " + pkgName
				+ " becomes Path:" + relPath);

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL resource = classLoader.getResource(relPath);
		System.out.println("ClassDiscovery: Resource = " + resource);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		fullPath = resource.getFile();
		System.out.println("ClassDiscovery: FullPath = " + resource);

		try {
			directory = new File(resource.toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(
					pkgName
							+ " ("
							+ resource
							+ ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...",
					e);
		}
		catch (IllegalArgumentException e) {
			directory = null;
		}
		System.out.println("ClassDiscovery: Directory = " + directory);

		if (directory != null && directory.exists()) {
			// Get the list of the files contained in the package
			visitDirectory(classes, pkgName, directory);
		}
		else {
			try {
				visitArchive(classes, fullPath, relPath);
			}
			catch (Throwable e) {
				throw new RuntimeException(pkgName + " (" + directory
						+ ") does not appear to be a valid package", e);
			}
		}
		return classes;
	}

	private static void visitArchive(List<Class<?>> classes, String fullPath,
			String relPath) throws IOException {
		String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar")
				.replaceFirst("file:", "");
		JarFile jarFile = new JarFile(jarPath);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				String entryName = entry.getName();
				if (entryName.startsWith(relPath)
						&& entryName.length() > (relPath.length() + "/"
								.length())) {
					System.out
							.println("ClassDiscovery: JarEntry: " + entryName);
					String className = entryName.replace('/', '.')
							.replace('\\', '.').replace(".class", "");
					System.out.println("ClassDiscovery: className = "
							+ className);
					try {
						classes.add(Class.forName(className));
					}
					catch (Throwable e) {
						System.out.println(e.getMessage() + " while loading: "
								+ className);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param classes
	 * @param pkgName
	 * @param directory
	 */
	private static void visitDirectory(List<Class<?>> classes, String pkgName,
			File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				visitDirectory(classes, pkgName + "." + fileName, file);
			}
			else {
				// we are only interested in .class files
				if (fileName.endsWith(".class")) {
					// removes the .class extension
					String className = pkgName + '.'
							+ fileName.substring(0, fileName.length() - 6);
					System.out.println("ClassDiscovery: className = "
							+ className);
					try {
						classes.add(Class.forName(className));
					}
					catch (Throwable e) {
						System.out.println(e.getMessage() + " while loading: "
								+ className);
					}
				}
			}
		}
	}

}
