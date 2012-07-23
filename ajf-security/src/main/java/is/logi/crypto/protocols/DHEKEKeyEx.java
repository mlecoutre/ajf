package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.keys.DHKey;
import is.logi.crypto.keys.Key;

import java.math.BigInteger;

/**
 * Ancestor of Diffie-Hellman EKE key exchange objects
 *
 * Implementation notes:
 * <ul>
 *  <li>Alice sends no identification number in the first package. It is assumed
 *  that bob knows who is (supposedly) knocking. Normally these calsses would
 *  be used in conjunction with the CipherStreamsXxx classes, and you can
 *  simply do this preliminary phase before encapsulating the streams in the
 *  CipherStreamsXxx objects,
 *
 *  <li>The random strings that Alice and Bob choose are exactly the size of one
 *  block of the session key.
 *
 *  <li>When encrypting only one these random strings, ECB mode is used. All other
 *  encryptions are done in CBC mode. (CBC doesn't add anything when encrypting
 *  a single random number once)
 * </ul>
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
class DHEKEKeyEx extends Crypto {
	
	int modSize;
	String keyType;
	
	// Z_m = <g>
	protected BigInteger m;
	protected BigInteger g;
	
	// DH keys
	protected BigInteger myPrivate;
	protected BigInteger myPublic;
	
	// random values for authentication
	protected byte[] myRandom;
	
	// the shared secret used for authentication
	protected CipherKey secretKey;
	
	protected boolean completed=false;
	protected CipherKey sessionKey;
	protected int pbs;  // the size of a plain block for the session key.
	protected int cbs;  // the size of a cipher block for the session key.
	
	
    /**
     * Create a new  DHEKEKeyEx object which uses an <code>n</code> bit
     * modulus for the Diffie-Hellman protocol, the named key type and
     * the specified secret key.
     * <p>
     * There are pre-computed public modulus and gnerator pairs for
     * these values of <code>n</code>: 256, 512, 1024 and 2048. Using these
     * values speeds things up significantly. */
    public DHEKEKeyEx(int n, String keyType, CipherKey secretKey){
        m=DHKey.getModulus(n);
        g=DHKey.getGenerator(m);
        modSize=m.bitLength();
        do{
            myPrivate=new BigInteger(modSize,random);
        } while (myPrivate.compareTo(m)>0);
        myPublic=g.modPow(myPrivate,m);
        this.keyType = keyType;
        this.secretKey = secretKey;
    }
    /** Returns true iff this end of the protocol i completed. */
    public boolean completed(){
        return completed;
    }
    /**
     * Returns the key if it has been decided upon,
     * or <code>null</code> otherwise.
     */
    public Key sessionKey(){
        return completed ? sessionKey : null;
    }
}
