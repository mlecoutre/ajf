package is.logi.crypto.hash;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.InvalidCDSException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An object of this class holds the status of a fingerprint still being
 * calculated.
 * <p>
 * A fingerprint state object can be repeatedly updated with data. At
 * any time a Fingerprint object can be requested for the data that has
 * then been added to the fingerprint state.
 * 
 * @see is.logi.crypto.hash.SHA1State
 * @see is.logi.crypto.hash.Fingerprint
 * @see is.logi.crypto.sign.Signature
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public abstract class HashState extends Crypto{
	
    /**
     * Return the number of bytes needed to make a valid hash. If a multiple
     * of this number of bytes is hashed, no padding is needed. If no such
     * value exists, returns 0. */
    public abstract int blockSize();
    /**
     * Return a fingerprint for the curret state, without
     * destroying the state. */
    public abstract Fingerprint calculate();
    // FINGERPRINT CLASS LIBRARIAN
    
    /**
     * Create a HashState object for the named fingerprint
     * algorithm.
     *
     * @exception InvalidCDSException if a HashState object for the
     *            named algorithm could not be created.
     */
    public static HashState create(String algorithm) throws InvalidCDSException {
        // Construct an object
        Class cl=Crypto.makeClass(algorithm+"State");

        Constructor con;
        try {
            Class[] parType = new Class[0];
            con=cl.getConstructor(parType);
        } catch (Exception e) {
            throw new InvalidCDSException(algorithm+" does not have a "+algorithm+"State() constructor");
        }
        
        Object r;
        try{
            Object[] arg = new Class[0];
            r = con.newInstance(arg);
        } catch (InvocationTargetException e1){
            throw new InvalidCDSException("Unable to create an instance of "+algorithm+"State [ "+e1.getTargetException().toString()+" ]");
        } catch (Exception e){
            throw new InvalidCDSException("Unable to create an instance of "+algorithm);
        }
        try{
            return (HashState) r;
        } catch (ClassCastException e){
            throw new InvalidCDSException(algorithm+"State is not a descendant of HashState");
        }
    }
    // INSTANCE METHODS
    
    /** Return the name of the algorithm used by this HashState object. */
    public abstract String getName();
    /**
     * Returns the size of a fingerprint in bytes. */
    public abstract int hashSize();
    /** Reset the state. */
    public abstract void reset();
    /** Update the fingerprint state with the bytes from <code>buf</code>. */
    public void update(byte[] buf){
        update(buf, 0, buf.length);
    }
    /**
     * Update the fingerprint state with the bytes from
     * <code>buf[offset, offset+length-1]</code>. */
    public abstract void update(byte[] buf, int offset, int length);
    /** Update the fingerprint state with the characters from <code>s</code>. */
    public void update(String s){
        update(s.getBytes());
    }
}
