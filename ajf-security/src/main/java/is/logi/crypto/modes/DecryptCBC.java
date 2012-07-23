package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * Use this class to decrypt ciphertext generated by EncryptCBC.
 * <p>
 * If you are using symmetric keys, the DecryptECB object must be
 * initialized with the same key as the EncryptECB object it is
 * decrypting data from. If you are using asymetric keys, the DecryptECB
 * object must be initialized with the other key from the key-pair.
 *
 * @see is.logi.crypto.modes.EncryptCBC
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DecryptCBC extends DecryptMode{

  /** The key used for decryption. */
  private CipherKey key;

  /** Input block size and output block size. */
  private int ibs;
  private int obs;
  
  /**
   * <code>buffer[0..bufPos-1]</code> is data waiting to be part of a
   * full block.
   */
  private byte[] buffer;
  private int bufPos=0;
  
  /**
   * The last cipher-block if any, otherwise null. */
  private byte[] last;
  
  /**
   * Create a new CBC-mode decrypt session with no key. No decryption
   * can be performed until a call to <code>setKey()</code> has been
   * made.
   */
  public DecryptCBC(){
  }
  /** 
   * Create a new CBC-mode decrypt session with the specified key.
   */
  public DecryptCBC(CipherKey key){
    setKey(key);
  }
  /**
   * Send bytes to the DecryptCBC object for decryption.
   * <p>
   * Decrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the plaintext. Data may
   * be encrypted in blocks in which case only whole blocks of
   * plaintext are written to <code>dest</code>. Any remaining data
   * will be stored and prepended to <code>source</code> in the next
   * call to <code>decrypt</code>.
   */
  public synchronized byte[] decrypt(byte[] source, int i, int length){
    int blocks = (bufPos+length)/ibs;

    // Get the IV from the stream.
    if(last==null){
      last = new byte[obs];
      System.arraycopy(source,i, last,0, obs);
      blocks--;  // One of the blocks is the IV.
      length-=ibs;
      i+=ibs;
    }
    byte[] dest = new byte[blocks*obs]; // dest[0..j-1] is plaintext
    int j=0;    

    // Use saved data    
    if (bufPos>0){
      // We have encrypted data in the buffer
      int n=Math.min(length, buffer.length-bufPos);
      System.arraycopy(source,i, buffer,bufPos, n);
      bufPos+=n;
      i+=n;
      length-=n;

      if (bufPos==buffer.length){
	// We've filled the buffer
	key.decrypt(buffer,0, dest,0);
        for(int k=0; k<obs; k++)
	  dest[k]^=last[k];
        System.arraycopy(source,0, last,0, obs);
	j += obs;
	bufPos=0;
      } else
	return dest;  // can't even fill one block...
    }
    int ii=i;
    
    // Decrypt first block
    if(blocks>0){
      key.decrypt(source, i, dest, j);
      for(int k=0; k<obs; k++)
        dest[j+k]^=last[k];
      i+=ibs;
      j+=obs;

      // Decrypt complete blocks
      for (int b=1; b<blocks; b++){
        key.decrypt(source, i, dest, j);
        System.arraycopy(source,i-ibs, last,0, obs);
        for(int k=0; k<obs; k++)
          dest[j+k]^=source[i+k-ibs];
	i+=ibs;
	j+=obs;
      }
      System.arraycopy(source,i-ibs, last,0, obs);
    }

    // Save incomplete block
    bufPos = length-(i-ii);
    if(bufPos!=0)
      System.arraycopy(source,i, buffer,0, bufPos);

    return dest;
  }
  /** 
   * Return the key used for decryption. */
  public CipherKey getKey(){
    return key;
  }
  /**
   * Set the key to use for decryption. Do not call this method when
   * there may be data in the internal buffer.
   */
  public void setKey(CipherKey key){
    this.key=key;
    ibs=key.cipherBlockSize();
    obs=key.plainBlockSize();
    buffer = new byte[key.plainBlockSize()];
    last=null;
  }
}