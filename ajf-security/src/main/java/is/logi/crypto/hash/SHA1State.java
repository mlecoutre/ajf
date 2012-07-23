package is.logi.crypto.hash;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.CryptoCorruptError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * An object of this class holds the state of a SHA-1 fingerprint still
 * being calculated.
 * <p>
 * This class actually uses java.security.MessageDigest to do all the work.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class SHA1State extends HashState {

  MessageDigest md;
  
  /** Create a new clear SHA1State. */
  public SHA1State() {
    try {
      md=MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      throw new CryptoCorruptError("SHA1 algortihm is missing from the java class library.");
    }
  }
  /**
   * Return the number of bytes needed to make a valid hash. If a multiple
   * of this number of bytes is hashed, no padding is needed. If no such
   * value exists, returns 0. */
  public int blockSize(){
    return 64;
  }
  /**
   * Return a Fingerprint for the curret state, without
   * destroying the state. */
  public Fingerprint calculate(){
    try{
      byte[] dig=((MessageDigest)md.clone()).digest();
      return new Fingerprint("SHA1",dig,0,dig.length);
    } catch (CloneNotSupportedException e) {
      throw new CryptoCorruptError("SHA1 algortihm is not cloneable java class library.");
    }
  }
  /** The name of the algorithm is "MD5". */
  public String getName(){
    return "SHA1";
  }
  /**
   * Returns the size of a fingerprint in bytes. */
  public int hashSize(){
    return 20;
  }
  /** Reset the object. */
  public void reset(){
    md.reset();
  }
  /**
   * Update the fingerprint state with the bytes from
   * <code>buf[offset, offset+length-1]</code>. */
  public void update(byte[] buffer, int offset, int length){
    md.update(buffer,offset,length);
  }
}
