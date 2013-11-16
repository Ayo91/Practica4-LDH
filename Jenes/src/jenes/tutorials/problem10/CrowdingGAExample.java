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
package jenes.tutorials.problem10;

import java.util.Arrays;
import jenes.algorithms.CrowdingGA;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.stage.operator.Crowder;
import jenes.stage.operator.common.DeJongCrowder;
import jenes.stage.operator.common.DeterministicCrowder;
import jenes.stage.operator.common.MultiNicheCrowder;

/**
 * Tutorial implementing a crowding genetic algorithm with different operators.
 * The problem consists in maximizing a function.
 * 
 * @version 2.0
 * @since 1.0
 */
public class CrowdingGAExample {

    private static int POPULATION_SIZE = 500;
    private static int CHROMOSOME_LENGTH = 2;
    private static int GENERATION_LIMIT = 100;

    public static void main(String... a) {
        Individual<DoubleChromosome> sample = new Individual<DoubleChromosome>(new DoubleChromosome(CHROMOSOME_LENGTH, -5.12, 5.11));
        Population<DoubleChromosome> pop = new Population<DoubleChromosome>(sample, POPULATION_SIZE);

        Fitness<DoubleChromosome> fit = new FitnessFunction(true);

        deJongOperator(fit, pop);
        System.out.println("-------------");
        multiNicheOperator(fit, pop);
        System.out.println("-------------");
        deterministicOperator(fit, pop);
    }

    private static void deJongOperator(Fitness<DoubleChromosome> fit, Population<DoubleChromosome> pop) {
        Crowder deJongCrowder = new DeJongCrowder(3, 1);
        CrowdingGA crowdingGA = new CrowdingGA(fit, deJongCrowder, pop, GENERATION_LIMIT);
        
        crowdingGA.evolve();

        Population.Statistics stats = crowdingGA.getCurrentPopulation().getStatistics();

        Group legals = stats.getGroup(Population.LEGALS);

        Individual solution = legals.get(0);

        System.out.println("Operator: De Jong");
        System.out.println("Solution: " + Arrays.toString(solution.getAllScores()) + "\n");
        System.out.println(solution);
    }

    private static void multiNicheOperator(Fitness<DoubleChromosome> fit, Population<DoubleChromosome> pop) {
        Crowder multiNicheCrowder = new MultiNicheCrowder();
        CrowdingGA crowdingGA = new CrowdingGA(fit, multiNicheCrowder, pop, GENERATION_LIMIT);

        crowdingGA.evolve();

        Population.Statistics stats = crowdingGA.getCurrentPopulation().getStatistics();

        Group legals = stats.getGroup(Population.LEGALS);

        Individual solution = legals.get(0);

        System.out.println("Operator: MultiNiche");
        System.out.println("Solution: " + Arrays.toString(solution.getAllScores()) + "\n");
        System.out.println(solution);
    }

    private static void deterministicOperator(Fitness<DoubleChromosome> fit, Population<DoubleChromosome> pop) {
        Crowder deterministicCrowder = new DeterministicCrowder(DeterministicCrowder.SelectionMethod.TOURNAMENT, DeterministicCrowder.CrossoverMethod.SINGLEPOINT, 0.8, 0.02);
        CrowdingGA crowdingGA = new CrowdingGA(fit, deterministicCrowder, pop, GENERATION_LIMIT);

        crowdingGA.evolve();

        Population.Statistics stats = crowdingGA.getCurrentPopulation().getStatistics();

        Group legals = stats.getGroup(Population.LEGALS);

        Individual solution = legals.get(0);

        System.out.println("Operator: Deterministic");
        System.out.println("Solution: " + Arrays.toString(solution.getAllScores()) + "\n");
        System.out.println(solution);
    }
}
