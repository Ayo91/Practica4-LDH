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
package jenes.tutorials.old.problem2;

import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.utils.Random;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.stage.AbstractStage;
import jenes.stage.Parallel;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.stage.operator.common.TwoPointsCrossover;
import jenes.tutorials.utils.Utils;


/**
 * Tutorial showing how to extend <code>GeneticAlgorithm</code> and how to use
 * the flexible and configurable breeding structure in Jenes.
 * The problem consists in searching a pattern of integers with a given precision.
 * Solutions flow through two different crossovers in parallel. Some are processed by
 * a single point crossover, the other by a double point crossover.
 * After solutions are mutated.
 *
 * This is the main class that specifies the problem.
 *
 * @version 1.3
 *
 * @since 1.0
 */
public class PatternProblem implements GenerationEventListener<IntegerChromosome> {
    
    private static int POPULATION_SIZE = 100;
    private static int CHROMOSOME_LENGTH = 10;
    private static int GENERATION_LIMIT = 1000;
    private static int MAX_INT = 49;
    
    private PatternGA algorithm = null;
    
    public PatternProblem() {
        IntegerChromosome chrom = new IntegerChromosome(CHROMOSOME_LENGTH,0,MAX_INT);
        Individual<IntegerChromosome> ind = new Individual<IntegerChromosome>(chrom);
        Population<IntegerChromosome> pop = new Population<IntegerChromosome>(ind, POPULATION_SIZE);
        
        algorithm = new PatternGA(pop, GENERATION_LIMIT);
        algorithm.setElitism(5);
        
        AbstractStage<IntegerChromosome> selection = new TournamentSelector<IntegerChromosome>(2);
        
        Parallel<IntegerChromosome> parallel = new Parallel<IntegerChromosome>(new SimpleDispenser<IntegerChromosome>(2));
        
        AbstractStage<IntegerChromosome> crossover1p = new OnePointCrossover<IntegerChromosome>(0.8);
        parallel.add(crossover1p);
        
        AbstractStage<IntegerChromosome> crossover2p = new TwoPointsCrossover<IntegerChromosome>(0.5);
        parallel.add(crossover2p);
        
        AbstractStage<IntegerChromosome> mutation = new SimpleMutator<IntegerChromosome>(0.02);
        
        algorithm.addStage(selection);
        algorithm.addStage(parallel);
        algorithm.addStage(mutation);
        algorithm.setBiggerIsBetter(false);
        algorithm.addGenerationEventListener(this);
    }
    
    public void run(int[] target, int precision) {
        algorithm.setTarget(target);
        algorithm.setPrecision(precision);
        algorithm.evolve();
        
        Population.Statistics stats = algorithm.getCurrentPopulation().getStatistics();
        GeneticAlgorithm.Statistics algostats = algorithm.getStatistics();
        
        System.out.println();
        System.out.print("Target:[");
        for( int i = 0; i < target.length; ++i ) {
            System.out.print(target[i]+ ( i < target.length - 1 ? " " : ""));
        }
        System.out.println("]");
        System.out.println();
        
        System.out.println("Solution: ");
        System.out.println(stats.getLegalLowestIndividual().getChromosome() );
        System.out.println(stats.getLegalLowestIndividual());
        System.out.format("found in %d ms and %d generations.\n", algostats.getExecutionTime(), algostats.getGenerations() );
        System.out.println();
        Utils.printStatistics(stats);
    }
    
    
    public void onGeneration(GeneticAlgorithm ga, long time) {
        Statistics stat = ga.getCurrentPopulation().getStatistics();
        System.out.println("Current generation: " + ga.getGeneration());
        System.out.println("\tBest score: " + stat.getLegalLowestScore());
        System.out.println("\tAvg score: " + stat.getLegalScoreAvg());
    }
    
    private static void randomize(int[] sample) {
        for(int i=0;i<sample.length;i++)
            sample[i] = Random.getInstance().nextInt(0, MAX_INT+1);
    }
    
    public static void main(String[] args) {
        
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 2:");
        System.out.println("This algorithm aims to autonomously find a vector of integers that best matches with a target vector.");
        System.out.println();
        
        Random.getInstance().setStandardSeed();
        
        PatternProblem problem = new PatternProblem();
        int[] target = new int[CHROMOSOME_LENGTH];
        
        randomize(target);
        problem.run(target, 2);
        
        randomize(target);
        problem.run(target, 0);
        
    }
    
}
