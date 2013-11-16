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
package jenes.tutorials.problem5;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.GenericAlleleSet;
import jenes.chromosome.ObjectChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.population.Population.Statistics.Group;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;
import jenes.tutorials.utils.Utils;
import jenes.utils.Random;

/**
 * Tutorial illustrating the use of object-oriented chromosomes, whose
 * allele set can be defined by the user for each gene.
 *
 * In this example the chromosomes are combinations of colors. We aim at finding
 * the vector of colors closest to a given sequence.
 *
 * This class defines the problem.
 *
 * @version 2.0
 * @since 1.0
 */
public class OCProblem {
    
    private static int POPULATION_SIZE = 100;
    private static int GENERATION_LIMIT = 100;
    
    private static ObjectChromosome template =
            new ObjectChromosome( IntegerAlleleSet.createUniform(10, 0, 9),
            IntegerAlleleSet.createUniform(21, 10, 30),
            DoubleAlleleSet.createRandom(10, 0, 1),
            new GenericAlleleSet<Boolean>(true, false),
            new GenericAlleleSet<Color>(Color.values()) );
    
    private static Fitness<ObjectChromosome> fitness = new Fitness<ObjectChromosome>(true) {

        @Override
        public void evaluate(Individual<ObjectChromosome> individual) {
            ObjectChromosome template = individual.getChromosome();
            
            int i1 = (Integer)template.getValue(0);
            int i2 = (Integer)template.getValue(1);
            double d = (Double)template.getValue(2);
            boolean b = (Boolean)template.getValue(3);
            Color c = (Color)template.getValue(4);
            
            double acc = b ? (3*i1 + 4*i2 + d) : i1;
            
            switch( c ) {
            case BLACK : acc += 10; break;
            case RED   : acc += 10; break;
            case WHITE : acc += 10; break;
            }
            
            individual.setScore(acc);
        }        
    };
    
    private static GeneticAlgorithm<ObjectChromosome> ga =
            new GeneticAlgorithm<ObjectChromosome>( fitness, new Population<ObjectChromosome>(new Individual<ObjectChromosome>(template), POPULATION_SIZE), GENERATION_LIMIT);
        
    
    public static void main(String[] args)throws Exception {
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 5:");
        System.out.println("Find the sequence of colors nearest to the target.");
        System.out.println();
        
        Random.getInstance().setStandardSeed();
        
        ga.addStage(new TournamentSelector<ObjectChromosome>(3));
        ga.addStage(new OnePointCrossover<ObjectChromosome>(0.8));
        ga.addStage(new SimpleMutator<ObjectChromosome>(0.02));
        
        ga.evolve();
        
        Statistics stats = ga.getCurrentPopulation().getStatistics();
        GeneticAlgorithm.Statistics algostats = ga.getStatistics();

        Group legals = stats.getGroup(Population.LEGALS);
        
        System.out.println("Solution: ");
        System.out.println(legals.get(0));
        System.out.format("found in %d ms.\n", algostats.getExecutionTime() );
        System.out.println();
        
        Utils.printStatistics(stats);
    }
}
