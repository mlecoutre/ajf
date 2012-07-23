package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.keys.KeyRecord;

/**
 * This class implements the server portion of the key-exchange protocol
 * whereby the client sends the server the hash of the key that should
 * be used for decryption. The server is assumed to have this key in
 * its default KeySource.
 *
 * @see is.logi.crypto.protocols.SendHashKeyExClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class SendHashKeyExServer extends SendHashKeyEx implements NoninterKeyExServer {

  /** Create a new SendHashKeyExServer. */
  public SendHashKeyExServer(){
    super(null);
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
      throw new ProtocolException("A KeyExServer should never send the first message.");
    try{
      KeyRecord kr = keySource.byFingerprint((Fingerprint)fromString(new String(received)));
      if (kr==null)
	throw new ProtocolException("No Key matching the specified Fingerprint found in keySource.");
      sessionKey = kr.getKey();
      keyDecided=true;
      return null;
    } catch (InvalidCDSException e1){
      throw new ProtocolException("A CDS for a Fingerprint object expected.");
    } catch (ClassCastException e2){
      throw new ProtocolException("A CDS for a Fingerprint object expected.");
    }
  }
}
