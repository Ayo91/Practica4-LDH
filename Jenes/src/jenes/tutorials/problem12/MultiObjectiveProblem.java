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
package jenes.tutorials.problem12;

import java.io.File;
import java.io.IOException;
import java.util.List;
import jenes.population.Fitness;
import jenes.algorithms.NSGA2;
import jenes.chromosome.BitwiseChromosome;
import jenes.chromosome.BitwiseChromosome.BitCoding;
import jenes.chromosome.codings.WordCoding;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Pareto;
import jenes.stage.operator.common.OnePointCrossover;
import jenes.stage.operator.common.SimpleMutator;
import jenes.statistics.StatisticsLogger;
import jenes.tutorials.utils.Utils;
import jenes.utils.Random;
import jenes.utils.XLSLogger;

/**
 *
 */
public class MultiObjectiveProblem {

    private static final int POPULATION_SIZE = 20;
    private static final int GENERATION_LIMIT = 100;
    private static final BitCoding<Integer> CODING = new WordCoding();
    private static final double CROSSOVER = 1.0;
    private static final double MUTATION = 0.02;
    private static final int TRIALS = 3;
    private static int res = (1 << CODING.SIZE.BITS) - 1;
    private static final String FOLDER = "files.Tutorial12" + File.separatorChar;

    public static void main(String... args) throws IOException {

        Utils.printHeader();
        System.out.println();

        System.out.println("TUTORIAL 12:");
        System.out.println("This algorithm aims to find a optimal solution to multi objective problems.");
        System.out.println();

        Random.getInstance().setSeed(108978322);

        final Function f = new Schaffer();
        final int nvars = f.getNVars();

        Individual<BitwiseChromosome> sample = new Individual<BitwiseChromosome>(f.getGoals(), new BitwiseChromosome(nvars, CODING));
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(sample, POPULATION_SIZE);

        final StatisticsLogger xlslogger = new StatisticsLogger(
                new XLSLogger(new String[]{}, FOLDER + "log.xls", FOLDER + "log.tpl.xls"));

        Fitness<BitwiseChromosome> fitness = new ProblemFitness(f, nvars);

        NSGA2<BitwiseChromosome> ga = new NSGA2<BitwiseChromosome>(fitness, pop, GENERATION_LIMIT, TRIALS) {

            @Override
            public void onGeneration(long time) {
                Pareto pareto = this.getCurrentPopulation().getPareto();

                List<Individual<?>> group = pareto.getFront(0);

//                xlslogger.record(group);
//                
                for (Individual<?> i : group) {
                    System.out.println("BEST:" + i.getStatistics().getScore()[0] + "--" + i.getStatistics().getScore()[1]);
                    System.out.println("BEST: I.Generation" + this.getGeneration());
                    System.out.println("------------------");
                    //                    xlslogger.log();
                }

            }
        };

        ga.addStage(new OnePointCrossover(CROSSOVER));
        ga.addStage(new SimpleMutator(MUTATION));

        ga.evolve();
        
        Population<BitwiseChromosome> result = ga.getCurrentPopulation();

        Pareto pareto = result.getPareto();

        List<Individual<?>> best = pareto.getFront(0);

        double[] x = new double[nvars];
        for (int i = 0; i < best.size(); ++i) {
            Individual<BitwiseChromosome> ind = (Individual<BitwiseChromosome>) best.get(i);
            BitwiseChromosome chrom = ind.getChromosome();
            decode(f, chrom, x);
            double[] y = f.evaluate(x);

            Fitness.dominance(best.get(0), ind, fitness.getBiggerIsBetter());

            System.out.print("x " + i + " : ");
            for (int j = 0; j < nvars; ++j) {
                System.out.print(x[j] + " ");
            }
            System.out.print("[ ");
            for (int j = 0; j < y.length; ++j) {
                System.out.print(y[j] + " ");
            }
            System.out.println("]");

        }

        System.out.println("Dominant solutions: " + best.size());
    }

    public static void decode(Function f, BitwiseChromosome chrom, double[] x) {

        final int nvars = f.getNVars();
        final double[][] bounds = f.bounds;

        for (int i = 0; i < nvars; ++i) {
            int tau = (Integer) chrom.getValueAt(i);
            double sigma = (double) tau / res;
            x[i] = sigma * (bounds[i][1] - bounds[i][0]) + bounds[i][0];
        }
    }

    public static abstract class Function {

        protected double[][] bounds;
        protected int goals;
        protected String name;

        public Function(String name, double[][] bounds, int goals) {
            this.name = name;
            this.bounds = bounds;
            this.goals = goals;
        }

        public double[][] getBounds() {
            return bounds;
        }

        public int getNVars() {
            return this.bounds.length;
        }

        public int getGoals() {
            return this.goals;
        }

        public String getName() {
            return this.name;
        }

        public abstract double[] evaluate(double... x);
    }

    public static class ProblemFitness extends Fitness<BitwiseChromosome> {

        private double[] x;
        private Function f;

        public ProblemFitness(Function f, int nvars) {
            super(f.goals, true);
            this.x = new double[nvars];
            this.f = f;
        }

        @Override
        public void evaluate(Individual<BitwiseChromosome> individual) {
            BitwiseChromosome chrom = individual.getChromosome();

            decode(f, chrom, x);
            double[] y = f.evaluate(x);

            individual.setScore(y);
        }

        @Override
        protected ProblemFitness duplicate() {
            ProblemFitness pf = new ProblemFitness(this.f, this.x.length);
            this.x = new double[pf.x.length];
            System.arraycopy(pf.x, 0, this.x, 0, x.length);
            return pf;
        }

    }
}
