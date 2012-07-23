package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;

/**
 * Output Fedback Mode iterates the encryption routine on the IV and xors
 * the resulting stream with the plaintext to produce the ciphertext.
 * <p>
 * This is as fast as ECB or CBC mode, but has the streaming properties of
 * CFB mode. In addition, the stream xored with the plaintext is
 * precalculated in a separate thread, to give better response (although
 * this does not not lower the total time spent calculating).
 * <p>
 * However, since (with plaintext P, ciphertext C and xor-stream S)
 * <p><blockquote>
 *   P = C ^ S, 
 * </blockquote><p>
 * the opponent can alter the plaintext received by changing
 * <p><blockquote>
 *  C' = C ^ A,
 * </blockquote><p>
 * so that
 * <p><blockquote>
 *  P' = C' ^ S = C ^ A ^ S = P ^ A
 * </blockquote><p>
 * is seen. But the opponent will not be able to learn P or control what P'
 * is.
 *
 * @see is.logi.crypto.modes.DecryptOFB
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptOFB extends EncryptMode{
  
  /** Calculates the stream. */
  private OFBThread thread;

  /** Stores desired buffer size until setKey() is called. */
  private int bufSize;

  /** Has IV been written yet? */
  private boolean wroteIV=false;
  
  /**
   * Create a new OFB-mode encrypt session with no key. No
   * encryption can be performed until the <code>setKey()</code>
   * method has been called.
   * <p>
   * A buffer of <code>bufSize</code> bytes is created to hold a
   * pre-calculated xor-stream.*/
  public EncryptOFB(int bufSize){
    this.bufSize=bufSize;
  }
  /**
   * Create a new OFB-mode encrypt session with the specified
   * <code>key</code>. A buffer of <code>bufSize</code> bytes is created
   * to hold a pre-calculated xor-stream.
   */
  public EncryptOFB(CipherKey key, int bufSize){
    this.bufSize=bufSize;
    setKey(key);
  }
  /**
   * Send bytes to the EncryptOFB object for encryption.
   * <p>
   * Encrypt <code>length</code> bytes from <code>source</code>,
   * starting at <code>i</code> and return the ciphertext.
   */
  public synchronized byte[] encrypt(byte[] source, int i, int length){
    byte[] dest;
    int destPos=0;

    if (!wroteIV){
      dest = new byte[length+thread.ibs];
      System.arraycopy(thread.buffer,0, dest,0, thread.ibs);
      destPos = thread.ibs;
      while(thread.inBuffer()<=thread.ibs){
        thread.waitForBytes();
      }
      thread.bufStart+=thread.ibs;
      wroteIV=true;
    } else
      dest = new byte[length];

    while(length>0){
      int n = Math.min(length, thread.inBuffer());
      length-=n;
      int end=i+n;
      while(i<end){
        dest[destPos++] = (byte)(source[i++] ^ thread.buffer[thread.bufStart]);
        // bufStart increased *after* the byte is used,
        // so it isn't overwritten by the calc thread!
        thread.bufStart=(thread.bufStart+1)%thread.bufSize;
      }
      thread.resume();  // We have used some bytes from the buffer, so we'll start calculating
      if(length>0)
        thread.waitForBytes();
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
    return thread.key;
  }
  /**
   * Set the key to use for encryption.
   */
  public synchronized void setKey(CipherKey key){
    thread = new OFBThread(key,bufSize);
    thread.start();
  }
}
