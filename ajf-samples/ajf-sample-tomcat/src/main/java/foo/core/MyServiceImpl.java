package foo.core;

import foo.inject.Monitored;
import foo.lib.MyService;

public class MyServiceImpl implements MyService {

	public MyServiceImpl() {
		super();
	}

	@Monitored
	public String sayHello(String who) {
		return "Hello ".concat(who);
	}

}
