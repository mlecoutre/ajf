package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;

/**
 * Descendants of EncryptMode encrypt arbtrarily large arrays of
 * plaintext.  A corresponding DecryptMode should be used for
 * decryption.
 * <p>
 * Most EncryptModes use a CipherKey object to do actual
 * encryption and do additional computations to mask repetitions in
 * the plaintext.
 *
 * @see is.logi.crypto.modes.DecryptMode
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> 
 *         (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public abstract class EncryptMode extends Crypto {

  /**
   * Send bytes to the EncryptMode for encryption.
   * <p>
   * Encrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the ciphertext. Data may be
   * encrypted in blocks in which case only whole blocks of ciphertext
   * are written to <code>dest</code>. Any remaining plaintext will be
   * stored and prepended to <code>source</code> in the next call to
   * <code>encrypt</code>.
   */
  public abstract byte[] encrypt(byte[] source, int i, int length);
  /**
   * Pads the internal buffer, encrypts it and returns the
   * ciphertext.
   */
  public abstract byte[] flush();
  /**
   * Equivalent to calling <code>encrypt(source,i,length)</code>
   * followed by <code>flush()</code>.
   */
  public byte[] flush(byte[] source, int i, int length){
    byte[] e1 = encrypt(source,i,length);
    byte[] e2 = flush();
    if(e2.length==0)
      return e1;
    byte[] r = new byte[e1.length+e2.length];
    System.arraycopy(e1,0, r,0        , e1.length);
    System.arraycopy(e2,0, r,e1.length, e2.length);
    return r;
  }
  /** Return the key used for encryption. */
  public abstract CipherKey getKey();
  /**
   * Set the key to use for encryption. Do not call this method when
   * there may be data in the internal buffer.
   */
  public abstract void setKey(CipherKey key);
}
