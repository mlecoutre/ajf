package is.logi.crypto;

// Copyright (C) 1998 Logi Ragnarsson


/**
 * This exception or its sub-classes are thrown
 * whenever a cryptographic error occurs.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class CryptoException extends Exception {

    /** Create a new CryptoException with no message. */
    public CryptoException(){
    }
    /** Create a new CryptoException with the message msg. */
    public CryptoException(String msg){
        super(msg);
    }
}
