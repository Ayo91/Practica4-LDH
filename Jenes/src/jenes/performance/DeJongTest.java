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
import jenes.utils.Random;
import jenes.chromosome.BitwiseChromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.stage.operator.common.TournamentSelector;

public class DeJongTest {

    protected int POPULATION_SIZE;
    protected int GENERATION_LIMIT;
    protected int CHROMOSOME_SIZE;
    protected double MIN_VALUE;
    protected double MAX_VALUE;
    protected GeneticAlgorithm<BitwiseChromosome> ga;

    public DeJongTest(int problem, int popSize, int genLimit) {
        POPULATION_SIZE = popSize;
        GENERATION_LIMIT = genLimit;
        switch (problem) {
            case 1:
                buildProblem1();
                break;
            case 2:
                buildProblem2();
                break;
            case 3:
                buildProblem3();
                break;
            case 4:
                buildProblem4();
                break;
            case 5:
                buildProblem5();
                break;
        }
    }

    protected void buildProblem1() {

        MIN_VALUE = -5.12;
        MAX_VALUE = 5.12;

        CHROMOSOME_SIZE = 3;

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(CHROMOSOME_SIZE));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);
        ga = new GeneticAlgorithm<BitwiseChromosome>(pop, GENERATION_LIMIT) {

            @Override
            public void evaluateIndividual(Individual<BitwiseChromosome> individual) {

                BitwiseChromosome chrom = individual.getChromosome();

                double x1 = convert(chrom.getIntValueAt(0));
                double x2 = convert(chrom.getIntValueAt(1));
                double x3 = convert(chrom.getIntValueAt(2));

                individual.setScore(x1 * x1 + x2 * x2 + x3 * x3);
            }
        };
        ga.setBiggerIsBetter(false);
        ga.addStage(new TournamentSelector<BitwiseChromosome>(2));
        ga.addStage(new OnePointCrossover<BitwiseChromosome>(1));
        ga.addStage(new SimpleMutator<BitwiseChromosome>(0.2));
    }

    protected void buildProblem2() {

        MIN_VALUE = -2.048;
        MAX_VALUE = 2.048;

        CHROMOSOME_SIZE = 2;

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(CHROMOSOME_SIZE));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);

        ga = new GeneticAlgorithm<BitwiseChromosome>(pop, GENERATION_LIMIT) {

            @Override
            public void evaluateIndividual(Individual<BitwiseChromosome> individual) {

                BitwiseChromosome chrom = individual.getChromosome();

                double x1 = convert(chrom.getIntValueAt(0));
                double x2 = convert(chrom.getIntValueAt(1));

                individual.setScore(100 * (x1 * x1 - x2) * (x1 * x1 - x2) + (1 - x1) * (1 - x1));
            }
        };
        ga.setBiggerIsBetter(false);
        ga.addStage(new TournamentSelector<BitwiseChromosome>(2));
        ga.addStage(new OnePointCrossover<BitwiseChromosome>(1));
        ga.addStage(new SimpleMutator<BitwiseChromosome>(0.2));
    }

    protected void buildProblem3() {

        MIN_VALUE = -5.12;
        MAX_VALUE = 5.12;

        CHROMOSOME_SIZE = 5;

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(CHROMOSOME_SIZE));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);

        ga = new GeneticAlgorithm<BitwiseChromosome>(pop, GENERATION_LIMIT) {

            @Override
            public void evaluateIndividual(Individual<BitwiseChromosome> individual) {

                BitwiseChromosome chrom = individual.getChromosome();

                double x1 = convert(chrom.getIntValueAt(0));
                double x2 = convert(chrom.getIntValueAt(1));
                double x3 = convert(chrom.getIntValueAt(2));
                double x4 = convert(chrom.getIntValueAt(3));
                double x5 = convert(chrom.getIntValueAt(4));

                individual.setScore(25 + Math.floor(x1) + Math.floor(x2) + Math.floor(x3) + Math.floor(x4) + Math.floor(x5));
            }
        };
        ga.setBiggerIsBetter(false);
        ga.addStage(new TournamentSelector<BitwiseChromosome>(2));
        ga.addStage(new OnePointCrossover<BitwiseChromosome>(1));
        ga.addStage(new SimpleMutator<BitwiseChromosome>(0.2));

    }

    protected void buildProblem4() {

        MIN_VALUE = -1.28;
        MAX_VALUE = 1.28;

        CHROMOSOME_SIZE = 30;

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(CHROMOSOME_SIZE));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);

        ga = new GeneticAlgorithm<BitwiseChromosome>(pop, GENERATION_LIMIT) {

            @Override
            public void evaluateIndividual(Individual<BitwiseChromosome> individual) {

                BitwiseChromosome chrom = individual.getChromosome();
                int len = chrom.getIntLength();

                double value = 0;
                for (int i = 0; i < len; i++) {
                    double xi = convert(chrom.getIntValueAt(i));
                    value += (i + 1) * xi * xi * xi * xi + Random.getInstance().nextGaussian();
                }

                individual.setScore(value);
            }
        };
        ga.setBiggerIsBetter(false);
        ga.addStage(new TournamentSelector<BitwiseChromosome>(2));
        ga.addStage(new OnePointCrossover<BitwiseChromosome>(1));
        ga.addStage(new SimpleMutator<BitwiseChromosome>(0.2));

    }

    protected void buildProblem5() {

        MIN_VALUE = -65.536;
        MAX_VALUE = 65.536;

        CHROMOSOME_SIZE = 2;

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(new BitwiseChromosome(CHROMOSOME_SIZE));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);

        ga = new GeneticAlgorithm<BitwiseChromosome>(pop, GENERATION_LIMIT) {

            private int a[][] = {
                {-32, -16, 0, 16, 32,
                    -32, -16, 0, 16, 32,
                    -32, -16, 0, 16, 32,
                    -32, -16, 0, 16, 32,
                    -32, -16, 0, 16, 32},
                {-32, -32, -32, -32, -32,
                    -16, -16, -16, -16, -16,
                    0, 0, 0, 0, 0,
                    16, 16, 16, 16, 16,
                    32, 32, 32, 32, 32}
            };

            @Override
            public void evaluateIndividual(Individual<BitwiseChromosome> individual) {

                BitwiseChromosome chrom = individual.getChromosome();
                double value = 0;
                double x[] = {convert(chrom.getIntValueAt(0)), convert(chrom.getIntValueAt(1))};
                double lowtot, prod;
                for (int j = 0; j < 25; ++j) {
                    lowtot = (double) (j + 1);
                    for (int i = 0; i < 2; ++i) {
                        prod = 1.0f;
                        for (int power = 0; power < 6; ++power) {
                            prod *= x[i] - a[i][j];
                        }
                        lowtot += prod;
                    }
                    value += 1.0 / lowtot;
                }

                individual.setScore(value);
            }
        };
        ga.setBiggerIsBetter(false);
        ga.addStage(new TournamentSelector<BitwiseChromosome>(2));
        ga.addStage(new OnePointCrossover<BitwiseChromosome>(1));
        ga.addStage(new SimpleMutator<BitwiseChromosome>(0.2));

    }

    protected final double convert(int x) {
        long q = (long) Integer.MAX_VALUE - Integer.MIN_VALUE;
        long p = (long) x - Integer.MIN_VALUE;

        double ratio = (double) p / q;
        return ratio * (MAX_VALUE - MIN_VALUE) + MIN_VALUE;
    }

    public void solve() {
        this.ga.evolve();
//		System.out.println("Max: " + ga.getCurrentPopulation().getStatistics().getLegalHighestScore());
//		System.out.println("Dev: " + ga.getCurrentPopulation().getStatistics().getLegalScoreDev());
    }

    public static void main(String[] args) {

        for (int i = 1; i <= 5; ++i) {
            System.out.println("De Jong's Problem #" + i);
            DeJongTest problem = new DeJongTest(i, 100, 100);
            problem.solve();
        }
    }
}
