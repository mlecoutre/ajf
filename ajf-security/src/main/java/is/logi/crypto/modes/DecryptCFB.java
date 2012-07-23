package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * Use this class to decrypt ciphertext generated by EncryptCFB.
 * <p>
 * Note that unlike ECB or CBC mode, CFB mode uses the <i>same</i>
 * key to encrypt and decrypt, even with asymmetric ciphers.
 *
 * @see is.logi.crypto.modes.EncryptCFB
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class DecryptCFB extends DecryptMode{

  /** The key used for decryption. */
  private CipherKey key;
  
  /** Input block size and output block size. */
  private int ibs;
  private int obs;
  
  /** CFB queue. */
  private byte[] queue;
  
  /**
   * Create a new CFB-mode decrypt session with no key. No decryption
   * can be performed until a call to <code>setKey()</code> has been
   * made.
   */
  public DecryptCFB(){
  }
  /** 
   * Create a new CFB-mode decrypt session with the specified key. */
  public DecryptCFB(CipherKey key){
    setKey(key);
  }
  /**
   * Send bytes to the DecryptCFB object for encryption.
   * <p>
   * Decrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the plaintext.
   */
  public synchronized byte[] decrypt(byte[] source, int i, int length){
    if(queue==null){
      // Get the IV from source
      queue=new byte[ibs];
      System.arraycopy(source,0, queue,0, ibs);
      i+=ibs;
      length-=ibs;
    }
    byte[] dest=new byte[length];
    int destPos=0;
    
    int end=i+length;
    byte[] buf=new byte[obs];
    while(i<end){
      //System.err.println("D: "+hexString(queue));
      key.encrypt(queue,0, buf,0);
      dest[destPos]=(byte)(source[i] ^ buf[0]);
      System.arraycopy(queue,1, queue,0, ibs-1);
      queue[ibs-1]=source[i];
      i++;
      destPos++;
    }
    return dest;
  }
  /** 
   * Return the key used for decryption.
   */
  public CipherKey getKey(){
    return key;
  }
  /**
   * Set the key to use for decryption.
   */
  public void setKey(CipherKey key){
    this.key=key;
    ibs=key.plainBlockSize();
    obs=key.cipherBlockSize();
  }
}