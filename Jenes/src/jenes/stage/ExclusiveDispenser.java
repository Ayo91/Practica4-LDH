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
import jenes.population.Individual;
import jenes.population.Population;

/**
 * An abstract dispencer useful when each individual can be in only one parallel branch, so the 
 * distribute method is exclusive.<br>
 * The only method to implement is {@link #distribute(Individual)} to specify the branch number in which store the
 * specified Individual.<br>
 * <br>
 * Note: the predistribute method of this class has empty implementation. 
 * Override it if you need to evaluate preDistrubute statistics.
 * 
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 1.2
 * @since 1.0
 * 
 * @see     jenes.stage.Parallel
 */
public abstract class ExclusiveDispenser<T extends Chromosome> extends Dispenser<T> {

    /**
     * Constructs a new exclusive dispencer with the specified amplitude
     *
     * @param span the dispencer amplitude
     */
    public ExclusiveDispenser(int span) {
        super(span);
    }

    /**
     * Distributes the specified population between those ones in the specified array.
     * If some populations within inStagePop are not empty they will contain the initial
     * individuals too at the end of distribute operation.
     *
     * The branch in which each individual is dispatched is given by the
     * {@link jenes.stage.ExclusiveDispenser#distribute(Individual)} method
     *
     * <p>
     * @param in the population to be distributed
     * @param branches the array of sub populations of the initial one
     */
    public final void distribute(Population<T> in, Population<T>[] branches) {
        this.preDistribute(in);
        //each individual is added at only one array population
        for (Individual<T> i : in) {
            branches[distribute(i)].add(i);
        }
        this.postDistribute(branches);
    }

    /**
     * Returns the branch number where to add the specified individual.
     * Branches are numbered from 0 to span-1
     *
     * @param ind the individual to distribute
     * @return the branch number where to add the individual
     */
    public abstract int distribute(Individual<T> ind);

    /**
     * Merges the populations within the specified array in the specified one.
     * If population is not empty it will contain the initial individuals too at
     * the end of merge operation.
     *
     * <i>Note:</i> if out have a different size than the input population used in the
     * {@link jenes.stage.ExclusiveDispenser#distribute(Population, Population[])} method
     * out will be resized to fit the input population size.
     * <p>
     * @param out the final population
     * @param branches the populations to be merged
     */
    public final void mergePopulation(Population<T>[] branches, Population<T> out) {

        this.preMerge(branches);

        int count = 0;
        for (Population<T> branch : branches) {
            for (Individual<T> i : branch) {
                Individual<T> dest = out.getIndividual(count++);
                // This check is necessary because out could have
                // lesser elements than those resulting from branches
                if (dest != null) {
                    dest.setAs(i);
                } else {
                    if (count <= out.size()) {
                        throw new IllegalStateException("out population can't contains null individual");
                    }
                    out.add(i.clone());
                }
            }
        }

        // If out has more elements than the sum of elements
        // resulting from branches, we remove the exceeding elements
        int outSize = out.size();
        for (int i = outSize - 1; i >= count; --i) {
            out.remove(i);
        }

        this.postMerge(out);
    }

    /**
     * Callback method invoked just before distribution of individuals begins.
     * By default this method does nothing. It an be overriden in order to perform custom operations.
     *
     * @param population the population to distribute
     */
    public void preDistribute(Population<T> population) {
        //do nothing
    }

    /**
     * Callback method invoked just after distribution of individuals is done.
     * By default this method does nothing. It an be overriden in order to perform custom operations.
     *
     * @param branches populations as distributed
     */
    public void postDistribute(Population<T>[] branches) {
        //do nothing
    }

    /**
     * Callback method invoked just before merge of branches begins.
     * By default this method does nothing. It an be overriden in order to perform custom operations.
     *
     * @param branches populations as resulting from the different branches
     */
    public void preMerge(Population<T>[] branches) {
        //do nothing
    }

    /**
     * Callback method invoked just after merge of individuals is done.
     * By default this method does nothing. It an be overriden in order to perform custom operations.
     *
     * @param population the merged population
     */
    public void postMerge(Population<T> population) {
        //do nothing
    }

}
