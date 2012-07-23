package is.logi.crypto.keys;

// Copyright (C) 1998-1999 Logi Ragnarsson

import is.logi.crypto.hash.Fingerprint;
import is.logi.crypto.sign.Signature;

/**
 * This interface is implemented by keys that can be used to
 * create and validate signatures on blocks of data.
 * 
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 * (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public interface SignatureKey extends Key {
	
    /**
     * Create a signature for a Fingerprint with a private key.
     *
     * @exception KeyException if the key modulus is shorter than the signature.
     * @exception KeyException if this is not a private key
     */
    public Signature sign(Fingerprint fp) throws KeyException ;
    /**
     * Returns the length of a signature in bytes. */
    public int signatureSize();
    /**
     * Returns the maximum size in bytes of the fingerprints
     * that can be signed. */
    public int signBlockSize();
    /**
     * Verify a Signature on a Fingerprint.
     * <p>
     * In the case of an asymmetric algorithm, this method can only be called
     * on the public key in a pair and verifies signatures generated with the
     * private key in the pair.
     * <p>
     * In the case of a symmetric algorithm, this method verifies signatures
     * generated with the same key.
     *
     * @exception KeyException if this is a private key for an asymmetric algorithm
     */
    public boolean verify(Signature s, Fingerprint fp) throws KeyException;
}
