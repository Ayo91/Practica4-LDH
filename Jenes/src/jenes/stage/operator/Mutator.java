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
package jenes.stage.operator;

import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.StageException;

/**
 * A generic mutation operator. The operation is executed according to a mutation probability.
 * <p>
 * The actual operator is implemented by subclassing this abstract class and providing the {@link Mutator#mutate(Individual)}
 * implementation: the method is required to mutate an {@link Individual} according to a mutation strategy.
 * No new individual copies have to be created during the mutation operation: the individual specified at the {@link Mutator#mutate(Individual)}
 * will take parte to the output mutator population.
 * <p>
 * A {@link Mutator.Statistics} is associated to each mutator operator.
 * 
 * @param <T> The class of chromosomes to work with.
 *
 * @version 1.2
 * @since 1.0
 * 
 * @see Individual
 * @see Population
 */
public abstract class Mutator<T extends Chromosome> extends Operator<T> {

    /** The mutation probablility */
    protected double probability;

    /**
     * Constructs a new mutator instance with the specified mutator probability
     * 
     * @param probability the mutator probability
     */
    public Mutator(double probability) {
        this.probability = probability;
        super.statistics = new Statistics();
    }

    /**
     * Returns the mutator probability
     * 
     * @return the mutator probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Sets the mutator probability
     *
     * @param probability the new mutator probability
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public final void process(Population<T> in, Population<T> out) throws StageException {
        long startInstant = System.currentTimeMillis();
        ((Statistics) this.statistics).mutations = 0;

        out.swap(in);

        for (Individual<T> p : out) {

            if (random.nextBoolean(this.probability)) {
                this.mutate(p);
                p.setNotEvaluated();
                ++((Statistics) this.statistics).mutations;
            }
        }
        this.statistics.executionTime = System.currentTimeMillis() - startInstant;
    }

    /**
     * Mutates a single individual. This abstract method is implemented according to 
     * a mutation policy
     * 
     * @param t the individual to mutate
     */
    protected abstract void mutate(Individual<T> t);

    /**
     * A statistics object holding the number of mutation performed and the time spent to execute them. 
     * The statistics is available by two methods:
     * {@link Mutator#getStatistics()} to have a new statistics setted according to the mutator state or 
     * {@link Mutator#updateStatistics(jenes.stage.operator.Operator.Statistics)} to modify an existing statistics according
     * to the mutator state.
     * <p>
     * Esamples of use are showed below. 
     * <p><blockquote><pre>
     * Mutator.Statistics stat = a_mutator.getStatistics();
     * </pre></blockquote>
     * <p>
     * returns a new statistics object setted according to the specified mutator state.
     * <p><blockquote><pre>
     * Mutator.Statistics stat = new Mutator.Statistics();
     * a_mutator.updateStatistics(stat);
     * </pre></blockquote>
     * <p>
     * modifies the existing statistics according to the specified mutator state.
     */
    public class Statistics extends Operator<T>.Statistics {

        /** Number of mutations performed. */
        protected long mutations;

        /**
         * Returns the number of mutations performed
         * 
         * @return the number of mutations performed
         */
        public long getMutations() {
            return mutations;
        }

        @Override
        protected void fill(Operator<T>.Statistics stats) {
            super.fill(stats);
            ((Statistics) stats).mutations = this.mutations;
        }
    }
}