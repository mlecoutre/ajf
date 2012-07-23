package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.hash.HashState;
import is.logi.crypto.hash.SHA1State;
import is.logi.crypto.sign.Signature;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.math.BigInteger;

/**
 * The RSA algorithm is probably the best known and most widely used
 * public key algorithm. Breaking one RSA key is believed to be as
 * difficult as factoring the large integer that comprises the key, and
 * there is no known way to do this in a reasonable time. Therefore RSA
 * should be about as secure as anything if you keep your keys long. 1024
 * bits should be more than enough in most cases, but the clinically
 * paranoid may want to use up to 4096 bit keys.
 * <p>
 * Each RSA key is a pair (r,n) of integers and matches another key (s,n).
 * If P is a block of plain data represented as an integer smaller than n,
 * then it can be encrypted with the transformation:
 * <blockquote>
 *   <code>E = (P^r) mod n</code>
 * </blockquote>
 * which has the inverse transformation:
 * <blockquote>
 *   <code>P = (E^s) mod n</code>
 * </blockquote>
 * <p>
 * The key owner will keep one key secret and publish the other as widely
 * as possible. This allows anyone who gets hold of the public key to
 * encrypt data which can only be decrypted with the corresponding private
 * key.
 * <p>
 * Data that is encrypted with a private key can similarly only be
 * decrypted with the corresponding public key. This is useful for digital
 * signatures.
 * <p>
 * When P is created from an array of bytes, it will correspond to as many
 * bytes of plain data as the bytes needed to store n, less one.
 * <p>
 * Each chunk of ciphertext encrypted with RSAKey has as many bytes as the
 * key modulo. However, the plaintext it encodes has one less byte.
 * <p>
 * The CDS for the RSAKey class is <code>RSAKey(r,n,pub)</code> for a public key,
 * <code>RSAKey(r,n,pri)</code> for a private key or <code>RSAKey(r,n,p)</code>
 * for a private key where we know one factor of <code>n</code>. In all cases
 * <code>r</code>, <code>n</code> and  <code>p</code> are hexadecimal numbers.
 *
 * @see is.logi.crypto.sign.Signature
 * @see is.logi.crypto.Crypto#fromString(String)
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class RSAKey extends K implements CipherKey,SignatureKey {
	
	/** <code>R</code> is the exponent used in all created public keys. */
	protected static final BigInteger R=BigInteger.valueOf(65537);
	
	/** The RSA key exponent */
	protected BigInteger r;
	
	/** The RSA key modulo */
	protected BigInteger n;
	
	/** Is it a private key? */
	protected boolean pri;
	
	/** The fingerprint for the other key in the pair, or null. */
	protected Fingerprint matchPrint=null;
	
    ///////////////////////////////////////////////////////////////////////
    // KEY MANAGEMENT CODE
    
    /**
     * Create a new RSA key <code>(r,n)</code>.
     * It is a private key if <code>pri</code> is true.
     */
    public RSAKey(BigInteger r, BigInteger n, boolean pri) {
        super();
        this.pri = pri;
        this.r = r;
        this.n = n;
    }
    /**
     * Calculate the fingerprint for this key or the other in the
     * pair.
     *
     * @see is.logi.crypto.K#getFingerprint
     * @see is.logi.crypto.K#matchFingerprint
     */
    protected Fingerprint calcFingerprint(boolean other){
        // The key-pair is uniquely defined by n, since it factors uniquely
        // into p and q which are used to calculate both exponents.
        HashState fs=new SHA1State();
        fs.update(n.toByteArray());
        if(other==pri)
            fs.update("pub");
        else
            fs.update("pri");
        return fs.calculate();
    }
    /**
     * Returns the size of the blocks that can be decrypted in one call
     * to decrypt(). For RSA keys this depends on the size of the key.
     */
    public int cipherBlockSize(){
        return plainBlockSize()+1;
    }
    /**
     * Create a pair of public/private keys. The key modulo will be
     * <code>bitLength</code> or <code>bitLength-1</code> bits.
     */
    public static KeyPair createKeys(int bitLength){
        if (bitLength<256)
            bitLength=256;
        
        BigInteger p=null;
        BigInteger q=null;
        BigInteger s=null;
        BigInteger n=null;
        while(n==null) {
            // FIXME! Pick random odd number and check every second
            //        value from there, as per Brandt & Damgaard
            // FIXME! Use java.util.Random to test for primality.
            //        (but of course use good random bits to select
            //         the starting point)
            try{
                p=new BigInteger(bitLength/2, primeCertainty, random);
                q=new BigInteger(bitLength/2, primeCertainty, random);
                BigInteger m=p.subtract(ONE).multiply(q.subtract(ONE));
                s=R.modInverse(m);
                n=p.multiply(q);
            } catch (ArithmeticException e) {
                // Key generation failed... just try again.
                n=null;
            }
        }
        
        Key pub=new RSAKey    (R,n,false);
        Key pri=null;
        try {
            pri=new RSAKeyChin(s,n,p, true);
        } catch (KeyException e){
            // Won't happen unless there is a bug in the above, but just in case...
            e.printStackTrace();
            pri=new RSAKey(s,n,true);
        }
        return new KeyPair(pub,pri);
    }
    /**
     * Create a KeyPair object holding objects for the public RSA key
     * <code>(r,n)</code> and the private RSA key (s,n).
     *
     * @exception KeyException if (r,n) and (s,n) does not describe a valid
     * pair of RSA keys.
     */
    public static KeyPair createKeys(BigInteger r, BigInteger s, BigInteger n) throws KeyException {
        Key pub = new RSAKey(r,n, false);
        Key pri = new RSAKey(s,n, true);
        try{
            return new KeyPair(pub,pri);
        } catch (Exception e) {
            throw new KeyException("The (r,s,n) triplet does not specify a matching pair of RSA keys.");
        }
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
        BigInteger P=C.modPow(r,n);
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
    /** Return true iff the two keys are equivalent. */
    public boolean equals(Object o){
        if (o==null)
            return false;
        if((o instanceof RSAKey) || (o instanceof RSAKeyChin)){
            RSAKey rsa = (RSAKey)o;
            return (r.equals(rsa.r) &&
                    n.equals(rsa.n) &&
                    pri==rsa.pri);
        } else
            return false;
    }
    /** The name of the algorithm is "RSA". */
    public String getAlgorithm(){
        return ("RSA");
    }
    /** Return the size of the key modulo in bits. */
    public int getSize(){
        return n.bitLength();
    }
    /** Return true iff this is a private key. */
    public boolean isPrivate(){
        return pri;
    }
    /**
     * Check if a key mathces this. This is true if this and key are a matched
     * pair of public/private keys. */
    public final boolean matches(Key key){
        if (key.getClass() != this.getClass())
            return false;
        RSAKey k=(RSAKey)key;
        if (!n.equals(k.n))
            return false;
        return true;
    }
    /**
     * If "RSAKey( key )" is a valid CDS for a RSAKey, then
     * RSAKey.parseCDS(key) will return the described RSAKey object.
     * <p>
     * A valid CDS can be created by calling the RSAKey.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static RSAKey parseCDS(String key) throws InvalidCDSException{
        RSAKey ret=null;
        try {
            StreamTokenizer st=new StreamTokenizer(new StringReader(key));
            st.ordinaryChars('0','9');
            st.wordChars('0','9');
            
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as first argument to RSAKey()");
            BigInteger r = new BigInteger(st.sval,16);
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+r.toString(16));
            
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as second argument to RSAKey()");
            BigInteger n = new BigInteger(st.sval,16);
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+n.toString(16));
            
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("modulus factor, \"pub\" or \"pri\" expected as third argument to RSAKey()");
            if(st.sval.equals("pri"))
                // private key
                ret=new RSAKey(r,n,true);
            else if(st.sval.equals("pub"))
                // public key
                ret=new RSAKey(r,n,false);
            else {
                // we have a modulus factor
                try{
                    BigInteger p = new BigInteger(st.sval,16);
                    ret=new RSAKeyChin(r,n,p,true);
                } catch (Exception e) {
                    throw new InvalidCDSException(e.getMessage());
                }
            }

            String last = st.sval;
            if(st.nextToken()!=StreamTokenizer.TT_EOF){
                //System.out.println(st.ttype);
                throw new InvalidCDSException("no more parameters expected in RSAKey after "+last);
            }
        } catch (IOException e){
            // StringReader doesn't really throw exceptions.
        }
        return ret;
    }
    ///////////////////////////////////////////////////////////////////////
    // CIPHER CODE
    
    /**
     * Returns the size of the blocks that can be encrypted in one call
     * to encrypt(). For RSA keys this depends on the size of the key.
     */
    public int plainBlockSize(){
        return (n.bitLength()-1)/8;
    }
    /**
     * Create a signature for a Fingerprint fith a private key.
     *
     * @exception KeyException if the key modulus is shorter than the signature.
     * @exception KeyException if this is not a private key
     */
    public Signature sign(Fingerprint fp) throws KeyException {
        if(!pri)
            throw new KeyException("Signatures can only be verified with public RSA keys.");
        
        byte[] buf=fp.getBytes();
        byte[] bigBuf=new byte[plainBlockSize()];
        byte[] cipher=new byte[cipherBlockSize()];
        if(bigBuf.length<buf.length)
            throw new KeyException("This key is to short to sign this hash.");
        System.arraycopy(buf,0, bigBuf,bigBuf.length-buf.length, buf.length);
        
        // Pad with random bytes
        int i=bigBuf.length-buf.length-1;
        int n=i%8;
        i=(i/8)*8;
        writeBytes(random.nextInt(), bigBuf, i, n);
        for(i=i-8; i>=0; i-=8)
            writeBytes(random.nextLong(), bigBuf, i, 8);
        
        // Encrypt
        encrypt(bigBuf,0, cipher,0);
        return new Signature(cipher, fp.getName(), matchFingerprint());
    }
    /**
     * Returns the length of the signature in bytes. */
    public int signatureSize(){
        return cipherBlockSize();
    }
    ///////////////////////////////////////////////////////////////////////
    // SIGNATURE CODE
    
    /**
     * Returns the maximum size in bytes of the fingerprint
     * that can be signed. */
    public int signBlockSize(){
        return plainBlockSize();
    }
    /**
     * Return a CDS for this key.
     *
     * @see is.logi.crypto.Crypto#fromString
     */
    public String toString(){
        return "RSAKey("+r.toString(16)+','+n.toString(16)+','+(pri?"pri":"pub")+")";
    }
    /**
     * Verify a Signature on a Fingerprint with a public key.
     * <p>
     * The method returns true iff <code>s</code> is a signature for
     * <code>fp</code> created with the mathcin private key.
     *
     * @exception KeyException if this is not a public key
     */
    public boolean verify(Signature s, Fingerprint fp) throws KeyException{
        if(pri)
            throw new KeyException("Signatures can only be verified with public RSA keys.");
        
        byte[] buf=s.getBytes();
        byte[] bigBuf=new byte[cipherBlockSize()];
        byte[] plain=new byte[plainBlockSize()];
        
        // Decrypt
        System.arraycopy(buf,0, bigBuf,bigBuf.length-buf.length, buf.length);
        decrypt(bigBuf,0,plain,0);
        
        // Chack for equality (of the appropriate sub-string)
        byte[] fpb = fp.getBytes();
        return equalSub(plain,plain.length-fpb.length, fpb,0, fpb.length);
    }
}
