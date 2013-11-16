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

import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.operator.common.TournamentSelector;

public class TravelSalesmanProblem {

    public static final int MAX_DISTANCE = 10;
    private TSPGA algorithm;
    private int cities;

    public static void main(String[] args) {
        double[][] m1 = getMap();
        TravelSalesmanProblem tsp1 = new TravelSalesmanProblem(m1, 10, 10);
        tsp1.solve();
    }

    public TravelSalesmanProblem(double[][] matrix, int popSize, int genLimit) {

        cities = matrix[0].length;

        IntegerChromosome chrom = new IntegerChromosome(cities, -1, cities - 1);
        for (int i = 0; i < cities; ++i) {
            chrom.setValue(i, i < cities - 1 ? i + 1 : 0);
        }
        Individual<IntegerChromosome> sample = new Individual<IntegerChromosome>(chrom);
        Population<IntegerChromosome> pop = new Population<IntegerChromosome>(sample, popSize);

        algorithm = new TSPGA(matrix, pop, genLimit);

        AbstractStage<IntegerChromosome> selection = new TournamentSelector<IntegerChromosome>(2);
        AbstractStage<IntegerChromosome> crossover = new TSPCrossover(1);
        AbstractStage<IntegerChromosome> mutation = new TSPMutator(0.2);

        algorithm.addStage(selection);
        algorithm.addStage(crossover);
        algorithm.addStage(mutation);

        algorithm.setBiggerIsBetter(false);
    }

    public void solve() {
        algorithm.evolve();
//		
//		Population.Statistics stats = algorithm.getCurrentPopulation().getStatistics();
//		GeneticAlgorithm.Statistics algostats = algorithm.getStatistics();
//		
//		System.out.println(stats.getLegalHighestIndividual().getChromosome() );
//		System.out.println(stats.getLegalHighestIndividual());
//		System.out.format("found in %d ms and %d generations.\n", algostats.getExecutionTime(), algostats.getGenerations() );
//		System.out.println();
    }

    public static double[][] getMap() {
        double[][] matrix = new double[20][20];
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 20; ++j) {
                matrix[i][j] = getDistance(i, j);
            }
        }
        return matrix;
    }

    private static double getDistance(int i, int j) {
        int xa = positions[i][0];
        int ya = positions[i][1];

        int xb = positions[j][0];
        int yb = positions[j][1];

        return Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
    }
    //valori usati anche da galib
    private static int[][] positions = {
        {1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {3, 1}, {3, 2}, {3, 3},
        {3, 4}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {5, 1}, {5, 2}, {5, 3}, {5, 4}
    };
}
