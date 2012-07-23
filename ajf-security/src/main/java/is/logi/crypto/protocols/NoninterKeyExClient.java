package is.logi.crypto.protocols;

// Copyright (C) 1998 Logi Ragnarsson

/**
 * This interface is implemented by classes for the client portion of a
 * non-interactive key-exchange protocol.
 * <p>
 * In this context, the party which initiates the protocol is considered the
 * client. Non-interactive means that the client does not need to recieve any
 * information from the server, so the protocol can be used off-line.
 * <p>
 * A non-interactive key-exchange protocol can of course also be used on-line
 * and is therefore also considered an Interactive key-exchange protocol. This
 * is why the NoninterKeyEx interfaces extend the InterKeyEx interfaces.
 * <p>
 * An example of a non-interactive key-exchange protocol is sending the hash
 * of the key to use and assume that the server will be able to look it up in
 * a KeySource object. This is a somewhat wider definition of key exchange.
 *
 * @see is.logi.crypto.protocols.NoninterKeyExServer
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public interface NoninterKeyExClient extends NoninterProtocolClient, InterKeyExClient{

}
