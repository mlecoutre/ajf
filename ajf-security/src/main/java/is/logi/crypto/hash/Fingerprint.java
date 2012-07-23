package is.logi.crypto.hash;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.InvalidCDSException;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
/**
 * This class is used to hold a fingerprint of a particular data buffer.
 * The idea is to calculate a fingerprint in such a way that it is extremely
 * difficult to create a buffer that gives a particular fingerprint. If
 * that buffer also has to match some other criteria, such as being a valid 
 * text file in a particular language, then it becomes next to impossible.
 * <p>
 * All this depends on the hash function used to create the fingerprint
 * being a good one. Fingerprints are created by the various subclasses
 * of HashState, so you should look there for information about
 * a particular hash function.
 * <p>
 * The CDS for a Fingerprint object is <code>Fingerprint(NAME,FP)</code>
 * where <code>NAME</code> is the name of the algorithm used and
 * <code>FP</code> the actual fingerprint.
 *
 * @see is.logi.crypto.hash.HashState
 * @see is.logi.crypto.sign.Signature
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class Fingerprint extends Crypto{
	
	// =================================================================== //
	
	/** Holds the actual bytes of the fingerprint value. */
	protected byte[]fp=null;
	
	/** Holds the name of the algorithm used to create this fingerprint. */
	protected String algorithm=null;
	
    /** 
     * Creates a new Fingerprint object. It contains the hash value from
     * <code>fp[offset..offset+n-1]</code> which was generated with the
     * named algorithm.
     */
    public Fingerprint(String algorithm, byte[] fp, int offset, int n){
        this.algorithm = algorithm;
        this.fp = new byte[n];
        System.arraycopy(fp,offset, this.fp,0, n);
    }
    // CREATE FINGERPRINTS DIRECTLY
    
    /**
     * Create a Fingerprint object. It will contain a fingerprint for the
     * data in <code>buf[offset..offset+length-1]</code> calculated with
     * the named fingerprint algorithm.
     *
     * @exception InvalidCDSException if a HashState object for the
     *            named algorithm could not be created.
     */
    public static Fingerprint create(byte[] buf, int offset, int length, String algorithm) throws InvalidCDSException {
        HashState fps = HashState.create(algorithm);
        fps.update(buf, offset, length);
        return fps.calculate();
    }
    /**
     * Create a Fingerprint object. It will contain a fingerprint for the
     * data in <code>buf</code> calculated with the named
     * fingerprint algorithm.
     *
     * @exception InvalidCDSException if a HashState object for the
     *            named algorithm could not be created.
     */
    public static Fingerprint create(byte[] buf, String algorithm) throws InvalidCDSException {
        return Fingerprint.create(buf, 0, buf.length, algorithm);
    }
    /**
     * Create a Fingerprint object. It will contain a fingerprint for the
     * string s calculated with the named fingerprint algorithm.
     *
     * @exception InvalidCDSException if a HashState object for the
     *            named algorithm could not be created.
     */
    public static Fingerprint create(String s, String algorithm) throws InvalidCDSException {
        HashState fps = HashState.create(algorithm);
        fps.update(s);
        return fps.calculate();
    }
    /**
     * Test for equality with another object. Returns true if <code>obj</code>
     * is a Fingerprint equal to <code>this</code>.
     */
    public boolean equals(Object obj){
        if (obj==null)
            return false;
        if (obj.getClass()!=this.getClass())
            return false;
        // It's the same class
        
        Fingerprint f=(Fingerprint)obj;
        if (!f.algorithm.equals(algorithm))
            return false;
        // It's the same algorithm.
        
        for (int i=fp.length-1; i>=0; i--)
            // this.fp[j] == fp[j] for j=0, 1, ..., i-1.
            if (fp[i]!=f.fp[i])
                return false;
        
        return true;
    }
    /** Return an array of the bytes in the fingerprint. */
    public byte[] getBytes(){
        byte[] fp=new byte[this.fp.length];
        System.arraycopy(this.fp,0, fp,0, this.fp.length);
        return fp;
    }
    /** Return the name of the algorithm used for this fingerprint. */
    public String getName(){
        return algorithm;
    }
    /**
     * Return a hash-code based on the bytes of the
     * fingerprint and the algorithm name.
     */
    public int hashCode(){
        int h=algorithm.hashCode();
        for (int i=0; i<fp.length; i++)
            h = h ^ ( fp[i] << (i%4)*8 );
        return h;
    }
    /**
     * If "Fingerprint( key )" is a valid CDS for a Fingerprint, then
     * Fingerprint.parseCDS(key) will return the described Fingerprint object.
     * <p>
     * A valid CDS can be created by calling the Fingerprint.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static Fingerprint parseCDS(String arg) throws InvalidCDSException{
        Fingerprint ret=null;
        try{
            StreamTokenizer st=new StreamTokenizer(new StringReader(arg));
            st.ordinaryChars('0','9');
            st.wordChars('0','9');
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Fingerprint algorithm name expected as first argument to Fingerprint()");
            String algorithm=st.sval;
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+algorithm);
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as second argument to Fingerprint()");
            byte[] fp=fromHexString(st.sval);

            ret = new Fingerprint(algorithm, fp, 0, fp.length);
        } catch (IOException e){
            // StringReader doesn't actually throw any exceptions
        }
        return ret;
    }
    /**
     * Return a CDS for this fingerprint.
     */
    public String toString(){
        return "Fingerprint("+algorithm+","+hexString(fp)+")";
    }
}
