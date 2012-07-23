package am.ajf.security.utils;

import is.logi.crypto.keys.RSAKey;

import java.io.IOException;
import java.math.BigInteger;
import java.util.StringTokenizer;

import crypto.BASE64Decoder;

/**
 * This type was created in VisualAge.
 */
public class SignatureDecrypt {
	private String CryptedString;
	public String domain = null; 
	public String user = null;
	private String password = null;
	private RSAKey privateKey;
	/**
	 * SignatureDecrypt constructor comment.
	 */
	public SignatureDecrypt() {
		super();
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void decrypt() throws IOException {
		String bufferDest;
		byte[] tblDeCrypt = new byte[32];
		BASE64Decoder myBase64Decoder = new BASE64Decoder();

		getPrivateKey().decrypt(
			myBase64Decoder.decodeBuffer(getCryptedString()),
			0,
			tblDeCrypt,
			0);
		bufferDest = new String(tblDeCrypt);
		StringTokenizer myTokenizer =
			new java.util.StringTokenizer(bufferDest, ":");
		
		String userDomain = null;
		String uid = myTokenizer.nextToken().trim();
		int idx = uid.indexOf("@");
		if (idx>0) {
			userDomain = uid.substring(idx+1);
			if ("null".equalsIgnoreCase(userDomain))
				userDomain = null;
			uid = uid.substring(0,idx); 
		}
		
		setUser(uid);
		setDomain(userDomain);
		setPassword(myTokenizer.nextToken().trim());

	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	private String getCryptedString() {
		return CryptedString;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * This method was created in VisualAge.
	 * @return is.logi.crypto.keys.RSAKey
	 */
	private is.logi.crypto.keys.RSAKey getPrivateKey() {
		if (privateKey == null) {
			BigInteger s =
				new BigInteger(
					"880f6adffed213e600546dd2c2f50455e2473c7b44c675d7be39e1a0aed0241",
					16);
			BigInteger n =
				new BigInteger(
					"505cb2c62d851e6f99f4cf8c597f7b493ac3b03f3df6602512efaa74d14636dd",
					16);
			privateKey = new RSAKey(s, n, true);
		}
		return privateKey;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String getUser() {
		return user;
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void init() {

		setCryptedString("RuPb4f3LlXbzGRQOMuVwFQubNhayZzD0KZ4LbbeLGeA=");
		try {
			decrypt();
			//System.out.println(getUser() + " " + getPassword());
		} catch (IOException e) {
			// Nothing to do
		}

	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	public void setCryptedString(String newValue) {
		this.CryptedString = newValue;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	private void setPassword(String newValue) {
		this.password = newValue;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	private void setUser(String newValue) {
		this.user = newValue;
	}

	/**
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param string
	 */
	public void setDomain(String string) {
		domain = string;
	}

}
