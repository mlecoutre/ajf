package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.Key;

/**
 * Ancestor of SendHashKeyEx classes.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class SendHashKeyEx extends Crypto {
	
	protected boolean keyDecided=false;
	protected Key sessionKey;
	
    /** Create a new SendHashKeyEx with the Key <code>k</code>. */
    protected SendHashKeyEx(Key k){
        sessionKey=k;
    }
    /** Returns true iff this end of the protocol i completed. */
    public boolean completed(){
        return keyDecided;
    }
    /**
     * Returns the key if it has been decided upon,
     * or <code>null</code> otherwise.
     */
    public Key sessionKey(){
        return keyDecided ? sessionKey : null;
    }
}
