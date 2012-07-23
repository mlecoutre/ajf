package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.keys.DHKey;
import is.logi.crypto.keys.KeyException;

/**
 * Diffie-Hellman key exchange without exchanging keys. Both parties
 * need to know the other party's public DHKey. No messages are
 * sent in either direction, but a unique session key is created for each
 * pair of Diffie-Hellman keys used.
 * <p>
 * This class is both the client and server for the protocol.
 *
 * @see is.logi.crypto.protocols.DHKeyExServer
 * @see is.logi.crypto.protocols.DHKeyExClient
 * @see is.logi.crypto.keys.DHKey
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHKeyExNoninter extends DHKeyEx implements NoninterKeyExClient, NoninterKeyExServer {
	
    /**
     * Create a new  DHKeyExClient object which uses
     * the private DH key from <code>pri</code> and the public
     * DH key from <code>pri</code> to generate a session key.
     *
     * @exception KeyException if the public/private flag of either key
     *            is wrong.
     * @exception InvalidCDSException if the session key object can not
     *            be created.
     */
    public DHKeyExNoninter(DHKey pri, DHKey pub, String keyType) throws KeyException, InvalidCDSException {
        super(pri,keyType);
        byte[] k=pub.getKey().modPow(pri.getKey(),m).toByteArray();
        sessionKey=makeSessionKey(keyType,k);
        keyDecided=true;
    }
    /**
     * Expects and sends null, since no messages are needed for this protocol.
     *
     * @exception ProtocolException if called with a parameter other than null.
     */
    public byte[] message(byte[] received) throws ProtocolException {
        if (received!=null)
            throw new ProtocolException("No messages should be sent or received.");
        return null;
    }
}
