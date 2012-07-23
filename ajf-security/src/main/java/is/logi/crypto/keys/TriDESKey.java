package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;

/**
 * This is the class for triple-DES keys used in an EDE3 configuration. This
 * is a variant of the standard DES cipher which increases the size of the
 * key-space to make exhaustive key-search infeasible.
 * <p>
 * The CDS for a triple-DES key is <code>TriDESKey(key)</code> with
 * <code>key</code> a string of 48 hexadecimal digits to create a specific key
 * or <code>TriDESKey(?)</code> for a random TriDESKey object.
 *
 * @see is.logi.crypto.keys.DESKey
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class TriDESKey extends SymmetricKey implements CipherKey {
	
	private DESKey k1;
	private DESKey k2;
	private DESKey k3;
	
    /** Create a new random triple-DES key. */
    public TriDESKey(){
        k1 = new DESKey();
        k2 = new DESKey();
        k3 = new DESKey();
    }
    /** Create a new triple-DES key with the key bits from <code>key[0..23]</code>. */
    public TriDESKey(byte[] key){
        k1=new DESKey(makeLong(key, 0,8));
        k2=new DESKey(makeLong(key, 8,8));
        k3=new DESKey(makeLong(key,16,8));
    }
    /** The block-size for the triple-DES cipher is 8 bytes. */
    public int cipherBlockSize(){
        return 8;
    }
    /**
     * Decrypt one block of data. The encrypted data is taken from
     * <code>dest[i..i+23]</code> and plaintext is written to
     * <code>source[j..j+23]</code>.
     */
    public void decrypt(byte[] source, int i, byte[] dest, int j){
        long block = makeLong(source,i,8);
        block = pickBits(block,DESKey.IP);
        block = k3.subDecrypt(block);
        block = k2.subCrypt(block);
        block = k1.subDecrypt(block);
        block = pickBits(block,DESKey.FP);
        writeBytes(block, dest, j, 8);
    }
    /**
     * Encrypt one block of data. The plain data is taken from
     * <code>source[i..i+23]</code> and ciphertext is written to
     * <code>dest[j..j+23]</code>.
     */
    public void encrypt(byte[] source, int i, byte[] dest, int j){
        long block = makeLong(source,i,8);
        block = pickBits(block,DESKey.IP);
        block = k1.subCrypt(block);
        block = k2.subDecrypt(block);
        block = k3.subCrypt(block);
        block = pickBits(block,DESKey.FP);
        writeBytes(block,dest,j,8);
    }
    /** Return true iff the two keys are equivalent. */
    public boolean equals(Object o){
        if (o==null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        TriDESKey k=(TriDESKey)o;
        return k1.equals(k.k1) && k2.equals(k.k2) && k3.equals(k.k3);
    }
    /** The name of the algorithm is "DES". */
    public String getAlgorithm(){
        return "TriDES";
    }
    /** Return the key-bits for this key as an array of 24 bytes. */
    public byte[] getKey(){
        byte[] k=new byte[24];
        System.arraycopy(k1.getKey(),0, k, 0, 8);
        System.arraycopy(k2.getKey(),0, k, 8, 8);
        System.arraycopy(k3.getKey(),0, k,16, 8);
        return k;
    }
    /** The key-size for the DES cipher is 168 bits. */
    public int getSize(){
        return 168;
    }
    /**
     * If "TriDESKey( key )" is a valid CDS for a TriDESKey, then
     * TriDESKey.parseCDS(key) will return the described TriDESKey object.
     * <p>
     * A valid CDS can be created by calling the TriDESKey.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static TriDESKey parseCDS(String key) throws InvalidCDSException{
        int i=0;
        while((i<key.length()) && Character.isWhitespace(key.charAt(i)))
            i++;
        if(i==key.length())
            throw new InvalidCDSException("Empty argument in TriDESKey(String)");
        int j=key.length()-1;
        while((j>=0) && Character.isWhitespace(key.charAt(j)))
            j--;
        // key[i..j] is the contents of key[] with spaces stripped.
        if(j==i && key.charAt(i)=='?'){
            return new TriDESKey();
        }
        else {
            byte[] k = fromHexString(key);
            return new TriDESKey(k);
        }
    }
    /** The block-size for the triple-DES cipher is 8 bytes. */
    public int plainBlockSize(){
        return 8;
    }
    /**
     * Return a CDS for this key.
     *
     * @see is.logi.crypto.Crypto#fromString
     */
    public String toString(){
        StringBuffer sb=new StringBuffer(56);
        sb.append("TriDESKey(");
        sb.append(hexString(k1.getKey()));
        sb.append(hexString(k2.getKey()));
        sb.append(hexString(k3.getKey()));
        sb.append(")");
        return sb.toString();
    }
}
