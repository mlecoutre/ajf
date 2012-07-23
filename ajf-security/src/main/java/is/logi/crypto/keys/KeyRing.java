package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;

import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This implementation of the KeySource interface stores keys in a
 * hash-table. It can be converted to and from a CDS for storage.
 * <p>
 * A database key-source would be more appropriate for large
 * collections of keys, but hasn't been written yet.
 * An interface to a key-server would be more appropriate for
 * really huge key collections but will have to wait even longer.
 * <p>
 * The CDS for a KeyRing object is <code>KeyRing(k1,k2,...,kn)</code>
 * with <code>n</code>>=0 and each <code>ki</code> the CDS for a
 * KeyRecord or Key object.
 *
 * @see is.logi.crypto.keys.Key
 * @see is.logi.crypto.keys.KeyRecord
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class KeyRing extends Crypto implements KeySource{
	
	Hashtable keys;
	
    /** Create empty key ring. */
    public KeyRing(){
        keys = new Hashtable();
    }
    /**
     * Retreive the key with the given fingerprint. If it is not found in the
     * key-source, null is returned. <code>fingerprint</code> must be
     * created with the same algorithm as the the Key object uses, which
     * will be SHA1 for the Key classes included with Crypto.
     */
    public KeyRecord byFingerprint(Fingerprint fingerprint){
        return (KeyRecord)keys.get(fingerprint);
    }
    /**
     * Insert the Key <code>k</code> into the KeyRing. It will be wrapped in
     * a KeyRecord containing empty strings and no certificates.
     */
    public void insert(Key k){
        insert(new KeyRecord(k,"","",""));
    }
    /** Insert the KeyRecord <code>k</code> into the KeyRing. */
    public void insert(KeyRecord kc){
        keys.put(kc.getKey().getFingerprint(),kc);
    }
    /**
     * If "KeyRing( key )" is a valid CDS for a KeyRing, then
     * KeyRing.parseCDS(key) will return the described KeyRing object.
     * <p>
     * A valid CDS can be created by calling the KeyRing.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static KeyRing parseCDS(String arg) throws InvalidCDSException{
        KeyRing ret=new KeyRing();
        
        try{
            StringReader in=new StringReader(arg);
            while(true){
                Object k=fromString(in);
                if(k instanceof Key)
                    ret.insert((Key)k);
                else if(k instanceof KeyRecord)
                    ret.insert((KeyRecord)k);
                else
                    throw new InvalidCDSException("Only descendants of Key or KeyRecord expected");
                int ch=in.read();
                while(Character.isWhitespace((char)ch))
                    ch=in.read();
                if(ch==-1)
                    break;
                if(ch!=',')
                    throw new InvalidCDSException(", expected after "+k);
            }
        } catch (IOException e){
            // StringReader doesn't actually throw an IOException.
        }
        return ret;
    }
    /** Return a CDS for this KeyRing. */
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("KeyRing(");
        
        Enumeration e=keys.elements();
        if(e.hasMoreElements())
            sb.append(e.nextElement().toString());
        while(e.hasMoreElements()){
            sb.append(",");
            sb.append(e.nextElement().toString());
        }
        sb.append(")");
        
        return sb.toString();
    }
}
