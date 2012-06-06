package am.ajf.web.resources;

import javax.ws.rs.core.MediaType;

/*
 import org.jboss.resteasy.client.ClientRequest;
 import org.jboss.resteasy.client.ClientResponse;
 */

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldsResourceTest {

	private final Logger logger = LoggerFactory
			.getLogger(HelloWorldsResourceTest.class);

	@Test
	public void testGet() throws Exception {

		/*
		 * ClientRequest request = new ClientRequest(
		 * "http://localhost:8080/test-simple/services/helloworlds");
		 * request.accept(MediaType.TEXT_PLAIN); // we're expecting a String
		 * back ClientResponse<String> response = request.get(String.class); if
		 * (response.getStatus() == 200) // OK! { String str =
		 * response.getEntity(); logger.info("Return: " + str); } else {
		 * logger.info("Return status: " + response.getStatus()); }
		 */

		// create the rest client instance
		RestClient restClient = new RestClient();

		// create the resource instance to interact with
		String url = "http://localhost:8080/test-simple/services/helloworlds";
		Resource resource = restClient.resource(url);

		// perform a GET on the resource. The resource will be returned as
		// String
		String res = resource.accept(MediaType.TEXT_PLAIN).get(String.class);
		logger.info(res);

	}

}
