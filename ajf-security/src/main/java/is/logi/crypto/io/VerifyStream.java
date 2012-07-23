package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.hash.HashState;
import is.logi.crypto.keys.KeyException;
import is.logi.crypto.keys.SignatureKey;
import is.logi.crypto.sign.Signature;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This InputStream reads the stream generated by a SignStream and verifies
 * the embedded signatures. It will not pass any data through until that data
 * has been verified.
 *
 * @see is.logi.crypto.io.SignStream
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class VerifyStream extends FilterInputStream{
	
	private HashState fs;
	private SignatureKey key;
	
	/**
	 * <code>buffer[bufPos..bufPos-1]</code> is the data which
	 * has been read from the underlying stream but not from this. */
	private byte[] buffer;
	private int bufPos;
	private int bufSize;
	
    /**
     * Create a new VerifyStream object. It matches the SignStream object
     * with the same <code>blockSize</code> and <code>fs</code> and with the
     * key that matches <code>key</code>
     *
     * @see is.logi.crypto.io.SignStream
     */
    public VerifyStream(InputStream in, int blockSize, SignatureKey key, HashState fs) {
        super(in);
        this.key = key;
        this.bufSize=(blockSize/fs.blockSize())*fs.blockSize();
        if (this.bufSize==0)
            this.bufSize=fs.blockSize();
        buffer=new byte[bufSize];
        bufPos=bufSize; // no unread data in buffer
        this.fs = fs;
    }
    /** 
     * Returns the number of bytes that can be read from this input stream
     * without blocking. */
    public int available() throws IOException{
        return (bufSize-bufPos)+(in.available()/bufSize)*bufSize;
    }
    /**
     * Fills the buffer with data and verifies the signature. 
     * Sets bufSize==0 if EOF is reached.
     */
    private void fillBuffer() throws IOException {
        bufPos=0;
        bufSize = Crypto.readInt(in);
        while(bufPos<bufSize){
            int n=in.read(buffer,bufPos,bufSize-bufPos);
            if (n<0){
                // EOF inside block! terminate!!
                buffer=null;
                return;
            }
            bufPos+=n;
        }
        // buffer is filled.
        
        bufPos =0;
        byte[] rfp = new byte[ (key==null) ? fs.hashSize() : key.signatureSize() ];
        int rfpPos=0;
        while(rfpPos<rfp.length){
            int n=in.read(rfp,0,rfp.length);
            if (n<0){
                // EOF inside signature! terminate!!
                buffer=null;
                return;
            }
            rfpPos+=n;
        }
        // rfp contains the next fingerprint or signature fron the
        // stream. buffer[0..bufSize-1] contains the undelivered data.
        fs.update(buffer,0,bufSize);
        Fingerprint fp=fs.calculate();
        if(key==null){
            if(!Crypto.equal(rfp,fp.getBytes()))
                throw new IOException("Data was not verified");
        } else {
            Signature sig = new Signature(rfp, fp.getName(), key.getFingerprint());
            try{
                if(!key.verify(sig,fp))
                    throw new IOException("Signature was not verified");
            } catch (KeyException e) {
                throw new IOException(e.getMessage());
            }
        }
    }
    /** 
     * Returns false. (This could be implemented, but I've never seen it
     * used. Mail me if you want it!) */
    public boolean markSupported(){
        return false;
    }
    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an int in the range 0 to 255. If no byte is
     * available because the end of the stream has been reached, the
     * value -1 is returned. This method blocks until input data is
     * available, the end of the stream is detected, or an exception
     * is thrown.  */
    public synchronized int read() throws IOException{
        if (bufPos==bufSize)
            fillBuffer();
        if(buffer==null)
            return -1;
        return buffer[bufPos++];
    }
    /**
     * Reads up to len bytes of data from this input stream into an array of
     * bytes. This method blocks until some input is available.
     * <p>
     * The actual number of bytes read is returned or -1 if the end of the
     * stream is reached.
     */
    public synchronized int read(byte b[], int off, int len) throws IOException{
        if(bufPos==bufSize)
            fillBuffer();
        if(buffer==null)
            return -1;
        
        if (len>bufSize-bufPos)
            len=bufSize-bufPos;
        System.arraycopy(buffer,bufPos, b,off, len);
        bufPos+=len;
        return len;
    }
    /**
     * Skips over and discards n bytes of data from the input stream. The
     * skip method may, for a variety of reasons, end up skipping over some
     * smaller number of bytes, possibly 0. The actual number of bytes
     * skipped is returned.
     */
    public synchronized long skip(long n) throws IOException{
        long m=available();
        if (m>n)
            m=n;
        else
            n=m;
        // n==m==min(original.n, available())
        while(m>bufSize-bufPos){
            // throw the buffer away
            m-=(bufSize-bufPos);
            fillBuffer();
        }
        bufPos+=m;  // throw away some bytes
        
        return n;
    }
}