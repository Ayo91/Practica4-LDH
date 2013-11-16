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
 * This class defines an allele set made of doubles.
 *
 * @version 2.0
 * @since 1.0
 */

public class DoubleAlleleSet extends GenericAlleleSet<Double> {
    
    public DoubleAlleleSet(Set<Double> set) {
        super(set);
    }
    
    /**
     * Builds a DoubleAlleleSet with random values within the range [lowerBound,upperBound]
     * <p>
     * @param size the allala set cardinality
     * @param lowerBound the min value to choose
     * @param upperBound the max value to choose
     * @return a new DoubleAlleleSet
     */
    public static DoubleAlleleSet createRandom(int size,  double lowerBound, double upperBound ) {
        HashSet<Double> values = new HashSet<Double>();
        Random rand = Random.getInstance();
        
        for( int i = 0; i < size; ++i  ) {
            values.add(rand.nextDouble(lowerBound, upperBound+Double.MIN_VALUE));
        }
        
        return new DoubleAlleleSet(values);
    }
    
    /**
     * Builds a new DoubleAlleleSet with uniformly distributed values within the range [lowerBound,upperBound]
     * <p>
     * @param size the allala set cardinality
     * @param lowerBound the min value to choose
     * @param upperBound the max value to choose
     * @return a new DoubleAlleleSet
     */
    public static DoubleAlleleSet createUniform(int size,  int lowerBound, int upperBound ) {
        HashSet<Double> values = new HashSet<Double>();
        
        double step = 1.0/upperBound - lowerBound;
        for( double x = lowerBound; x <= upperBound; x += step ) {
            values.add(x);
        }
        
        return new DoubleAlleleSet(values);
    }
    
    
}
