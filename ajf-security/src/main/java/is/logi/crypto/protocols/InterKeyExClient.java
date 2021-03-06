package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.Key;

/**
 * This interface is implemented by classes for the client portion of an
 * interactive key-exchange protocol.
 * <p>
 * In this context, the party which initiates the protocol is considered the
 * client. Interactive means that the client may need to recieve information
 * from the server, so the protocol can be used off-line.
 * <p>
 * An example of an interactive key-exchange protocol is the Diffie-Hellman
 * protocol where each party must send the value <code>g^a</code> to the
 * other before they can both calculate the key <code>g^{ab}</code>. 
 *
 * @see is.logi.crypto.protocols.InterKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface InterKeyExClient extends InterProtocolClient{
	
    /**
     * Returns the key if it has been decided upon,
     * or <code>null</code> otherwise.
     */
    public Key sessionKey();
}
