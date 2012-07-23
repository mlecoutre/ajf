package is.logi.crypto.random;

// Copyright (C) 1998 Logi Ragnarsson

import java.util.Random;

/**
 * This class uses the scheduler to generate random numbers. It counts the number of times
 * a loop is repeated before a thread has slept for a specified number of milliseconds.
 * The 8 lowest-order bits of these numbers are then used.
 * <p>
 * If you feel tempted to use this class, look at using RandomSpinner with a
 * very high <code>round</code> parameter. If it is set to higher than 16 it
 * will collect more entropy than the PureSpiner.
 * <p>
 * The <a href="http://www.cs.berkeley.edu/~daw/java-spinner2">helper class</a>
 * which does the actual number generation is by
 * Henry Strickland (<a href="strix@versant.com">strix@versant.com</a>) and
 * Greg Noel (<a href="greg@qualcomm.com">greg@qualcomm.com</a>). It is based on
 * <a href="ftp://ftp.research.att.com/dist/mab/librand.shar">similar C code</a>
 * by Matt Blaze, Jack Lacy, and Don Mitchell.
 *
 * @see is.logi.crypto.random.RandomSpinner
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class PureSpinner extends Random {

	private int t;

    public PureSpinner(){
        t=Spinner.guessTime(1024);
    }
    /** Generates the next random number. */
    protected synchronized int next(int bits){
        return
            (Spinner.spin(t) <<  0) +
            (Spinner.spin(t) <<  8) +
            (Spinner.spin(t) << 16) +
            (Spinner.spin(t) << 24);
    }
}
