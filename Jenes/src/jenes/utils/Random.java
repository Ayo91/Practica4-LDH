/*
 * JENES
 * A time and memory efficient Java library for genetic algorithms and more 
 * Copyright (C) 2011 Intelligentia srl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package jenes.utils;

/**
 * This class provides the random generator used by JENES.
 * <p>
 * Random extends the {@link MersenneTwisterFast} class with some methods useful to JENES for obtaining values within a range.
 * </p>
 * <p>
 * Random implements the singleton design pattern, thus only one instance of Random is available and retrieved by invoking the method {@link #getInstance()}.
 * </p>
 * <p>
 * The random sequence is controlled by the seed. The standard seed assures that the same sequence of random values is produced by different runs. The time based seed, assures that the sequences varies run by run.
 * </p>
 *
 * @version 1.2
 * @since 1.0
 */
@SuppressWarnings("serial")
public class Random extends MersenneTwisterFast {
  
	
    /** The static Random instance */
    private static Random mtfe=null;
    
    /** The seed used as default */
    public static final long STANDARD_SEED = 4357;
    
    /**
     * Constructs a new Random instance.
     *
     */
    private Random(){
        super();
    }
    
    /**
     * Returns the Random singleton.
     * At the first invocation the Random object is instantiated.
     *
     * @return the Random instance
     */
    public static Random getInstance(){
        if (mtfe==null) {
            mtfe=new Random();
            mtfe.setTimeSeed();
        }
        return mtfe;
    }
    
    /**
     * Returns the Random sigleton imposing the seed given as argument
     * @param seed
     * @return 
     */
    public static Random getInstance(long seed) {
        if( mtfe == null ) {
            mtfe = new Random();
        }
        mtfe.setSeed(seed);
        return mtfe;
    }
    /**
     * Sets the current time as Random seed.
     * This method will make the Random sequence of values different run by run.
     */
    public final void setTimeSeed() {
        super.setSeed(System.currentTimeMillis());
    }
    
    /**
     * Sets the stardard value as Random seed.
     * This method will assure the Random sequence of values will not change by run.
     * It should be used for debugging purpose only.
     */
    public final void setStandardSeed() {
        super.setSeed(STANDARD_SEED);
    }
    
    /**
     * Return the current seed used for random
     * @return 
     */
    @Override
    public long getSeed() {
        return super.getSeed();
    }
    
    /**
     * Returns a random double uniformly distributed within the interval [0,bound[.
     * Please note that bound is excluded.
     * <p>
     * @param bound the upper bound
     * @return a random double uniformly distributed within [0,bound[
     */
    public final double nextDouble(final double bound) {
        return this.nextDouble()*bound;
    }
    
    /**
     * Returns a double uniformly distributed within the interval [lowerBound,upperBound[.
     * Please note that the upperBound is excluded.
     * <p>
     * @param lowerBound the interval lower bound
     * @param upperBound the interval upper bound
     * @return a double uniformly distributed within [lowerBound,upperBound[
     */
    public final double nextDouble(final double lowerBound, final double upperBound) {
        double range = upperBound-lowerBound;
        return this.nextDouble(range) + lowerBound;
    }
    
    /**
     * Returns a random integer drawn uniformly in the interval [lowerBound, upperBound[.
     * Please note that upperBound is excluded. Thus the integer is between lowerBound and upperBound-1.
     * <p>
     * @param lowerBound the interval lower bound
     * @param upperBound the interval upper bound
     * @return an integer drawn uniformly from lowerBound to upperBound-1.
     */
    public final int nextInt(final int lowerBound, final int upperBound) {
        //nextInt(range) provides an integer in [0,range-1]
        //the returned integer is in [lowerBound,(upperBound-1)]
        return this.nextInt(upperBound - lowerBound) + lowerBound;
    }
    
    /**
     * Returns a random boolean value.
     * <p>
     * @param coin the probability to have a true value
     * @return a boolean value
     */
    public final boolean nextBoolean(final double coin) {
        return this.nextDouble() < coin;
    }
}
