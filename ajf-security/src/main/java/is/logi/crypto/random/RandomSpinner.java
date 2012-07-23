package is.logi.crypto.random;

// Copyright (C) 1998 Logi Ragnarsson

import is.logi.crypto.Crypto;
import is.logi.crypto.hash.MD5State;

import java.util.Random;

/**
 * This class uses the scheduler to generate random numbers. It counts the
 * number of times a loop is repeated before a thread has slept for a
 * specified number of milliseconds. These numbers are then fed to a hash
 * function to mask any possible correlations.
 * <p>
 * Before any random bytes are returned the internal entropy pool is filled
 * with the specified number of bytes from the scheduler. This initialization
 * is performed in a separate thread and may take a few seconds. You should
 * therefore create instances of the RandomSpinner well before using them.
 * <p>
 * The <a href="http://www.cs.berkeley.edu/~daw/java-spinner2">helper class</a>
 * which is used to gather entropy was written by
 * Henry Strickland (<a href="strix@versant.com">strix@versant.com</a>) and
 * Greg Noel (<a href="greg@qualcomm.com">greg@qualcomm.com</a>). It is based on
 * <a href="ftp://ftp.research.att.com/dist/mab/librand.shar">similar C code</a>
 * by Matt Blaze, Jack Lacy, and Don Mitchell.
 * <p>
 * This generator is normally somewhat slower than the SecureRandom generator in
 * the java library, since it spends some time collecting entropy.
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a> (<a href="mailto:logir@hi.is">logir@hi.is</a>)
 */
public class RandomSpinner extends Random {

	MD5State.SubState ss = new MD5State.SubState();  // Entropy pool
	private int t;                                   // Time to spin spinner

	private int roundEnt;       // Bytes to get from spinner per hash

	// Initialization thread which gathers initial entropy
	private Thread initThread;  
	
	private class InitThread extends Thread{
		
		private int initial;  // Bytes to get from spinner initially.
		
		public InitThread(int initial){
			this.initial = initial;
		}

		// Fill entropy pool
		public void run(){
			t=Spinner.guessTime(1024);
			for(int i=0; i<initial; i++){
				if ((i%64==0) && i!=0)
					ss.transform(ss.buffer,0);
				ss.buffer[i%64] = (byte)Spinner.spin(t);
			}
			if (initial%64!=0)
				ss.transform(ss.buffer,0);
			initThread=null;
		}
		
	}
	
	/** unused[unusedPos..15] is unused pseudo-random numbers. */
	byte[] unused;
	int unusedPos;

	int poolSweep=0;

    public RandomSpinner(){
        this(128,2);
    }
    /**
     * Creates a new instance of the RandomSpinner class. It will be
     * initialized with <code>initial</code> bytes from the Spinner and
     * collect <code>round</code> bytes from it for every 16 bytes it outputs.
     *
     * @param round must be in the range <code>0..64</code>
     */
    public RandomSpinner(int initial, int round){
        initThread = new InitThread(initial);
        initThread.start();
        roundEnt = (round<=64) ? round : 64;
        unused=new byte[16];
        unusedPos=16;
    }
    /** Generates the next random number. */
    protected synchronized int next(int bits){
        //System.out.println(bits);
        if(unusedPos==16)
            update();
        int r=0;
        for(int b=0; b<bits; b+=8)
            r = (r<<8) + unused[unusedPos++];
        return r;
    }
    /** Get new unused bytes. */
    private void update(){
        // Wait if initialization is not done.
        while(initThread!=null){
            //System.err.print(".");
            try {
                initThread.join();
            } catch (InterruptedException e) {
            }
            //System.out.println();
        }
        
        // Inject entropy into the pool
        for(int i=0; i<roundEnt; i++){
            ss.buffer[poolSweep] += Spinner.spin(t)+1;
            poolSweep = (poolSweep+1)%64;
        }
        
        ss.transform(ss.buffer,0);
        Crypto.writeBytes(ss.hash[0], unused, 0,4);
        Crypto.writeBytes(ss.hash[1], unused, 4,4);
        Crypto.writeBytes(ss.hash[2], unused, 8,4);
        Crypto.writeBytes(ss.hash[3], unused,12,4);
        unusedPos=0;
    }
}
