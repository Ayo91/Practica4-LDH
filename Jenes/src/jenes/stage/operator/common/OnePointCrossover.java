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
import jenes.stage.operator.Crossover;

/**
 * A one-point crossover operator. It performes the simplest crossover algorithm according to a specified probability.
 * It represents a 2-parents and 2-children crossover.
 * A cross-point is randomly chosen and the genes are crossed.
 * 
 * @param <T> The class of chromosomes to work with.
 *
 * @version 2.0
 * @since 1.0
 * 
 * @see Individual
 * @see Chromosome
 */
public class OnePointCrossover<T extends Chromosome> extends Crossover<T>{
    
    /**
     * Constructs a new one-point crossover with the specified probability
     *
     * @param probability the crossover probability
     */
    public OnePointCrossover(double probability) {
        super(probability);
    }

    @Override
    public int spread() {
        return 2;
    }
    
    protected void cross(Individual<T> offsprings[]) {     
    	
        T chromC1 = offsprings[0].getChromosome();
        T chromC2 = offsprings[1].getChromosome();
        
        int s1 = chromC1.length();
        int s2 = chromC2.length();
        
        int min = (s1 < s2) ? s1 : s2;
        
        int pos = random.nextInt(0,min);
        chromC1.cross(chromC2,pos);
    }
    
}
