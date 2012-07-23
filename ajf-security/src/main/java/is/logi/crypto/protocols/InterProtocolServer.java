package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This interface is implemented by classes for the server portion of an
 * interactive protocol.
 *
 * @see is.logi.crypto.protocols.InterProtocolClient
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface InterProtocolServer{
  
    /** Returns true iff this end of the protocol is completed. */
    public boolean completed();
    /**
     * Get the next message in the protocol.
     * <p>
     * <code>received</code> is the last message received form the client
     * and has not yet been sent to the client.
     * <p>
     * The returned value is the next message to send to the client or null
     * if no more messages need to be sent and the protocol is terminated.
     *
     * @exception ProtocolException if a problem arises with the protocol.
     */
    public byte[] message(byte[] received) throws ProtocolException;
}
