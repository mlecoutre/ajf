package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.keys.DHKey;
import is.logi.crypto.keys.KeyException;

import java.math.BigInteger;

/**
 * Diffie-Hellman key exchange client. It expects to talk to a
 * DHKeyExServer object.
 *
 * @see is.logi.crypto.protocols.DHKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHKeyExClient extends DHKeyEx implements InterKeyExClient {
	
    /**
     * Create a new  DHKeyExClient object.  It uses
     * an <code>n</code> bit modulus and the named key type.
     * <p>
     * There are pre-computed public modulus and gnerator pairs for
     * these values of <code>n</code>: 256, 512, 1024, 2048. Using
     * one of these values saves you from a rather long wait.
     */
    public DHKeyExClient(int n, String keyType){
        super(n,keyType);
    }
    /**
     * Create a new DHKeyExClient object. It uses
     * the private DH key from <code>pri</code>.
     *
     * @exception KeyException if the key is not private. */
    public DHKeyExClient(DHKey pri, String keyType) throws KeyException {
        super(pri,keyType);
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
        if(received!=null){
            hisPublic = new BigInteger(1,received);
            byte[] k= hisPublic.modPow(myPrivate,m).toByteArray();
            try{
                sessionKey=makeSessionKey(keyType,k);
                keyDecided=true;
            } catch (InvalidCDSException e) {
                throw new ProtocolException(e.getMessage());
            }
            return null;
        }
        byte[] r = myPublic.toByteArray();
        if(r.length==modSize/8)
            return r;
        byte[] s = new byte[modSize/8];
        int l=Math.min(r.length, modSize/8);
        System.arraycopy(r, r.length-l, s,s.length-l, l);
        return s;
    }
}
