/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem12;

import jenes.population.Fitness;
import jenes.algorithms.NSGA2;
import jenes.chromosome.BooleanChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;

/**
 * 
 */
public class NSGA2BooleanProblem {
    
    private static int POPULATION_SIZE = 50;
    private static int CHROMOSOME_LENGTH = 10;
    private static int GENERATION_LIMIT = 500;
    
    public static void main(String... a) {
        Fitness fitness = new BooleanProblem(true, true);
        Individual<BooleanChromosome> sample = new Individual<BooleanChromosome>(new BooleanChromosome(CHROMOSOME_LENGTH));
        Population<BooleanChromosome> pop = new Population<BooleanChromosome>(sample, POPULATION_SIZE);
        
        NSGA2 nsga2 = new NSGA2(fitness, pop, GENERATION_LIMIT);
        
        AbstractStage<BooleanChromosome> crossover = new OnePointCrossover<BooleanChromosome>(0.8);
        AbstractStage<BooleanChromosome> mutation = new SimpleMutator<BooleanChromosome>(0.02);
        
        nsga2.addStage(crossover);
        nsga2.addStage(mutation);

        nsga2.evolve();

        Group<BooleanChromosome> legals = nsga2.getCurrentPopulation().getStatistics().getGroup(Population.LEGALS);
        for(Individual ind : legals){
            System.out.println(ind);
        }
        Individual solution = legals.get(0);
        
        System.out.println("Solution: ");
        System.out.println(solution);
    }
    
    private static class BooleanProblem extends Fitness<BooleanChromosome> {
        
        public BooleanProblem(boolean... bis) {
            super(bis);
        }
        
        @Override
        public void evaluate(Individual<BooleanChromosome> individual) {
            BooleanChromosome chrom = individual.getChromosome();
            int count0 = 0;
            int count1 = 0;
            int length = chrom.length();
            for (int i = 0; i < length; i++) {
                if (chrom.getValue(i)) {
                    count1++;
                } else {
                    count0++;
                }
            }
            
            individual.setScore(new double[]{count0, count1});
        }
    }
}
