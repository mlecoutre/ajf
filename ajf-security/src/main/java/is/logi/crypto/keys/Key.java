package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.hash.Fingerprint;

/**
 * This interface is implemented by cryptographic keys of any type.
 * Any actual functionality of the keys is defined in the interfaces derived
 * from Key.
 * <p>
 * Classes implementing Key should be "read-only" in the same sense as the
 * String class and not implement any methods to change the state of the
 * object.
 *
 * <hr>
 *
 * <b>Overview over Key classes:</b>
 * <p>
 * <table border=1>
 *   <tr>
 *     <td><b>Class</b></td>
 *     <td><b>P-K</b></td>
 *     <td><b>Key Size</b></td>
 *     <td><b>Cipher</b></td>
 *     <td><b>Block Size</b></td>
 *     <td><b>Signature</b></td>
 *     <td><b>Notes</b></td>
 *   </tr>
 *   <tr>
 *     <td><a href="CaesarKey.html">CaesarKey</a></td>
 *     <td>no</td>
 *     <td>1</td>
 *     <td>yes</td>
 *     <td>1</td>
 *     <td>no</td>
 *     <td>Very weak. Only included as an example.</td>
 *   </tr>
 *   <tr>
 *     <td><a href="DESKey.html">DESKey</a></td>
 *     <td>no</td>
 *     <td>56</td>
 *     <td>yes</td>
 *     <td>8</td>
 *     <td>no</td>
 *     <td>Old US Government standard. Can be broken with exchaustive search.</td>
 *   <tr>
 *   </tr>
 *     <td><a href="TriDESKey.html">TriDESKey</a></td>
 *     <td>no</td>
 *     <td>168</td>
 *     <td>yes</td>
 *     <td>8</td>
 *     <td>no</td>
 *     <td>Newer US Government standard. Applies DES three times. Slow.</td>
 *   <tr>
 *   </tr>
 *     <td><a href="BlowfishKey.html">BlowfishKey</a></td>
 *     <td>no</td>
 *     <td><=448</td>
 *     <td>yes</td>
 *     <td>8</td>
 *     <td>no</td>
 *     <td>Fast. Free for all use.</td>
 *   <tr>
 *   </tr>
 *     <td><a href="RSAKey.html">RSAKey</a></td>
 *     <td>yes</td>
 *     <td>>=256</td>
 *     <td>yes</td>
 *     <td>K-1 / K</td>
 *     <td>yes</td>
 *     <td>Most widely used public-key algorithm. Patented in the USA and Canada.</td>
 *   <tr>
 *   </tr>
 *     <td><a href="ElGamalKey.html">ElGamalKey</a></td>
 *     <td>yes</td>
 *     <td>>=256</td>
 *     <td>yes</td>
 *     <td>K-1 / 2*K</td>
 *     <td>yes</td>
 *     <td>Unpatented, free for all use.</td>
 *   <tr>
 *   </tr>
 *     <td><a href="DHKey.html">DHKey</a></td>
 *     <td>yes</td>
 *     <td>>=256</td>
 *     <td>no</td>
 *     <td>n/a</td>
 *     <td>no</td>
 *     <td>Only used for key-exchange. Patent expired.</td>
 *   <tr>
 * </table>
 * <p>
 * <b>Class:</b> The name of the class implementing an algorithm. It will be
 * based on the name of the algorithm.
 * <p>
 * <b>P-K:</b> Is this a public-key algorithm?
 * <p>
 * <b>Key Size:</b> The size of the key in bits. In some cases variable.
 * <p>
 * <b>Cipher:</b> Can this algorithm be used for encryption?
 * <p>
 * <b>Block Size:</b> The size of the blocks of data which is encrypted at
 *    one time with this algorithm. If the resulting ciphertext block is
 *    not the same size, its size is also given, separated by a slash. The
 *    size may be based on the size of the key, which is denoted by K. All
 *    sizes are given in bytes. Thus "K-1 / 2*K" means that the input block
 *    size is one byte less than the size of the key, while the output size
 *    is twice the size of the key.
 * <p>
 * <b>Signature:</b> Can this algorithm be used to sign data?
 *
 * <hr>
 *
 * @see is.logi.crypto.keys.CipherKey
 * @see is.logi.crypto.keys.SignatureKey
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface Key {

  /**
   * Return the name of the algorithm used by this key. */
  public String getAlgorithm();
  /** Return the key's SHA1 fingerprint. */
  public Fingerprint getFingerprint();
  /**
   * Return the "size" of the key. This is a (fairly inaccurate) measure
   * of how difficult it is to break and is heavily dependant on the
   * algorithm used.
   */
  public int getSize();
  /**
   * Return a hash-code based on the keys SHA1 fingerprint. */
  public int hashCode();
  /**
   * Returns true iff this is a private key. */
  public boolean isPrivate();
  /**
   * Check if a key mathces this. This is true if this and key are a matched
   * pair of public/private keys or the same symmetric key. */
  public boolean matches(Key key);
  /**
   * Returns the fingerprint of the matching key in the key-pair. */
  public Fingerprint matchFingerprint();
}
