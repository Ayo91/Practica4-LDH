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

import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;
import jenes.stage.operator.Crossover;

/**
 * IntermediateCrossover performs a convex combination of DoubleChromosome by a coefficient <code>code</code>.
 * 
 * @param <T> extends {@link DoubleChromosome}
 */
public class IntermediateCrossover<T extends DoubleChromosome> extends Crossover<T> {

    /** Combination ratio */
    protected double ratio;

    /**
     * Creates IntermediateCrossover
     * 
     * @param p crossover probability 
     */
    public IntermediateCrossover(double p) {
        super(p);
        this.ratio = Double.NaN;
    }

    /**
     * Creates IntermediateCrossover
     * 
     * @param p crossover probability
     * @param r ratio
     */
    public IntermediateCrossover(double p, double r) {
        super(p);
        this.setRatio(r);
    }

    /**
     * Returns ratio
     * @return 
     */    
    public final double getRatio() {
        return this.ratio;
    }

    /**
     * Sets the ratio
     * @param r     a value between 0 and 1; if r = NaN, the ratio is set randomly at each iteration
     */
    public void setRatio(double r) {

        if (Double.isNaN(r)) {
            this.setRandom();
        } else {
            if (r < 0) {
                r = 0;
            } else if (r > 1) {
                r = 1;
            }
            this.ratio = r;
        }
    }

    /**
     * Says if the ratio is random
     * @return 
     */
    public final boolean isRandom() {
        return Double.isNaN(ratio);
    }

    /**
     * Sets the ratio random
     */
    public final void setRandom() {
        this.ratio = Double.NaN;
    }

    @Override
    public int spread() {
        return 2;
    }

    @Override
    protected void cross(Individual<T>[] offsprings) {

        double r = this.isRandom() ? this.random.nextDouble() : this.ratio;

        DoubleChromosome c0 = offsprings[0].getChromosome();
        DoubleChromosome c1 = offsprings[1].getChromosome();

        c0.average(c1, r);
    }
}
