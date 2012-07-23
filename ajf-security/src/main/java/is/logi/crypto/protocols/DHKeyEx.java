package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.DHKey;
import is.logi.crypto.keys.Key;
import is.logi.crypto.keys.KeyException;

import java.math.BigInteger;

/**
 * Ancestor of Diffie-Hellman key exchange objects
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHKeyEx extends Crypto {
	
	int modSize;
	String keyType;
	
	protected BigInteger m;
	protected BigInteger g;
	
	protected BigInteger myPrivate;
	protected BigInteger myPublic;
	protected BigInteger hisPublic;
	
	protected boolean keyDecided=false;
	protected Key sessionKey;
	
    /**
     * Create a new  DHKeyEx object which uses
     * an <code>n</code> bit modulus and the named key type.
     * <p>
     * There are pre-computed public modulus and gnerator pairs for
     * values these of <code>n</code>: 256, 512, 1024 usig these values
     * speeds things up significantly. (I am calcualting the values for
     * 2048) */
    protected DHKeyEx(int n, String keyType){
        m=DHKey.getModulus(n);
        g=DHKey.getGenerator(m);
        modSize=m.bitLength();
        do{
            myPrivate=new BigInteger(modSize,random);
        } while (myPrivate.compareTo(m)>0);
        myPublic=g.modPow(myPrivate,m);
        this.keyType = keyType;
    }
    /**
     * Create a new  DHKeyEx object which uses
     * the private DH key from <code>pri</code>.
     *
     * @exception KeyException if the key is not private.
     */
    protected DHKeyEx(DHKey pri, String keyType) throws KeyException {
        if(!pri.isPrivate())
            throw new KeyException("Expecting a private key.");
        m=pri.getM();
        g=pri.getG();
        myPrivate=pri.getKey();
        myPublic=g.modPow(myPrivate,m);
        modSize=m.bitLength();
        this.keyType = keyType;
    }
    /** Returns true iff this end of the protocol i completed. */
    public boolean completed(){
        return keyDecided;
    }
    /**
     * Returns the key if it has been decided upon,
     * or <code>null</code> otherwise.
     */
    public Key sessionKey(){
        return keyDecided ? sessionKey : null;
    }
}
