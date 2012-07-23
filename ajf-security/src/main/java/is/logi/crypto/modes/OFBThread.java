package is.logi.crypto.modes;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.keys.CipherKey;

/**
 * This class is used by EncryptOFB and DecryptOFB to hold the
 * pre-calculated xor-stream.
 * <p>
 * It is <b>not</b> thread-safe and should be a private member of a
 * class which handles synchronization of acces to it.
 * <p>
 * It is probably not useful for anything else, but could be used as
 * a template for other classes which pre-calculate data in a separate
 * thread.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 *
 * @see is.logi.crypto.modes.EncryptOFB
 * @see is.logi.crypto.modes.DecryptOFB
 */
class OFBThread extends Thread{

  /** The key to use for encryption. */
  CipherKey key;
  
  /** Input block size and output block size. */
  int ibs;
  int obs;

  /**
   * Stream pre-calc buffer, size of buffer, start of unused bytes
   * and end of unused bytes.
   * <p>
   * buffer[bufStart..bufEnd] is unused stream bytes, with indices
   * mod bufSize.
   * <p>
   * bufSize is constant. bufEnd is increased (mod bufSize) by this thread
   * after data has been but in the buffer and bufStart is increased
   * (mod bufSize) by the owner after data has been copied from buffer.
   * <p>
   * ibs divides bufSize. */
  byte[] buffer;
  int bufSize;
  int bufStart, bufEnd;

  /** Which thread, if any, is waiting. null iff none is waiting. */
  private Thread waitingThread=null;

  /**
   * Create new OFB thread with the given key and buffer size,
   * but random IV. */
  protected OFBThread(CipherKey key, int bufSize){
    init(key,bufSize);
    for (int i=0; i<ibs; i++)
      buffer[i]=(byte)Crypto.random.nextInt();
    key.encrypt(buffer,0, buffer,ibs);  // Put some data in the buffer
    bufStart=ibs;
    bufEnd=2*ibs;
  }
  /**
   * Create new OFB thread with the given key, buffer size
   * and IV[i..i+key.plainBlockSize()-1]. */
  protected OFBThread(CipherKey key, int bufSize, byte[] IV, int i){
    init(key,bufSize);
    System.arraycopy(IV,i, buffer,0, ibs);
    key.encrypt(buffer,0, buffer,ibs);  // Put some data in the buffer
    bufStart=ibs;
    bufEnd=2*ibs;
  }
  /** Bytes available in buffer. */
  public int inBuffer(){
    return (bufEnd+bufSize-bufStart) % bufSize;
  }
  private void init(CipherKey key, int bufSize){
    ibs=key.plainBlockSize();
    obs=key.cipherBlockSize();
    bufSize = (bufSize/ibs)*ibs;
    if (bufSize<8*ibs)
      bufSize=8*ibs;
    this.setDaemon(true);

    buffer = new byte[bufSize+obs-ibs];
    // if the output blocks are bigger, we get an index-out-of-bounds
    // exception when we try to fill the end of the buffer if we don't
    // add this slack.

    this.bufSize=bufSize;
    this.key=key;
  }
  public void run(){
    while(true){
      while(bufSize-inBuffer() <= obs)
        // There is no room for new bytes
        this.suspend();
      
      // make new bytes
      key.encrypt(buffer, (bufEnd-ibs+bufSize)%bufSize, buffer,bufEnd);
      bufEnd=(bufEnd+ibs)%bufSize;

      if (waitingThread!=null && (inBuffer()>0))
        // Someone wants the new bytes
        waitingThread.resume();
    }
  }
  /**
   * Waits until data is available in the buffer. This method may only be
   * called by one thread at a time!
   */
  public void waitForBytes(){
    synchronized(this){
      waitingThread=Thread.currentThread();
    }
    this.resume();
    Thread.yield();
    while(inBuffer()==0)
      waitingThread.suspend();
    synchronized(this){
      waitingThread=null;
    }
    this.resume();
  }
}
