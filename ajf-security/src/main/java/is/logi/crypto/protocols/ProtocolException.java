package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.CryptoException;

/**
 * This exception is thrown when a problem arises in a cryptographic
 * protocol, such as an invalid message being received.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class ProtocolException extends CryptoException{

    /** Create a new ProtocolException with no message. */
    public ProtocolException(){
    }
    /** Create a new ProtocolException with the message msg. */
    public ProtocolException(String msg){
        super(msg);
    }
}
