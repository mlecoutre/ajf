package is.logi.crypto.random;

// Copyright (C) 1998 Logi Ragnarsson

import java.io.IOException;
import java.io.Reader;
import java.util.Random;

/**
 * This class reads bits from a Reader object and returns them as
 * random values. No randomness checking is done and an Error is
 * thrown if the end of the Reader is ever reached.
 * <p>
 * This class is useful f.ex. for reding random bits from the
 * <code>/dev/random</code> or <code>/dev/urandom</code> devices in
 * linux. This would be done with the following code:
 * <blockquote><pre>
 * Random rand;
 * try {
 *   rand=new RandomFromReader(new FileReader("/dev/random"));
 * } catch (FileNotFoundException e) {
 *   rand=new RandomSpinner();
 * }
 * </pre></blockquote>
 *
 * @author <a href="http://www.hi.is/~logir/">Logi Ragnarsson</a>
 *         (<a href="mailto:logir@hi.is">logir@hi.is</a>) */
public class RandomFromReader extends Random {

  private Reader r;

  /**
   * Create a new RandomFromReader obejct. Random bits are read from
   * <code>r</code>
   */
  public RandomFromReader(Reader r){
    this.r=r;
  }
  /** Generates the next random number. */
  protected synchronized int next(int bits){
    try{
      return
        (r.read()<<24) |
        (r.read()<<16) |
        (r.read()<< 8) |
        (r.read()    );
    } catch (IOException e) {
      throw new Error("RandomFromReader ran out of random bytes.");
    }
  }
}
