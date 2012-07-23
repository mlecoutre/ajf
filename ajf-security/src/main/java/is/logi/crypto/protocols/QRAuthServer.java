package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;

/**
 * Query-response authenticaton server. It expects to talk to a
 * QRAuthClient object.
 * <p>
 * If the protocol is completed, the server is certain that the client
 * also knows the secret key passed to the constructor.
 *
 * @see is.logi.crypto.protocols.QRAuthServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class QRAuthServer extends Crypto implements InterAuthServer {
	
	/** The secret key. */
	CipherKey key;
	
	/** The random number chosen by the client. */
	private byte[] r;
	
	private boolean completed;
	
    /**
     * Creates a new QRAuthServer object with the specified secret
     * <code>key</code>.
     */
    public QRAuthServer(CipherKey key){
        this.key = key;
    }
    /** Returns true iff this end of the protocol is completed. */
    public boolean completed(){
        return completed;
    }
    /**
     * Get the next message in the protocol.
     * <p>
     * <code>received</code> is the last message received form the server
     * and has not yet been sent to the client.
     * <p>
     * The returned value is the next message to send to the server or null
     * if no more messages need to be sent and the protocol is terminated.
     *
     * @exception ProtocolException if a problem arises with the protocol.
     */
    public byte[] message(byte[] received) throws ProtocolException {
        if(r==null){
            // The protocol is just beginning. The client should now
            // be sending us E(r) to which we reply E(r+1)

            if(received==null)
                throw new ProtocolException("The client should always send the first message");
            
            r = new byte[key.plainBlockSize()];
            key.decrypt(received,0, r,0);
            
            QRAuthClient.addOne(r);
            byte[] e = new byte[key.cipherBlockSize()];
            key.encrypt(r,0, e,0);
            
            return e;
        } else {
            // The client should now be sending us E(r+2)
            
            if(received.length != key.cipherBlockSize())
                throw new ProtocolException("Received message has the wrong length.");
            
            byte[] guess = new byte[key.cipherBlockSize()];
            key.decrypt(received,0, guess,0);
            
            QRAuthClient.addOne(r);
            if(!equal(guess,r))
                throw new ValidationException("The client does not know the secret");

            completed = true;
            return null;
        }
    }
}
