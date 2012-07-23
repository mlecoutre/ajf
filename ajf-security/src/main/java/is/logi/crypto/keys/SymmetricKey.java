package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.CryptoCorruptError;
import is.logi.crypto.InvalidCDSException;
import is.logi.crypto.hash.Fingerprint;

/**
 * This abstract class implements some (more) of the methods from
 * the Key interface.
 *
 * @see is.logi.crypto.keys.KeyPair
 * @see is.logi.crypto.keys.KeyRing
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public abstract class SymmetricKey extends K {
  
  // -----------------------------------------------------------
  // INSTANCE METHODS
  
  /** 
   * Calculate the fingerprint for this key or the other in the pair. The
   * default behaviour is to return the fingerprint of the CDS for this
   * key. */
  protected Fingerprint calcFingerprint(boolean other){
    try {
      return fingerprint = Fingerprint.create(toString(),"SHA1");
    } catch (InvalidCDSException e) {
      throw new CryptoCorruptError("The SHA1State class is missing");
    }
  }
  /**
   * Returns true iff this is a private key.
   * <p>
   * Symmetric keys simply return <code>true</code>. */
  public boolean isPrivate(){
    return true;
  }
  /**
   * Returns true if this and key are the same symmetric key.
   * <p>
   * Symmetric keys simply call <code>equals(key)</code>. */
  public boolean matches(Key key){
    return equals(key);
  }
}
