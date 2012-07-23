package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.DecryptCBC;
import is.logi.crypto.modes.EncryptCBC;

import java.math.BigInteger;

/**
 * Diffie-Hellman EKE key exchange and authentication server. It expects to
 * talk to a DHEKEKeyExClient object.
 *
 * @see is.logi.crypto.protocols.DHEKEKeyExClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DHEKEKeyExServer extends DHEKEKeyEx implements InterKeyExServer, InterAuthServer {
	
	private int nextStep=1;
	
    /**
     * Create a new DHEKEKeyExClient object which uses an <code>n</code> bit
     * modulus, the named key type and the specified secret key.
     * <p>
     * There are pre-computed public modulus and gnerator pairs for
     * these values of <code>n</code>: 256, 512, 1024, 2048. Using
     * one of these values saves you from a rather long wait.
     */
    public DHEKEKeyExServer(int n, String keyType, CipherKey secretKey){
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
        int l;
        
        switch(nextStep){
        case 1: {
            // Get client public key and calculate session key.
            // Send my public key encrypted with secret key and
            //      my random string encrypted with session key.
            
            if(received==null)
                throw new ProtocolException("Message expected");
            nextStep++;
            
            { // sessionKey = makeSessionKey(keyType, received^myPrivate % m)
                BigInteger hisPublic = new BigInteger(1,received);
                //System.out.println("S:  hisPublic="+hisPublic.toString(16));
                byte[] k= hisPublic.modPow(myPrivate,m).toByteArray();
                try{
                    sessionKey=makeSessionKey(keyType,k);
                    pbs=sessionKey.plainBlockSize();
                    cbs=sessionKey.plainBlockSize();
                    //System.out.println("S: sessionKey="+sessionKey);
                } catch (InvalidCDSException e) {
                    throw new ProtocolException(e.getMessage());
                }
            }
            
            byte[] r1;
            { // r1 := encrypt with secretKey in CBC mode (myPublc.length:16b, myPublic)
                byte[] mp = myPublic.toByteArray();
                //System.out.println("S:   myPublic="+myPublic.toString(16));
                byte[] plain = new byte[mp.length+2];
                writeBytes(mp.length,plain,0,2);
                System.arraycopy(mp,0, plain,2, mp.length);
                r1 = new EncryptCBC(secretKey).flush(plain,0,plain.length);
            }
            
            byte[] r2;
            { // myRandom := new random string of pbs bytes.
                // r2 := encrypt with sessionKey (myRandom)
                myRandom=new byte[pbs];
                random.nextBytes(myRandom);
                //System.out.println("S:   myRandom="+hexString(myRandom));
                r2 = new byte[cbs];
                sessionKey.encrypt(myRandom,0,r2,0);
            }
            
            byte[] r;
            { // r := (r1.length:16, r1, r2)
                r= new byte[2+r1.length+r2.length];
                writeBytes(r1.length,r,0,2);
                System.arraycopy(r1,0, r,2          , r1.length);
                System.arraycopy(r2,0, r,2+r1.length, r2.length);
            }
            //System.out.println();
            return r;
        }
        
        case 2: {
            // Get the clients random string and the clients guess at my random string.
            // Check that the clients guess is correct.
            // Send the clients random string encrypted with the session key.
            
            if(received==null)
                throw new ProtocolException("Message expected");
            nextStep++;
            
            byte[] rarb;
            { // rarb := decrypt with sessionKey in CBC mode (received)
                // assert(rarb[0..pbs-1]==myRandom)
                rarb = new DecryptCBC(sessionKey).decrypt(received,0,received.length);
                if(rarb.length<2*pbs)
                    throw new ProtocolException("The message was too short");
                //{
                //  // Debug guess
                //  byte[] hisGuess = new byte[pbs];
                //  System.arraycopy(rarb,pbs, hisGuess,0,pbs);
                //  System.out.println("S:   hisGuess="+hexString(hisGuess));
                //}
                //{
                //  // Debug block
                //  byte[] hisRandom = new byte[pbs];
                //  System.arraycopy(rarb,0, hisRandom,0,pbs);
                //  System.out.println("S:  hisRandom="+hexString(hisRandom));
                //}
                for(int i=0; i<pbs; i++)
                    if(rarb[pbs+i]!=myRandom[i])
                        throw new ValidationException("The client does not know the secret");
            }
            
            byte[] r;
            { // r := encrypt with sessionKey(hisRandom)
                //    = encrypt with session Key(rarb[pbs..2*pbs-1]
                r = new byte[cbs];
                sessionKey.encrypt(rarb,0, r,0);
            }
            completed=true;
            //System.out.println();
            return r;
        }
        
        default:
            throw new ProtocolException("The protocol has been completed");
        }
        
    }
}
