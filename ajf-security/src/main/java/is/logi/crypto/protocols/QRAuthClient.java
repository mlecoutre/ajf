package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;

/**
 * Query-response authenticaton client. It expects to talk to a
 * QRAuthServer object.
 * <p>
 * If the protocol is completed, the client is certain that the server
 * also knows the secret key passed to the constructor.
 *
 * @see is.logi.crypto.protocols.QRAuthServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class QRAuthClient extends Crypto implements InterAuthClient {

	/** The secret key. */
	private CipherKey key;

	/** The random number chosen by the client. */
	private byte[] r;

	private boolean completed;
	
    /**
     * Creates a new QRAuthClient object with the specified secret
     * <code>key</code>.
     */
    public QRAuthClient(CipherKey key){
        this.key=key;
    }
    /** Add one to the number stored in a[0..a.length-1]. */
    protected static void addOne(byte[] a){
        int i=a.length-1;
        a[i]++;
        while( (i>=0) && (a[i]==0) )
            a[i++]++;
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
            // The protocol is just beginning. Send E(r) to server.
            if(received!=null)
                throw new ProtocolException("The client should always send the first message");
            
            r = new byte[key.plainBlockSize()];
            random.nextBytes(r);

            byte[] e = new byte[key.cipherBlockSize()];
            key.encrypt(r,0, e,0);

            return e;
        } else {
            // The server should now be sending us E(r+1) to which we
            // reply E(r+2)

            if(received.length != key.cipherBlockSize())
                throw new ProtocolException("Received message has the wrong length.");

            byte[] guess = new byte[key.cipherBlockSize()];
            key.decrypt(received,0, guess,0);

            addOne(r);
            if(!equal(guess,r))
                throw new ValidationException("The server does not know the secret");
            addOne(r);

            byte[] e = new byte[key.cipherBlockSize()];
            key.encrypt(r,0, e,0);

            completed = true;
            return e;
        }
    }
}
