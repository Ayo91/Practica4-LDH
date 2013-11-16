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
package jenes.performance;

import jenes.GeneticAlgorithm;
import jenes.chromosome.BitwiseChromosome;
import jenes.chromosome.codings.ByteCoding;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;

public class RoyalTest {

    protected GeneticAlgorithm<BitwiseChromosome> ga;

    public static void main(String[] args) {
        RoyalTest rt = new RoyalTest(100, 100);
        rt.solve();
    }

    public RoyalTest(int popSize, int genLimit) {
        ByteCoding bc = new ByteCoding();
        BitwiseChromosome chrom = new BitwiseChromosome(16, bc);

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(chrom);
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, popSize);

        ga = new RoyalGA(pop, genLimit, bc.SIZE.BITS, bc.SIZE.BITS, 16);

        AbstractStage<BitwiseChromosome> selection = new TournamentSelector<BitwiseChromosome>(2);
        AbstractStage<BitwiseChromosome> crossover = new OnePointCrossover<BitwiseChromosome>(1);
        AbstractStage<BitwiseChromosome> mutation = new SimpleMutator<BitwiseChromosome>(0.2);

        ga.addStage(selection);
        ga.addStage(crossover);
        ga.addStage(mutation);

    }

    public void solve() {
        ga.evolve();

//		Population.Statistics stats = ga.getCurrentPopulation().getStatistics();
//		GeneticAlgorithm.Statistics algostats = ga.getStatistics();
//		
//		//System.out.println(stats.getLegalHighestIndividual().getChromosome() );
//		System.out.println(stats.getLegalHighestIndividual().getScore());
//		System.out.println(stats.getLegalScoreAvg());
//		System.out.println(stats.getLegalScoreDev());
//		System.out.format("found in %d ms and %d generations.\n", algostats.getExecutionTime(), algostats.getGenerations() );
//		System.out.println();
    }
}
