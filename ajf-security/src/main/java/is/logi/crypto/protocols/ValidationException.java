package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This exception is thrown if data can't be validated, f.ex in
 * VerifyStream.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class ValidationException extends ProtocolException{

    /** Create a new ValidationException with no message. */
    public ValidationException(){
    }
    /** Create a new ValidationException with the message msg. */
    public ValidationException(String msg){
        super(msg);
    }
}
