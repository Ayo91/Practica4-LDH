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
package jenes.algorithms;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;
import jenes.stage.operator.Crossover;
import jenes.stage.operator.Mutator;
import jenes.stage.operator.Selector;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.RouletteWheelSelector;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.stage.operator.common.TwoPointsCrossover;

/**
 * A facade providing a simple interface to GeneticAlgorithm.
 * <p>
 * <code>SimpleGA</code> implements a three stages genetic algorithm made of
 * <ul>
 * <li> Selector: {@link TournamentSelector} (default) or {@link RouletteWheelSelector}
 * <li> Crossover: {@link OnePointCrossover} (default) or {@link TwoPointsCrossover}  
 * <li> Mutator: {@link SimpleMutator}
 * </ul>
 * The class provides a set of constructors by which to instantiate a <code>SimpleGA</code> algorithm.
 * <p>
 * Constructors allows to decide which selection method (i.e. Tournament or Roulette Wheel)
 * or crossover method (i.e. One Point or Two Points) to adopt. Also crossover and mutation probability
 * can be specified at constraction time.
 * <p>
 * <code>SimpleGA</code> is a <code>GeneticAlgorithm</code> subclass. Thus, it is possible to use all inheritated
 * methods and properties. For example,
 * <p><blockquote><pre>
 *  sga.setElitism(10);
 *  sga.setMutationRate(0.2);
 *  sga.setBiggerIsBetter(false);
 *  sga.evolve();
 * </pre></blockquote>
 * <p>
 * Among the available method, there are those able to alter the algorithm's body. Although
 * this is possible, we discourage from using them as this would result in lesser comphrensible code. Instead we suggest
 * to directly subclass <code>GeneticAlgorithm</code>.
 * <p>
 * 
 * @param <T> extends Chromosome
 * 
 * @version 2.0
 * @since 1.0
 */
public class SimpleGA<T extends Chromosome> extends GeneticAlgorithm<T> {

    /** Provides the available selection methods */
    public static enum SelectionMethod {

        ROULETTE, TOURNAMENT
    }

    /** Provides the available crossover methods */
    public static enum CrossoverMethod {

        SINGLEPOINT, TWOPOINTS
    }

    /** The default generation limit */
    public static final int DEFAULT_GENERATION_LIMIT = 100;

    /** The default crossover probability */
    public static final double DEFAULT_CROSSOVER_PROBABILITY = 0.8;

    /** The default mutation probability */
    public static final double DEFAULT_MUTATION_PROBABILITY = 0.02;

    /** The default elitism factor */
    public static final int DEFAULT_ELITISM = 1;

    /** The default selection method */
    public static final SelectionMethod DEFAULT_SELECTION_METHOD = SelectionMethod.TOURNAMENT;

    /** The default crossover method */
    public static final CrossoverMethod DEFAULT_CROSSOVER_METHOD = CrossoverMethod.SINGLEPOINT;

    /** The default elitism strategy */
    public static final ElitismStrategy DEFAULT_ELITISM_STRATEGY = ElitismStrategy.RANDOM;

    /* The algorithm operators */
    private Selector<T> selector;
    private Crossover<T> crossover_op;
    private Mutator<T> mutator;

    /**
     * Builds a new SimpleGa with no initial population, the default generation limit, crossover and mutation probability,
     * elitism, selection and crossover methods, and elitism strategy.
     */
    @Deprecated
    public SimpleGA() {
        this(   null,
                null,
                DEFAULT_GENERATION_LIMIT,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default generation limit, crossover and mutation probability,
     * elitism, selection and crossover methods, and elitism strategy.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population) {
        this(   fitness,
                population,
                DEFAULT_GENERATION_LIMIT,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default crossover and mutation probability,
     * elitism, selection and crossover methods, and elitism strategy.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     * @param generations the generation limit
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population, final int generations) {
        this(   fitness,
                population,
                generations,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default
     * elitism, selection and crossover methods, and elitism strategy.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population, final int generations, final double crossover, final double mutation) {
        this(   fitness,
                population,
                generations,
                crossover,
                mutation,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default selection and crossover methods, and elitism strategy.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism) {
        this(   fitness,
                population,
                generations,
                crossover,
                mutation,
                elitism,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default elitism strategy.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     * @param selmethod the selector method to use
     * @param crossmethod the crossover method to use
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism, final SelectionMethod selmethod, final CrossoverMethod crossmethod) {
        this(   fitness,
                population,
                generations,
                crossover,
                mutation,
                elitism,
                selmethod,
                crossmethod,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa.
     * 
     * @param fitness the fitness to use
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     * @param selmethod the selector method to use
     * @param crossmethod the crossover method to use
     * @param es the elitism strategy to use
     */
    public SimpleGA(final Fitness<T> fitness, final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism, final SelectionMethod selmethod, final CrossoverMethod crossmethod, ElitismStrategy es) {
        super(fitness, population, generations);

        selector =
                selmethod == SelectionMethod.TOURNAMENT ? new TournamentSelector<T>(2) : new RouletteWheelSelector<T>();

        crossover_op =
                crossmethod == CrossoverMethod.SINGLEPOINT ? new OnePointCrossover<T>(crossover) : new TwoPointsCrossover<T>(crossover);

        mutator = new SimpleMutator<T>(mutation);

        this.elitism = elitism;
        this.addStage(selector);
        this.addStage(crossover_op);
        this.addStage(mutator);
    }

    /**
     * Builds a new SimpleGa with the default generation limit, crossover and mutation probability,
     * elitism, selection and crossover methods, and elitism strategy.
     * <p>
     * @param population the initial population
     */
    @Deprecated
    public SimpleGA(final Population<T> population) {
        this(   null,
                population,
                DEFAULT_GENERATION_LIMIT,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default crossover and mutation probability,
     * elitism, selection and crossover methods, and elitism strategy.
     * <p>
     * @param population the initial population
     * @param generations the generation limit
     */
    public SimpleGA(final Population<T> population, final int generations) {
        this(   null,
                population,
                generations,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default
     * elitism, selection and crossover methods, and elitism strategy.
     * <p>
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     */
    @Deprecated
    public SimpleGA(final Population<T> population, final int generations, final double crossover, final double mutation) {
        this(   null,
                population,
                generations,
                crossover,
                mutation,
                DEFAULT_ELITISM,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default selection and crossover methods, and elitism strategy.
     * <p>
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     */
    @Deprecated
    public SimpleGA(final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism) {
        this(   null,
                population,
                generations,
                crossover,
                mutation,
                elitism,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa with the default elitism strategy.
     * <p>
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     * @param selmethod the selector method to use
     * @param crossmethod the crossover method to use
     */
    @Deprecated
    public SimpleGA(final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism, final SelectionMethod selmethod, final CrossoverMethod crossmethod) {
        this(   null,
                population,
                generations,
                crossover,
                mutation,
                elitism,
                selmethod,
                crossmethod,
                DEFAULT_ELITISM_STRATEGY );
    }

    /**
     * Builds a new SimpleGa.
     * <p>
     * @param population the initial population
     * @param generations the generation limit
     * @param crossover the crossover probability
     * @param mutation the mutation probability
     * @param elitism the elisitm factor
     * @param selmethod the selector method to use
     * @param crossmethod the crossover method to use
     * @param es the elitism strategy to use
     */
    @Deprecated
    public SimpleGA(final Population<T> population, final int generations, final double crossover, final double mutation, final int elitism, final SelectionMethod selmethod, final CrossoverMethod crossmethod, ElitismStrategy es) {
        super(population, generations);

        selector =
                selmethod == SelectionMethod.TOURNAMENT ? new TournamentSelector<T>(2) : new RouletteWheelSelector<T>();

        crossover_op =
                crossmethod == CrossoverMethod.SINGLEPOINT ? new OnePointCrossover<T>(crossover) : new TwoPointsCrossover<T>(crossover);

        mutator = new SimpleMutator<T>(mutation);

        this.elitism = elitism;
        this.addStage(selector);
        this.addStage(crossover_op);
        this.addStage(mutator);
    }

    /**
     * Returns the selector used by this genetic algorithm
     *
     * @return the selection operator
     */
    public final Selector<T> getSelector() {
        return this.selector;
    }

    /**
     * Returns the maximum rate of illegal individuals.
     *
     * @return the maximum rate of illegal individuals
     */
    public final double getMaxIllegalRate() {
        return selector.getMaxIllegalRate();
    }

    /**
     * Sets the max rate of illegal individuals.
     * This prevents that population will be dominated by invalid individuals.
     * This is the case of constrained problems where valid solutions are
     * sparse within the search space.
     *
     * @param rate the new maximum rate of illegal individuals
     */
    public void setMaxIllegalRate(final double rate) {
        selector.setMaxIllegalRate(rate);
    }

    /**
     * Returns the crossover used by this genetic algorithm.
     *
     * @return the crossover operator
     */
    public final Crossover<T> getCrossover() {
        return this.crossover_op;
    }

    /**
     * Returns the crossover probability.
     *
     * @return the crossover probability
     */
    public final double getCrossoverProbability() {
        return this.crossover_op.getProbability();
    }

    /**
     * Sets the crossover probability.
     *
     * @param p the new crossover probability
     */
    public void setCrossoverProbability(final double p) {
        this.crossover_op.setProbability(p);
    }

    /**
     * Returns the mutator used by this genetic algorithm.
     *
     * @return the mutation operator
     */
    public final Mutator<T> getMutator() {
        return this.mutator;
    }

    /**
     * Returns the mutation probability.
     *
     * @return the mutation probability
     */
    public final double getMutationProbability() {
        return this.mutator.getProbability();
    }

    /**
     * Sets the mutation probability.
     *
     * @param p the new mutation probability
     */
    public void setMutationProbability(final double p) {
        this.mutator.setProbability(p);
    }
}
