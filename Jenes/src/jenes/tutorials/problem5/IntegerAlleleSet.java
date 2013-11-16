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
package jenes.tutorials.problem5;

import java.util.HashSet;
import java.util.Set;

import jenes.utils.Random;
import jenes.chromosome.GenericAlleleSet;

/**
 * Tutorial illustrating the use of object-oriented chromosomes, whose
 * allele set can be defined by the user for each gene.
 *
 * In this example the chromosomes are combinations of colors. We aim at finding
 * the vector of colors closest to a given sequence.
 *
 * This class defines a set of integers.
 *
 * @version 2.0
 * @since 1.0
 */
public class IntegerAlleleSet extends GenericAlleleSet<Integer> {
    
    public IntegerAlleleSet(Set<Integer> set) {
        super(set);
    }
    
    /**
     * Builds an IntegerAlleleSet with random values within the range [lowerBound,upperBound]
     * <p>
     * @param size the allala set cardinality
     * @param lowerBound the min value to choose
     * @param upperBound the max value to choose
     * @return a new IntegerAlleleSet
     */
    public static IntegerAlleleSet createRandom(int size,  int lowerBound, int upperBound ) {
        HashSet<Integer> values = new HashSet<Integer>();
        int s0 = upperBound - lowerBound + 1;
        if( size > s0 ) size = s0;
        
        Random rand = Random.getInstance();
        
        for( int i = 0; i < s0; ++i  ) {
            
            int chosen = values.size();
            double coin = ((double)size-chosen)/(s0-i);
            boolean justEnough = s0-i == size-chosen;
            
            if( justEnough || rand.nextBoolean(coin) ) {
                values.add(lowerBound + i);
            }
        }
        
        return new IntegerAlleleSet(values);
    }
    
    /**
     * Builds a new IntegerAlleleSet with uniformly distributed values within the range [lowerBound,upperBound]
     * <p>
     * @param size the allala set cardinality
     * @param lowerBound the min value to choose
     * @param upperBound the max value to choose
     * @return a new IntegerAlleleSet
     */
    public static IntegerAlleleSet createUniform(int size,  int lowerBound, int upperBound ) {
        HashSet<Integer> values = new HashSet<Integer>();
        int s0 = upperBound - lowerBound + 1;
        if( size > s0 ) size = s0;
        
        double step = 1.0/(upperBound - lowerBound);
        for( double x = lowerBound; x <= upperBound; x += step ) {
            int i = (int) Math.round(x);
            values.add(i);
        }
        
        return new IntegerAlleleSet(values);
    }
    
}
