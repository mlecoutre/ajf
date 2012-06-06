package am.ajf.web.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/helloworlds")
public class HelloWorldsResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloWorlds() {
		String res = "Hello World!";
		return res;
	}

}