package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.keys.Key;
import is.logi.crypto.modes.DecryptECB;
import is.logi.crypto.modes.DecryptMode;

/**
 * Receive an encrypted key from the correstponding EncryptedKeyExClient
 *
 * @see is.logi.crypto.protocols.EncryptedKeyExClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptedKeyExServer extends EncryptedKeyEx implements NoninterKeyExServer {

  /**
   * Creates a new EncryptedKeyExchangeServer object. It uses <code>key</code>
   * to decrypt the session key sent to it by te client.
   */
  public EncryptedKeyExServer(CipherKey key){
    super(key,null);
  }
  /**
   * Get the next message in the protocol.
   * <p>
   * <code>received</code> is the last message received form the client
   * and has not yet been sent to the client.
   * <p>
   * The returned value is the next message to send to the client or null
   * if no more messages need to be sent and the protocol is terminated.
   *
   * @exception ProtocolException if a problem arises with the protocol.
   */
  public byte[] message(byte[] received) throws ProtocolException {
    if(received==null)
      throw new ProtocolException("null message received");
    
    DecryptMode dm=new DecryptECB(key);
    int pl=(int)makeLong(received,0,4);
    byte[] p1=dm.decrypt(received,4,received.length-4);
    byte[] plain;
    if(pl==p1.length)
      plain=p1;
    else {
      plain=new byte[pl];
      System.arraycopy(p1,0, plain,0, pl);
    }
    try{
      sessionKey = (Key)fromString(new String(plain));
      keyDecided=true;
      return null;
    } catch (Exception e) {
    }
    throw new ProtocolException("Did not receive a valid cds for a CipherKey object.");
  }
}
