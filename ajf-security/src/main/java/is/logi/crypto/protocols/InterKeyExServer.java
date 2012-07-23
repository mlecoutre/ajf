package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.Key;

/**
 * This interface is implemented by classes for the server portion of an
 * interactive key-exchange protocol.
 *
 * @see is.logi.crypto.protocols.InterKeyExClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface InterKeyExServer extends InterProtocolServer {
  
    /**
     * Returns the key if it has been decided upon,
     * or <code>null</code> otherwise.
     */
    public Key sessionKey();
}
