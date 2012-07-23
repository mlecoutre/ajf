package crypto;

import java.io.IOException;

public class StreamExhausted extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StreamExhausted() {
		super();
	}

	public StreamExhausted(String string) {
		super(string);
	}

}
