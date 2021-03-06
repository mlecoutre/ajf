package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.hash.Fingerprint;

/**
 * This interface is implemente by classes used to retrieve keys from
 * some source, such as a simple file, a database or a key server.
 * <p>
 * More methods will be added for searching for specific keys.
 */
public interface KeySource{

  /**
   * Retreive the key with the given fingerprint. If it is not found in the
   * key-source, null is returned.
   */
  public KeyRecord byFingerprint(Fingerprint fingerprint);
}
