package am.ajf.forge.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.impl.MethodImpl;

import am.ajf.forge.core.generators.templates.McrGenerationTemplate;

@Singleton
public class JavaHelper {

	/**
	 * Retrieve the list of attributes of a java class object. Only attribute
	 * associated to a getter will be retrieved (starting with 'get'). Attribute
	 * names without the get prefixe will NOT be returned, others will be
	 * returned with a lower case first letter. </br> </br>If the class contain
	 * the methods :</br> <code>public void getFirstName()</code> ...</br>
	 * <code>public void setFirstName(String firstname)</code>...</br>
	 * <code>public void getName()</code>...</br>
	 * <code>public void setName(String name)</code>...</br>
	 * <code>public void setAge(int age)</code>...</br>
	 * <code>public void retrieveAge(...)</code>...</br> </br> the method will
	 * return the following list of String:</br><code>firstName</code></br>
	 * <code>name</code></br>
	 * 
	 * @param javasource
	 * @return List<String> attributeList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> retrieveAttributeList(JavaSource javasource) {

		List<Member> members = javasource.getMembers();
		List<String> entityAttributes = new ArrayList<String>();
		for (Member member : members) {

			// if we find a getter we extract the corresponding
			// attribute.

			if (member instanceof MethodImpl) { // We keep only methods
				MethodImpl method = (MethodImpl) member; // cast member to
															// method

				// check if first 3 letters of method name is 'get'
				if (("get".equals(method.getName().substring(0, 3)))) {

					// extract the Attribute name without the "get"
					// with first letter on lower case
					String attributeName = WordUtils.uncapitalize(member
							.getName().substring(3));

					// keep attributes Name avoiding double (because of set and
					// get)
					if (!entityAttributes.contains(attributeName))
						entityAttributes.add(attributeName);
				}
			}
		}

		// un comment to log list of attribute
		// for (String attribute : entityAttributes) {
		// System.out.println(attribute);
		// }

		return entityAttributes;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> retrieveMethodList(JavaSource javaRes) {

		List<Member> members = javaRes.getMembers();
		List<String> methodNames = new ArrayList<String>();

		for (Member member : members) {

			// if we find a getter we extract the corresponding
			// attribute.

			if (member instanceof MethodImpl) { // We keep only methods
				MethodImpl method = (MethodImpl) member; // cast member to
															// method
				if (!methodNames.contains(method.getName()))
					methodNames.add(method.getName());
			}
		}

		return methodNames;

	}

	/**
	 * Returns the same list of String, but each data in the list will have it's
	 * first letter capitalized
	 * 
	 * @param valuesList
	 * @return
	 */
	public List<String> capitalizeDatas(List<String> valuesList) {
		List<String> capitalizedDatas = new ArrayList<String>();

		for (String value : valuesList) {
			capitalizedDatas.add(WordUtils.capitalize(value));
		}
		return capitalizedDatas;
	}

	/**
	 * Return as String the txt file set as input. Note, the input txt file must
	 * be in the Resource Zip file JavaClassStringTemplate.zip
	 * 
	 * @param fileName
	 * @return String
	 * @throws Exception
	 */
	@Deprecated
	public String getJavaClassAsString(String zipContainingTxts, String fileName)
			throws Exception {

		try {
			// tmp directory to extract zip
			File tmpDirectory = new File(FileUtils.getTempDirectoryPath()
					+ "/ajfForge/tmpJavaClassTxt");

			// extract zip file
			ExtractionUtils.unzipFile(zipContainingTxts, tmpDirectory);

			// load txt file
			File javaClassTxt = new File(tmpDirectory.getAbsolutePath().concat(
					"/" + fileName));

			// open stream in file
			FileInputStream javaClassTxtIs = new FileInputStream(javaClassTxt);

			// Read stream and write into String
			char[] byteArr = new char[1024];
			Writer stringWriter = new StringWriter();
			Reader reader = new BufferedReader(new InputStreamReader(
					javaClassTxtIs, "UTF-8"));
			int n;
			while ((n = reader.read(byteArr)) != -1) {
				stringWriter.write(byteArr, 0, n);
			}

			// Result of Java class as String
			String output = stringWriter.toString();

			// Clean everything
			javaClassTxtIs.close();
			javaClassTxtIs = null;
			stringWriter.close();
			stringWriter = null;
			reader.close();
			reader = null;
			FileUtils.forceDelete(tmpDirectory);

			return output;

		} catch (IOException e) {

			throw new Exception(
					"ERROR occured in getJavaMethodBody for fileName = "
							+ fileName);
		}

	}

	/**
	 * Update managed bean with input UT <br>
	 * <br>
	 * -generate empty Methods for new UTs<br>
	 * -Add import for new Result Beans<br>
	 * -generate getter and setters of new attributes (foir list or create UT)<br>
	 * -update init method if needed<br>
	 * 
	 * @param utToBeAdded
	 * @param function
	 * @param entityName
	 * @param libDTOPackage
	 * @param managedBeanJavaclass
	 * @param projectManagement
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateManagedBean(List<String> utToBeAdded, String function,
			String entityName, String libDTOPackage,
			JavaClass managedBeanJavaclass,
			McrGenerationTemplate projectManagement) throws Exception {

		for (String ut : utToBeAdded) {
			System.out.println("Adding UT:" + ut + "...");

			// Temp file to store templated method
			File tempFile = new File(FileUtils.getTempDirectoryPath().concat(
					"/ajf-forge/managedBeanMethodForUT.tmp"));
			try {

				/*
				 * Generate Method for new UT
				 */
				// generate method for managed bean for additional UT
				projectManagement.buildManagedBeanMethod(tempFile, function,
						entityName, ut, libDTOPackage);

				// parse as java class (containing one method, which
				// is the method we want)
				JavaClass temp = (JavaClass) JavaParser.parse(tempFile);

				// if new DTO are ADDED, we must set the corresponding
				// import
				for (Import imprt : temp.getImports()) {
					// Import new Result bean if needed
					if (!managedBeanJavaclass.getImports().contains(imprt)) {
						managedBeanJavaclass.addImport(imprt);
					}
				}

				/*
				 * add also new attributes and getter setters
				 */
				boolean modifFlag = false;
				for (Member member : temp.getMembers()) {

					if (member instanceof Field<?>) {
						// Add field if needed
						managedBeanJavaclass
								.addField(((Field<JavaClass>) member)
										.toString());
						modifFlag = true;
					}

					if (member instanceof Method) {
						// Add method if needed (mainly for getter setters)
						Method<JavaClass> memberMethod = (Method<JavaClass>) member;

						// We add all method except the INI method as it will be
						// treated later
						if (!"init".equals(memberMethod.getName())) {
							if (!managedBeanJavaclass.getMethods().contains(
									memberMethod)) {

								Method<JavaClass> newMethod = managedBeanJavaclass
										.addMethod(memberMethod.toString());
								newMethod.setBody(memberMethod.getBody());
								modifFlag = true;
							}
						}
					}
				}

				/*
				 * Update init method if needed
				 */
				// if modif have been done, may need to update the init
				// method of managed bean for initialization of new entities
				if (modifFlag) {
					try {

						Method<JavaClass> initMethodMBean = managedBeanJavaclass
								.getMethod("init");
						Method<JavaClass> initemp = temp.getMethod("init");

						if (!initemp.getBody()
								.equals(initMethodMBean.getBody())) {

							initMethodMBean.setBody(initMethodMBean.getBody()
									+ "\n" + initemp.getBody());

						}

					} catch (Exception e) {
						System.err
								.println("**ERROR : Fail to update the @PostCostruct init method of managed bean. You may encouter initialization errors");
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				System.err
						.println("Problem occured while generating additional UT "
								.concat(ut + ":").concat(e.toString()));
				throw e;

			} finally {

				// clean temp file
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}

		}

	}
}