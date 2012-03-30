package am.ajf.forge.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;

import am.ajf.forge.lib.ForgeConstants;

public class ExtractionUtils {

	/**
	 * Unzip zip file corresponding to input resourceZipFileName to the
	 * destination folder set as input parameter
	 * 
	 * @param resourceZipFileName
	 *            : Path to the zip resource to be unzipped
	 * @param destinationFolder
	 *            : where to unzip
	 * @throws IOException
	 */
	public static boolean unzipFile(String resourceZipFileName,
			File destinationFolder) throws IOException {

		InputStream resourceZipFileStream = UIProjectUtils.class
				.getClassLoader().getResourceAsStream(resourceZipFileName);

		byte[] buf = new byte[1024];

		ZipEntry zipentry;
		ZipInputStream zipInputStream = new ZipInputStream(
				resourceZipFileStream);

		zipentry = zipInputStream.getNextEntry();

		while (zipentry != null) {
			// for each entry to be extracted
			String entryName = zipentry.getName();
			// System.out.println("entryname " + entryName);
			int n;
			FileOutputStream fileoutputstream;
			File newFile = new File(entryName);
			String directory = newFile.getParent();

			if (directory == null) {
				if (newFile.isDirectory())
					break;
			}

			File myFile = new File(destinationFolder + "/" + entryName);

			if (!myFile.isDirectory() && entryName.contains(".")) {

				if (!myFile.exists()) {
					myFile.getParentFile().mkdirs();
					myFile.createNewFile();
				}
				fileoutputstream = new FileOutputStream(myFile);

				while ((n = zipInputStream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				fileoutputstream = null;
			}
			zipInputStream.closeEntry();
			zipentry = zipInputStream.getNextEntry();
			myFile = null;
			newFile = null;

		}// while

		zipInputStream.close();

		zipInputStream = null;
		zipentry = null;

		return true;

	}

	/**
	 * Extract the persistence.xml file into the Resource META-INF folder of the
	 * current project
	 * 
	 * @param project
	 * @throws IOException
	 */
	public static void extractPersistenceXmlFile(Project project)
			throws IOException {

		ResourceFacet resFacet = project.getFacet(ResourceFacet.class);

		File resourceFolder = resFacet.getResourceFolder()
				.getUnderlyingResourceObject();

		File metaInfFolder = new File(resourceFolder.getAbsolutePath().concat(
				"/META-INF"));

		unzipFile(ForgeConstants.PERSISTENCE_XML_ZIP, metaInfFolder);

	}

}
