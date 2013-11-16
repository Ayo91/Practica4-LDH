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
import jenes.stage.AbstractStage;
import jenes.stage.operator.Selector;
import jenes.stage.operator.common.RouletteWheelSelector;
import jenes.stage.operator.common.SteadyState;
import jenes.stage.operator.common.TournamentSelector;

/**
 * Steady-state genetic algorithm
 *
 * @version 2.0
 * @since 2.0
 */
public class SteadyStateGA<T extends Chromosome> extends GeneticAlgorithm<T> {

    /** Provides the available selection methods */
    public static enum SelectionMethod {

        ROULETTE, TOURNAMENT
    }
    public static final SelectionMethod DEFAULT_SELECTION_METHOD = SelectionMethod.TOURNAMENT;
    /** The default generation limit */
    public static final int DEFAULT_GENERATION_LIMIT = 5000;

    /**
     * Builds a new SteadyStateGA with <tt>null</tt> population, default generation limit, default replacement rate, 
     * defaul selection rate, default selection method.
     * 
     * @param fitness the fitness to use
     */
    public SteadyStateGA(final Fitness fitness) {
        this(fitness,
                null,
                DEFAULT_GENERATION_LIMIT,
                SteadyState.DEFAULT_REPLACEMENT_RATE,
                SteadyState.DEFAULT_SELECTION_RATE,
                DEFAULT_SELECTION_METHOD);
    }

    /**
     * Builds a new SteadyStateGA with default generation limit, default replacement rate, 
     * defaul selection rate, default selection method.
     * 
     * @param fitness the fitness to use
     * @param pop  the initial population
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop) {
        this(fitness,
                pop,
                DEFAULT_GENERATION_LIMIT,
                SteadyState.DEFAULT_REPLACEMENT_RATE,
                SteadyState.DEFAULT_SELECTION_RATE,
                DEFAULT_SELECTION_METHOD);
    }

    /**
     * Builds a new SteadyStateGA with default replacement rate, 
     * defaul selection rate, default selection method.
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit) {
        this(fitness,
                pop,
                genlimit,
                SteadyState.DEFAULT_REPLACEMENT_RATE,
                SteadyState.DEFAULT_SELECTION_RATE,
                DEFAULT_SELECTION_METHOD);
    }

    /**
     * Builds a new SteadyStateGA with default replacement rate, 
     * defaul selection rate
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     * @param selmethod - selection method
     * @param stages - stages of algorithm
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit, final SelectionMethod selmethod, final AbstractStage<T>... stages) {
        this(fitness,
                pop,
                genlimit,
                SteadyState.DEFAULT_REPLACEMENT_RATE,
                SteadyState.DEFAULT_SELECTION_RATE,
                selmethod,
                stages);
    }

    /**
     * Builds a new SteadyStateGA 
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     * @param rr - replace rate
     * @param sr - selection method
     * @param selmethod - selection method
     * @param stages - stages of algorithm
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit, final int rr, final int sr, final SelectionMethod selmethod, final AbstractStage<T>... stages) {
        super(fitness, pop, genlimit);
        Selector<T> selector;
        switch (selmethod) {
            case ROULETTE:
                selector = new RouletteWheelSelector<T>();
                break;
            case TOURNAMENT:
            default:
                selector = new TournamentSelector<T>(2);
        }

        SteadyState<T> ss = new SteadyState<T>(rr, sr, selector, stages);
        this.body.appendStage(ss);
        this.elitism = 0;
    }

    /**
     * Builds a new SteadyStateGA 
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     * @param selector  - selector
     * @param stages - stages of algorithm
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit, final Selector<T> selector, final AbstractStage<T>... stages) {
        this(fitness, pop, genlimit, new SteadyState<T>(selector, stages));
    }

    /**
     * Builds a new SteadyStateGA 
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     * @param rr - replace rate
     * @param sr - selection method
     * @param selector - selector
     * @param stages - stages of algorithm
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit, final int rr, final int sr, final Selector<T> selector, final AbstractStage<T>... stages) {
        this(fitness, pop, genlimit, new SteadyState<T>(rr, sr, selector, stages));
    }

    /**
     * Builds a new SteadyStateGA 
     * 
     * @param fitness -  the fitness to use
     * @param pop - the initial population
     * @param genlimit - generation limit
     * @param ss - a SteadyState stage
     */
    public SteadyStateGA(final Fitness fitness, final Population<T> pop, final int genlimit, final SteadyState<T> ss) {
        super(fitness, pop, genlimit);
        this.body.appendStage(ss);
        this.elitism = 0;
    }
}
