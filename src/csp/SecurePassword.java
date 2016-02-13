package csp;

import java.security.SecureRandom;
import java.util.Random;

public class SecurePassword {
	
	private static char[] VALID_CHARACTERS =
		    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

	public static String SecureRandomAlphaNumericString() {
	    SecureRandom srand = new SecureRandom();
	    Random rand = new Random();
	    char[] buff = new char[32];

	    for (int i = 0; i < 32; ++i) {
	      // reseed rand once you've used up all available entropy bits
	      if ((i % 10) == 0) {
	          rand.setSeed(srand.nextLong()); // 64 bits of random!
	      }
	      buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
	    }
	    return new String(buff);
	}
}
