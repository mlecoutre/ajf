package is.logi.crypto;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This exception is thrown whenever logi.crypto detects that
 * it has been corrupted in some manner. This could be missing classes
 * ot classes that behave in an obiously wrong way.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class CryptoCorruptError extends Error {

    /** Create a new CryptoCorruptError with no message. */
    public CryptoCorruptError(){
    }
    /** Create a new CryptoCorruptError with the message msg. */
    public CryptoCorruptError(String msg){
        super(msg);
    }
}
