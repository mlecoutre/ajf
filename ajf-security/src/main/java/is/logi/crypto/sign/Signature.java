package is.logi.crypto.sign;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;

/**
 * This class stores a digital signature. It is created with a SignatureKey
 * from a Fingerprint and can later be used to verify that Fignerprint with
 * the same symmetric key or the other asymmetric key from the pair.
 *
 * @see is.logi.crypto.hash.Fingerprint
 * @see is.logi.crypto.keys.SignatureKey
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 *         (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class Signature extends Crypto {
	
	private byte[] s;             // encrypted fingerprint.
	private Fingerprint keyPrint; // Fingerprint of the key used to create the signature.
	private String fingAlg;       // Fingerptint algorithm used.
	
    /**
     * Create a new Signature object. It contains the signature <code>s</code>
     * which was generated from a fingerprint using the specified
     * <code>algorithm</code>. It can be verified with the key with fingerprint
     * <code>k</code>.
     */
    public Signature(byte[] s, String algorithm, Fingerprint k){
        this.s = new byte[s.length];
        System.arraycopy(s,0, this.s,0, s.length);
        fingAlg=algorithm;
        keyPrint = k;
    }
    /**
     * Return the bytes from this signature.
     */
    public byte[] getBytes(){
        byte[] ss = new byte[s.length];
        System.arraycopy(s,0, ss,0, s.length);
        return ss;
    }
    /**
     * Return the name of the algorithm used to fingerprint the data
     * before signing.
     */
    public String getFingerprintAlgorithm(){
        return fingAlg;
    }
    /**
     * Return the fingerprint of the key used to verify this signature.
     */
    public Fingerprint getKeyPrint(){
        return keyPrint;
    }
    /**
     * If "Signature( key )" is a valid CDS for a Signature, then
     * Signature.parseCDS(key) will return the described Signature object.
     * <p>
     * A valid CDS can be created by calling the Signature.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static Signature parseCDS(String arg) throws InvalidCDSException{
        try{
            int a=0;
            int i = arg.indexOf(',');
            int b=i;
            while(b>0 && Character.isWhitespace(arg.charAt(b-1)))
                b--;
            while(a<b && Character.isWhitespace(arg.charAt(a)))
                a++;
            String sign=arg.substring(a,b);
            
            a=i+1;
            i = arg.indexOf(',',i+1);
            b=i;
            while(b>0 && Character.isWhitespace(arg.charAt(b-1)))
                b--;
            while(a<b && Character.isWhitespace(arg.charAt(a)))
                a++;
            String fingAlg=arg.substring(a,b);
            
            a=i+1;
            i = arg.lastIndexOf(')');
            b=i;
            while(b>0 && Character.isWhitespace(arg.charAt(b-1)))
                b--;
            while(a<b && Character.isWhitespace(arg.charAt(a)))
                a++;
            String keyPrint=arg.substring(a,b+1);

            return new Signature(fromHexString(sign),
                                 fingAlg,
                                 (Fingerprint)fromString(keyPrint)
                                );
        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidCDSException("Wrong number of arguments to Signature()");
        }
    }
    /**
     * Return a CDS for this object.
     */
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("Signature(");
        sb.append(hexString(s));
        sb.append(',');
        sb.append(fingAlg);
        sb.append(',');
        sb.append(keyPrint.toString());
        sb.append(')');
        return sb.toString();
    }
}
