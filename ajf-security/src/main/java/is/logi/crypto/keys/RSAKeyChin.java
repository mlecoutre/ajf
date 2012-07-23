package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import java.math.BigInteger;

/**
 * This class uses the Chineese Remainder Theorem to speed up operations
 * on private RSA keys. It may not be used for public key objects, since
 * this would expose the factorization of the modulus and give away the
 * private key.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
class RSAKeyChin extends RSAKey {
	
	/** The factors of the RSA modulus */
	private BigInteger p,q;

	/** The inverse of q, modulo p. */
	private BigInteger u;
	
    ///////////////////////////////////////////////////////////////////////
    // KEY MANAGEMENT CODE
    
    /**
     * Create a new RSA key <code>(r,n)</code>.
     * It is a private key if <code>pri</code> is true.
     */
    public RSAKeyChin(BigInteger r, BigInteger n, BigInteger p, boolean pri) throws KeyException {
        super(r,n,pri);
        if(!pri)
            throw new KeyException("RSAKeyChin objects must not contain public keys");

        if(p.equals(ZERO))
            throw new KeyException("The given modulus factor is not correct");
        BigInteger[] t = n.divideAndRemainder(p);
        if(!t[1].equals(ZERO))
            throw new KeyException("The given modulus factor is not correct");
        
        this.p=p;
        this.q=t[0];
        
        u = q.modInverse(p);
    }
    /**
     * Decrypt one block of data. The ciphertext is taken from
     * <code>source</code> starting at offset <code>i</code> and
     * plaintext is written to <code>dest</code>, starting at
     * offset <code>j</code>.
     * <p>
     * The amount of data read and written will match the values returned
     * by <code>cipherBlockSize()</code> and <code>plainBlockSize()</code>.
     */
    public void decrypt(byte[] source, int i, byte[] dest, int j){
        int plainSize=plainBlockSize();
        
        byte[] cipher = new byte[plainSize+1];
        
        System.arraycopy(source,i, cipher,0, plainSize+1);
        BigInteger C=new BigInteger(1,cipher);

        // CHANGE ME TO CHINEESE
        //BigInteger P=C.modPow(r,n);
        BigInteger a = C.modPow(r,p);
        BigInteger b = C.modPow(r,q);
        BigInteger P = a.subtract(b).multiply(u).mod(p).multiply(q).add(b);
        
        byte[]plain = P.toByteArray();
        if(plain.length >= plainSize)
            // The output is a full plain block
            System.arraycopy(plain,plain.length-plainSize, dest, j, plainSize);
        else {
            // The output is a bit on the short side
            System.arraycopy(plain,0, dest,j+plainSize-plain.length, plain.length);
            for (int k=plainSize-plain.length-1; k>=0; k--)
                dest[j+k]=0;
        }
    }
    ///////////////////////////////////////////////////////////////////////
    // CIPHER CODE
    
    /**
     * Encrypt one block of data. The plaintext is taken from
     * <code>source</code> starting at offset <code>i</code> and
     * ciphertext is written to <code>dest</code>, starting at
     * offset <code>j</code>.
     * <p>
     * The amount of data read and written will match the values returned
     * by <code>plainBlockSize()</code> and <code>cipherBlockSize()</code>.
     */
    public void encrypt(byte[] source, int i, byte[] dest, int j){
        int plainSize = plainBlockSize();
        byte[] plain = new byte[plainSize];
        System.arraycopy(source,i, plain,0, plainSize);
        BigInteger P = new BigInteger(1,plain);

        // CHANGE ME TO CHINEESE
        BigInteger C = P.modPow(r,n);

        
        byte[] cipher = C.toByteArray();
        if(cipher.length >= plainSize+1)
            // The output is a full cipher block.
            System.arraycopy(cipher,cipher.length-(plainSize+1), dest, j, plainSize+1);
        else{
            // The output is a bit on the short side
            System.arraycopy(cipher,0, dest, j+(plainSize+1)-cipher.length, cipher.length);
            for (int k=plainSize-cipher.length; k>=0; k--)
                dest[j+k]=0;
        }
    }
    /**
     * Return a CDS for this key.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public String toString(){
        return "RSAKey("+r.toString(16)+','+n.toString(16)+','+p.toString(16)+")";
    }
}
