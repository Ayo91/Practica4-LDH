/*
 * JENES
 * A time asnd memory efficient Java library for genetic algorithms and more 
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
package jenes.tutorials.problem8;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.stage.operator.Crossover;
import jenes.stage.operator.common.HeuristicCrossover;
import jenes.stage.operator.common.IntermediateCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;

/**
 * Tutorial showing how to use IntermediateCrossover and HeursiticCrossover in 
 * numerical optimization problems.
 *
 * @version 2.0
 * @since 2.0
 * 
 */
public class NumericCrossover {
    
    public static int LENGHT = 3;
    
    public static int POPSIZE = 100;
    public static int GENLIM = 300;
    
    public static double CROSSRATE = 0.8;
    public static double MUTPROB = 0.02;
    
    public static double RATIO = 0.6;
    
    public static GeneticAlgorithm<DoubleChromosome> buildGA(Crossover<DoubleChromosome> crossover, int n) {
        
        DoubleChromosome genome = new DoubleChromosome(n, -1.0, 1.0);
        
        Individual<DoubleChromosome> sample = new Individual(genome);
        Population<DoubleChromosome> pop = new Population<DoubleChromosome>(sample);
        
        Fitness<DoubleChromosome> fit = new Fitness(false) {

            @Override
            public void evaluate(Individual individual) {
                
                DoubleChromosome genome = (DoubleChromosome) individual.getChromosome();
                
                double s = 0;
                for( int i = 0; i < genome.length(); ++i ) {
                    s += Math.pow(genome.getValue(i), i+1);
                }
                
                individual.setScore(s);
            }
            
        };
        
        GeneticAlgorithm<DoubleChromosome> ga = new GeneticAlgorithm<DoubleChromosome>(fit, pop, GENLIM );
        ga.addStage( new TournamentSelector<DoubleChromosome>(3) );
        ga.addStage(crossover);
        ga.addStage(new SimpleMutator<DoubleChromosome>(MUTPROB));
        ga.setElitism(1);
        
        return ga;
    }
    
    public static double serror(Individual ind) {
        DoubleChromosome genome = (DoubleChromosome) ind.getChromosome();

        double err = 0;
        for (int i = 0; i < genome.length(); ++i) {
            double d = (i + 1) % 2 == 0 ? 0 : -1;
            err += Math.pow(genome.getValue(i) - d, 2);
        }

        err = Math.sqrt(err / genome.length());
        return err;
    }
    
    public static void main(String ... args) {

        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 7:");
        System.out.println("Application of numeric crossover.");
        System.out.println();
        
        
        System.out.println("Intermediate crossover");
        
        GeneticAlgorithm ga2 = buildGA( new IntermediateCrossover(CROSSRATE, RATIO), LENGHT );
        ga2.evolve();
        
        Statistics.Group best2 = ga2.getCurrentPopulation().getStatistics().getGroup( Population.LEGALS );
        System.out.println(best2.get(0));
        System.out.println( serror(best2.get(0)) );
        System.out.println();

        System.out.println("Heuristic crossover");
        
        GeneticAlgorithm ga3 = buildGA( new HeuristicCrossover(CROSSRATE, RATIO), LENGHT );
        ga3.evolve();
        
        Statistics.Group best3 = ga3.getCurrentPopulation().getStatistics().getGroup( Population.LEGALS );
        System.out.println(best3.get(0));
        System.out.println( serror(best3.get(0)) );
        System.out.println();
    }
    
}
