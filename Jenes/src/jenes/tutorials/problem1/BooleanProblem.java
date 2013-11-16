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
package jenes.tutorials.problem1;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.BooleanChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;

/**
 * Tutorial implementing a basic genetic algorithm.
 * The problem consists in finding a vector full of zeros or ones.
 * 
 * @version 2.0
 * @since 1.0
 */
public class BooleanProblem {
    
    private static int POPULATION_SIZE=50;
    private static int CHROMOSOME_LENGTH=100;
    private static int GENERATION_LIMIT=1000;
    
    public static void main(String[] args) throws Exception {
        
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 1:");
        System.out.println("This algorithm aims to find a vector of booleans that is entirely true or false.");
        System.out.println();
        
        // Random.getInstance().setStandardSeed();
        
        Individual<BooleanChromosome> sample = new Individual<BooleanChromosome>(new BooleanChromosome(CHROMOSOME_LENGTH));
        Population<BooleanChromosome> pop = new Population<BooleanChromosome>(sample, POPULATION_SIZE);
        
        Fitness<BooleanChromosome> fit = new Fitness<BooleanChromosome>(false) {

            @Override
            public void evaluate(Individual<BooleanChromosome> individual) {
                BooleanChromosome chrom = individual.getChromosome();
                int count = 0;
                int length=chrom.length();
                for(int i=0;i<length;i++)
                    if(chrom.getValue(i))
                        count++;
                
                individual.setScore(count);
            }
            
        };
        
        GeneticAlgorithm<BooleanChromosome> ga = new GeneticAlgorithm<BooleanChromosome>
                (fit, pop, GENERATION_LIMIT);
        
        AbstractStage<BooleanChromosome> selection = new TournamentSelector<BooleanChromosome>(3);
        AbstractStage<BooleanChromosome> crossover = new OnePointCrossover<BooleanChromosome>(0.8);
        AbstractStage<BooleanChromosome> mutation = new SimpleMutator<BooleanChromosome>(0.02);
        ga.addStage(selection);
        ga.addStage(crossover);
        ga.addStage(mutation);
        
        ga.setElitism(1);
        
        ga.evolve();
        
        Population.Statistics stats = ga.getCurrentPopulation().getStatistics();
        GeneticAlgorithm.Statistics algostats = ga.getStatistics();
        
        System.out.println("Objective: " + (fit.getBiggerIsBetter()[0] ? "Max! (All true)" : "Min! (None true)"));
        System.out.println();
        
        Group legals = stats.getGroup(Population.LEGALS);
        
        Individual solution = legals.get(0);
                
        System.out.println("Solution: ");
        System.out.println( solution );
        System.out.format("found in %d ms.\n", algostats.getExecutionTime() );
        System.out.println();
        
        Utils.printStatistics(stats);
    }
    
}
