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

import java.util.List;
import jenes.population.Fitness;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.operator.Selector;

/**
 * A classic tournament selection operator. A number of attempts is specified at the instantiation time; it
 * represents the number of iterations the selection procedure is repeated.
 * Individuals are randomly choosen and the one with the best fitness will be selected.
 * <p>
 * The {@link #preSelect(Population)} method is useless, so there is an overriding implementation in this selection algorithm.
 * <p>
 * @param <T> The class of chromosomes to work with.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see Individual
 * @see Population
 */
public class TournamentSelector<T extends Chromosome> extends Selector<T> {
    
    /** the number of tournament attemps */
    private int attempts;

    /**
     * Constructs a new tournament selection object
     * <p>
     * @param attempts the number of tournament attempts
     */
    public TournamentSelector(int attempts) {
        if (attempts <= 0) {
            throw new IllegalArgumentException("The number of attempts has to be greater than 0");
        }

        this.attempts = attempts;
    }
    
    /**
     * Returns the number of attempts of this tournament selection
     * <p>
     * @return the number of attempts
     */
    public int getAttempts() {
        return this.attempts;
    }

    @Override
    protected Individual<T> select(List<Individual<T>> list) {
        
        int size = list.size();
        boolean bib[] = this.fitness != null ? this.fitness.getBiggerIsBetter(): new boolean[] {this.biggerIsBetter};

        int h = random.nextInt(0,size);
        Individual<T> candidate = list.get(h);
        
        for(int i=0; i < attempts; ++i){
            int k = random.nextInt(0,size);
            Individual<T> challenger = list.get(k);
            
            if( candidate != challenger ) {
                boolean better =  Fitness.dominates(challenger, candidate, bib);
                if( better  )
                    candidate = challenger;                
            }
        }
        
        return candidate;
    }
    
}
