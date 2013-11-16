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
import jenes.population.Population;
import jenes.stage.operator.Scaling;

/**
 * Implements proportional scaling of individual fitness, so that each score is proportionally rescaled within the range [min,max] of each objective.
 * Score is 0, when fitness is min, and 1 when fitness is max.
 * 
 * @version 2.0
 * @since 2.0
 */
public class ProportionalScaling<T extends Chromosome> extends Scaling<T> {

    @Override
    public void scale(Population<T> pop) {

        int m = this.fitness != null ? this.fitness.getNumOfObjectives() : 1;
        
        for (int h = 0; h < m; ++h) {
            double max = Double.POSITIVE_INFINITY;
            double min = Double.NEGATIVE_INFINITY;

            int len = pop.size();
            for (int i = 0; i < len; ++i) {
                double s = pop.getIndividual(i).getScore(h);
                if (s < min) {
                    min = s;
                } else if (s > max) {
                    max = s;
                }
            }

            double range = max - min;

            for (int i = 0; i < len; ++i) {
                double s = (pop.getIndividual(i).getScore(h) - min) / range;
                pop.getIndividual(i).setScore(s);
            }
        }
        
    }

}
