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
package jenes.tutorials.problem3;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.utils.Random;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;

/**
 * Tutorial showing how to implement problem specific operators.
 * The problem faced in this example is the well known Tavel Salesman Problem (TSP)
 *
 * This class implements the algorithm.
 *
 * @version 2.0
 * @since 1.0
 */
public class TSPGA extends GeneticAlgorithm<IntegerChromosome> {
    
    private double[][] matrix;
    private TSPFitness fitness;
    
    public TSPGA(double[][] matrix, Population<IntegerChromosome> pop, int genlimit) {
        super(null, pop, genlimit);
        this.matrix = matrix;
        fitness = new TSPFitness();
        this.setFitness(fitness);
    }
        
    @Override
    protected void randomizeIndividual(Individual<IntegerChromosome> individual) {
        Random rand = Random.getInstance();
        int len = individual.getChromosomeLength();
        for( int i = 0; i < 100; ++i ) {
            int j = rand.nextInt(len);
            int k = rand.nextInt(len);
            individual.getChromosome().swap(j, k);
        }
    }

    public class TSPFitness extends Fitness<IntegerChromosome> {

        public TSPFitness() {
            super(false);
        }

        @Override
        public void evaluate(Individual<IntegerChromosome> individual) {
            IntegerChromosome chrom = individual.getChromosome();
            double count = 0;
            int size = chrom.length();
            for (int i = 0; i < size - 1; i++) {
                int val1 = chrom.getValue(i);
                int val2 = chrom.getValue(i + 1);
                count += matrix[val1][val2];
            }
            count += matrix[size - 1][0];

            individual.setScore(count);
        }
    }
    
    
}
