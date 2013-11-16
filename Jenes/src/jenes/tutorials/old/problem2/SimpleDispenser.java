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
package jenes.tutorials.old.problem2;

import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.ExclusiveDispenser;

/**
 * Tutorial showing how to extend <code>GeneticAlgorithm</code> and how to use
 * the flexible and configurable breeding structure in Jenes.
 * The problem consists in searching a pattern of integers with a given precision.
 * Solutions flow through two different crossovers in parallel. Some are processed by
 * a single point crossover, the other by a double point crossover.
 * After solutions are mutated.
 *
 * This class implements the strategy for dispensing solutions in the two branches.
 * Odd solutions goes to the first, even to the second.
 *
 * @version 1.0
 *
 * @since 1.0
 */
public class SimpleDispenser<T extends Chromosome> extends ExclusiveDispenser<T> {
    
    private int count;
    
    public SimpleDispenser(int span) {
        super(span);
    }
    
    public void preDistribute(Population<T> population){
        this.count = 0;
    }
    
    @Override
    public int distribute(Individual<T> ind) {
        return count++ % span;
    }
    
}
