package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.keys.Key;

/**
 * Ancestor of EncryptedKeyEx classes.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptedKeyEx extends Crypto {
	
	protected boolean keyDecided=false;
	protected CipherKey key;
	protected Key sessionKey;
	
    /**
     * Called from sub-classes. Use The Source.
     */
    protected EncryptedKeyEx(CipherKey key, Key sessionKey){
        this.key=key;
        this.sessionKey=sessionKey;    
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
