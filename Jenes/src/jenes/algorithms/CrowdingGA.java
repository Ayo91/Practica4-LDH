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
import jenes.stage.operator.Crowder;

/**
 * A genetic algorithm based on crowding
 * 
 * @param <T> extends Chromosome
 *
 * @version 2.0
 * @since 2.0
 */
public class CrowdingGA<T extends Chromosome> extends GeneticAlgorithm<T> {

    /** The default generation limit */
    public static final int DEFAULT_GENERATION_LIMIT = 100;
    protected Crowder crowder;

    /**
     * Default constructor
     * @param fitness the Fitness considered for this algorithm
     * @param crowder the crowder to use
     */
    public CrowdingGA(final Fitness fitness, final Crowder crowder) {
        this(fitness, crowder, null, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Create a new CrowdingGA by setting the initial population and the generation limit
     * @param fitness the Fitness considered for this algorithm
     * @param crowder the crowder to use
     * @param population the initial population to consider
     * @param generations number of generations to evolve
     */
    public CrowdingGA(final Fitness fitness, final Crowder crowder, final Population<T> population, final int generations) {
        super(fitness, population, generations);

        this.crowder = crowder;
        super.getBody().appendStage(crowder);
    }
    
    
    /**
     * A stage is added to the crowder evolution pipeline
     * @param stage 
     */
    @Override
    public void addStage(AbstractStage<T> stage) {
        this.crowder.getBody().appendStage(stage);
    }

    /**
     * Access the current crowder setted for this algorithm
     * @return
     */
    public Crowder getCrowder() {
        return this.crowder;
    }
}
