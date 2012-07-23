package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.Key;

/**
 * This class implements the client portion of the key-exchange protocol
 * whereby the client sends the server the hash of the key that should
 * be used for decryption. The server is assumed to have this key in
 * its default KeySource.
 *
 * @see is.logi.crypto.protocols.SendHashKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class SendHashKeyExClient extends SendHashKeyEx implements NoninterKeyExClient {

  /**
   * Create a new SendHashKeyEx object.
   * It uses the Key <code>k</code>.
   */
  public SendHashKeyExClient(Key k){
    super(k);
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
      throw new ProtocolException("A KeyExClient should never receive messages.");
    byte[] r=sessionKey.matchFingerprint().toString().getBytes();
    keyDecided=true;
    return r;
  }
}
