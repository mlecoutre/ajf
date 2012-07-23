package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.hash.HashState;
import is.logi.crypto.keys.KeyException;
import is.logi.crypto.keys.SignatureKey;
import is.logi.crypto.sign.Signature;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This OutputStream signs everything written to it using the specified
 * HashState and SignatureKey.
 *
 * @see is.logi.crypto.io.VerifyStream
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class SignStream extends FilterOutputStream{

  private HashState fs;
  private SignatureKey key;

  private byte[] buffer; // unwritten bytes buffer
  private int bufSize;   // number of bytes in the block
  private int bufPos;    // number of bytes written to current block

  /**
   * Creates a new SignStream. It passes
   * everything written to it through <code>fs</code> and after each
   * approximately <code>blockSize</code> bytes it inserts a signature of the
   * fingerprint into the underlying stream. It then writes the data to
   * <code>out</code>.
   * <p>
   * If <code>key</code> is null the fingerprints will be written unsigned
   * to the underlying stream.
   */
  public SignStream(OutputStream out, int blockSize, SignatureKey key, HashState fs) {
    super(out);
    this.key = key;
    this.bufSize=(blockSize/fs.blockSize())*fs.blockSize();
    if (this.bufSize==0)
      this.bufSize=fs.blockSize();
    buffer = new byte[this.bufSize];
    this.fs = fs;
  }
  /**
   * Closes this output stream and releases any system resources associated
   * with this stream.
   *
   * @exception IOException if there is a problem with the underlying stream
   * or the key fails to sign the fingerprint.
   */
  public synchronized void close() throws IOException{
    flush();
    out.close();
  }
  /**
   * Flushes this output stream and forces any buffered output bytes to
   * be written out to the stream.
   */
  public synchronized void flush() throws IOException{
    signAndWrite();
    out.flush();
  }
  private void signAndWrite() throws IOException {
    if(bufPos>0){
      Crypto.writeInt(out,bufPos);
      fs.update(buffer,0,bufPos);
      out.write(buffer,0,bufPos);
      Fingerprint fp=fs.calculate();
      if(key==null)
        out.write(fp.getBytes());
      else {
        try {
          Signature sig=key.sign(fp);
          out.write(sig.getBytes());
        } catch (KeyException e) {
          throw new IOException("KeyException: "+e.getMessage());
        }
      }
      bufPos=0;
    }
  }
  /**
   * Writes <code>len</code> bytes from the specified byte array starting
   * at offset <code>off</code> to this output stream.
   *
   * @exception IOException if there is a problem iwth the underlying stream
   * or the key fails to sign the fingerprint. */
  public synchronized void write(byte[] buf, int off, int len) throws IOException{
    while(len>0){
      int n = (len<bufSize-bufPos) ? len : bufSize-bufPos;
      System.arraycopy(buf,off, buffer,bufPos, n);
      bufPos += n;
      len    -= n;
      off    += n;
      if(bufPos==bufSize)
        signAndWrite();
    }
  }
  /** Writes the specified byte to this output stream. */
  public synchronized void write(int b) throws IOException{
    buffer[bufPos++] = (byte)b;
    if (bufPos==bufSize)
      signAndWrite();
  }
}
