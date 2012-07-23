package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.DecryptMode;
import is.logi.crypto.modes.EncryptMode;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Parent of CipherStreamClient and CipherStreamServer.
 *
 * @see is.logi.crypto.io.CipherStreamServer
 * @see is.logi.crypto.io.CipherStreamClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class CipherStream extends Crypto {

  protected OutputStream out;
  protected InputStream  in;

  protected EncryptStream cOut;
  protected DecryptStream cIn;

  protected EncryptMode encrypt;
  protected DecryptMode decrypt;
  
  /**
   * Get the key used for decryption. */
  public CipherKey getDecryptKey(){
    return encrypt.getKey();
  }
  /**
   * Get the key used for encryption. */
  public CipherKey getEncryptKey(){
    return encrypt.getKey();
  }
  /**
   * Get the encrypted input-stream. */
  public InputStream getInputStream(){
    return cIn;
  }
  /**
   * Get the encrypted output-stream. */
  public OutputStream getOutputStream(){
    return cOut;
  }
}
