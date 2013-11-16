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
package jenes.stage.operator.common;

import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.operator.Mutator;

/**
 * A simple mutation operator. It performes the simplest mutation algorithm according to a specified probability.
 * A chromosome position is randomly chosen and its gene randomized.
 * <p>
 * @param <T> The class of chromosomes to work with.
 *
 * @version 1.2
 * @since 1.0
 * 
 * @see Individual
 * @see Population
 */
public class SimpleMutator<T extends Chromosome> extends Mutator<T> {

    /**
     * Constructs a new simple mutator with the specified probability
     * 
     * @param probability the mutation probability
     */
    public SimpleMutator(double probability) {
        super(probability);
    }

    protected void mutate(Individual<T> ind) {
        Chromosome c = ind.getChromosome();
        int size = c.length();
        int pos = random.nextInt(0, size);
        c.randomize(pos);
    }
}
