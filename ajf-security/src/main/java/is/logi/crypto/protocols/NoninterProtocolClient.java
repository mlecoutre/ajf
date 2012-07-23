package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This interface is implemented by classes for the client portion of a
 * non-interactive protocol.
 * <p>
 * In this context, the party which initiates the protocol is considered the
 * client. Non-interactive means that the client does not need to recieve any
 * information from the server, so the protocol can be used off-line.
 * <p>
 * A non-interactive protocol can of course also be used on-line and is
 * therefore also considered an Interactive protocol. This is why the
 * NoninterProtocol interfaces extend the InterProtocol interfaces.
 *
 * @see is.logi.crypto.protocols.NoninterProtocolServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface NoninterProtocolClient extends InterProtocolClient{

}
