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
package jenes.tutorials.old.problem7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jenes.GenerationEventListener;
import jenes.GeneticAlgorithm;
import jenes.chromosome.BooleanChromosome;
import jenes.utils.Random;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.population.Population.Statistics;
import jenes.tutorials.utils.Utils;
import jenes.utils.CSVLogger;
import jenes.statistics.StatisticsLogger;
import jenes.tutorials.old.problem6.KnapsackGA;
import jenes.utils.XLSLogger;

/**
 * A tutorial showing how to log statistics on different media.
 *
 * @version 1.3
 *
 * @since 1.3
 */
public class KnapsackLoggedProblem  {
    
    private static int POPSIZE=20;
    private static int GENERATION_LIMIT=100;
    
    private static final double[] WEIGHTS = {1, 5, 3, 2, 8, 6, 4, 7, 9, 6};
    private static final double[] UTILITIES = {7, 2, 7, 1, 6, 4, 2, 8, 9, 2};
    
    private KnapsackGA algorithm;
    private double[] utilities;
    private double[] weights;
    
    private StatisticsLogger csvlogger;
    private StatisticsLogger xlslogge1;
    private StatisticsLogger xlslogge2;
    private XLSLogger xlslogge3;
    private int exec;

    private final String FOLDER = "files.Tutorial7" + File.separatorChar;

    public KnapsackLoggedProblem(double[] utilities, double[] weights) throws IOException {
        algorithm = new KnapsackGA(POPSIZE, GENERATION_LIMIT, utilities, weights);
        this.weights = weights;
        this.utilities = utilities;

        csvlogger = new StatisticsLogger(
                    new CSVLogger(new String[]{"LegalHighestScore","LegalScoreAvg","LegalScoreDev"}, FOLDER+"knapsackproblem.csv" ) );

        xlslogge1 = new StatisticsLogger(
                    new XLSLogger(new String[]{"LegalHighestScore","LegalScoreAvg","LegalScoreDev"}, FOLDER+"knapsack1.log.xls" ) );

        xlslogge2 = new StatisticsLogger(
                    new XLSLogger(new String[]{"LegalHighestScore", "LegalScoreAvg" , "IllegalScoreAvg"}, FOLDER+"knapsack2.log.xls", FOLDER+"knapsack.tpl.xls" ) );

        xlslogge3 = new XLSLogger(new String[]{"LegalHighestScore", "LegalScoreAvg" , "Run"}, FOLDER+"knapsack3.log.xls");

    }
    
    @SuppressWarnings("unchecked")
    public void run() {
        this.algorithm.evolve();
        
        Statistics stat=algorithm.getCurrentPopulation().getStatistics();
        Individual solution=stat.getLegalHighestIndividual();
        System.out.println(solution.getChromosome());
        System.out.format("W: %f U: %f\n", algorithm.getWeightOf(solution), algorithm.getUtilityOf(solution) );
    }
    
    public double getCapacity() {
        return this.algorithm.getCapacity();
    }
    
    public void setCapacity(double c) {
        this.algorithm.setCapacity(c);
    }
    
    public double[] getUtilities() {
        return utilities;
    }
    
    public double[] getWeights() {
        return weights;
    }
    
    public static KnapsackLoggedProblem build(int n) throws FileNotFoundException, IOException {
        
        Random r = Random.getInstance();
        
        double[] utilities = new double[n];
        for( int i = 0; i < n; ++i ) {
            utilities[i] = r.nextInt(10);
        }
        
        double[] weights = new double[n];
        for( int i = 0; i < n; ++i ) {
            weights[i] = r.nextInt(10);
        }
        
        return new KnapsackLoggedProblem(utilities, weights);
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 7:");
        System.out.println("Logging the Knapsack Problem.");
        System.out.println();
        
        final KnapsackLoggedProblem prb = KnapsackLoggedProblem.build(20);

        System.out.println("Utilities: " + toString(prb.getUtilities()) );
        System.out.println("  Weights: " + toString(prb.getWeights()) );
        System.out.println();

        GenerationEventListener<BooleanChromosome> logger1 = new GenerationEventListener<BooleanChromosome>() {

            public void onGeneration(GeneticAlgorithm ga, long time) {
                Population.Statistics stats = ga.getCurrentPopulation().getStatistics();
                
                prb.csvlogger.record(stats);
                prb.xlslogge1.record(stats);
                prb.xlslogge2.record(stats);

            }

        };

        prb.algorithm.addGenerationEventListener(logger1);


        System.out.println("50 random elements, capacity 50");
        prb.setCapacity(50);
        prb.run();
        System.out.println();

        System.out.println("Saving the logs ...");
        prb.csvlogger.close();
        prb.xlslogge1.close();
        prb.xlslogge2.close();
        System.out.println("Done.");

        prb.algorithm.removeGenerationEventListener(logger1);

        GenerationEventListener<BooleanChromosome> logger2 = new GenerationEventListener<BooleanChromosome>() {
            public void onGeneration(GeneticAlgorithm ga, long time) {
                Population.Statistics stats = ga.getCurrentPopulation().getStatistics();

                prb.xlslogge3.put("LegalHighestScore", stats.getLegalHighestScore());
                prb.xlslogge3.put("LegalScoreAvg", stats.getLegalScoreAvg());
                prb.xlslogge3.put("Run", prb.exec);

                prb.xlslogge3.log();
            }

        };

        prb.algorithm.addGenerationEventListener(logger2);

        System.out.println();
        System.out.println("Repeating 10 times: 20 random elements, capacity 50");
        for( prb.exec = 0; prb.exec < 10; ++prb.exec) {
             System.out.println((prb.exec+1) + " of 10");
             prb.run();
        }
        prb.xlslogge3.close();
        System.out.println("Done.");
    }
    
    private static String toString(double[] values) {
        String s = "[";
        for(int i = 0; i < values.length; ++i ){
            s += values[i]+ (i < values.length-1 ? " " : "]");
        }
        return s;
    }
    
}
