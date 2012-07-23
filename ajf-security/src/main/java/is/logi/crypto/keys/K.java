package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.hash.Fingerprint;

/**
 * This abstract class implements some of the methods from the Key interface.
 * It is used as the superclass of all the key classes in Crypto.
 * <p>
 * You should probably never declare variables of this type, but rather of the
 * more abstract Key interface, since it is extended by the CipherKey and
 * SignatureKey interfaces.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public abstract class K extends Crypto implements Key {
  
  // -----------------------------------------------------------
  // INSTANCE VARIABLES

  // The key's SHA1 fingerprint or null.
  protected Fingerprint fingerprint;

  // The SHA1 fingerprint of the other key in the pair or null.
  protected Fingerprint otherFingerprint;

  /** 
   * Calculate the fingerprint for this key or the other in the pair. */
  protected abstract Fingerprint calcFingerprint(boolean other);
  /** Return the key's SHA1 fingerprint. */
  public final Fingerprint getFingerprint(){
    if (fingerprint==null)
      fingerprint=calcFingerprint(false);
    return fingerprint;
  }
  // -----------------------------------------------------------
  // INSTANCE METHODS
  
  /**
   * Return the "size" of the key. This is a (fairly inaccurate) measure
   * of how difficult it is to break and is heavily dependant on the
   * algorithm used.
   */
  public abstract int getSize();
  /**
   * Return a hash-code based on the keys SHA1 fingerprint. */
  public final int hashCode(){
    return getFingerprint().hashCode();
  }
  /**
   * Returns the fingerprint of the matching key in the key-pair. */
  public Fingerprint matchFingerprint(){
    if (otherFingerprint==null)
      otherFingerprint=calcFingerprint(true);
    return otherFingerprint;
  }
}
