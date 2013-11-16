/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem11;

import jenes.GeneticAlgorithm;
import jenes.GeneticAlgorithm.Statistics;
import jenes.algorithms.SimpleGA;
import jenes.chromosome.BitwiseChromosome;
import jenes.chromosome.codings.IntCoding;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.tutorials.utils.Utils;
import jenes.utils.multitasking.MultiThreadEvaluator;

/**
 * This class represent a simple example of how to use multi-thread feature in 
 * Jenes 2.0
 * 
 * @since 2.0
 */
public class MultiThreadExample {
    

    public static void main(String... a) throws Exception{
        Utils.printHeader();
        System.out.println();

        System.out.println("TUTORIAL 11:");
        System.out.println("This tutorial show how to use multi-thread execution.");
        System.out.println();

        /** The angle to use as target for matching */
        int targetAngle = 10;
        
        //instatiate fitness function
        ImageMatchingFitness f = new ImageMatchingFitness(targetAngle);
        
        //code sample individual, initial population, than simple ga
        BitwiseChromosome.BitCoding coding = new IntCoding();
        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(1, coding));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, 100);
        SimpleGA<BitwiseChromosome> ga = new SimpleGA<BitwiseChromosome>(f, pop);
        
        //run a test-set
        testAlgorithm(ga);
        
        System.exit(0);
    }

    /**
     * Execute a test-set using different thread pool size configuration. Best
     * results are achieved using a num of thread equals to the avaible CPU cores of your hardware
     * @param algorithm the algorithm to test
     */
    private static void testAlgorithm(GeneticAlgorithm algorithm) {
        //parallel evaluation
        int[] testSet = new int[]{32, 16, 8, 4, 2, 1};
        for (int test : testSet) {
            MultiThreadEvaluator eval = new MultiThreadEvaluator(test);
            eval.execute(algorithm);
            printStatistics(algorithm, test);
            
            /* try to free memory between tests */
            System.gc();
        }
        
        //sequential evaluation
        algorithm.evolve();
        printStatistics(algorithm, 0);
        
    }

    /**
     * Print statistics
     * @param ga
     * @param threads 
     */
    private static void printStatistics(GeneticAlgorithm<BitwiseChromosome> ga, int threads) {
        Statistics stats = ga.getStatistics();
        String header = null;
        if (threads == 0) {
            header = "[sequential]";
        } else if (threads == 1) {
            header = "[1 thread] the main thread produces tasks the helper one evaluate fitness";
        } else {
            header = String.format("[%d threads] the main thread produces tasks and the %d helpers "
                    + "evaluate fitness in parallel", threads, threads);
        }
        
        Group group = ga.getCurrentPopulation().getStatistics().getGroup(Population.LEGALS);
        Individual best = group.get(0);
        
        double distance = best.getScore();
        
        System.out.format("%s\n\tEnd in %d ms, time per fitness %d (n: %d, ratio: %f%%) "
                + "-> distance from target (0 = exact matching): %.2f\n", 
                header,
                stats.getExecutionTime(),
                stats.getTimeSpentForFitnessEval(),
                stats.getFitnessEvaluationNumbers(),
                stats.getTimeSpentForFitnessEval() * 100d / stats.getExecutionTime(),
                distance);
    }
}
