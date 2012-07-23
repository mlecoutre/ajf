package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.EncryptMode;
import is.logi.crypto.protocols.NoninterKeyExClient;
import is.logi.crypto.protocols.NoninterProtocolClient;
import is.logi.crypto.protocols.ProtocolException;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This OutputStream encrypts everything written to it using the specified
 * CipherKey and EncryptMode. It optionally first executes a non-interactive
 * key-exchange protocol.
 * <p>
 * Beware that depending on the EncryptMode you use, garbage may be inserted
 * into the stream when it is flushed or closed.
 *
 * @see is.logi.crypto.io.DecryptStream
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class EncryptStream extends FilterOutputStream{
	
	private EncryptMode emode;
	
    /**
     * Create a new EncryptStream. Ciphertext is written to <code>out</code>,
     * <code>emode</code> is used for encryption and if <code>kex</code>
     * is not null it will be used to generate a session key and/or send it
     * to the server. See the various key-exchange client classes for
     * details.
     * <p>
     * Note that if <code>kex</code> is not null, it controls which
     * session key is used and <code>emode</code> receives the session key
     * when it has been decided. If <code>kex</code> is null, then
     * <code>emode</code> must be initialized with a key, and the same symmetric
     * key or matching asymmetric key must be used on the server.
     *
     * @exception ProtocolException if a problem arises with the key-exchange protocol.
     * @exception IOException if problems arise with the underlying OutputStream.  */
    public EncryptStream(OutputStream out, NoninterKeyExClient kex, EncryptMode emode) throws ProtocolException, IOException{
        super(out);
        this.emode = emode;
        
        if(kex!=null){
            // Let's do key-exchange
            execute(kex);
            try{
                CipherKey key=(CipherKey)kex.sessionKey();
                if(key==null)
                    throw new ProtocolException("A non-interactive protocol should only need one message.");
                emode.setKey(key);
            } catch (ClassCastException e){
                throw new ProtocolException("The key-exchange protocol proposes to use a non-cipher key for encryption.");
            }
        }
        
    }
    /**
     * Executes a non-interactive protocol.
     *
     * @exception ProtocolException if there is a problem with the protocol.
     * @exception IOException if there is a problem with the underlying streams.
     */
    public void execute(NoninterProtocolClient prot) throws IOException, ProtocolException{
        byte[] msg = prot.message(null);
        if(msg==null){
            out.write(0);
            out.write(0);
            out.write(0);
            out.write(0);
        } else {
            int l = msg.length;
            out.write(l >>> 24);
            out.write(l >>> 16);
            out.write(l >>>  8);
            out.write(l       );
            out.write(msg);
        }
        if(!prot.completed())
            throw new ProtocolException("The protocol was not completed");
    }
    /**
     * Flushes this output stream and forces any buffered output bytes to
     * be written out to the stream. If the number of bytes written is not
     * a multiple of the plainBlockSize of the Key used for encryption, up
     * to one byte less than a whole block of garbage may be appended to
     * the data when flush is called.
     */
    public synchronized void flush() throws IOException{
        byte[] ctext = emode.flush();
        out.write(ctext,0,ctext.length);
        out.flush();
    }
    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this output stream. 
     */
    public synchronized void write(byte[] buf, int off, int len) throws IOException{
        byte[] ctext = emode.encrypt(buf,off,len);
        out.write(ctext,0,ctext.length);
    }
    /** Writes the specified byte to this output stream. */
    public synchronized void write(int b) throws IOException{
        byte[] buf = { (byte)b };
        write(buf,0,1);
    }
}
