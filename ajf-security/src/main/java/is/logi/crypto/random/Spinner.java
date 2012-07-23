package is.logi.crypto.random;

// This file is in the public domain. Do with it as you wish.

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/** Helper class for the RandomSpinner class. */
public class Spinner extends Thread {

	private long t;
	
    private Spinner(long t) {
        this.t=t;
    }
    /**
     * Returns t such that spin(t) is larger than n. This value may change as
     * the load of the system changes.
     */
    public static int guessTime(int n) {
        int t=5;
        while(spin(t)<n)
            t=(t*3)/2;
        return t;
    }
    /**
     * Call with optional parameter t.
     * <p>
     * Calls spin(t) 1024 times and outputs the 8 lowest-order bits to a
     * file named "spin.t", where t is replaced with the value of the
     * parameter t. 
     * <p>
     * If t is omitted, t=guessTime(1024) will be used.
     * <p>
     * the output of this program can be compressed to estimate the entropy
     * of the random number generator. On my system the output does not
     * compress at all for t>=5.
     */
    public static void main(String[] arg) throws IOException{
        int t;
        if(arg.length > 0)
            t = Integer.parseInt(arg[0]);
        else {
            t = guessTime(1024);
        }
       // System.out.println("Using t="+t);
        DataOutputStream out = new DataOutputStream(new FileOutputStream("spin."+t));
        for(int i=0; i<1024; i++){
            int n = spin(t);
            out.writeByte(n);
        /*    if(i%128 == 0)
                System.out.println(i+" "+n);*/
        }
       // System.out.println(1024);
        out.flush();
    }
    public void run() {
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
        }
    }
    /** Return the number of spins performed in t milliseconds. */
    public static int spin(long t) {
        int counter = 0;
        Thread s = new Spinner(t);
        s.start();
        do {
            ++counter;
            Thread.yield();
        } while (s.isAlive());
        return counter;
    }
}
