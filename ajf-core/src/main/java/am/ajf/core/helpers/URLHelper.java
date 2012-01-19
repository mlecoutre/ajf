package am.ajf.core.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class URLHelper {

	/**
	 * 
	 * @param url
	 * @param sep
	 * @return
	 * @throws IOException
	 */
	public static String loadURLAsString(URL url, String sep) throws IOException {

		String result = null;

		// Connect to the URL with timeout management
		BufferedReader buffer = null;
		try {
			InputStream inputStream = url.openStream();
			buffer = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer res = new StringBuffer();
			String str = null;
			String theSep = "";
			while ((str = buffer.readLine()) != null) {
				res.append(theSep).append(str);
				theSep = sep;
			}
			result = res.toString();
		} finally {
			if (null != buffer) {
				try {
					buffer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

}
