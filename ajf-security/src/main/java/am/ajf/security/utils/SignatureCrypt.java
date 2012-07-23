package am.ajf.security.utils;

import is.logi.crypto.keys.RSAKey;

import java.math.BigInteger;

import crypto.BASE64Encoder;

public class SignatureCrypt {
	
	private String domain = null;
	private String user;
	private String password;
	public String cryptedString;
	private RSAKey publicKey;

	/**
	 * This method was created in VisualAge.
	 */
	public SignatureCrypt() {
		super();
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void crypt() {

		byte[] tblCrypt = new byte[32];
		String blank = "                                ";

		String bufferSrc = getUser();
		if ((null != getDomain()) && (getDomain().trim().length() > 0)) {
			bufferSrc += "@" + getDomain();
		}
		bufferSrc += ":" + getPassword();
		
		bufferSrc += blank.substring(bufferSrc.length());
		getPublicKey().encrypt(bufferSrc.getBytes(), 0, tblCrypt, 0);
		
		//setCryptedString(myBase64Encoder.encode(tblCrypt));
		//Utilisation d'un encodeur sp�cifique pour �viter les caract�res sp�ciaux
		//non accept�s dans les cookies [] () = , " / ? @ : ;	
		
		BASE64Encoder encoder = new BASE64Encoder();
		setCryptedString(encoder.encode(tblCrypt));

	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String getCryptedString() {
		return cryptedString;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	private String getPassword() {
		return password;
	}
	/**
	 * This method was created in VisualAge.
	 * @return is.logi.crypto.keys.RSAKey
	 */
	public RSAKey getPublicKey() {
		if (publicKey == null) {
			BigInteger r = new BigInteger("10001", 16);
			BigInteger n =
				new BigInteger(
					"505cb2c62d851e6f99f4cf8c597f7b493ac3b03f3df6602512efaa74d14636dd",
					16);
			publicKey = new RSAKey(r, n, false);
		}
		return publicKey;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	private String getUser() {
		return user;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	private void setCryptedString(String newValue) {
		this.cryptedString = newValue;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	public void setPassword(String newValue) {
		this.password = newValue;
	}

	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.String
	 */
	public void setUser(String newValue) {
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
