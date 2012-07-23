package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This interface is implemented by keys which handle encryption and
 * decryption of single blocks of data.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface CipherKey extends Key {
  
  /**
   * Returns the size of the blocks that can be decrypted in one call
   * to decrypt(). */
  public int cipherBlockSize();
  /**
   * Decrypt one block of data. The ciphertext is taken from
   * <code>source</code> starting at offset <code>i</code> and
   * plaintext is written to <code>dest</code>, starting at
   * offset <code>j</code>.
   * <p>
   * The amount of data read and written will match the values returned
   * by <code>cipherBlockSize()</code> and <code>plainBlockSize()</code>.
   */
  public void decrypt(byte[] source, int i, byte[] dest, int j);
  /**
   * Encrypt one block of data. The plaintext is taken from
   * <code>source</code> starting at offset <code>i</code> and
   * ciphertext is written to <code>dest</code>, starting at
   * offset <code>j</code>.
   * <p>
   * The amount of data read and written will match the values returned
   * by <code>plainBlockSize()</code> and <code>cipherBlockSize()</code>.
   */
  public void encrypt(byte[] source, int i, byte[] dest, int j);
  /**
   * Returns the size of the blocks that can be encrypted in one call
   * to encrypt(). */
  public int plainBlockSize();
}
