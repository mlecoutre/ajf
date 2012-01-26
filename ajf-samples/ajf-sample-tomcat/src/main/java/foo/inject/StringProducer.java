package foo.inject;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class StringProducer {

	public StringProducer() {
		super();
	}

	@Produces
	public String produceString(InjectionPoint ip) throws Throwable {

		String res = "a String for ".concat(ip.getMember().getName());
		return res;

	}

}
