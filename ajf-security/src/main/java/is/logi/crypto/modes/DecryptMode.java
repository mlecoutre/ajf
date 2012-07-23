package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;

/**
 * DecryptMode objects are used to decrypt ciphertext generated with
 * a correpsonding EncryptMode object. They must in most cases be
 * initialized with the appropriate key.
 *
 * @see is.logi.crypto.modes.EncryptMode
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 *         (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public abstract class DecryptMode extends Crypto {

  /**
   * Send bytes to the DecryptMode for decryption.
   * <p>
   * Decrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the plaintext. Data may
   * be encrypted in blocks in which case only whole blocks of
   * plaintext are written to <code>dest</code>. Any remaining data
   * will be stored and prepended to <code>source</code> in the next
   * call to <code>decrypt</code>.
   */
  public abstract byte[] decrypt(byte[] source, int i, int length);
  /**
   * Return the key used for decryption.
   */
  public abstract CipherKey getKey();
  /**
   * Set the key to use for decryption. Do not call this method when
   * there may be data in the internal buffer.
   */
  public abstract void setKey(CipherKey key);
}
