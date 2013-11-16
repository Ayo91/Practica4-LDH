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
package jenes.tutorials.old.problem6;

import jenes.utils.Random;
import jenes.population.Individual;
import jenes.population.Population.Statistics;
import jenes.tutorials.utils.Utils;

/**
 * Tutorial showing how to minimization and maximization sub-prolems can cohesists in
 * the breeding structure of Jenes.
 *
 * This class defines the problem to solve.
 *
 * @version 1.0
 * @since 1.0
 */
public class KnapsackProblem {
    
    private static int POPSIZE=100;
    private static int GENERATION_LIMIT=100;
    
    private static final double[] WEIGHTS = {1, 5, 3, 2, 8, 6, 4, 7, 9, 6};
    private static final double[] UTILITIES = {7, 2, 7, 1, 6, 4, 2, 8, 9, 2};
    
    private KnapsackGA algorithm;
    private double[] utilities;
    private double[] weights;
    
    public KnapsackProblem(double[] utilities, double[] weights) {
        algorithm = new KnapsackGA(POPSIZE, GENERATION_LIMIT, utilities, weights);
        this.weights = weights;
        this.utilities = utilities;
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
    
    public static KnapsackProblem build(int n) {
        
        Random r = Random.getInstance();
        
        double[] utilities = new double[n];
        for( int i = 0; i < n; ++i ) {
            utilities[i] = r.nextInt(10);
        }
        
        double[] weights = new double[n];
        for( int i = 0; i < n; ++i ) {
            weights[i] = r.nextInt(10);
        }
        
        return new KnapsackProblem(utilities, weights);
    }
    
    public static void main(String[] args) {
        
        Utils.printHeader();
        System.out.println();
        
        System.out.println("TUTORIAL 6:");
        System.out.println("The Knapsack Problem.");
        System.out.println();
        
        KnapsackProblem p1 = new KnapsackProblem(UTILITIES, WEIGHTS);
        
        System.out.println("Case 1: 10 elements, capacity 15");
        System.out.println("Utilities: " + toString(p1.getUtilities()) );
        System.out.println("  Weights: " + toString(p1.getWeights()) );
        p1.setCapacity(15);
        p1.run();
        System.out.println();
        
        System.out.println("Case 2: 10 elements, capacity 30");
        System.out.println("Utilities: " + toString(p1.getUtilities()) );
        System.out.println("  Weights: " + toString(p1.getWeights()) );
        p1.setCapacity(30);
        p1.run();
        System.out.println();
        
        KnapsackProblem p2 = KnapsackProblem.build(20);
        
        System.out.println("Case 3: 20 random elements, capacity 50");
        System.out.println("Utilities: " + toString(p2.getUtilities()) );
        System.out.println("  Weights: " + toString(p2.getWeights()) );
        p2.setCapacity(50);
        p2.run();
        System.out.println();
    }
    
    private static String toString(double[] values) {
        String s = "[";
        for(int i = 0; i < values.length; ++i ){
            s += values[i]+ (i < values.length-1 ? " " : "]");
        }
        return s;
    }
    
}
