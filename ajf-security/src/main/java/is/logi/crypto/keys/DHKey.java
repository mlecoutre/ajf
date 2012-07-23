package is.logi.crypto.keys;

// Copyright (C) 1999 Logi Ragnarsson

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
 * This object holds one Diffie-Hellman key. They can be used for
 * Diffie-Hellman key-exchange with the DHKeyExNoninter and related
 * classes or directly for encryption and signatures, in which case
 * it uses the ElGamal algorithm.
 * <p>
 * CDS for a Diffie-Hellman key is <code>DHKey(x,g,m,pub)</code>
 * for a public key or <code>DHKey(x,g,m,pri)</code> for a private
 * key. In both cases <code>x</code>,<code>g</code> and <code>m</code> are
 * hexadecimal numbers.
 *
 * @see is.logi.crypto.protocols.DHKeyExNoninter
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHKey extends K implements Key, CipherKey, SignatureKey {
	
	/** Private key or null. */
	private BigInteger x;

	/** Public key. */
	private BigInteger y;
	
	/** Generator */
	private BigInteger g;
	
	/** Modulus */
	private BigInteger m;
	
    /**
     * Create a random private DHKey with an <code>n</code>
     * bit modulus.
     * <p>
     * Pre-calcualted modula exist for bit lengths 256, 512, 1024 and 2048.
     * Using these values saves a lot of time and does not weaken the keys.
     */
    public DHKey(int n){
        n = (n<256) ? 256 : (((n+7)/8)*8); // round up to nearest byte.
        
        m=getModulus(n);
        g=getGenerator(m);
        do{
            x=new BigInteger(n,random);
        } while (x.compareTo(m)>=0);
        y=g.modPow(x,m);
    }
    ///////////////////////////////////////////////////////////////////////
    // KEY MANAGEMENT CODE
    
    /**
     * Create a new Diffie-Hellman key object. An object is created for
     * <code>x</code> in the group modulo <code>m</code> with generator
     * <code>g</code>. It is a private key iff </code>pri<code> is
     * <code>true</code>.
     */
    public DHKey(BigInteger x, BigInteger g, BigInteger m, boolean pri){
        this.g=g;
        this.m=m;
        if(pri){
            this.x=x;
            this.y=g.modPow(x,m);
        } else {
            this.y=x;
        }
    }
    /**
     * Calculate the fingerprint for this key or the other in the pair.
     */
    protected Fingerprint calcFingerprint(boolean other){
        // The key-pair is uniquely defined by the public key, since the
        // private key is "merely" the dicrete log of the public key.
        HashState fs=new SHA1State();
        fs.update(getPublic().getKey().toByteArray());
        fs.update(getPublic().getM().toByteArray());
        if(other==(x==null))
            fs.update("pri");
        else
            fs.update("pub");
        return fs.calculate();
    }
    /**
     * Returns the size of the blocks that can be decrypted in one call
     * to decrypt(). For ElGamal keys this depends on the size of the key.
     */
    public int cipherBlockSize(){
        return 2*signBlockSize()+2;
    }
    /**
     * Create a pair of public/private keys in a group with an
     * <code>n</code> bit modulo.
     * <p>
     * Pre-calcualted modula exist for bit lengths 256, 512, 1024 and
     * 2048. Using these values saves a lot of time and does not weaken
     * the keys.
     */
    public static KeyPair createKeys(int n){
        DHKey pri = new DHKey(n);
        return new KeyPair(pri.getPublic(),
                           pri);
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
        int size=(m.bitLength()+7)/8;
        byte[] t = new byte[size];

        System.arraycopy(source,i,      t,0, size);
        BigInteger a = new BigInteger(1,t);
        
        System.arraycopy(source,i+size, t,0, size);
        BigInteger b = new BigInteger(1,t);

        BigInteger M = a.modPow(x.negate(),m).multiply(b).mod(m);

        int pbs=size-1;
        
        t=M.toByteArray();
        if(t.length>=pbs){
            // t is either the exact length or has a sign bit in front which
            // may be discarded.
            System.arraycopy(t, t.length-pbs, dest,j, pbs);
        } else {
            // t is short.
            for(int ii=j; ii<j+pbs-t.length; ii++) // zero out unused space
                dest[ii]=0;
            System.arraycopy(t,0, dest, j+pbs-t.length, t.length);
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
        BigInteger k;
        BigInteger m1=m.subtract(ONE);
        do{
            k=new BigInteger(m.bitLength(),random);
        } while (k.compareTo(m)>=0 || !k.gcd(m1).equals(ONE));
        // k is a random element in Z_m relatively prime to m-1.

        int pbs = plainBlockSize();
        byte[] t = new byte[pbs];
        System.arraycopy(source,i, t,0, plainBlockSize());
        BigInteger M = new BigInteger(1,t);
        
        BigInteger a = g.modPow(k,m);
        BigInteger b = y.modPow(k,m).multiply(M).mod(m);

        int size=pbs+1;
        
        t=a.toByteArray();
        if(t.length>=size){
            // t is either the exact length or has a sign bit in front which
            // may be discarded.
            System.arraycopy(t, t.length-size, dest,j, size);
        } else {
            // t is short.
            for(int ii=j; ii<j+size-t.length; ii++) // zero out unused space
                dest[ii]=0;
            System.arraycopy(t,0, dest, j+size-t.length, t.length);
        }
        
        t=b.toByteArray();
        if(t.length>=size){
            // t is either the exact length or has a sign bit in front which
            // may be discarded.
            System.arraycopy(t,t.length-size, dest,j+size, size);
        } else {
            // t is short.
            for(int ii=j+size; ii<j+2*size-t.length; ii++) // zero out unused space
                dest[ii]=0;
            System.arraycopy(t,0, dest, j+2*size-t.length, t.length);
        }
    }
    /**
     * Return true iff the two keys are equivalent.
     */
    public final boolean equals(Object o){
        if (o==null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        DHKey key=(DHKey)o;
        return (key.getKey().equals(getKey()) &&
                key.getM().equals(getM())     &&
                key.getG().equals(getG())
               );
    }
    /**
     * The name of the algorithm is "Diffie-Hellman".
     */
    public String getAlgorithm(){
        return "Diffie-Hellman";
    }
    /**
     * Return the generator for this key.
     */
    public BigInteger getG(){
        return g;
    }
    /**
     * Return a Generator for a modulus group.
     * <p>
     * Returns the smallest number <code>g</code> > 1 with
     * gcd(<code>g</code>,<code>m-1</code>)=1.
     */
    public static BigInteger getGenerator(BigInteger m){
        BigInteger g=TWO;
        BigInteger n=m.subtract(ONE);
        while(!n.gcd(g).equals(ONE))
            g=g.add(ONE);
        return g;
    }
    /**
     * Return the BigInteger representing this key.
     */
    public BigInteger getKey(){
        return (x!=null) ? x : y;
    }
    /**
     * Return the modulus for this key.
     */
    public BigInteger getM(){
        return m;
    }
    /**
     * Return a Diffie-Hellman modulus.
     * <p>
     * Return the largest prime <code>p</code> &lt; 2^<code>n</code> such
     * that (<code>p</code>-1)/2 is prime. This takes a long time unless
     * <code>n</code> is one of 256, 512, 1024 or 2048.
     * <p> 
     * The reason for the second constraint on <code>p</code> is to make
     * the Discrete-Logarithm problem harder in the group Zp. In
     * particular it thwarts the Pholig-Hellman algorithm.
     */
    public static BigInteger getModulus(int n){
        if(n<256)
            n=256;
        else
            n=((n+7)/8)*8; // round up to nearest byte.
        
        if (n==256) 
            return TWO.pow(n).subtract(BigInteger.valueOf(36113));
        if (n==512)
            return TWO.pow(n).subtract(BigInteger.valueOf(38117));
        if (n==1024)
            return TWO.pow(n).subtract(BigInteger.valueOf(1093337));
        if (n==2048)
            return TWO.pow(n).subtract(BigInteger.valueOf(1942289));

        // The user asked for a non-standard modulus-size. We will
        // comply, but he probably won't have the patience to wait.

        BigInteger m=TWO.pow(n).subtract(ONE);
        //int sub=1;
        BigInteger k=m.divide(TWO);
        while(!m.isProbablePrime(2) || // First check very quickly
              !k.isProbablePrime(2) ||
              !m.isProbablePrime(4) ||
              !k.isProbablePrime(4) ||
              !m.isProbablePrime(8) ||
              !k.isProbablePrime(8) ||
              !m.isProbablePrime(primeCertainty-14)|| // Then check very well
              !k.isProbablePrime(primeCertainty-14)){
            m=m.subtract(FOUR);
            //sub+=4;
            k=m.divide(TWO);
        }
        return m;
    }
    /**
     * Return the public key from the pair.
     */
    public DHKey getPublic(){
        return (x==null) ? this : new DHKey(y,g,m,false);
    }
    /**
     * Return the "size" of the key. This is a (fairly inaccurate) measure
     * of how difficult it is to break and is heavily dependant on the
     * algorithm used.
     */
    public int getSize(){
        return m.bitLength();
    }
    /**
     * Returns true iff this is a private key.
     */
    public boolean isPrivate(){
        return (x!=null);
    }
    /**
     * Check if a key mathces this. This is true if this and key are a matched
     * pair of public/private.
     */
    public boolean matches(Key key){
        if(key instanceof DHKey)
            return getPublic().equals(((DHKey)key).getPublic());
        else return false;
    }
    /**
     * If "DHKey( key )" is a valid CDS for a DHKey, then
     * DHKey.parseCDS(key) will return the described DHKey object.
     * <p>
     * A valid CDS can be created by calling the DHKey.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static DHKey parseCDS(String key) throws InvalidCDSException{
        DHKey ret=null;
        try {
            StreamTokenizer st=new StreamTokenizer(new StringReader(key));
            st.ordinaryChars('0','9');
            st.wordChars('0','9');
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as first argument to DHKey()");
            BigInteger x = new BigInteger(st.sval,16);
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+x.toString(16));
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as second argument to DHKey()");
            BigInteger g = new BigInteger(st.sval,16);
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+g.toString(16));
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("Hexadecimal string expected as third argument to DHKey()");
            BigInteger m = new BigInteger(st.sval,16);
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+m.toString(16));
            if(st.nextToken()!=StreamTokenizer.TT_WORD)
                throw new InvalidCDSException("\"pub\" or \"pri\" expected as second argument to DHKey()");
            String pubpri=st.sval;
            if(pubpri.equals("pri"))
                ret=new DHKey(x,g,m,true);
            else if(pubpri.equals("pub"))
                ret=new DHKey(x,g,m,false);
            else
                throw new InvalidCDSException("\"pub\" or \"pri\" expected as second argument to DHKey()");
            if(st.nextToken()!=StreamTokenizer.TT_EOF){
                throw new InvalidCDSException("no more parameters expected in DHKey() after "+pubpri);
            }
        } catch (IOException e){
            // StringReader doesn't really throw exceptions.
        }
        return ret;
    }
    ///////////////////////////////////////////////////////////////////////
    // CIPHER CODE (ElGamal algorithm)
    
    /**
     * Returns the size of the blocks that can be encrypted in one call
     * to encrypt(). For ElGamal keys this depends on the size of the key.
     */
    public int plainBlockSize(){
        return (m.bitLength()-1)/8;
    }
    /**
     * Create a signature for a Fingerprint fith a private key.
     *
     * @exception KeyException if the key modulus is shorter than the signature.
     * @exception KeyException if this is not a private key
     */
    public Signature sign(Fingerprint fp) throws KeyException {
        if(x==null)
            throw new KeyException("Signatures can only be verified with public ElGamal keys.");
        
        BigInteger k;
        BigInteger m1=m.subtract(ONE);
        do{
            k=new BigInteger(m.bitLength(),random);
        } while (k.compareTo(m)>=0 || !k.gcd(m1).equals(ONE));
        // k is a random element in Z_m relatively prime to m-1.
        
        BigInteger M = new BigInteger(1,fp.getBytes());
        BigInteger a = g.modPow(k,m);
        BigInteger b = k.modInverse(m1).multiply(M.subtract(x.multiply(a))).mod(m1);

        int size=(m.bitLength()+7)/8;
        byte[] sb = new byte[2*size];
        
        byte[] t=a.toByteArray();
        if(t.length>=size){
            // t is either the exact length or has a sign bit in front which
            // may be discarded.
            System.arraycopy(t, t.length-size, sb,0, size);
        } else {
            // t is short.
            System.arraycopy(t,0, sb, size-t.length, t.length);
        }
        
        t=b.toByteArray();
        if(t.length>=size){
            // t is either the exact length or has a sign bit in front which
            // may be discarded.
            System.arraycopy(t,t.length-size, sb,size, size);
        } else {
            // t is short.
            System.arraycopy(t,0, sb, 2*size-t.length, t.length);
        }
        
        return new Signature(sb, fp.getName(), matchFingerprint());
    }
    /**
     * Returns the length of a signature in bytes. */
    public int signatureSize(){
        return 2*signBlockSize()+2;
    }
    ///////////////////////////////////////////////////////////////////////
    // SIGNATURE CODE (ElGamal algorithm)
    
    /**
     * Returns the maximum size in bytes of the fingerprints
     * that can be signed. */
    public int signBlockSize(){
        return (m.bitLength()-1)/8;
    }
    /**
     * Return a CDS for this key.
     */
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("DHKey(");
        if(x==null)
            sb.append(y.toString(16));
        else
            sb.append(x.toString(16));
        sb.append(',');
        sb.append(g.toString(16));
        sb.append(',');
        sb.append(m.toString(16));
        sb.append(x==null ? ",pub)" : ",pri)");
        return sb.toString();
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
        if(x!=null)
            throw new KeyException("Signatures can only be verified with public ElGamal keys.");
        
        int size=(m.bitLength()+7)/8;
        byte[] sb = s.getBytes();
        if(sb.length!=2*size)
            return false;
        
        byte[] t = new byte[size];

        System.arraycopy(sb,0,   t,0, size);
        BigInteger a = new BigInteger(1,t);
        
        System.arraycopy(sb,size, t,0, size);
        BigInteger b = new BigInteger(1,t);

        BigInteger M = new BigInteger(1,fp.getBytes());

        BigInteger l = y.modPow(a,m).multiply(a.modPow(b,m)).mod(m);
        BigInteger r = g.modPow(M,m);
        return l.equals(r);
    }
}
