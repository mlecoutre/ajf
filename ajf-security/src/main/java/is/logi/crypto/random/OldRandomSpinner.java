package is.logi.crypto.random;

// Copyright (C) 1998 Logi Ragnarsson

import java.util.Random;

/**
 * This class uses the scheduler to generate random numbers. It counts the number of times
 * a loop is repeated before a thread has slept for a specified number of milliseconds.
 * The two lowest order bits of this number are then used.
 * <p>
 * The <a href="http://www.cs.berkeley.edu/~daw/java-spinner2">helper class</a>
 * which does the actual number generation is by
 * Henry Strickland (<a href="strix@versant.com">strix@versant.com</a>) and
 * Greg Noel (<a href="greg@qualcomm.com">greg@qualcomm.com</a>). It is based on
 * <a href="ftp://ftp.research.att.com/dist/mab/librand.shar">similar C code</a>
 * by Matt Blaze, Jack Lacy, and Don Mitchell.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class OldRandomSpinner extends Random {

	private static final int NUM_ITER=256;  // should be a multiple of 16

    public OldRandomSpinner(){
    }
    /** Generates the next random number. */
    protected synchronized int next(int bits){
        return (int)
            ((Spinner.spin(10) & 0x3) << 30
            |(Spinner.spin(10) & 0x3) << 28
            |(Spinner.spin(10) & 0x3) << 26
            |(Spinner.spin(10) & 0x3) << 24
            |(Spinner.spin(10) & 0x3) << 22
            |(Spinner.spin(10) & 0x3) << 20
            |(Spinner.spin(10) & 0x3) << 18
            |(Spinner.spin(10) & 0x3) << 16
            |(Spinner.spin(10) & 0x3) << 14
            |(Spinner.spin(10) & 0x3) << 12
            |(Spinner.spin(10) & 0x3) << 10
            |(Spinner.spin(10) & 0x3) <<  8
            |(Spinner.spin(10) & 0x3) <<  6
            |(Spinner.spin(10) & 0x3) <<  4
            |(Spinner.spin(10) & 0x3) <<  2
            |(Spinner.spin(10) & 0x3));
    }
}
