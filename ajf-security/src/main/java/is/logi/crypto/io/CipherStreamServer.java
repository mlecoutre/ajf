package is.logi.crypto.io;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.keys.CipherKey;
import is.logi.crypto.modes.DecryptMode;
import is.logi.crypto.modes.EncryptMode;
import is.logi.crypto.protocols.InterKeyExServer;
import is.logi.crypto.protocols.InterProtocolServer;
import is.logi.crypto.protocols.ProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class can be used to apply an interactive key exchange protocol to a
 * pair of streams and then encrypt all data going through them with the session
 * key exchanged. This class expects to talk to an equivalent client class.
 *
 * @see is.logi.crypto.io.CipherStreamClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class CipherStreamServer extends CipherStream {
	
    /**
     * Create a new CipherStreamServer object and ecxhange keys.
     * <p>
     * Create a new object which uses <code>kex</code> to exchange keys with
     * a remote client and then <code>encrypt</code> to encrypt the data to
     * <code>out</code> and <code>decrypt</code> to decrypt data from
     * <code>in</code> and <code>out</code>.
     *
     * @exception ProtocolException if there is a problem exchanging keys.
     * @exception IOException if there is a problem with the underlying streams.
     */
    public CipherStreamServer(InputStream in, OutputStream out, InterKeyExServer kex, EncryptMode encrypt, DecryptMode decrypt) throws ProtocolException, IOException {
        this.in  = in;
        this.out = out;
        if(kex!=null){
            execute(kex);
            CipherKey key;
            try{
                key=(CipherKey)kex.sessionKey();
            } catch (ClassCastException e){
                throw new ProtocolException("The exchanged Key was not a CipherKey.");
            }
            encrypt.setKey(key);
            decrypt.setKey(key);
        }
        cOut = new EncryptStream(out, null, encrypt);
        cIn = new DecryptStream(in, null, decrypt);
        this.encrypt = encrypt;
        this.decrypt = decrypt;
    }
    /**
     * Executes an interactive protocol.
     *
     * @exception ProtocolException if there is a problem with the protocol keys.
     * @exception IOException if there is a problem with the underlying streams.
     */
    public void execute(InterProtocolServer prot) throws IOException, ProtocolException{
        byte[] msg=null;
        while(true) {
            int l=readInt(in);
            if((msg==null) || (msg.length!=l))
                msg=new byte[l];
            l=in.read(msg);
            if(l<0)
                throw new ProtocolException("Client broke connection");
            msg=prot.message(msg);
            if(msg==null)
                break;
            writeInt(out,msg.length);
            out.write(msg);
            if(prot.completed())
                break;
        }
    }
}
