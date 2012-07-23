package is.logi.crypto;

// Copyright (C) 1998 Logi Ragnarsson


/**
 * This exception is thrown whenever a malformed CDS is encountered.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class InvalidCDSException extends CryptoException {

    /** Create a new InvalidCDSException with no message. */
    public InvalidCDSException(){
    }
    /** Create a new InvalidCDSException with the message msg. */
    public InvalidCDSException(String msg){
        super(msg);
    }
}
