package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.CryptoException;

/**
 * This exception is thrown when there is a problem with a key object. This
 * hapens if a key can't be found, a key is too short for a particular
 * purpose, etc.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class KeyException extends CryptoException{

    /** Create a new KeyException with no message. */
    public KeyException(){
    }
    /** Create a new KeyException with the message msg. */
    public KeyException(String msg){
        super(msg);
    }
}
