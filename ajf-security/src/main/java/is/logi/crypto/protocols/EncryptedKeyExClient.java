package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.keys.Key;
import is.logi.crypto.modes.EncryptECB;
import is.logi.crypto.modes.EncryptMode;

/**
 * Exchange keys by sending an encrypted key from this class to the
 * corresponding EncryptedKeyExServer.
 *
 * @see is.logi.crypto.protocols.EncryptedKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class EncryptedKeyExClient extends EncryptedKeyEx implements NoninterKeyExClient {
  
  /**
   * Create a new EncryptedKeyExClient object. It uses <code>key</code> to
   * encrypt <code>sessionKey</code> and then sneds it to the server.
   */
  public EncryptedKeyExClient(CipherKey key, Key sessionKey){
    super(key,sessionKey);
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
    if(received!=null)
      throw new ProtocolException("A non-interactive key-exchange client should not receive messages.");

    byte[] plain=sessionKey.toString().getBytes();
    EncryptMode em=new EncryptECB(key);
    byte[] c1=em.encrypt(plain,0,plain.length);
    byte[] c2=em.flush();

    byte[] cipher=new byte[c1.length+c2.length+4];
    writeBytes(plain.length, cipher,0, 4);
    System.arraycopy(c1,0, cipher,4          , c1.length);
    System.arraycopy(c2,0, cipher,4+c1.length, c2.length);
    
    keyDecided=true;
    return cipher;
  }
}
