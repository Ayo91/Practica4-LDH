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
import jenes.stage.operator.Scaling;

/**
 * This operator implements the rank scaling. Rank scaling re-assigns the rank as fitness value to indiduals.  
 * The operator is compatible with multi-objective optimization. In that case, rank is in turn computed for each each objective.
 * 
 * @version 2.0
 * @since 2.0
 */
public class RankScaling<T extends Chromosome> extends Scaling<T> {

    @Override
    public void scale(Population<T> pop) {

        boolean[] bib = this.fitness != null ? this.fitness.getBiggerIsBetter() : null;
        int m = this.fitness != null ? this.fitness.getNumOfObjectives() : 1;

        List<Individual<T>> inds = pop.getIndividuals();
        int len = pop.size();                
        
        for( int h = 0; h < m; ++h ) {  
            boolean b = bib != null ? bib[h] : this.biggerIsBetter;
            Fitness.partialsort(inds, h, b);
            
            int rank = 1;
            for (int i = 0; i < len; ++i) {
                
                double lsc = inds.get(i).getScore(h);
                int j = 0;
                for( j = i+1; j < len; ++j ) {
                    if( inds.get(j).getScore(h) != lsc ) {
                        break;
                    }
                }
                
                for( int l = i; l < j; ++l ) {
                    inds.get(j).setScore(rank);
                }
                
                rank++;
            }
        }
        
       
    }

}
