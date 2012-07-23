package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.DecryptMode;
import is.logi.crypto.protocols.NoninterKeyExServer;
import is.logi.crypto.protocols.NoninterProtocolServer;
import is.logi.crypto.protocols.ProtocolException;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decrypt a stream of data encrypted with a corresponding EncryptStream
 * ojbect.
 * <p>
 * The DecryptStream must be initialized with a NoninterKeyExServer
 * object complementing the NoninterKeyExClient object used in the
 * EncryptStream and a DecryptMode object complementing the EncryptMode
 * object used in the DecryptStream.
 * <p>
 *
 * @see is.logi.crypto.io.EncryptStream
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class DecryptStream extends FilterInputStream{
	
	private DecryptMode dmode;
	
	/**
	 * <code>buffer[bufPos..buffer.length-1]</code> is the data which
	 * has been decrypted but not read from the stream.  */
	private byte[] buffer;
	private int bufPos;
	
	/** The size of the buffer of ciphertext to read at once. */
	private int cipherBufferSize;
	
    /**
     * Create a new DecryptStream. Plaintext is written to <code>out</code>,
     * <code>dmode</code> is used for decryption and if <code>kex</code>
     * is not null it will be used to generate the session key or receive i
     * from the client. See the various key-exchange server classes for
     * details.
     * <p>
     * Note that if <code>kex</code> is not null, it controls which
     * session key is used and <code>dmode</code> receives the session key
     * when it has been decided. If <code>kex</code> is null, then
     * <code>dmode</code> must be initialized with a key, and the same symmetric
     * key or matching asymmetric key must be used on the client.
     *
     * @exception ProtocolException if a problem arises with the key-exchange protocol.
     * @exception IOException if problems arise with the underlying OutputStream.  */
    public DecryptStream(InputStream in, NoninterKeyExServer kex, DecryptMode dmode) throws ProtocolException, IOException{
        super(in);
        this.dmode=dmode;
        
        if(kex!=null){
            // Let's do key-exchange
            execute(kex);
            try{
                CipherKey key=(CipherKey)kex.sessionKey();
                if(key==null)
                    throw new ProtocolException("A non-interactive protocol should only need one message.");
                dmode.setKey(key);
            } catch (ClassCastException e){
                throw new ProtocolException("The key-exchange protocol proposes to use a non-cipher key for decryption.");
            }
        }
        int blockSize=dmode.getKey().cipherBlockSize();
        cipherBufferSize=(1024/blockSize+1)*blockSize;
        buffer=new byte[0];
    }
    /** Returns the number of bytes that can be read from this input stream without blocking. */
    public synchronized int available() throws IOException{
        int r=buffer.length-bufPos;
        if(r>0)
            // We've got some plain bytes to return
            return r;
        int a=in.available();
        if(a==0)           // The underlying stream is blocked
            return 0;
        // There is ciphertext in the underlying stream
        byte[] ciphertext = new byte[a];
        a=in.read(ciphertext,0,a);
        if(a>0){           // We actually read some data!
            buffer=dmode.decrypt(ciphertext,0,a);
            bufPos=0;
            return buffer.length-bufPos;
        }
        // Nothing read from the underlying stream (possibly EOF)
        return 0;
    }
    /**
     * Executes a non-interactive protocol.
     *
     * @exception ProtocolException if there is a problem with the protocol.
     * @exception IOException if there is a problem with the underlying streams.
     */
    public void execute(NoninterProtocolServer prot) throws IOException, ProtocolException{
        int l=
            ((in.read() & 0xff) << 24) |
            ((in.read() & 0xff) << 16) |
            ((in.read() & 0xff) <<  8) |
            ((in.read() & 0xff)       );
        if(l>0){
            byte[] msg=new byte[l];
            in.read(msg);
            prot.message(msg);
        } else
            prot.message(null);
        if(!prot.completed())
            throw new ProtocolException("The protocol was not completed");
    }
    /**
     * Fills the internal buffer if possible.
     * Returns false iff this stream is at EOF.
     */
    private boolean fillBuffer() throws IOException{
        byte[] cipher=new byte[cipherBufferSize];
        int l=in.read(cipher);
        if(l==-1){
            buffer=new byte[0];
            bufPos=0;
            return false;
        }
        buffer=dmode.decrypt(cipher,0,l);
        bufPos=0;
        return true;
    }
    /** Returns false. */
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
        while(bufPos==buffer.length){
            // We could read ciphertext from in which is not enough to
            // make a full block, so buffer will have length 0. We
            // then want to read more ciphertext, possibly blocking in
            // the process. Thus thw "while" rather than "if".
            if (!fillBuffer())
                return -1;
        }
        return (int)buffer[bufPos++] & 0xff;
    }
    /**
     * Reads up to len bytes of data from this input stream into an array of
     * bytes. This method blocks until some input is available.
     */
    public synchronized int read(byte b[], int off, int len) throws IOException{
        while(bufPos==buffer.length){
            // We could read ciphertext from in which is not enough to make
            // a full block, so buffer will have length 0. We then want to
            // read more ciphertext, possibly blocking in the process. Thus
            // thw "while" rather than "if".
            if (!fillBuffer())
                return -1;
        }
        int l = (len<=buffer.length-bufPos) ? len : buffer.length-bufPos;
        System.arraycopy(buffer,bufPos, b,off, l);
        bufPos+=l;
        return l;
    }
    /**
     * Skips over and discards n bytes of data from the input stream. The
     * skip method may, for a variety of reasons, end up skipping over some
     * smaller number of bytes, possibly 0. The actual number of bytes
     * skipped is returned.
     */
    public synchronized long skip(long n) throws IOException{
        while(bufPos==buffer.length){
            // We could read ciphertext from in which is not enough to make
            // a full block, so buffer will have length 0. We then want to
            // read more ciphertext, possibly blocking in the process. Thus
            // thw "while" rather than "if".
            if (!fillBuffer())
                return 0;
        }
        int inBuf=buffer.length-bufPos;
        if(n<inBuf){
            // We can skip n bytes from the buffer.
            bufPos+=n;
            return n;
        }
        // Discard the buffer and recurse
        return inBuf+skip(n-inBuf);
    }
}
