package fr.kriket.oso.tools;

import java.util.Random;

/**
 * Created by fred on 2/1/17.
 */

public class random {

    public static char rndChar () { // TODO: 1/24/17 move to tools package
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);

    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int) java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {   // TODO: 1/24/17  move to tools package

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }
}
