package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This interface is implemented by classes for the client portion of an
 * interactive protocol.
 * <p>
 * In this context, the party which initiates the protocol is considered the
 * client. Interactive means that the client may need to recieve information
 * from the server, so the protocol can be used off-line.
 *
 * @see is.logi.crypto.protocols.InterProtocolServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface InterProtocolClient{

    /** Returns true iff this end of the protocol is completed. */
    public boolean completed();
     /**
      * Get the next message in the protocol.
      * <p>
      * <code>received</code> is the last message received form the server
      * and has not yet been sent to the client.
      * <p>
      * The returned value is the next message to send to the server or null
      * if no more messages need to be sent and the protocol is terminated.
      *
      * @exception ProtocolException if a problem arises with the protocol.
      */
    public byte[] message(byte[] received) throws ProtocolException;
}
