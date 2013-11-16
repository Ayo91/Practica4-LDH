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
package jenes.tutorials.problem9;

import java.io.File;
import java.io.IOException;
import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.algorithms.IslandGA;
import jenes.algorithms.SimpleGA;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.utils.XLSLogger;
import jenes.utils.multitasking.MultiThreadEvaluator;

/**
 * Tutorial implementing a Island genetic algorithm and a Simple genetic algorithm
 * The problem consists in maximizing a function.
 * 
 * @version 2.0
 * @since 1.0
 */
public class IslandGAvsSimpleGA {

    private static int POPULATION_SIZE = 250;
    private static int CHROMOSOME_LENGTH = 2;
    private static int GENERATION_LIMIT = 100;
    private static final String FOLDER = "files.Tutorial9" + File.separatorChar;
    private static XLSLogger xlslogger;

    public static void main(String... a) throws IOException {
        Individual<DoubleChromosome> sample = new Individual<DoubleChromosome>(new DoubleChromosome(CHROMOSOME_LENGTH, -5, +5));
        Population<DoubleChromosome> pop = new Population<DoubleChromosome>(sample, POPULATION_SIZE);

        Fitness<DoubleChromosome> fit = new FitnessFunction(true);
        xlslogger = new XLSLogger(new String[]{"Chromosome IslandGA", "Chromosome SimpleGA"}, FOLDER + "comparison.log.xls", FOLDER + "comparison.tpl.xls");
        Group<DoubleChromosome> resIsland = executeIslandGA(fit, pop);
        Group<DoubleChromosome> resSimple = executeSimplaGA(fit, pop);

        printResults(resIsland, resSimple);
        System.out.println("Completed. The results are stored in comparison.xls");
    }

    private static Group<DoubleChromosome> executeIslandGA(Fitness<DoubleChromosome> fit, Population<DoubleChromosome> pop) {
        GeneticAlgorithm<DoubleChromosome> island = new SimpleGA<DoubleChromosome>(fit, pop, GENERATION_LIMIT);
        island.setRandomization(1);

        IslandGA<DoubleChromosome> islandGA = new IslandGA<DoubleChromosome>(fit, pop, 50, 5, island);

        islandGA.setMigration(5);

        MultiThreadEvaluator eval = new MultiThreadEvaluator();
        eval.execute(islandGA);

        Population.Statistics stats = islandGA.getCurrentPopulation().getStatistics();
        return stats.getGroup(Population.LEGALS);
    }

    private static Group<DoubleChromosome> executeSimplaGA(Fitness<DoubleChromosome> fit, Population<DoubleChromosome> pop) {
        GeneticAlgorithm<DoubleChromosome> simple = new SimpleGA<DoubleChromosome>(fit, pop, GENERATION_LIMIT);
        simple.setRandomization(1);

        MultiThreadEvaluator eval = new MultiThreadEvaluator();
        eval.execute(simple);

        Population.Statistics stats = simple.getCurrentPopulation().getStatistics();
        return stats.getGroup(Population.LEGALS);
    }

    private static void printResults(Group<DoubleChromosome> resIsland, Group<DoubleChromosome> resSimple) {
        int min = resIsland.getNumOfIndividuals();
        int otherInds = resSimple.getNumOfIndividuals();
        if (otherInds < min) {
            min = otherInds;
        }

        for (int i = 0; i < min; i++) {
            xlslogger.put("Chromosome IslandGA", resIsland.get(i).getChromosome());
            xlslogger.put("Chromosome SimpleGA", resSimple.get(i).getChromosome());
            xlslogger.log();
        }

        xlslogger.close();
    }
}
