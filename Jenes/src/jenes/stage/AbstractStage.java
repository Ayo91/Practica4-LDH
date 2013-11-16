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
package jenes.stage;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * A generic genetic algorithm stage.<br>
 * <br>
 * In Jenes the genetic algorithm body is a "pipe" of stages:  
 * each stage processes the input population and produces the output population.<br>
 * A genetic algoritm invokes the {@link #process(Population, Population)} method of each stage. 
 * <br>
 * It is important to consider that the output population is pre-initialized with recicled individuals (for performance reasons), 
 * so {@link #process(Population, Population)} generally doesn't allocate new Individuals but only changes the genome of that in the output population.
 * The default output population size is equal to the input one, 
 * but the stage can (if needed) add or remove individuals from the output population. 
 * <br><br>
 * Note: a stage can modify the input population. So input passed to process method 
 * can be mutated when the process method ends.
 * 
 * <p>
 * @param <T> The class chromosomes flowing across the stage.
 * 
 * @version 2.0
 * @since 1.0
 * 
 */
public abstract class AbstractStage<T extends Chromosome> {

    /** The genetic algorithm, this stage belongs to */
    protected GeneticAlgorithm<T> ga;
    /** True if higher scores entail better individuals */
    protected boolean biggerIsBetter = true;
    /** The current Fitness */
    protected Fitness<T> fitness = null;
    /** Flag indicating if fitness is changed recently */
    private boolean fitnessChanged = false;

    /**
     * Processes the input population and tranforms it into the output population.
     * Note:
     * 	- 	Out population is made of recicled individuals. There is a need of new individuals
     *      to add only when there is a need to increase the population size.
     * 		For pre-initalized individual just use setAs method. This is done for an efficient memory management.
     * 	-	A stage can modify the input population. So input passed to process method
     * 		can be mutated when the process method ends.
     * <p>
     * @param in the input population
     * @param out the output population
     * @throws StageException
     */
    public abstract void process(Population<T> in, Population<T> out) throws StageException;

    /**
     * Initializes this stage according to the genetic algorithm
     * that uses it
     *
     * @param ga the Genetic Algorithm in wchic this stage run
     * @throws StageException
     */
    public void init(GeneticAlgorithm<T> ga) throws StageException {
        this.ga = ga;
    }

    /**
     * Disposes this stage
     *
     * @throws StageException
     */
    public void dispose() throws StageException {
        //do nothing
    }

    /**
     * @deprecated deprecated due to the use of {@link Fitness}. In next releasese this method will be removed
     * Says if the best individuals have the higher fitness or not.
     *
     * @return <code>true</code> if the best individuals have the higher fitness> <code>false</code> otherwise
     */
    public boolean isBiggerBetter() {
        return this.biggerIsBetter;
    }

    /**
     * @deprecated deprecated due to the use of {@link Fitness}. In next releasese this method will be removed
     * Sets if the best individuals have the higher fitness or not.
     * For maximization, this property is set to true;
     * for minimization, this property is set to false.
     * This setting is propagated down to every sub-stage the stage is made of.
     *
     * @param flag true, if the best individual has the higher fitness
     */
    public void setBiggerIsBetter(boolean flags) {
        this.setBiggerIsBetter(flags, true);
    }

    /**
     * @deprecated deprecated due to the use of {@link Fitness}. In next releasese this method will be removed
     * Sets if the best individuals have the higher fitness or not.
     * This setting can be or not propagated down to sub-stages.
     *
     * @param flag true, if the best individual has the higher fitness
     * @param recursively true, to propagate this setting down, otherwise false.
     */
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        this.biggerIsBetter = flag;
        this.setFitness(null, true);
    }

    /**
     * Get the {@link Fitness} currently setted for this stage
     * @return 
     */
    public final Fitness<T> getFitness() {
        return this.fitness;
    }

    /**
     * Change the {@link Fitness} to this stage propagating the change recursively 
     * @param fit 
     */
    public void setFitness(Fitness<T> fit) {
        this.setFitness(fit, true);
    }

    /**
     * Change the {@link Fitness} to this stage propagating the change recursively 
     * according to the flag given as parameter
     * @param fit
     * @param recursively 
     */
    public void setFitness(Fitness<T> fit, boolean recursively) {
        this.fitnessChanged = this.fitness != null && fit != this.fitness;
        this.fitness = fit;
    }

    /**
     * Test if the fitness is recently changed
     * @return 
     */
    public boolean isFitnessChanged() {
        return this.fitnessChanged;
    }
}
