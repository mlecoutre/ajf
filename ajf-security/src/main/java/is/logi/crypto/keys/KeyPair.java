package is.logi.crypto.keys;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;

/**
 * This class is a simple holder for a pair of public/private keys. Some
 * encryption algorithms only use a single key, in which case the
 * public and private fields of a KeyPair may reference the same object.
 * Either the public or private fields may be null if the key is unknown..
 * 
 * @see is.logi.crypto.keys.Key
 * @see is.logi.crypto.keys.KeyRing
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class KeyPair extends Crypto {

	// public and private key in the pair.
	Key pub,pri;

    /** Create a new KeyPair holder. */
    public KeyPair(Key pub, Key pri){
        this.pub=pub;
        this.pri=pri;
    }
    /** Return the private key from the pair. */
    public Key getPrivate(){
        return pri;
    }
    /** Return the public key from the pair. */
    public Key getPublic(){
        return pub;
    }
  /**
   * Return a CDS for this key-pair.
   *
   * @see is.logi.crypto.Crypto#fromString(String)
   */
    public String toString(){
        return "KeyPair("+pub.toString()+","+pri.toString()+")";
    }
}
