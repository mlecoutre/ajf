package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * Electronic Codebook Mode simply encrypts each block of plaintext
 * independently. It is not as secure as for example CBC mode.
 * <p>
 * Because ECB mode encrypts one block at a time, encryption is only
 * performed when a full block of data has been sent to the object.
 * This also means that when you call the flush() method, random data
 * is appended to the block before encryption. This can be avoided in
 * two ways: Either by flushing regularly and sending the size of the
 * data packets to the reciever, or by using the CFB or OFB modes.
 *
 * @see is.logi.crypto.modes.DecryptECB
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptECB extends EncryptMode{
  
  /** The key to use for encryption. */
  private CipherKey key;
  
  /** Input block size and output block size. */
  private int ibs;
  private int obs;
  
  /**
   * <code>buffer[0..bufPos-1]</code> is data waiting to be part of a
   * full block. */
  private byte[] buffer;
  private int bufPos;
  
  /**
   * Create a new ECB-mode encrypt session with no key. No
   * encryption can be performed until the <code>setKey()</code>
   * method has been called.  */
  public EncryptECB(){
  }
  /** Create a new ECB-mode encrypt session with the specified key. */
  public EncryptECB(CipherKey key){
    setKey(key);
  }
  /**
   * Send bytes to the EncryptECB object for encryption.
   * <p>
   * Encrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the ciphertext. Data is
   * encrypted in blocks, so only whole blocks of ciphertext
   * are written to <code>dest</code>. Any remaining plaintext will be
   * stored and prepended to <code>source</code> in the next call to
   * <code>encrypt</code>.
   */
  public synchronized byte[] encrypt(byte[] source, int i, int length){
    int blocks = (bufPos+length)/ibs;
    byte[] dest = new byte[blocks*obs];
    int j=0;    // dest[0..j-1] is ciphertext
    
    if (bufPos>0){
      // We have unencrypted data in the buffer
      int n=Math.min(length, buffer.length-bufPos);
      System.arraycopy(source,i, buffer,bufPos, n);
      bufPos+=n;
      i+=n;
      length-=n;
      blocks--;
      if (bufPos==buffer.length){
	// We've filled the buffer
	key.encrypt(buffer,0, dest,0);
	j += obs;
	bufPos=0;
      } else
	return dest;  // can't even fill one block...
    }
    int ii=i;
    
    // Encrypt entire blocks
    for (int b=0; b<blocks; b++){
      key.encrypt(source, i, dest, j);
      i+=ibs;
      j+=obs;
    }
    
    // Put possible incomplete block in the buffer
    bufPos = length-(i-ii);
    if (bufPos!=0)
      System.arraycopy(source,i, buffer,0, bufPos);
    
    return dest;
  }
  /**
   * Pads the internal buffer, encrypts it and returns the
   * ciphertext. */
  public synchronized byte[] flush(){
    byte[] padding = new byte[buffer.length-bufPos];
    random.nextBytes(padding);
    byte[] dest = encrypt(padding,0,padding.length);
    return dest;
  }
  /** Return the key used for encryption. */
  public CipherKey getKey(){
    return key;
  }
  /** Set the key to use for encryption. Do not call this method
   * when there may be data in the internal buffer. */
  public void setKey(CipherKey key){
    this.key=key;
    ibs=key.plainBlockSize();
    obs=key.cipherBlockSize();
    buffer = new byte[key.plainBlockSize()];
  }
}
