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
package jenes.stage;

import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * A Dispencer distributes a population between the branches of a parallel stage and merges the
 * output of each branch in the output population of the parallel.<br>
 * <br>
 * The distribute method adds individual taken from the input population in one (or more) of the input population
 * of the branches (see {@link #distribute(Population, Population[])}). The number of branches is indicated by the
 * span parameter passed to the constructor.<br>
 * <br>
 * For example, if span==2 can have<br> 
 * <p><blockquote><pre>		
 * 	int count=0;	
 * 	for( Individual<T> i : in ) {
 * 		int branch=count%2;
 * 			branches[branch].add(i);
 * 	}
 * </pre></blockquote>
 * The method {@link #mergePopulation(Population[], Population)} takes the output populations from the branches
 * array and merges them in the output population according to some policy. A simple implementation is:
 * <p><blockquote><pre>
 * 	int count = 0;
 * 	for(Population<T>  branch : branches) {
 * 		for(Individual<T> i : branch) {
 * 			Individual<T> dest = out.getIndividual(count++);
 * 			// This check is necessary because out could have
 * 			// lesser elements than those resulting from branches
 * 			if( dest != null )
 * 				dest.setAs(i);
 * 			else{
 * 				if(count<=out.size())
 * 					throw new IllegalStateException("out population can't contains null individual");
 * 				out.add(i.clone());
 * 			}
 * 		}
 * 	}
 * 
 * 	// If out has more elements than the sum of elements
 * 	// resulting from branches, we remove the exceeding elements
 * 	int outSize=out.size();
 * 	for( int i = outSize-1; i >= count; --i )
 * 		out.remove(i);
 * </blockquote></pre>		
 * <p>
 *
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 1.2
 * @since 1.0
 * 
 * @see     jenes.stage.Parallel
 */
public abstract class Dispenser<T extends Chromosome> {

    /** The dispencer amplitude, that is the number of populations where it will add individuals
     *  in the distribution method */
    protected int span;

    /**
     * Constructs a new dispencer with the specfied amplitude
     * 
     * @param span the dispencer amplitude
     */
    public Dispenser(int span) {
        this.span = span;
    }

    /**
     * Returns the dispenser amplitude.
     * <p>
     * @return the dispenser amplitude 
     */
    public int span() {
        return this.span;
    }

    /**
     * Distributes the specified population between those ones in the specified array.
     * If some populations within inStagePop are not empty they will contain the initial
     * individuals too at the end of distribute operation.
     * <p>
     * @param in the population to be distributed
     * @param branches the array of sub populations of the initial one
     */
    public abstract void distribute(Population<T> in, Population<T>[] branches);

    /**
     * Merges the populations within the specified array in the specified one.
     * If population is not empty it will contain the initial individuals too at
     * the end of merge operation.
     * <p>
     * @param out the final population
     * @param branches the populations to be merged
     */
    public abstract void mergePopulation(Population<T>[] branches, Population<T> out);
}
