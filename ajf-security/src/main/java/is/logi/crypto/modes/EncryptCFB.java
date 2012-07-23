package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * This class implements 8-bit Cipherblock FeedBack mode which encrypts
 * a whole block for each plaintext character. This makes it much slower
 * than ECB or CBC mode, but it can be used for streaming and no garbage
 * is ever inserted.
 *
 * @see is.logi.crypto.modes.DecryptCFB
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptCFB extends EncryptMode{
  
  /** The key to use for encryption. */
  private CipherKey key;

  /** Input block size and output block size. */
  private int ibs;
  private int obs;
  
  /** CFB queue.*/
  private byte[] queue;
  
  /**
   * Create a new CBF-mode encrypt session with no key. No
   * encryption can be performed until the <code>setKey()</code>
   * method has been called.  */
  public EncryptCFB(){
  }
  /** Create a new CBF-mode encrypt session with the specified key. */
  public EncryptCFB(CipherKey key){
    setKey(key);
  }
  /**
   * Send bytes to the EncryptCFB object for encryption.
   * <p>
   * Encrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the ciphertext.
   */
  public synchronized byte[] encrypt(byte[] source, int i, int length){
    byte[] dest;
    int destPos=0;
    if(queue==null){
      // We need an IV now
      queue = new byte[ibs];
      random.nextBytes(queue);
      dest = new byte[length+ibs];
      System.arraycopy(queue,0, dest,0, ibs);
      destPos=ibs;
    } else {
      // Create 'small' output buffer
      dest = new byte[length];
    }
    
    int end=i+length;
    byte[] buf=new byte[obs];
    while(i<end){
      //System.err.println("E: "+hexString(queue));
      key.encrypt(queue,0, buf,0);
      dest[destPos]=(byte)(source[i] ^ buf[0]);
      System.arraycopy(queue,1, queue,0, ibs-1);
      queue[ibs-1]=dest[destPos];
      i++;
      destPos++;
    }
    
    return dest;
  }
  /**
   * Pads the internal buffer, encrypts it and returns the ciphertext.
   * Since CBF mode doesn't use an internal buffer, an empty array is
   * returned.
   */
  public synchronized byte[] flush(){
    return new byte[0];
  }
  /** Return the key used for encryption. */
  public CipherKey getKey(){
    return key;
  }
  /**
   * Set the key to use for encryption.
   */
  public void setKey(CipherKey key){
    this.key=key;
    ibs=key.plainBlockSize();
    obs=key.cipherBlockSize();
  }
}
