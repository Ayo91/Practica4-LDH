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

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * A stage wrapping an algoithm in order to make it part of a wider algorithm.
 *
 * <p>
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 2.0
 * @since 1.3
 *
 */
public class AlgorithmStage<T extends Chromosome> extends AbstractStage<T> {

    /** The algorithm wrapped */
    private GeneticAlgorithm<T> algorithm;

    /**
     * Builds a wrapper stage for the specified algorithm.
     *
     * @param algorithm the algorithm to wrap.
     */
    public AlgorithmStage(GeneticAlgorithm<T> algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Returns the wrapped algorithm
     *
     * @return the algorithm
     */
    public final GeneticAlgorithm<T> getAlgorithm() {
        return this.algorithm;
    }

    @Deprecated
    @Override
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        this.algorithm.setBiggerIsBetter(flag);
    }

    @Override
    public void setFitness(Fitness fit, boolean recursively) {
        this.algorithm.setFitness(fit);
    }

    @Override
    public void process(Population<T> in, Population<T> out) throws StageException {
        algorithm.evolve(in);
        out.setAs(algorithm.getCurrentPopulation());
    }
}
