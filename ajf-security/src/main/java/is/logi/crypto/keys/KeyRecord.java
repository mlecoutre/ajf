package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.hash.HashState;
import is.logi.crypto.hash.SHA1State;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;

/* This class holds key certificates for a particular key, linking it */
/** This class holds a particular key, linking it
 * to the owners name and e-mail and annotation.
 * <p>
 * The CDS for a KeyRecord object is <code>KeyRecord(key,ownerName,ownerMail,notes)
 * </code>where key is a CDF for a Key object and the other parameters are strings,
 * which may be quoted.
 *
 * @see is.logi.crypto.keys.Key
 * @see is.logi.crypto.keys.KeySource
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */ 
public class KeyRecord extends Crypto{
	
	private String ownerName;
	private String ownerMail;
	private String notes;
	
	private Key key;
	
	Vector signatures;
	
    /** 
     * Create a new KeyRecord. It contains <code>key</code> which supposedly
     * belongs to <code>ownerName</code> who has e-mail adress
     * <code>ownerMail</code>. Additional notes are taken from
     * <code>notes</code>. 
     */
    public KeyRecord(Key key, String ownerName, String ownerMail, String notes){
        this.ownerName = ownerName;
        this.ownerMail = ownerMail;
        this.notes = notes;
        this.key = key;
    }
    /**
     * Return the SHA1 fingerprint of this KeyRecord. Signing this is
     * equivalent to signing the record.
     */
    public Fingerprint getFingerprint(){
        byte[] empty={ 0,0,0,0 };
        HashState fs=new SHA1State();
        fs.update(key.getFingerprint().getBytes());
        fs.update(empty);
        fs.update(ownerName);
        fs.update(empty);
        fs.update(ownerMail);
        fs.update(empty);
        fs.update(notes);
        return fs.calculate();
    }
    /** Return the key from this record. */
    public Key getKey(){
        return key;
    }
    /** Return the notes about this key. */
    public String getNotes(){
        return notes;
    }
    /** Return the e-mail address of the key's owner. */
    public String getOwnerMail(){
        return ownerMail;
    }
    /** Return the name of the key's owner. */
    public String getOwnerName(){
        return ownerName;
    }
    /**
     * If "KeyRecord( key )" is a valid CDS for a KeyRecord, then
     * KeyRecord.parseCDS(key) will return the described KeyRecord object.
     * <p>
     * A valid CDS can be created by calling the KeyRecord.toString() method.
     *
     * @exception InvalidCDSException if the CDS is malformed.
     *
     * @see is.logi.crypto.Crypto#fromString(String)
     */
    public static KeyRecord parseCDS(String arg) throws InvalidCDSException{
        KeyRecord ret = null;
        try{
            Reader in=new StringReader(arg);
            Object k = fromString(in);
            if(!(k instanceof Key))
                throw new InvalidCDSException("CDS for a Key object expected as first argument to KeyRecord()");
            
            StreamTokenizer st = new StreamTokenizer(in);
            st.ordinaryChars('0','9');
            st.wordChars('0','9');
            
            int TT_WORD=StreamTokenizer.TT_WORD;
            int TT_NUMBBER=StreamTokenizer.TT_NUMBER;
            
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+k);
            
            if(st.nextToken()!=TT_WORD && st.ttype!='"')
                throw new InvalidCDSException("String expected as second argument to KeyRecord()");
            String ownerName = st.sval;
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+ownerName);
            if(st.nextToken()!=TT_WORD && st.ttype!='"')
                throw new InvalidCDSException("String expected as third argument to KeyRecord()");
            String ownerMail = st.sval;
            if(st.nextToken()!=',')
                throw new InvalidCDSException(", expected after "+ownerMail);
            if(st.nextToken()!=TT_WORD && st.ttype!='"')
                throw new InvalidCDSException("String expected as fourth argument to KeyRecord()");
            String notes = st.sval;

            ret = new KeyRecord((Key)k, ownerName, ownerMail, notes);
        } catch (IOException e){
            // StringReader doesn't actually throw exceptions
        }
        return ret;
    }
    /**
     * Returns the Vector holding the signatures contained in this record.
     */
    /*public Vector getSignatures(){
     return signatures;
     }*/
    
    /**
     * Sign this record. A signature is generated with <code>sk</code>
     * and inserted in the records list of signatures.
     *
     * @exception KeyException if sk can't be used to sign this
     *            record for some reason. 
     */
    /*public void sign(SignatureKey sk) throws KeyException{
     signatures.addElement(sk.sign(getFingerprint()));
     }*/
    
    /** Return a CDS for this KeyRecord. */
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("KeyRecord(");
        sb.append(key);
        sb.append(",\"");
        sb.append(ownerName);
        sb.append("\",\"");
        sb.append(ownerMail);
        sb.append("\",\"");
        sb.append(notes);
        sb.append("\")");
        return sb.toString();
    }
}
