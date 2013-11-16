/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem9;

import jenes.population.Fitness;
import jenes.chromosome.DoubleChromosome;
import jenes.population.Individual;

public class FitnessFunction extends Fitness<DoubleChromosome> {

    public FitnessFunction(boolean... bis) {
        super(bis);
    }

    @Override
    public void evaluate(Individual<DoubleChromosome> individual) {
        DoubleChromosome chromosome = individual.getChromosome();
        //f(x , y) = - x^4 - y^4 + 4xy 
        double x, y, f, x4, y4;

        x = chromosome.getValue(0);
        y = chromosome.getValue(1);
        x4 = x * x * x * x;
        y4 = y * y * y * y;

        f = -x4 - y4 + 4 * x * y;

        individual.setScore(f);
    }
}
