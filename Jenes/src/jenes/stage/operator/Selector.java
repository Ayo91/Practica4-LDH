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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Filter;
import jenes.stage.StageException;

/**
 * A class representing a generic selection operator.
 * <p>
 * The actual selector is implemented by subclassing this abstract class and providing the {@link Selector#select(Population)} and
 * {@link Selector#preSelect(Population)} method implementations: the former is required to select individuals from
 * the specified {@link Population}; the latter is required to set up the selector state when a new selection begins.
 * <p>
 * A {@link Selector#maxIllegalRate} is specified to obtain a population with a max number of illegal individuals selected.
 * When this threshould is reached, the new illegal selected individuals are not added at the output population, and the
 * selection process is repeated.
 * <p>
 * At the start selection time the output population has already the individuals; the individuals selected from the input
 * population have not to be added at the output population: each output population individual is set as
 * the one selected by the input population.
 * <p>
 * The output population size will be equal to the input population one; automatically new individuals are added or old ones are deleted to make
 * the sizes equal.
 * <p>
 * A {@link Selector.Statistics} is associated to each selector operator.
 *
 * @param <T> The class of chromosomes to work with.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see Individual
 * @see Population
 */
public abstract class Selector<T extends Chromosome> extends Operator<T> {

    /** The default percentage of allowed illegal individuals */
    public static final double DEFAULT_MAX_ILLEGAL_RATE = 0.3;
    private double maxIllegalRate = DEFAULT_MAX_ILLEGAL_RATE;
    /** The number of individuals to select */
    private int selectionRate;
    /** The population to select */
    private Population<T> population;
    /** The filter to apply to population */
    private Population.Filter filter;
    /** The mating pool */
    private List<Individual<T>> matingPool;

    /**
     * Constructs a new Selector operator.
     */
    public Selector() {
        this(-1);
    }

    /**
     * Constructs a new Selector operator, specifying the number of individuals to select.
     *
     * @param n the number of individuals to select
     */
    public Selector(int n) {
        super.statistics = new Statistics();
        this.setSelectionRate(n);
        this.matingPool = new ArrayList<Individual<T>>();
    }

    /**
     * Returns the max number of illegal individuals this selector operator can select.
     *
     * @return the max illegal rate
     */
    public final double getMaxIllegalRate() {
        return maxIllegalRate;
    }

    /**
     * Sets the  max number of illegal individuals this selector operator can select.
     *
     * @param rate the max illegal rate
     */
    public void setMaxIllegalRate(double rate) {
        if (rate < 0) {
            maxIllegalRate = 0;
        } else if (rate > 1) {
            maxIllegalRate = 1;
        } else {
            maxIllegalRate = rate;
        }
    }

    /**
     * Returns the number of individuals selected by this operator.
     *
     * @return the number of individuals selected.
     */
    public final int getSelectionRate() {
        return this.selectionRate;
    }

    /**
     * Sets the number of individuals being selected by this operator.
     * If the given number is zero or negative, then it is set to -1,
     * meaning that a number equal to to the input population will be selected.
     *
     * @param n the number of individuals being selected.
     */
    public void setSelectionRate(int n) {
        this.selectionRate = n > 0 ? n : -1;
    }

    @Override
    public void init(GeneticAlgorithm<T> ga) {
        super.init(ga);
    }

    /**
     * Sets the individuals in the output population like the selected ones
     *
     *
     */
    public final void process(Population<T> in, Population<T> out) throws StageException {

        long startInstant = System.currentTimeMillis();
        ((Statistics) this.statistics).selections = 0;

        this.preSelect(in, Population.ALL);

        int m = this.selectionRate > 0 ? this.selectionRate : in.size();

        out.resize(m);

        int maxIllegalIndividuals = (int) (m * maxIllegalRate);
        int numOfIllegals = 0;

        // This list is initially set to the whole input population
        // When the maximum number of illegal individuals is reached
        // it can be set to only legal individuals, if any.
        for (int i = 0; i < m; ++i) {

            // If there is no legal individual in the population
            // we continue to take elements from the original population
            // otherwise the selection pool become the sub-population
            // of legal individuals
            if (numOfIllegals == maxIllegalIndividuals && in.hasLegals()) {
                this.preSelect(in, Population.LEGALS);
            }

            Individual<T> ind = select(this.matingPool);
            out.getIndividual(i).setAs(ind);

            if (!ind.isLegal()) {
                ++numOfIllegals;
            }
        }

        ((Statistics) this.statistics).selections = m;
        this.statistics.executionTime = System.currentTimeMillis() - startInstant;
    }

    /**
     * Sets up the selection state according a population's state. It is invoked
     * at the beginning of selection for the specified (filtered) population.
     *
     * @param pop the population to process by the stage
     * @param filter
     */
    protected void preSelect(Population<T> pop, Filter filter) {
        this.filter = filter;
        this.population = pop;

        this.matingPool.clear();
        for (Iterator<Individual<T>> it = pop.iterator(this.filter); it.hasNext();) {
            this.matingPool.add(it.next());
        }
    }

    /**
     * Selects an individual in the filtered population
     * <p>
     * @param pop from which to choose an individual.
     * @return the individual selected
     */
    protected abstract Individual<T> select(List<Individual<T>> pop);
    
    /**
     * Selects an individual in the population
     * @param pop
     * @return 
     */
    public final Individual<T> select(Population<T> pop) {
        if (pop != this.population) {
            this.preSelect(pop, Population.ALL);
        }
        return this.select(this.matingPool);
    }

    /**
     * A statistics object holding the number of selection performed and the time spent to execute them.
     * The statistics is available by two methods:
     * {@link Selector#getStatistics()} to have a new statistics setted according to the crossover state or
     * {@link Selector#updateStatistics(jenes.stage.operator.Operator.Statistics)} to modify an existing statistics according
     * to the selector state.
     * <p>
     * Esamples of use are showed below.
     * <p><blockquote><pre>
     * Selector.Statistics stat = a_selector.getStatistics();
     * </pre></blockquote>
     * <p>
     * returns a new statistics object setted according to the specified selector state.
     * <p><blockquote><pre>
     * Selector.Statistics stat = new Selector.Statistics();
     * a_selector.updateStatistics(stat);
     * </pre></blockquote>
     * <p>
     * modifies the existing statistics according to the specified selector state.
     */
    public class Statistics extends Operator<T>.Statistics {

        /** Number of selectionRate performed. */
        protected long selections;

        /**
         * Returns the number of selectionRate performed.
         *
         * @return the number of selectionRate performed.
         */
        public long getSelections() {
            return selections;
        }

        @Override
        protected void fill(Operator<T>.Statistics stats) {
            super.fill(stats);
            ((Statistics) stats).selections = this.selections;
        }
    }
}