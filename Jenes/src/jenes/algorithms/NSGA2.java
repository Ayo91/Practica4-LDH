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

import java.util.ArrayList;
import java.util.List;
import jenes.population.Fitness;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.operator.Crowder;
import jenes.stage.operator.common.TournamentSelector;

/**
 * Multi-objective genetic algorithm as proposed by Deb.
 * 
 * @version 2.0
 * @since 2.0
 */
public class NSGA2<T extends Chromosome> extends CrowdingGA<T> {

    /** The default generation limit */
    public static final int DEFAULT_GENERATION_LIMIT = 100;

    /** The default number of attempts to {@link TournamentSelector} */
    public static final int DEFAULT_SELECTION_TRIALS = 3;

    /**
     * Default constructor
     * @param fitness the fitness function to adopt in evolving this algorithm
     */
    public NSGA2(Fitness fitness) {
        this(   fitness,
                null,
                DEFAULT_GENERATION_LIMIT,
                DEFAULT_SELECTION_TRIALS
             );
    }

    /**
     * Generates a new NSGA2 instance
     * <p>
     * @param fitness the fitness function to adopt in evolving this algorithm
     * @param population the initial population
     */
    public NSGA2(final Fitness fitness, final Population<T> population) {
        this(   fitness,
                population,
                DEFAULT_GENERATION_LIMIT,
                DEFAULT_SELECTION_TRIALS
             );
    }

    /**
     * Generates a new NSGA2 instance
     * <p>
     * @param fitness the fitness function to adopt in evolving this algorithm
     * @param population the initial population
     * @param generations the generation limit
     */
    public NSGA2(final Fitness fitness, final Population<T> population, final int generations) {
        this(   fitness,
                population,
                generations,
                DEFAULT_SELECTION_TRIALS
             );
    }


    /**
     * Generates a new NSGA2 instance
     * <p>
     * @param fitness the fitness function to adopt in evolving this algorithm
     * @param population the initial population
     * @param generations the generation limit
     * @param trials number of attempts in tournament selector
     * @see TournamentSelector
     */
    public NSGA2(final Fitness fitness, final Population<T> population, final int generations, final int trials) {
        super(fitness, new DominanceCrowder<T>(), population, generations);
        this.crowder.getBody().appendStage( new TournamentSelector<T>(trials) );
    }


    /**
     * Dominance crowder used by NSGA2
     * @param <C> 
     */
    private static class DominanceCrowder<C extends Chromosome> extends Crowder<C> {

        private List<Individual<C>> jlist = new ArrayList<Individual<C>>();
        
        {
            this.elitist = true;
        }

        @Override
        protected void preselect(Population<C> in, Population<C> out) {
            out.setAs(in);
        }

        @Override
        protected void replace(Population<C> initial, Population<C> preselected, Population<C> evolved, Population<C> out) {

            jlist.clear();
            
            for( Individual<C> i : initial ) {
                jlist.add(i);
            }
            
            for( Individual<C> j : evolved ) {
                jlist.add(j);
            }
            
            this.getFitness().sort( Fitness.SortingMode.CROWDING, jlist );
                                    
            int size = evolved.size();

            out.resize(size);
            for( int i = 0; i < size; ++i ) {
                out.setIndividualAs(i, (Individual<C>) jlist.get(i) );
            }
        }

    }
     
}