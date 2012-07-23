package am.ajf.security.utils;

import org.junit.Test;

public class SignatureTest {

	@Test
	public void testEncryptDecrypt() {
		
		String ref = "PI72lHSzYtYDtWcm8Kz6PApLiWWofk+a9kT1nLM+NhE=";
		
		SignatureCrypt crypt = new SignatureCrypt();
		crypt.setUser("u002617");
		crypt.setPassword("myPrivateCredential");
		
		crypt.crypt();
		
		System.out.println(crypt.getCryptedString());
		System.out.println(ref.equals(crypt.getCryptedString()));
		
		
		
	}
	
}
