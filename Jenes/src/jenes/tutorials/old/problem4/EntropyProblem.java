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
package jenes.tutorials.old.problem4;

import jenes.GeneticAlgorithm;
import jenes.algorithms.SimpleGA;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.tutorials.utils.Utils;

/**
 * Tutorial showing how to set-up a minimization problem.
 * The problem is to find a vector whose entroy, after normalization, is minimal.
 *
 * @version 1.0
 * @since 1.0
 */
public class EntropyProblem {
    
    private static int POPULATION_SIZE   = 100;
    private static int CHROMOSOME_LENGTH = 5;
    private static int GENERATION_LIMIT  = 100;
    
    public static void main(String[] args) {
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 4:");
        System.out.println("Find the probability distribution that maximizes the Shannon's entropy.");
        System.out.println();
        
        Individual<DoubleChromosome> sample = new Individual<DoubleChromosome>(new DoubleChromosome(CHROMOSOME_LENGTH,0,1));
        Population<DoubleChromosome> pop = new Population<DoubleChromosome>(sample, POPULATION_SIZE);
        
        SimpleGA<DoubleChromosome> ga = new SimpleGA<DoubleChromosome>(pop, GENERATION_LIMIT) {
            @Override
            public void evaluateIndividual(Individual<DoubleChromosome> individual) {
                DoubleChromosome chrom = individual.getChromosome();
                
                int length = chrom.length();
                
                double sum = 0.0;
                for(int i=0; i<length; ++i) {
                    sum += chrom.getValue(i);
                }
                
                double entropy = 0.0;
                for(int i=0; i<length; ++i) {
                    double pxi = chrom.getValue(i)/sum;
                    chrom.setValue(i, pxi);
                    
                    entropy -= (pxi * Math.log(pxi) / Math.log(2));
                }
                
                individual.setScore(entropy);
            }
            
        };
    	ga.setBiggerIsBetter(true);
        ga.evolve();
        
        Statistics stats = ga.getCurrentPopulation().getStatistics();
        GeneticAlgorithm.Statistics algostats = ga.getStatistics();
        
        System.out.println("Solution: ");
        System.out.println(stats.getLegalHighestIndividual().getChromosome() );
        System.out.println(stats.getLegalHighestIndividual());
        System.out.format("found in %d ms.\n", algostats.getExecutionTime() );
        System.out.println();
        
        Utils.printStatistics(stats);
    }
}
