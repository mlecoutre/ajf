package is.logi.crypto.keys;

// Copyright (C) 1999 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;

import java.math.BigInteger;

/**
 * This class is only here to provide backward compatibility.
 *
 * @deprecated All functionality moved to the DHKey class.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class ElGamalKey extends DHKey {
	
    /**
     * Create a random private ElGamalKey with an <code>n</code>
     * bit modulus.
     * <p>
     * Pre-calcualted modula exist for bit lengths 256, 512, 1024 and 2048.
     * Using these values saves a lot of time and does not weaken the keys.
     */
    public ElGamalKey(int n){
        super(n);
    }
    ///////////////////////////////////////////////////////////////////////
    // KEY MANAGEMENT CODE
    
    /**
     * Create a new ElGamal key object. An object is created for
     * <code>x</code> in the group modulo <code>m</code> with
     * generator <code>g</code>. It is a private key iff </code>pri<code>
     * is <code>true</code>.
     */
    public ElGamalKey(BigInteger x, BigInteger g, BigInteger m, boolean pri){
        super(x,g,m,pri);
    }
    /**
     * If "ElGamalKey( key )" is a valid CDS for a ElGamalKey, then
     * ElGamalKey.parseCDS(key) will return the described DHKey object.
     * <p>
     * Note that this method returns an instance of the DHKey class which
     * has succeeded the ElGamal class.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static DHKey parseCDS(String key) throws InvalidCDSException{
        return DHKey.parseCDS(key);
    }
}
