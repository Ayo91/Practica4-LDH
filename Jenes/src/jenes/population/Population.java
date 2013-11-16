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
package jenes.population;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jenes.chromosome.Chromosome;

import jenes.statistics.StatisticsLogger.LoggableStatistics;
import jenes.statistics.StatisticsLogger.Loggable;
import jenes.utils.Random;

import static java.lang.Math.*;

/**
 * The Population class represents a population of <code>Individual</code>s.
 * <p>
 * An important property of a population is its size, that is the number of individuals it contains.
 * The {@link Population#DEFAULT_SIZE} constant is used as default if the size is not specified.
 * <p>
 * {@link Population.Statistics} collects statistics regarding the population. 
 * Statistics can be accessed by the {@link Population#getStatistics()} method and updated by the
 * {@link Population#updateStatistics(jenes.population.Population.Statistics)}.
 *
 * @param <T> The class of chromosomes individuals are made of.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see     jenes.population.Individual
 * @see     jenes.population.Population.Statistics
 */
public final class Population<T extends Chromosome> implements Iterable<Individual<T>> {

    /** The default population size */
    public static final int DEFAULT_SIZE = 100;
    /** The vector containing the population individuals */
    private ArrayList<Individual<T>> individuals;
    /** The vector containing the population legal individuals */
    private List<Individual<T>> legals;
    /** Population age */
    private int age = 0;
    /** Population pool */
    private Pool<T> pool;
    /** Defualt Pool shared among populations */
    private static Pool<?> defaultPool = null;
    /** Fitness that evaluates tis population */
    private Fitness<T> evaluatedBy;
    /** Array used to sort the population */
    private boolean[] sortingBy;
    /** Notify if the population individuals order is coherent */
    private boolean sorted = false;

    /**
     * Constructs a new empty population
     */
    public Population() {
        this.individuals = new ArrayList<Individual<T>>();
        this.pool = (Pool<T>) defaultPool;
    }

    /**
     * Constructs a new population from the specified one with the same size
     * <p>
     * @param population the population used as sample
     */
    public Population(final Population<T> population) {
        this();
        this.setAs(population);
    }

    /**
     * Constructs a new population with the specified list of individuals
     * <p>
     * @param individuals the list to be used in the population creation
     */
    public Population(final List<Individual<T>> individuals) {
        this.individuals = new ArrayList<Individual<T>>(individuals.size());
        this.pool = (Pool<T>) defaultPool;
        for (Individual<T> sample : individuals) {
            Individual<T> ind = this.newIndividual();
            ind.setAs(sample);
            //Due to sample clone, we grant population setting for individuals
            ind.setPopulation(this);

        }
    }

    /**
     * Constructs a new population by cloning the individual sample.
     * The population assumes the default size.
     * <p>
     * @param sample the individual sample
     */
    public Population(final Individual<T> sample) {
        this(sample, DEFAULT_SIZE);
    }

    /**
     * Constructs a new population by cloning the individual sample
     * and specifying the initial size.
     * <p>
     * @param sample the individual sample
     * @param size the length population
     */
    public Population(final Individual<T> sample, final int size) {
        this.individuals = new ArrayList<Individual<T>>();
        this.pool = (Pool<T>) defaultPool;
        for (int i = 0; i < size; ++i) {
            Individual<T> ind = this.newIndividual();
            ind.setAs(sample);
            //Due to sample clone, we grant population setting for individuals
            ind.setPopulation(this);
        }
    }

    /**
     * Check if the individuals order has been evaluated
     * @return 
     */
    public boolean isSorted() {
        return sorted;
    }

    /**
     * Specify the fitness that has scored population
     * @param fitness 
     */
    public final void setEvaluatedBy(Fitness fitness) {
        this.evaluatedBy = fitness;
    }

    /**
     * Specify the bigger is better
     * @param bib 
     */
    public final void setSortingBy(boolean... bib) {

        if (bib == null) {
            return;
        }

        if (this.sortingBy == null || this.sortingBy.length != bib.length) {
            this.sortingBy = new boolean[bib.length];
        }
        System.arraycopy(bib, 0, this.sortingBy, 0, bib.length);

        this.sorted = false;
    }

    /**
     * Return the genetic algorithm that has evaluated this population. If <tt>null</tt>
     * the population has not been evaluated yet.
     * @return 
     */
    public final Fitness<T> getEvaluatedBy() {
        return this.evaluatedBy;
    }

    /**
     * Return the sort criteria
     * @return 
     */
    public boolean[] getSortedBy() {
        return this.sortingBy;
    }

    /**
     * Returns the {@link Pool} of this population
     * 
     * @return the pool of this population
     */
    public final Pool<T> getPool() {
        return this.pool;
    }

    /**
     * Sets the {@link Pool} for this population. Remove all individuals from old 
     * pool and adds them to new pool
     * 
     * @param pool      the new pool for this population
     */
    public final void setPool(Pool<T> pool) {

        if (pool == this.pool) {
            return;
        }

        if (this.pool != null) {
            this.pool.remove(this.individuals);
        }
        this.pool = pool;
        if (this.pool != null) {
            this.pool.add(this.individuals);
        }
    }

    /**
     * Returns a new {@link Individual}. If this population has a pool, the new individual
     * is taken from the pool. Otherwise a new individual is created.
     * 
     * @return new individual
     */
    private Individual<T> newIndividual() {
        Individual<T> ind = pool != null ? pool.getIndividual() : new Individual<T>();
        ind.setPopulation(this);
        if (pool != null) {
            pool.assign(ind);
        }
        this.individuals.add(ind);
        return ind;
    }

    private void releaseIndividual(Individual<T> ind) {
        ind.setPopulation(null);
        if (pool != null) {
            pool.release(ind);
        }
        this.individuals.remove(ind);
    }

    /**
     * Returns an interator related the individuals of this population.
     */
    public final Iterator<Individual<T>> iterator() {
        return individuals.iterator();
    }

    /**
     * Returns a new {@link Iterator} of {@link Individual} that belong to a specific speciem.
     * 
     * @param speciem   speciem of individuals
     * @return new iterator
     */
    public final Iterator<Individual<T>> iterator(final int speciem) {
        return iterator(new Filter() {

            @Override
            public boolean pass(Individual<?> individual) {
                return individual.getSpeciem() == speciem;
            }
        });
    }

    /**
     * Returns a new {@link Iterator} of {@link Individual} filtered by the {@link Filter} given as argument.
     * 
     * @param filter        Filter object
     * @return new iterator
     */
    public final Iterator<Individual<T>> iterator(Filter filter) {
        return new FilteredIterator(filter);
    }

    /**
     * Returns the age of this population
     *
     * @return the age of this population
     */
    public final int getAge() {
        return this.age;
    }

    /**
     * Sets this population as the specified one.
     * <p>
     * @param pop the model population
     */
    public final void setAs(final Population<T> pop) {

        final int thisSize = this.size();
        final int popSize = pop.size();
        final int minS = thisSize < popSize ? thisSize : popSize;

        for (int i = 0; i < minS; i++) {
            this.individuals.get(i).setAs(pop.individuals.get(i));
        }

        if (thisSize < popSize) {
            //If s1 < s2 adds elements cloning individuals from pop
            for (int i = minS; i < popSize; ++i) {
                Individual<T> ind = this.newIndividual();
                ind.setAs(pop.individuals.get(i));
            }
        } else {
            //If s1 > s2 remove the exceeding elements
            for (int i = thisSize - 1; i >= popSize; --i) {
                Individual<T> ind = this.getIndividual(i);
                this.releaseIndividual(ind);
            }
        }

        this.age = pop.age + 1;

        //we ignore by default the fitness that evaluated the population given as argument
        this.setEvaluatedBy(pop.getEvaluatedBy());
        this.setSortingBy(pop.sortingBy);
        this.sorted = pop.sorted;

        if (this.legals != null) {
            this.legals.clear();
        }
    }

    /**
     * Swaps this population with the speficied one; the age and the individuals will be swapped
     * by this operation.
     *
     * Swap is fast in case of both populations share a common pool, or they don't have one assigned,
     * as not required to move individuals between the pools.
     *
     * @param pop the other population with witch make the individuals swap
     */
    public final void swap(final Population<T> pop) {

        if (this.pool != pop.pool) {
            if (this.pool != null) {
                this.pool.remove(this.individuals);
                this.pool.add(pop.individuals);
            }
            if (pop.pool != null) {
                pop.pool.remove(pop.individuals);
                pop.pool.add(this.individuals);
            }
        }

        final ArrayList<Individual<T>> v = pop.individuals;
        pop.individuals = this.individuals;
        this.individuals = v;

        //update individuals reference to population
        for (Individual i : pop.individuals) {
            i.setPopulation(pop);
        }
        for (Individual i : this.individuals) {
            i.setPopulation(this);
        }

        if (pop.legals != null) {
            pop.legals.clear();
        }

        if (this.legals != null) {
            this.legals.clear();
        }

        final int a = pop.age;
        pop.age = this.age;
        this.age = a;

        final Fitness f = pop.evaluatedBy;
        pop.evaluatedBy = this.evaluatedBy;
        this.evaluatedBy = f;

        final boolean[] sb = pop.sortingBy;
        pop.sortingBy = this.sortingBy;
        this.sortingBy = sb;

        boolean s = pop.sorted;
        pop.sorted = this.sorted;
        this.sorted = s;

    }

    /**
     * Adds an individual at the end of this population.
     * <p>
     * @param individual the individual to add.
     */
    public final void add(final Individual<T> individual) {
        if (individual != null) {
            if (individual.getPopulation() == null) {
                individual.setPopulation(this);

                if (pool != null) {
                    pool.assign(individual);
                }
                this.individuals.add(individual);
            } else {
                Individual i = this.newIndividual();
                i.setAs(individual);
            }

            this.sorted = false;
        }
    }

    /**
     * Adds multiple individuals at the end of this population.
     * <p
     * @param individuals the individuals to be added
     */
    public final void add(final Individual<T>[] individuals) {
        for (Individual<T> i : individuals) {
            if (i != null) {
                this.add(i);
            }
        }
    }

    /**
     * Adds the list of individuals at the end of this population.
     * <p
     * @param individuals the list of individuals to add
     */
    @SuppressWarnings("unchecked")
    public final void add(final List<Individual<T>> individuals) {
        for (Individual<T> i : individuals) {
            if (i != null) {
                this.add(i);
            }
        }
    }

    /**
     * Adds all the individuals contained by the specified population at this
     * population.
     * <p>
     * @param pop the population to add.
     */
    @SuppressWarnings("unchecked")
    public final void add(final Population<T> pop) {
        for (Individual<T> i : pop.individuals) {
            this.add(i);
        }
    }

    /**
     * Resizes the current population.
     *
     * If the new size is bigger than the old size, new individuals will be added cloning
     * random individuals, thus requiring that at least one individual is held.
     *
     * If the new size is smaller than the old size, individuals will be removed from the bottom.
     *
     *
     * @param size the population new size
     */
    public final void resize(int size) {

        final int currsize = individuals.size();
        // if( currsize == 0 )
        //   throw new RuntimeException("The population being resized must have at least 1 individual.");

        final Random rand = Random.getInstance();

        if (currsize < size) {
            for (int i = currsize; i < size; ++i) {
                Individual<T> ind = newIndividual();
                if (currsize > 0) {
                    int k = rand.nextInt(currsize);
                    ind.setAs(this.individuals.get(k));
                }
            }
        } else {
            for (int i = currsize - 1; i >= size; --i) {
                releaseIndividual(this.individuals.get(i));
            }
        }
    }

    /**
     * Resizes the current population.
     *
     * If the new size is bigger than the old size, new individuals will be added cloning
     * the corresponding ones in the population argument
     *
     * If the new size is smaller than the old size, individuals will be removed
     *
     * @param population the Population to get the size from
     */
    public final void resizeAs(final Population<T> population) {

        final int currsize = individuals.size();
        final int size = population.size();

        if (currsize < size) {
            for (int i = currsize; i < size; ++i) {
                Individual<T> ind = newIndividual();
                ind.setAs(population.individuals.get(i));
            }
        } else {
            for (int i = currsize - 1; i >= size; --i) {
                releaseIndividual(this.individuals.get(i));
            }
        }
    }

    /**
     * Removes the specified individual from this population.
     * <p>
     * @param index the individual to remove.
     * @return the removed individual.
     */
    public final Individual<T> remove(final int index) {
        Individual<T> ind = this.getIndividual(index);
        releaseIndividual(ind);
        return ind;
    }

    /**
     * Removes all the individuals from this population.
     */
    public final void clear() {

        final int len = this.individuals.size();

        // Deleting elements from to bottom is faster
        for (int i = len - 1; i >= 0; --i) {
            Individual<T> ind = this.individuals.get(i);
            releaseIndividual(ind);
        }
        this.individuals.clear();

        this.sorted = false;
    }

    /**
     * Trims the capacity of this  Population instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of a Population instance.
     */
    public final void trimToSize() {
        this.individuals.trimToSize();
    }

    /**
     * Reset the individuals belonging to this population
     * 
     * @param list      list of new individuals
     * @param sorted    flag indicating if the new individuals are sorted
     */
    public final void reset(List<Individual> list, boolean sorted) {

        this.individuals.clear();

        for (Individual<?> i : list) {
            if (i.getPopulation() == this) {
                this.individuals.add((Individual<T>) i);
            }
        }

        this.sorted = sorted;
    }

    /**
     * Sets the <code>Individual</code> at the specified position equal
     * to the specified one
     * <p>
     * @param k the position of the individual to be modify
     * @param individual the Individual model
     */
    public final void setIndividualAs(final int k, final Individual<T> individual) {
        this.individuals.get(k).setAs(individual);
    }

    /**
     * Returns the individual at the specified position.
     * <p>
     * @param index the index of the desired individual.
     * @return the desired individual.
     */
    public final Individual<T> getIndividual(final int index) {
        int size = this.individuals.size();
        return index >= 0 && index < size ? this.individuals.get(index) : null;
    }

    /**
     * Replaces the individual at the specified position
     * in this population with the specified one.
     *
     * If the individual passed as argument already belongs to some population, operation is aborted returning null.
     * <p>
     * @deprecated
     * @see #replace(int, jenes.population.Individual)
     * @param individual 
     * @param index index of individual to replace.
     * @return the individual previously at the specified position, or null if the operation cannot have place.
     */
    public final Individual<T> setIndividual(final Individual<T> individual, final int index) {
        if (individual.getPopulation() != null) {
            return null;
        }

        Individual<T> ind = this.individuals.get(index);
        releaseIndividual(ind);
        individual.setPopulation(this);
        if (pool != null) {
            pool.assign(individual);
        }
        return this.individuals.set(index, individual);
    }

    /**
     * Returns an array with all scores of all the individuals held by
     * this population. 
     * 
     * @return an array with the scores of this population individuals.
     */
    public final double[][] getAllScores() {
        final int s = this.individuals.size();

        if (this.evaluatedBy != null) {
            int numOfObjectives = this.evaluatedBy.getNumOfObjectives();
            final double[][] scores = new double[s][numOfObjectives];

            for (int obj = 0; obj < numOfObjectives; obj++) {
                for (int i = 0; i < s; ++i) {
                    scores[i][obj] = this.individuals.get(i).getScore(obj);
                }
            }
            return scores;
        } else {
            final double[][] scores = new double[s][0];
            for (int i = 0; i < s; ++i) {
                scores[i][0] = this.individuals.get(i).getScore(0);
            }
            return scores;
        }
    }

    /**
     * Returns an array with all scores for a specific objective of all the individuals held by
     * this population. If individuals have a objective the index given as argument is ignored 
     * because individuals have one score.
     * 
     * @param objIndex      index of objective
     * 
     * @return an array with the scores, for a specific objective, of this population individuals.
     * If individuals have a objective the index given as argument is ignored and returns the score at position <code>0</code>
     */
    public final double[] getScores(int objIndex) {
        final int s = this.individuals.size();

        if (this.evaluatedBy != null) {
            final double[] scores = new double[s];

            for (int i = 0; i < s; ++i) {
                scores[i] = this.individuals.get(i).getScore(objIndex);
            }
            return scores;
        } else {
            final double[] scores = new double[s];
            for (int i = 0; i < s; ++i) {
                scores[i] = this.individuals.get(i).getScore();
            }
            return scores;
        }
    }

    /**
     * Returns the list of legal individals held by the population.
     * The list returned is recycled, thus the reference to previous 
     * legal individuals could become invalid.
     * @return the list of legal individuals.
     */
    @Deprecated
    public final List<Individual<T>> getAllLegalIndividuals() {
        if (legals == null) {
            legals = new ArrayList<Individual<T>>();
        } else {
            legals.clear();
        }

        for (Individual<T> i : individuals) {
            if (i.isLegal()) {
                legals.add(i);
            }
        }
        return legals;
    }

    /**
     * Checks if the population has legal individuals
     * @return <tt>true</tt> if and only if exist at least one legal individual
     */
    public final boolean hasLegals() {
        for (Individual<T> ind : this.individuals) {
            if (ind.isLegal()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if this population is empty.
     * <p>
     * @return <code>true</code> if this population is empty; <code>false</code> otherwise.
     */
    public final boolean isEmpty() {
        return (this.individuals.isEmpty());
    }

    /**
     * Returns the population size.
     * <p>
     * @return the number of individuals in the population.
     */
    public final int size() {
        return this.individuals.size();
    }

    /**
     * Replaces the individual at the specified position with the specified
     * individual.
     *
     * <i>Note:</i>if a null Individual is passed as argument the individual at index position is removed
     * <p>
     * @param index the position of the individual to replace.
     * @param individual the replacing individual.
     * @return the individual previously held at that position, null if the opration does not succeed.
     */
    public final Individual<T> replace(final int index, final Individual<T> individual) {
        if (individual != null) {

            if (individual.getPopulation() != null) {
                return null;
            }

            Individual<T> ind = this.individuals.get(index);
            releaseIndividual(ind);
            individual.setPopulation(this);
            if (pool != null) {
                pool.assign(individual);
            }
            this.sorted = false;
            return this.individuals.set(index, individual);

        } else {
            Individual<T> ind = this.getIndividual(index);
            releaseIndividual(ind);
            return this.individuals.remove(index);
        }
    }

    /**
     * Returns a string complete representation of this population.
     * <p>
     * @return a new String complete string representing this population.
     */
    public final String toCompleteString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            sb.append("Position=");
            sb.append(i);
            sb.append(" individual=");
            sb.append(this.getIndividual(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a string representation of this population.
     * <p>
     * @return a new String object representing this population.
     */
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer("Age: ");
        sb.append(age);
        sb.append(", Number Individuals: ");
        sb.append(individuals.size());
        return sb.toString();
    }

    /**
     * Sorts the individuals of this population in descending order, from highest to lowest score.
     */
    public final void sort(boolean... bis) {

        if (bis.length == 0) {
            if (this.evaluatedBy != null) {
                this.evaluatedBy.sort(this);
                this.sorted = true;
            } else if (this.sortingBy != null && this.sortingBy.length > 0) {
                Fitness.sort(this.individuals, this.sortingBy);
                this.sorted = true;
            } else {
                throw new IllegalStateException("[Jenes]: Population cannot be sorted");
            }
        } else {
            this.setSortingBy(bis);
            Fitness.sort(this, bis);
            this.sorted = true;
        }

    }

    /**
     * Returns a Statistics of this population
     *
     * @return the statistics of this population
     */
    public final Statistics getStatistics(boolean collectable) {
        Statistics stat = new Statistics(this.sortingBy);

        /*
         * The default groups used by algorithm to find solutions, must be
         * collectables... in this way is preserved backward compatibility with Jenes 1.X
         */
        stat.addGroup(ALL, collectable);
        stat.addGroup(ILLEGALS, collectable);
        stat.addGroup(LEGALS, collectable);
        stat.addGroup(BEST, collectable);

        this.updateStatistics(stat);
        return stat;
    }

    /**
     * Returns a {@link Statistics} of this population 
     * @return statistic of this population
     */
    public final Statistics getStatistics() {
        return this.getStatistics(true);
    }

    /**
     * Return {@link Pareto} of this population
     * 
     * @return pareto of this population
     */
    public final Pareto getPareto() {
        if (this.evaluatedBy == null) {
            return null;
        }

        return new Pareto(this);

    }

    /**
     * Sets the specified statistics according to the statistics of this population
     *
     * @param statistics the statistics to be set
     */
    public final void updateStatistics(Statistics statistics) {
        if (statistics == null) {
            throw new IllegalArgumentException("[Jenes:]The statistics has not to be null");
        }

        statistics.update(this);
    }

    /**
     * Returns the (cloned) list of individuals
     *
     * @return individuals
     */
    public final List<Individual<T>> getIndividuals() {
        return (ArrayList<Individual<T>>) this.individuals.clone();
    }

    /**
     * Invalidates the population by setting as not evaluated its individuals.
     */
    public final void invalidate() {
        for (Individual<T> i : individuals) {
            i.setNotEvaluated();
        }
    }

    /**
     *Returns the default pool
     * 
     * @return default pool
     */
    public static Pool<?> getDefaultPool() {
        return defaultPool;
    }

    /**
     *Sets the default poll
     * 
     * @param pool new pool
     */
    public static void setDefaultPool(Pool<?> pool) {
        defaultPool = pool;
    }

    /**
     * The <code>Population.Statistics</code> class is only responsible for storing statistics
     * about a population. As each population can contains legal and illegal individials, it holds
     * the individuals with the higher and lower fitness (a deep-cloning is maked to store these individuals so they don't
     * change if the source population does),
     * the average and deviation values both regard legal individuals and illegal ones.
     * Esamples of use are showed below.
     * <p><blockquote><pre>
     * Population.Statistics stat = a_population.getStatistics();
     * </pre></blockquote>
     * <p>
     * returns a new statistics object setted according to the specified population state.
     * <p><blockquote><pre>
     * Population.Statistics stat = new Population.Statistics();
     * a_population.updateStatistics(stat);
     * </pre></blockquote>
     * <p>
     * modifies the existing statistics according to the specified population state.
     *
     */
    public static final class Statistics<T extends Chromosome> extends LoggableStatistics {

        /** */
        private double legalScoreAvg;
        private double legalScoreDev;
        private double illegalScoreAvg;
        private double illegalScoreDev;
        /** */
        private Individual<T> legalHighestIndividual;
        private double legalHighestScore;
        private Individual<T> legalLowestIndividual;
        private double legalLowestScore;
        /** */
        private Individual<T> illegalHighestIndividual;
        private double illegalHighestScore;
        private Individual<T> illegalLowestIndividual;
        private double illegalLowestScore;
        private int numOfIndividuals;
        private int numOfLegalIndividuals;
        private int numOfIllegalIndividuals;
        private Map<Filter, Group<T>> groups;
        private boolean[] biggerIsBetter;

        /**
         *
         * Constructs a new Population.Statitics
         *
         * @param bis array of flags. Each flag indicates if maximize or minimize the score 
         * for an objective in a specific position.
         */
        protected Statistics(boolean[] bis) {
            this.biggerIsBetter = bis;
            this.groups = new HashMap<Filter, Group<T>>();
        }

        /**
         * Updates all its information about population
         * @param population 
         */
        public final void update(final Population<T> population) {

            population.sort();

            for (Group<T> g : groups.values()) {
                g.reset(population);
            }

            Set<Filter> filters = groups.keySet();

            for (Individual<T> ind : population) {
                for (Filter f : filters) {
                    if (f.pass(ind)) {
                        Group g = groups.get(f);
                        g.update(ind);
                    }
                }
            }

            Group<T> legals = groups.get(LEGALS);
            Group<T> illegals = groups.get(ILLEGALS);

            int highestLegal = 0;
            int lowestLegal = legals.size() - 1;

            if (this.biggerIsBetter != null && !this.biggerIsBetter[0]) {
                highestLegal = legals.size() - 1;
                lowestLegal = 0;
            }

            this.legalHighestIndividual = legals.get(highestLegal);
            this.legalLowestIndividual = legals.get(lowestLegal);

            this.legalHighestScore = legals.getMax()[0];
            this.legalLowestScore = legals.getMin()[0];
            this.legalScoreAvg = legals.getMean()[0];
            this.legalScoreDev = legals.getStDev()[0];

            int highestIllegal = 0;
            int lowestIllegal = illegals.size() - 1;

            if (this.biggerIsBetter != null && !this.biggerIsBetter[0]) {
                highestIllegal = illegals.size() - 1;
                lowestIllegal = 0;
            }

            this.illegalHighestIndividual = illegals.get(highestIllegal);
            this.illegalLowestIndividual = illegals.get(lowestIllegal);

            this.illegalHighestScore = illegals.getMax()[0];
            this.illegalLowestScore = illegals.getMin()[0];
            this.illegalScoreAvg = illegals.getMean()[0];
            this.illegalScoreDev = illegals.getStDev()[0];

            this.numOfIndividuals = population.size();
            this.numOfLegalIndividuals = legals.getNumOfIndividuals();
            this.numOfIllegalIndividuals = illegals.getNumOfIndividuals();
        }

        private double[] calculateStdDev(Group<T> group) {

            int nObjective = group.get(0).getNumOfObjectives(); //equivalent to ind.getNumberOfObjectives();
            double[] score = new double[nObjective];
            double[] toReturn = new double[nObjective];
            double[] scoreAvg = new double[nObjective];

            for (int obj = 0; obj < nObjective; obj++) {
                for (int i = 0; i < group.size(); i++) {
                    score[obj] = group.get(i).getScore(obj);
                    scoreAvg[obj] = ((scoreAvg[obj] * i) + score[obj]) / (i + 1d);
                }
                for (int i = 0; i < group.size(); i++) {

                    score[obj] = group.get(i).getScore(obj) - scoreAvg[obj];
                    scoreAvg[obj] = ((scoreAvg[obj] * i) + (score[obj] * score[obj])) / (i + 1d);
                }
                toReturn[obj] = Math.sqrt(scoreAvg[obj]);
            }

            return toReturn;
        }

        /**
         * Returns a array that contains a flag for each objective. 
         * Each flag indicates if maximize or minimize the score for an objective in a specific position
         * 
         * @return 
         */
        public boolean[] getBiggerIsBetter() {
            return this.biggerIsBetter;
        }

        /**
         * Returns the number of objective.
         * 
         * @return the number of objective.
         */
        public int getNumOfObjectives() {
            return this.biggerIsBetter.length;
        }

        /**
         * Add a new {@link Group} of individuals definition to the population 
         * accessible in statistics
         * 
         * @param filter the filter to apply in generating a Group of solutions
         * @param collectable <tt>true</tt> if the group have to preserve the 
         * individuals that generate statistics; <tt>false</tt> if only numerical
         * statistics are required; in this case, the {@link Group#iterator()}
         * will return <tt>null</tt> individuals and {@link Group#getCardinality()]
         * will return ever <tt>1</tt>.
         * 
         * @return the {@link Group} generated applying the filter given as argument
         */
        public Group<T> addGroup(Filter filter, boolean collectable) {
            Group<T> g = this.groups.get(filter);
            if (g == null) {
                g = new Group<T>(this, collectable);
                this.groups.put(filter, g);
            }
            return g;
        }

        /**
         * Returns a {@link Group} for a specific {@link Filter} given as argument
         * 
         * @param filter to select a specific group
         * 
         * @return a group
         */
        public Group<T> getGroup(Filter filter) {
            return this.groups.get(filter);
        }

        /**
         * Returns a collection of all {@link Group}
         * 
         * @return all groups
         */
        public Collection<Group<T>> getAllGroups() {
            return this.groups.values();
        }

        /**
         * Returns the average score of the legal individual of this population
         * <p>
         * @return the average score of the legal individual of this population
         */
        @Loggable(label = "LegalScoreAvg")
        @Deprecated
        public final double getLegalScoreAvg() {
            return this.legalScoreAvg;
        }

        /**
         * Returns the deviation score of the legal individual of this population
         * <p>
         * @return the deviation score of the legal individual of this population
         */
        @Loggable(label = "LegalScoreDev")
        @Deprecated
        public final double getLegalScoreDev() {
            return this.legalScoreDev;
        }

        /**
         * Returns the highest legal score of this population
         * <p>
         * @return the highest legal score of this population
         */
        @Loggable(label = "LegalHighestScore")
        @Deprecated
        public final double getLegalHighestScore() {
            return this.legalHighestScore;
        }

        /**
         * Returns the highest legal score individual of this population
         * <p>
         * @return the highest legal score individual of this population
         */
        @Loggable(label = "LegalHighestIndividual")
        @Deprecated
        public final Individual<T> getLegalHighestIndividual() {
            return this.legalHighestIndividual;
        }

        /**
         * Returns the lowest legal score of this population
         * <p>
         * @return the lowest legal score of this population
         */
        @Loggable(label = "LegalLowestScore")
        @Deprecated
        public final double getLegalLowestScore() {
            return this.legalLowestScore;
        }

        /**
         * Returns the lowest legal score individual of this population
         * <p>
         * @return the lowest legal score individual of this population
         */
        @Deprecated
        public final Individual<T> getLegalLowestIndividual() {
            return this.legalLowestIndividual;
        }

        /**
         * Returns the average score of the illegal individual of this population
         * <p>
         * @return the average score of the illegal individual of this population
         */
        @Loggable(label = "IllegalScoreAvg")
        @Deprecated
        public final double getIllegalScoreAvg() {
            return this.illegalScoreAvg;
        }

        /**
         * Returns the deviation score of the illegal individual of this population
         * <p>
         * @return the deviation score of the illegal individual of this population
         */
        @Deprecated
        @Loggable(label = "IllegalScoreDev")
        public final double getIllegalScoreDev() {
            return this.illegalScoreDev;
        }

        /**
         * Returns the highest illegal score of this population
         * <p>
         * @return the highest illegal score of this population
         */
        @Deprecated
        @Loggable(label = "IllegalHighestScore")
        public final double getIllegalHighestScore() {
            return this.illegalHighestScore;
        }

        /**
         * Returns the highest illegal score individual of this population
         * <p>
         * @return the highest illegal score individual of this population
         */
        @Deprecated
        public final Individual<T> getIllegalHighestIndividual() {
            return this.illegalHighestIndividual;
        }

        /**
         * Returns the lowest illegal score of this population
         * <p>
         * @return the lowest illegal score of this population
         */
        @Loggable(label = "IllegalLowestScore")
        @Deprecated
        public final double getIllegalLowestScore() {
            return this.illegalLowestScore;
        }

        /**
         * Returns the lowest illegal score individual of this population
         * <p>
         * @return the lowest illegal score individual of this population
         */
        @Deprecated
        public final Individual<T> getIllegalLowestIndividual() {
            return this.illegalLowestIndividual;
        }

        /**
         * Provides the number of individuals in the population
         *
         * @return the number of individuals
         */
        @Loggable(label = "NumOfIndividuals")
        @Deprecated
        public final int getNumOfIndividuals() {
            return numOfIndividuals;
        }

        /**
         * Returns the number of legal individuals in the population
         *
         * @return the numer of legal individuals
         */
        @Loggable(label = "NumOfLegalIndividuals")
        @Deprecated
        public final int getNumOfLegalIndividuals() {
            return numOfLegalIndividuals;
        }

        /**
         * Provides the number of illegal individuals in the population
         *
         * @return the number of illegal individuals
         */
        @Loggable(label = "NumOfIllegalIndividuals")
        @Deprecated
        public final int getNumOfIllegalIndividuals() {
            return numOfIllegalIndividuals;
        }

        /**
         * Represents a group of individuals and related statistics.
         * 
         * @param <T> extendes {@link Chromosome}
         */
        public static final class Group<T extends Chromosome> extends LoggableStatistics implements Iterable<Individual<T>> {

            private Statistics<T> statistics;
            private boolean collectable;
            private int objectives;
            private List<Individual<T>> individuals;
            private double[] mean;
            private double[] sd;
            private double[] min;
            private double[] max;
            private int cardinality;

            private Group(Statistics<T> statistics, boolean collectable) {
                this.collectable = collectable;
                this.statistics = statistics;
            }

            private void reset(Population<T> population) {

                if (collectable) {
                    //individuals memory reset...
                    if (this.individuals == null) {
                        this.individuals = new ArrayList<Individual<T>>();
                    } else {
                        this.individuals.clear();
                    }
                }

                Individual<T> sample = population.getIndividual(0);
                int m = sample.getNumOfObjectives();

                if (this.objectives != m) {
                    this.mean = new double[m];
                    this.sd = new double[m];
                    this.min = new double[m];
                    this.max = new double[m];

                    this.objectives = m;
                }

                for (int i = 0; i < objectives; ++i) {
                    this.mean[i] = 0;
                    this.sd[i] = 0;
                    this.min[i] = 0;
                    this.max[i] = 0;
                    this.cardinality = 0;
                }

            }

            private void update(Individual<T> ind) {

                double[] scores = ind.getAllScores();

                if (this.cardinality == 0) {

                    int nObjective = this.mean.length; //equivalent to ind.getNumberOfObjectives();
                    for (int h = 0; h < nObjective; ++h) {
                        double x = scores[h];

                        this.mean[h] = x;
                        this.sd[h] = 0;
                        this.min[h] = x;
                        this.max[h] = x;
                    }

                } else {

                    for (int h = 0; h < this.mean.length; ++h) {

                        double x = scores[h];
                        int n = this.cardinality;

                        double mn = this.mean[h];
                        double mn1 = mn + (x - mn) / (n + 1);
                        double sn = pow(this.sd[h], 2);

                        //XXX need formula check
                        double sn1 = (n * sn + pow(n * mn1 - (n + 1) * mn, 2) + (n * pow(mn1, 2) - (n + 1) * pow(mn, 2))) / (n + 1);

                        this.mean[h] = mn1;
                        if (sn1 < 0) {
                            double v = ind.getScore(h) - mean[h];
                            this.sd[h] = sqrt((v * v) / n);

                        } else {
                            this.sd[h] = sqrt(sn1);
                        }

                        if (x < this.min[h]) {
                            this.min[h] = x;
                        }

                        if (x > this.max[h]) {
                            this.max[h] = x;
                        }
                    }


                }

                if (this.collectable) {
                    Individual<T> k = null;
                    if (this.cardinality < this.individuals.size()) {
                        k = this.individuals.get(this.cardinality);
                        k.setAs(ind);
                    } else {
                        k = ind.clone();
                        this.individuals.add(k);
                    }

                }

                this.cardinality++;

            }

            /**
             * Returns {@link Statistics} of group
             * 
             * @return statistics of group
             */
            public Statistics groupOf() {
                return this.statistics;
            }

            /**
             * Returns the number of individuals in gruop
             * 
             * @return number of individual
             */
            public int size() {
                return this.individuals == null ? 0 : this.individuals.size();
            }

            /**
             * Sets if the group must collect individuals 
             * 
             * @param flag <code> true </code> if the group must collect individuals. <code> false </code> otherwise 
             */
            public void setCollectable(boolean flag) {
                this.collectable = flag;
            }

            /**
             * Says if the group collects individuals
             * 
             * @return <code>true</code> if the group collects individuals. <code>false</code> otherwise
             */
            public boolean isCollectable() {
                return this.collectable;
            }

            /**
             * Returns the mean of scores
             * 
             * @return the mean of scores
             */
            @Loggable(label = "Mean")
            public double[] getMean() {
                return this.mean;
            }

            /**
             * Returns the stardard deviation of scores
             * 
             * @return the stardard deviation of scores
             */
            @Loggable(label = "StDev")
            public double[] getStDev() {
                return this.sd;
            }

            /**
             * Returns the minimum score
             * 
             * @return the minimum score
             */
            @Loggable(label = "FirstScore")
            public double[] getMin() {
                return this.min;
            }

            /**
             * Returns the maximum score
             * 
             * @return the maximum score
             */
            @Loggable(label = "Last")
            public double[] getMax() {
                return this.max;
            }

            /**
             * Return the number of individuals that belong to group
             * 
             * @return the number of individual that belong to group
             */
            @Loggable(label = "Cardinality")
            public int getNumOfIndividuals() {
                return this.cardinality;
            }

            /**
             * Returns the list of individuals that belong to group
             * 
             * @return the iterator of individuals
             */
            public Iterator<Individual<T>> iterator() {
                return new Iterator<Individual<T>>() {

                    int i = 0;

                    public boolean hasNext() {
                        return i < cardinality;
                    }

                    public Individual<T> next() {
                        return get(i++);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                };
            }

            /**
             * Return a specific {@link Individual} that belong to group
             * 
             * @param i     index of individual position
             * 
             * @return the individual at position <code>i</code> or <code>null</code> if gruop is not collectable
             */
            public final Individual<T> get(int i) {
                return (this.collectable && i >= 0 && i < cardinality) ? this.individuals.get(i) : null;
            }

            /**
             * Returns scores of all individuals
             * 
             * @return an array that contains scores of all individuals
             */
            @Loggable(label = "Score")
            public double[][] getAllScores() {
                int n = this.cardinality;
                int m = this.objectives;

                double[][] scores = new double[n][];

                for (int i = 0; i < n; ++i) {
                    Individual<T> ind = get(i);
                    scores[i] = ind.getAllScores();
                }

                return scores;
            }

            /**
             * Return {@link Pareto} of this group
             * 
             * @return pareto of this group
             */
            public Pareto getPareto() {
                return new Pareto(this);
            }
        }
    }

    /**
     * This class represents a Pareto front for a multi objective solution
     * 
     * @param <K> extends {@link Chromosome}
     */
    public static class Pareto<K extends Chromosome> {

        private List<List<Individual<K>>> fronts = new ArrayList<List<Individual<K>>>();

        private Pareto(Population<K> pop) {
            build(pop.individuals, pop.sortingBy);
        }

        private Pareto(Statistics.Group g) {
            build(g.individuals, g.statistics.biggerIsBetter);
        }

        /**
         * Builds a new pareto front.
         * 
         * @param list      list of individuals that make the pareto front
         * @param bis       flag that indicates if maximize or minimize the score. A flag for each objective.
         */
        public Pareto(List<Individual<K>> list, boolean... bis) {
            build(list, bis);
        }

        private void build(List<Individual<K>> list, boolean... bis) {
            Fitness.sort(Fitness.SortingMode.CROWDING, list, bis);

            List<Individual<K>> f = null;
            Individual<K> j = null;
            for (Individual i : list) {
                if (j == null || Fitness.dominates(j, i, bis)) {
                    f = new ArrayList<Individual<K>>();
                    this.fronts.add(f);
                }
                f.add(i);
                j = i;
            }
        }

        /**
         * Returns the number of fronts
         * 
         * @return the numbers of fronts 
         */
        public int getNumOfFronts() {
            return fronts.size();
        }

        /**
         * Returns a front at a specific position
         * 
         * @param k     index of front
         * 
         * @return a front at position <code>k</code>
         */
        public List<Individual<K>> getFront(int k) {
            return k >= 0 && k < fronts.size() ? fronts.get(k) : null;
        }
    }

    /**
     * Class that manages access to the filters 
     */
    private class FilteredIterator implements Iterator {

        private int idx = 0;
        private Filter filter;

        private FilteredIterator(Filter filter) {
            this.filter = filter;
        }

        @Override
        public boolean hasNext() {
            int len = individuals.size();
            for (; idx < len; ++idx) {
                Individual<T> ind = individuals.get(idx);
                if (filter.pass(ind)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Individual<T> next() {
            int len = individuals.size();
            for (; idx < len; ++idx) {
                Individual<T> ind = individuals.get(idx);
                if (filter.pass(ind)) {
                    idx++;
                    return ind;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            if (idx < individuals.size()) {
                individuals.remove(idx);
            }
        }
    }

    /**
     * The interface provides a method to create filters on individuals
     */
    public interface Filter {

        /**
         * Check if an {@link Individual} meets a specific criterion.
         * 
         * @param individual
         * @return <code>true</code> if individual meets a specific criterion. <code> false </code> otherwise.
         */
        public boolean pass(Individual<?> individual);
    }
    /**
     * Filter that passes all individuals
     */
    public static final Filter ALL = new Filter() {

        public boolean pass(Individual<?> individual) {
            return true;
        }
    };
    /**
     * Filter that passes all legals individuals
     */
    public static final Filter LEGALS = new Filter() {

        public boolean pass(Individual<?> individual) {
            return individual.isLegal();
        }
    };
    /**
     * Filter that passes all illegals individuals
     */
    public static final Filter ILLEGALS = new Filter() {

        public boolean pass(Individual<?> individual) {
            return !individual.isLegal();
        }
    };
    /**
     * Filter that passes the best individuals
     */
    public static final Filter BEST = new Filter() {

        public boolean pass(Individual<?> individual) {
            return individual.getRank() == 0;
        }
    };
}
