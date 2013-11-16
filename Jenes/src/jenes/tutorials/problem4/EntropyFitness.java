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
package jenes.tutorials.problem4;

import jenes.population.Fitness;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;

/**
 * Tutorial showing how to set-up a minimization problem.
 * The problem is to find a vector whose entroy, after normalization, is minimal.
 *
 * @version 2.0
 * @since 1.0
 */
public class EntropyFitness extends Fitness<DoubleChromosome> {

    public static EntropyFitness MAX = new EntropyFitness(true);
    public static EntropyFitness MIN = new EntropyFitness(false);

    private EntropyFitness(boolean maximize) {
        super(maximize);
    }

    @Override
    public void evaluate(Individual<DoubleChromosome> individual) {
        DoubleChromosome chrom = individual.getChromosome();

        int length = chrom.length();

        double sum = 0.0;
        for (int i = 0; i < length; ++i) {
            sum += chrom.getValue(i);
        }

        double entropy = 0.0;
        for (int i = 0; i < length; ++i) {
            double pxi = chrom.getValue(i) / sum;
            chrom.setValue(i, pxi);

            entropy -= (pxi * Math.log(pxi) / Math.log(2));
        }

        individual.setScore(entropy);
    }
}
