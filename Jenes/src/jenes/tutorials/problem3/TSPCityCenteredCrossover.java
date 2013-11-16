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
package jenes.tutorials.problem3;

import jenes.AlgorithmException;
import jenes.utils.Random;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.stage.operator.Crossover;

/**
 * Tutorial showing how to implement problem specific operators.
 * The problem faced in this example is the well known Tavel Salesman Problem (TSP)
 *
 * This class implements a specific crossover aimed at preserving permutations.
 *
 * Algorithm description:
 * <pre>
 *      parent1  5 2 1 4 6 3     parent2   1 3 2 4 6 5
 *      child1   _ _ _ _ _ _     child2    _ _ _ _ _ _
 *</pre>
 * Step 1: a city is choosed randomly. We copy all the cities until the selected one from each parent to
 * each child (parent1 in child1 and parent2 in child2)
 * <pre>
 *      parent1  5 2 1 4 6 3     parent2   1 3 2 4 6 5
 *      child1   5 2 _ _ _ _     child2    1 3 2 _ _ _
 * </pre>
 * Step 2: we fill child1 getting missing elements from parent2; these ones will have the same parent2 order
 * <pre>
 *      parent1  5 2 1 4 6 3     parent2  1 3 2 4 6 5
 *      child1   5 2 1 3 4 6     child2   1 3 2 5 4 6
 *</pre>
 *
 * We repeat these steps for child2
 * 
 * @version 2.0
 * @since 1.0
 */
public class TSPCityCenteredCrossover extends Crossover<IntegerChromosome>{
    
    public TSPCityCenteredCrossover(double pCross) {
        super(pCross);
    }
    
    /**
     * Returns the number of chromosomes (i.e. 2) this operator entails.
     */
    @Override
    public int spread() {
        return 2;
    }
    
    private IntegerChromosome chrom_parent1 = null;
    private IntegerChromosome chrom_parent2 = null;
    /**
     * This method implements the crossover operation.
     *
     * @param offsprings the chromosomes to be crossed.
     */
    protected void cross(Individual<IntegerChromosome> offsprings[]) {
        
        IntegerChromosome chrom_child1 = offsprings[0].getChromosome();
        IntegerChromosome chrom_child2 = offsprings[1].getChromosome();
        
        if( chrom_parent1 == null ) {
            chrom_parent1 = chrom_child1.clone();
            chrom_parent2 = chrom_child2.clone();
        } else {
            chrom_parent1.setAs(chrom_child1);
            chrom_parent2.setAs(chrom_child2);
        }
        
        final int size = chrom_child1.length();
        if( chrom_child2.length() != size )
            throw new AlgorithmException("Error: the two chromosomes are required to have the same length.");
        
        
        //we choose a random city
        int city = Random.getInstance().nextInt(0, size);
        
        //i1, i2 are the positions of the city respectively in child1 and child2
        int i1 = findPositionOf(city, chrom_child1);
        int i2 = findPositionOf(city, chrom_child2);
        
        int j1 = 0;
        int j2 = 0;
        for( int i = 0; i < size; ++i ) {
            // get the city c1 in position i for parent1
            int c1 = chrom_parent1.getValue(i);
            // find the position of c1 in parent 2
            int p2 = findPositionOf(c1, chrom_parent2);
            // if the position is over the cross-point, it copies c1 in child2
            if( p2 > i2 ) {
                chrom_child2.setValue(i2 + (++j2), c1);
            }
            
            // similarly we process the other pair
            int c2 = chrom_parent2.getValue(i);
            int p1 = findPositionOf(c2, chrom_parent1);
            if( p1 > i1 ) {
                chrom_child1.setValue(i1 + (++j1), c2);
            }
        }
    }
    
    /**
     * Finds the position of one specific city in the chromosome.
     * <p>
     * @param city the city to find
     * @param chrom the chromosome to search
     * @return the city position
     */
    private int findPositionOf(int city, IntegerChromosome chrom){
        final int size = chrom.length();
        for( int i = 0; i < size; ++i ) {
            if( chrom.getValue(i) == city )
                return i;
        }
        return -1;
    }
    
}