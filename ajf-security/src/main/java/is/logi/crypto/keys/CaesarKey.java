package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;

/**
 * The Caesar algorithm is supposedly the one Julius Caesar used by
 * hand many centuries ago. As you can imagine, this is <b>NOT A STRONG
 * CIPHER</b>, but included only to show how to write a very simple
 * Key class for the logi.crypto package. Often, the first assignment given
 * to cryptography students is to break this cipher.
 * <p>
 * Data is encrypted byte-by-byte by adding a constant value to it and
 * taking the 8 lowest order bits.
 * <p>
 * The CDS for a CaesarKey object is <code>CaesarKey(n)</code> where
 * n is a value in the range 0..255.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public final class CaesarKey extends SymmetricKey implements CipherKey {

	private byte shift;
	
    /** Create a new random Caesar key. */
    public CaesarKey() {
        shift = (byte)random.nextInt();;
    }
    /** Create a new Caesar key with the specified shift. */
    public CaesarKey(byte shift) {
        this.shift = shift;
    }
    /** The block-size for the Caesar cipher is one byte. */
    public final int cipherBlockSize(){
        return 1;
    }
    /**
     * Decrypt one byte. <code>source[i]</code> is decrypted and put in
     * <code>dest[j]</code>.
     */
    public final void decrypt(byte[] source, int i, byte[] dest, int j){
	dest[j]=(byte)(source[i]-shift);
    }
    /**
     * Encrypt one byte. <code>source[i]</code> is encrypted and put in
     * <code>dest[j]</code>.
     */
    public final void encrypt(byte[] source, int i, byte[] dest, int j){
        dest[j]=(byte)(source[i]+shift);
    }
    /** Return true iff the two keys are equivalent. */
    public final boolean equals(Object o){
        if (o==null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        return shift==((CaesarKey)o).shift;
    }
    /** The name of the algorithm is "Caesar". */
    public String getAlgorithm(){
        return ("Caesar");
    }
    /** The key-size for the Caesar cipher is 8 bytes. */
    public int getSize(){
        return 8;
    }
    /**
     * If "CaesarKey( key )" is a valid CDS for a CaesarKey, then
     * CaesarKey.parseCDS(key) will return the described CaesarKey object.
     * <p>
     * A valid CDS can be created by calling the CaesarKey.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static CaesarKey parseCDS(String key) throws InvalidCDSException{
        int i=0;
        while((i<key.length()) && Character.isWhitespace(key.charAt(i)))
            i++;
        if(i==key.length())
            throw new InvalidCDSException("Empty argument in CaesarKey(String)");
        int j=key.length()-1;
        while((j>=0) && Character.isWhitespace(key.charAt(j)))
            j--;
        // shift[i..j] is the contents of shift[] with spaces stripped.
        if(j==i && key.charAt(i)=='?')
            return new CaesarKey();
        else
            return new CaesarKey((byte)Integer.parseInt(key.substring(i,j+1)));
    }
    /** The block-size for the Caesar cipher is one byte. */
    public final int plainBlockSize(){
	return 1;
    }
    /**
     * Return a CDS for this key.
     *
     * @see is.logi.crypto.Crypto#fromString
     */
    public String toString(){
        if(shift<0)
            return "CaesarKey("+(256+shift)+")";
        else
            return "CaesarKey("+shift+")";
    }
}
