package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.DecryptCBC;
import is.logi.crypto.modes.EncryptCBC;

import java.math.BigInteger;

/**
 * Diffie-Hellman EKE key exchange and password verification client.
 * It expects to talk to a DHEKEKeyExServer object.
 * <p>
 * The protocol exchanges keys with the remot party and then each party
 * proves that they know the same secret key without giving it away to
 * anyone who does not allready know it.
 * <p>
 * The patent for this protocol is held by
 * <a href="http://www.lucent.com">Lucent</a>. You must pay them a steep
 * licensing fee if you intend to use it in the USA or Canada.
 *
 * @see is.logi.crypto.protocols.DHEKEKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHEKEKeyExClient extends DHEKEKeyEx implements InterProtocolClient, InterKeyExClient, InterAuthClient {
	
	private int nextStep=1;
	
    /*
     * Create a new DHEKEKeyExClient object which uses an <code>n</code> bit
     * modulus, the named key type and the specified secret key.
     * <p>
     * There are pre-computed public modulus and gnerator pairs for
     * these values of <code>n</code>: 256, 512, 1024, 2048. Using
     * one of these values saves you from a rather long wait.
     */
    public DHEKEKeyExClient(int n, String keyType, CipherKey secretKey){
        super(n,keyType,secretKey);
    }
    /**
     * If the key has not been decided upon and <code>received</code> is the
     * last message received from the other end,
     * <code>message(received)</code> returns the message to send the other
     * end as the next step in the protocol.
     *
     * @exception ProtocolException if a malformed message is received.
     * @exception ValidationException if validation fails.
     */
    public byte[] message(byte[] received) throws ProtocolException {
        switch(nextStep){
        case 1: {
            // Send my public key encrypted with secret key.
            
            if(received!=null)
                throw new ProtocolException("The client should always send the first message");
            nextStep++;
            //System.out.println("C:   myPublic="+myPublic.toString(16));
            return myPublic.toByteArray();
        }
        
        case 2: {
            // Get server public key and calculate session key.
            // Get server random string
            // Send server random and my random encrypted with session key
            
            if(received==null)
                throw new ProtocolException("Message expected");
            nextStep++;
            
            int l;
            { // l = received[0..1];
                // plain := decrypt with secretKey in CBC mode (received[0..l-1])
                // ll := plain[0..1]
                // hisPublic :=  plain[2..2+l-1]
                // sessionKey := makeSessionKey(keyType, hisPublic^myPrivate % m)
                try{
                    l=(int)makeLong(received,0,2);
                    byte[] plain=new DecryptCBC(secretKey).decrypt(received,2,l);
                    int ll = (int)makeLong(plain,0,2);
                    byte[] hp = new byte[ll];  // This could be disasterous, except that ll is < 32768
                    System.arraycopy(plain,2, hp,0, ll);
                    BigInteger hisPublic=new BigInteger(1,hp);
                    //System.out.println("C:  hisPublic="+hisPublic.toString(16));
                    byte[] k=hisPublic.modPow(myPrivate,m).toByteArray();
                    try{
                        sessionKey=makeSessionKey(keyType,k);
                        pbs=sessionKey.plainBlockSize();
                        cbs=sessionKey.plainBlockSize();
                        //System.out.println("C: sessionKey="+sessionKey);
                    } catch (InvalidCDSException e) {
                        throw new ProtocolException(e.getMessage());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // This often happens if the length of hisPublic is wrongly decrypted, meaning
                    // that the server didn't encrypt it with the same key we decrypted it with.
                    throw new ValidationException("The server does not know the secret.");
                }
            }
            
            byte [] r;
            { // hisRandom := decrypt with sessionKey (received[2+l...])
                // myRandom := new random string of pbs bytes
                // r := encrypt with sessionKey in CBC mode (myRandom,hisRandom))
                byte[] hisRandom = new byte[sessionKey.plainBlockSize()];
                sessionKey.decrypt(received,2+l, hisRandom,0);
                //System.out.println("C:  hisRandom="+hexString(hisRandom));
                myRandom = new byte[pbs];
                random.nextBytes(myRandom);
                //System.out.println("C:   myRandom="+hexString(myRandom));
                byte[] buf =new byte[hisRandom.length+myRandom.length];
                System.arraycopy(myRandom ,0, buf,0              , myRandom.length);
                System.arraycopy(hisRandom,0, buf,myRandom.length, hisRandom.length);
                r = new EncryptCBC(sessionKey).flush(buf,0,buf.length);
            }
            //System.out.println();
            return r;
        }
        
        case 3: {
            // Get the server's "guess" at my random
            // string and check that it is correct.
            
            if(received==null)
                throw new ProtocolException("Message expected");
            nextStep++;
            
            { // hisGuess = decrypt with sessionKey (received)
                // assert(hisGuess==myRandom)
                byte[] hisGuess = new byte[pbs];
                sessionKey.decrypt(received,0, hisGuess,0);
                //System.out.println("C:   hisGuess="+hexString(hisGuess));
                if(!equal(myRandom,hisGuess))
                    throw new ValidationException("The server does not know the secret");
            }
            
            completed=true;
            //System.out.println();
            return null;
        }
        
        default:
            throw new ProtocolException("The protocol has been completed");
        }
    }
}
