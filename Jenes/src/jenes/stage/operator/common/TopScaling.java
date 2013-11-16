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

import jenes.population.Fitness;
import jenes.chromosome.Chromosome;
import jenes.population.Population;
import jenes.stage.operator.Scaling;

/**
 * This operator performs fitness top-scaling. Given f, a fraction of individuals. 
 * Best individuals are given a score 1/m*n, where n is the number of individuals.
 * Alternatively, a number q of individuals can be provided, instead of fraction f. 
 * The others are given 0.
 * <p>
 * TopScaling is compatible with multi-objective optimization.
 * 
 * @version 2.0
 * @since 2.0
 */
public class TopScaling<T extends Chromosome> extends Scaling<T> {

    /** The defaulf fraction */
    public static double DEFAULT_FRACTION = 0.4;

    /** The fraction */
    private double fraction;
    
    /** The quantitiy */
    private int quantity;

    /** Creates an instance with default fraction */
    public TopScaling() {
        this( DEFAULT_FRACTION );
    }

    /** Creates an instance with the given fraction 
     * 
     * @param f fraction between 0 (0%) and 1 (i.e. 100%).
     */
    public TopScaling(double f) {
        this.setFraction(f);
    }

    /** Creates an instance with the given quantity of individuals
     * <p>
     * @param q quantity of top individuals 
     */
    public TopScaling(int q) {
        this.setQuantity(q);
    }

    /**
     * Returns the fraction
     * <p>
     * @return 
     */
    public final double getFraction() {
        return this.fraction;
    }

    /**
     * Sets the fraction of individuals to consider. The value provided as input is trimmed between 0 and 1.
     * <p>
     * @param f the fraction 
     */
    public final void setFraction(double f) {
        this.fraction = f;
        if( f > 1 ) this.fraction = 1;
        else if( f < 0 ) this.fraction = 0;
        this.quantity = -1;
    }

    /**
     * Return the number of top individuals to consider
     * 
     * @return quantity
     */    
    public final int getQuantity() {
        return this.quantity;
    }

    /**
     * Sets the number of top individuals to consider.
     * <p>
     * @param q quantity 
     */
    public final void setQuantity(int q) {
        this.quantity = q;
        this.fraction = -1;
    }

    @Override
    public void scale(Population<T> pop) {

        if( this.fitness != null ) {
            this.fitness.sort(pop);
        }
        else {
            Fitness.sort(pop, biggerIsBetter);
        }

        int sz = pop.size();
        int len = sz;
        if( fraction == 0 ) {
            len = quantity > 0 ? quantity : len + quantity;
        }
        else {
            len = (int) fraction * sz;
        }

        if (len > sz) {
            len = sz;
        } else if (len <= 0) {
            len = 1;
        }
        
        double sc = -1.0 / len;
        int m = this.fitness != null ? this.fitness.getNumOfObjectives() : 1;

        for (int i = 0; i < len; ++i) {
            for( int j = 0; j < m; ++j ) {
                pop.getIndividual(i).setScore(sc,j);                
            }
        }

        for (int i = len; i < sz; ++i) {
            for( int j = 0; j < m; ++j ) {
                pop.getIndividual(i).setScore(0,j);                
            }
        }

       
    }

}
