package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * Cipher Block Chaining mode xors each plain block with the previous
 * cipher block before encrypting.
 * <p>
 * Because CBC mode encrypts one block at a time, encryption is only
 * performed when a full block of data has been sent to the object.
 * This also means that when you call the flush() method, random data
 * is appended to the block before encryption. This can be avoided in
 * two ways: Either by flushing regularly and sending the size of the
 * data packets to the reciever, or by using the CFB of OFB modes.
 *
 * @see is.logi.crypto.modes.DecryptCBC
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> 
 *        (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class EncryptCBC extends EncryptMode{
  
  /** The key to use for encryption. */
  private CipherKey key;
  
  /**
   * <code>buffer[0..bufPos-1]</code> is data waiting to be part of a
   * full block. */
  private byte[] buffer;
  private int bufPos;

  /** Input block size and output block size. */
  private int ibs;
  private int obs;
  
  /**
   * The last cipher block if any, otherwise <code>null</code>. */
  private byte[] last;
  
  /**
   * Create a new CBC-mode encrypt session with no key. No
   * encryption can be performed until the <code>setKey()</code>
   * method has been called.  */
  public EncryptCBC(){
  }
  /** 
   * Create a new CBC-mode encrypt session with the specified key. */
  public EncryptCBC(CipherKey key){
    setKey(key);
  }
  /**
   * Send bytes to the EncryptCBC object for encryption.
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
    byte[] dest;
    int j=0;    // dest[0..j-1] is ciphertext
    
    // Create and write IV
    if(last==null){
      dest = new byte[blocks*obs+obs];
      for(int k=0; k<obs; k++)
	dest[k]=(byte)random.nextInt();
      last = new byte[ibs];
      System.arraycopy(dest,0, last,0, ibs);
      j=obs;
    } else {
      dest = new byte[blocks*obs];
    }
    
    // Use saved data
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
        for(int k=0; k<ibs; k++)
          buffer[k]^=last[k];
	key.encrypt(buffer,0, dest,0);
	System.arraycopy(dest,0, last,0, ibs);
	j += obs;
	bufPos=0;
      } else
	return dest;  // can't even fill one block...
    }
    int ii=i;

    // Encrypt first block    
    if(blocks>0){
      for(int k=0; k<ibs; k++)
        buffer[k]=(byte)(source[i+k]^last[k]);
      key.encrypt(buffer, 0, dest, j);
      i+=ibs;
      j+=obs;

      // Encrypt entire blocks
      for (int b=1; b<blocks; b++){
        for(int k=0; k<ibs; k++)
          // We know the last cipher block is still in dest!
          buffer[k]=(byte)(source[i+k]^dest[j+k-obs]);
        key.encrypt(buffer, 0, dest, j);
	i+=ibs;
	j+=obs;
      }
      System.arraycopy(dest,j-obs,last,0,ibs); // Save last block.
    }

    // Save possible incomplete block
    bufPos = length-(i-ii);
    if (bufPos!=0)
      System.arraycopy(source,i, buffer,0, bufPos);

    return dest;
  }
  /**
   * Pads the internal buffer, encrypts it and returns the
   * ciphertext.  */
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
