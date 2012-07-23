package is.logi.crypto;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.keys.KeySource;
import is.logi.crypto.random.RandomFromReader;
import is.logi.crypto.random.RandomSpinner;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Random;

/**
 * This class contains numerous static and final utility functions
 * along with global variables for the logi.crypto package.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public abstract class Crypto{
	
	// -----------------------------------------------------------
	// CLASS VARIABLES
	
	/**
	 * This is the default random generator used by various Crypto
	 * classes. It should be a cryptographically secure
	 * random number generator, preferably without any period, which
	 * rules out all generators based on iterated functions, such as
	 * java.util.Random.
	 * <p>
	 * If the file <code>/dev/urandom</code> exists, <code>random</code>
	 * is initialized to an instance of the RandomFromReader class
	 * which read that file. Otherwise it is initialized to an instance
	 * of the RandomSpinner class.
	 *
	 * @see is.logi.crypto.random.RandomSpinner
	 * @see is.logi.crypto.random.RandomFromReader */
	public static Random random=defaultRandom();
	
	/**
	 * We allow a chance of 0.5**primeCertainty chance that given a composite
	 * number, the primaility check will say it is a prime. For complicated
	 * reasons, the actual chance of generating a false prime is <i>much</i>
	 * lower.
	 */
	public static int primeCertainty=10;
	
	/** The constant zero. */
	protected static final BigInteger ZERO=BigInteger.valueOf(0);
	
	/** The constant one. */
	protected static final BigInteger ONE=BigInteger.valueOf(1);
	
	/** The constant two. */
	protected static final BigInteger TWO=BigInteger.valueOf(2);
	
	/** The constant four. */
	protected static final BigInteger FOUR=BigInteger.valueOf(4);
	
	/**
	 * The object used to store and retrieve keys.
	 * It is used by the <code>lookup(fingerprint)</code> CDS.
	 */
	public static KeySource keySource;
	
	/** The hexadecimal digits "0" through "f". */
	public static char[] NIBBLE = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
	};
	
	/** The binary digits "0" and "1". */
	public static char[] BIT = { '0', '1' };
	
	// -----------------------------------------------------------
	// CDS PARSING METHODS

	/**
	 * The array of names of packages that are searched for classes
	 * mentioned in a CDS.
	 */
	public static String[] cdsPath = {
		"is.logi.crypto.",
		"is.logi.crypto.keys.",
		"is.logi.crypto.hash.",
		"is.logi.crypto.sign."
	};

	/*
	 * Returns a nicely formated version of the CDS.
	 *
	 public static String prettyCDS(String cds){
	 return cds;
	 }
	 */
    /**
     * Convert an int to a string of binary digits.
     */
    public static final String binString(int a){
        StringBuffer sb = new StringBuffer(32);
        for (int i=0; i<32; i++)
            sb.append(BIT[(int)(a >>> (31-i)) & 0x1]);
        return sb.toString();
    }
    /**
     * Convert a long to a string of binary digits.
     */
    public static final String binString(long a){
        StringBuffer sb = new StringBuffer(64);
        for (int i=0; i<64; i++)
            sb.append(BIT[(int)(a >>> (63-i)) & 0x1]);
        return sb.toString();
    }
    private static final Random defaultRandom(){
        try {
            // Try to use random bits from the rnadom number device
            return new RandomFromReader(new FileReader("/dev/urandom"));
        } catch (Throwable e) {
            // /dev/random does not exists or is not readable
            // (may be security exception)
            return new RandomSpinner();
        }
    }
    /**
     * Return true iff two array contain the same bytes.
     */
    public static boolean equal(byte[] a, byte[] b){
        if(a.length!=b.length)
            return false;
        for(int i=a.length-1; i>=0; i--)
            if(a[i]!=b[i])
                return false;
        return true;
    }
    /**
     * Return true iff two arrays contain the same bytes, discounting
     * any zero bytes from the front of the arrays.
     */
    public static boolean equalRelaxed(byte[] a, byte[] b){
        // I assume the compiler knows that the various length fields are
        // constant. (I should look at the bytecode, but can't be bothered)
        if(a.length>b.length){
            for(int i=a.length-b.length-1; i>=0; i--)
                if(a[i]!=0)
                    return false;
            for(int i=a.length-1; i>a.length-b.length; i--)
                if(a[i]!=b[i+b.length-a.length])
                    return false;
        } else {
            for(int i=b.length-a.length-1; i>=0; i--)
                if(b[i]!=0)
                    return false;
            for(int i=b.length-1; i>b.length-a.length; i--)
                if(b[i]!=a[i+a.length-b.length])
                    return false;
        }
        return true;
    }
    /**
     * Return true iff a sub-array of two arrays contain the same bytes.
     * Compares <code>a[i..i+length-1]</code> and <code>b[j..j+length-1]</code>.
     */
    public static boolean equalSub(byte[] a, int i, byte[] b, int j, int length){
        int end=i+length;
        while(i<end)
            if (a[i++] != b[j++])
                return false;
        return true;
    }
    /**
     * Convert a hexadecimal digit to a byte.
     */
    public static byte fromHexNibble(char n){
        if(n<='9')
            return (byte)(n-'0');
        if(n<='G')
            return (byte)(n-('A'-10));
        return (byte)(n-('a'-10));
    }
    /**
     * Convert a string of hexadecimal digits to a byte array.
     */
    public static byte[] fromHexString(String hex){
        int l=(hex.length()+1)/2;
        byte[] r = new byte[l];
        int i = 0;
        int j = 0;
        if(hex.length()%2 == 1){
            // Odd number of characters: must handle half byte first.
            r[0]=fromHexNibble(hex.charAt(0));
            i=j=1;
        }
        while(i<l)
            r[i++] = (byte)((fromHexNibble(hex.charAt(j++)) << 4) | fromHexNibble(hex.charAt(j++)));
        return r;
    }
    /**
     * Parse the given Cipher Description String (CDS).
     * <p>
     * This method can be used to parse a CDS such as that returned by
     * the Key and Fingerprint <code>toString()</code> methods and return the
     * described object.
     * <p>
     * The CDS syntax is one of:<ul>
     *   <li><code>ClassName(parameters)</code>
     *   <li><code>lookup(fingerprint)</code>
     * </ul>
     * <p>
     * <code>ClassName</code> is the name of the class to generate. By default
     * the string <code>"is.logi.crypto."</code> is prepended to the
     * class name and an instance created with the <code>ClassName(String)
     * </code> constructor. See the documentation for various classes for
     * details.
     * <p>
     * The <code>lookup(fingerprint)</code> CDS assumes <code>fingerprint</code>
     * to be a CDS for a Fingerprint object. It then looks up the key with the
     * specified fingerprint in <code>keySource</code>.
     * <p>
     * This method may throw exceptions with very long, nested explanations if
     * an exception occurs in a sub-CDS.
     *
     * @see #keySource
     * @exception IOException if an error occured reading characers from <code>in</code>
     * @exception InvalidCDSException if the CDS is in some way malformed.
     */
    public static Object fromString(Reader cds) throws InvalidCDSException, IOException{
        // PARSE CDS
        StringBuffer sb=new StringBuffer();
        
        int ch=pastSpace(cds);
        if(ch==-1)
            return null;
        while(Character.isJavaIdentifierPart((char)ch) && (ch!=-1)){
            sb.append((char)ch);
            ch=cds.read();
        }
        String name=sb.toString();
        
        sb=new StringBuffer();
        if(Character.isWhitespace((char)ch)){
            ch=pastSpace(cds);
        }
        if(ch!='(')
            throw new InvalidCDSException("( expected after "+name);
        int parDepth=1;
        boolean quoted=false;
        ch=cds.read();
        while(true){
            if(ch=='"')
                quoted = !quoted;
            else if(!quoted){
                if(ch=='(')
                    parDepth++;
                else if(ch==')')
                    parDepth--;
            }
            if((parDepth==0) || (ch==-1))
                break;
            sb.append((char)ch);
            ch=cds.read();
        }
        if(ch==-1)
            throw new InvalidCDSException("parentheses after "+name+" never closed");
        String param=sb.toString();

        // FIND OR CREATE OBJECT
        
        if(name.equals("lookup")){
            // Look up an object by it's fingerprint
            Object f= fromString(param);
            if(!(f instanceof Fingerprint))
                throw new InvalidCDSException("Fingerprint object expected as parameter to lookup()");
            Fingerprint fi=(Fingerprint)f;
            if(keySource==null)
                throw new InvalidCDSException("No key-source has been assigned");
            return keySource.byFingerprint(fi);
        } else {
            // Construct an object by name
            Class cl = makeClass(name);
            Method parseCDS;
            try {
                Class[] parType = { Class.forName("java.lang.String") };
                parseCDS=cl.getMethod("parseCDS", parType);
            } catch (Exception e) {
                throw new InvalidCDSException(name+" does not have a "+name+"(String) constructor");
            }

            Object r;
            try{
                Object[] arg = { param };
                r = parseCDS.invoke(null,arg);
            } catch (InvocationTargetException e1){
                throw new InvalidCDSException("Unable to create an instance of "+name+" [ "+e1.getTargetException().toString()+" ]");
            } catch (Exception e){
                throw new InvalidCDSException("Unable to create an instance of "+name);
            }
            return r;
        }
    }
    /**
     * Parse the given Cipher Description String (CDS). This method calls the
     * <code>fromString(Reader)</code> method after wrapping th cds in a
     * StringReader.
     *
     * @exception InvalidCDSException if the CDS is in some way malformed.
     */
    public static Object fromString(String cds) throws InvalidCDSException{
        try{
            return fromString(new StringReader(cds));
        } catch (IOException e) {
            // StringReader doesn't actually throw any exceptions.
            return null;
        }
    }
    /**
     * Convert a byte array to a string of hexadecimal digits.
     */
    public static final String hexString(byte[] buf){
        StringBuffer sb = new StringBuffer(buf.length*2);
        for (int i=0; i<buf.length; i++){
            sb.append(NIBBLE[(buf[i]>>>4)&15]);
            sb.append(NIBBLE[ buf[i]     &15]);
        }
        return sb.toString();
    }
    /**
     * Convert a byte to a string of hexadecimal digits.
     */
    public static final String hexString(byte a){
        StringBuffer sb = new StringBuffer(2);
        sb.append(NIBBLE[(a>>>4)&0xf]);
        sb.append(NIBBLE[a&0xf]);
        return sb.toString();
    }
    /**
     * Convert an int to a string of hexadecimal digits.
     */
    public static final String hexString(int a){
        StringBuffer sb = new StringBuffer(8);
        for (int i=0; i<8; i++)
            sb.append(NIBBLE[(int)(a >>> (60-4*i)) & 0xf]);
        return sb.toString();
    }
    /**
     * Convert a long to a string of hexadecimal digits.
     */
    public static final String hexString(long a){
        StringBuffer sb = new StringBuffer(16);
        for (int i=0; i<16; i++)
            sb.append(NIBBLE[(int)(a >>> (60-4*i)) & 0xf]);
        return sb.toString();
    }
    /**
     * Create a Class object for the named class. The class is searched
     * for in the packages named in the CDS Path, which by default
     * includes the appropriate logi.crypto package names.
     *
     * @exception InvalidCDSException if the class could not be created
     */
    public static Class makeClass(String name) throws InvalidCDSException {
        for(int i=0; i<cdsPath.length; i++){
            try{
                return Class.forName(cdsPath[i]+name);
            } catch (ClassNotFoundException e) { }
        }
        throw new InvalidCDSException("The class "+name+" was not found");
    }
    /**
     * Convert a byte array to an int. Bits are collected from
     * <code>buf[i..i+length-1]</code>. */
    public static final int makeInt(byte[] buf, int i, int length){
        int r=0;
        length+=i;
        for (int j=i; j<length; j++)
            r= (r<<8) | (buf[j] & 0xff);
        return r;
    }
    // -----------------------------------------------------------
    // UTILITY METHODS
    
    /**
     * Convert a byte array to a long. Bits are collected from
     * <code>buf[i..i+length-1]</code>. */
    public static final long makeLong(byte[] buf, int i, int length){
        long r=0;
        length+=i;
        for (int j=i; j<length; j++)
            r= (r<<8) | (buf[j] & 0xffL);
        return r;
    }
    /**
     * Convert a byte array to a CipherKey. Returns a new key of type
     * <code>keyType</code>, with key-material from <code>bits</code>.
     * <p>
     * <code>keyType</code> should be the name of a class which implements
     * the CipherKey interface, such as "TriDESKey".
     *
     * @exception InvalidCDSException if the key could not be created
     */
    public static CipherKey makeSessionKey(String keyType, byte[] bits) throws InvalidCDSException{
        // Construct an object
        Class cl=makeClass(keyType);

        Constructor con;
        try {
            Class[] parType = { bits.getClass() };
            con=cl.getConstructor(parType);
        } catch (Exception e) {
            throw new InvalidCDSException(keyType+" does not have a "+keyType+"(byte[]) constructor");
        }

        Object r;
        try{
            Object[] arg = { bits };
            r = con.newInstance(arg);
        } catch (InvocationTargetException e1){
            throw new InvalidCDSException("Unable to create an instance of "+keyType+" [ "+e1.getTargetException().toString()+" ]");
        } catch (Exception e){
            throw new InvalidCDSException("Unable to create an instance of "+keyType);
        }

        try{
            return (CipherKey)r;
        } catch(ClassCastException e){
            throw new InvalidCDSException(keyType+" does not implement CipherKey");
        }
    }
    /**
     * Read characters from a Reader until a non-space character
     * is reached and return that character. */
    public static int pastSpace(Reader r) throws IOException{
        int ch=' ';
        while(Character.isWhitespace((char)ch) && (ch!=-1))
            ch=r.read();
        return ch;
    }
    /**
     * Construct an int by picking bits from another int. The number in
     * <code>bits[i]</code> is the index of the bit within <code>a</code>
     * that should be put at index <code>i</code> in the result.
     * <p>
     * The most-significant bit is number 0.
     */
    public static final int pickBits(int a, byte[] bits){
        int r=0;
        int l=bits.length;
        for (int b=0; b<l; b++)
            r = (r<<1) | ((a >>> (31-bits[b])) & 1);
        return r;
    }
    /**
     * Construct an long by picking bits from another long. The number in
     * <code>bits[i]</code> is the index of the bit within <code>a</code>
     * that should be put at index <code>i</code> in the result.
     * <p>
     * The most-significant bit is number 0.
     */
    public static final long pickBits(long a, byte[] bits){
        long r=0;
        int l=bits.length;
        for (int b=0; b<l; b++)
            r = (r<<1) | ((a >>> (63-bits[b])) & 1);
        return r;
    }
    /**
     * Read an int from an InputStream in bigendian order. */
    public static final int readInt(InputStream in) throws IOException {
        int r;
        r = (in.read() & 0xff);
        r = (r << 8 ) | (in.read() & 0xff);
        r = (r << 8 ) | (in.read() & 0xff);
        r = (r << 8 ) | (in.read() & 0xff);
        return r;
    }
    /**
     * Write an int to a byte array. Bits from <code>a</code> are written
     * to <code>dest[i..i+length-1]</code>. */
    public static final void writeBytes(int a, byte[] dest, int i, int length){
        for (int j=i+length-1; j>=i; j--){
            dest[j]=(byte)a;
            a = a >>> 8;
        }
    }
    /**
     * Write a long to a byte array. Bits from <code>a</code> are written
     * to <code>dest[i..i+length-1]</code>. */
    public static final void writeBytes(long a, byte[] dest, int i, int length){
        for (int j=i+length-1; j>=i; j--){
            dest[j]=(byte)a;
            a = a >>> 8;
        }
    }
    /**
     * Write an int to an OutputStream in bigendian order. */
    public static final void writeInt(OutputStream out, int x) throws IOException {
        out.write(x >>> 24);
        out.write(x >>> 16);
        out.write(x >>>  8);
        out.write(x       );
    }
}
