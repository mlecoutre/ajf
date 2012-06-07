package am.ajf.forge.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.impl.MethodImpl;

@Singleton
public class JavaHelper {

	/**
	 * Retrieve the list of attributes of a java class object. Only attribute
	 * associated to at least a getter or a setter will be retrieved (starting
	 * with 'get' or 'set'). Attribute names without the set or get prefixe will
	 * be returned, with a lower case first letter. </br> </br>If the class
	 * contain the methods :</br> <code>public void getFirstName()</code>
	 * ...</br> <code>public void setFirstName(String firstname)</code>...</br>
	 * <code>public void getName()</code>...</br>
	 * <code>public void setName(String name)</code>...</br>
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

			// if we find a setter or getter we extract the corresponding
			// attribute.

			if (member instanceof MethodImpl) { // We keep only methods
				MethodImpl method = (MethodImpl) member; // cast member to
															// method

				// check if first 3 letters of method name is 'get' or 'set'
				if (("get".equals(method.getName().substring(0, 3)))
						|| ("set".equals(method.getName().substring(0, 3)))) {

					// extract the Attribute name without the "set" or "get"
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
}