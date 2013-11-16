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
package jenes.utils.multitasking;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.population.Individual;
import jenes.population.Population;

/**
 * The Runner is the abstraction of an execution enviroinment per Jenes. By now
 * this approach has been used to parallelize the fitness evaluation step.
 * The programmer have to override the default call-backs than use {@link #execute(jenes.GeneticAlgorithm)} 
 * or {@link #execute(jenes.GeneticAlgorithm, boolean)} to activate Genetic Algorithm to 
 * start evolving in the enviroinment.
 * 
 * @since 2.0
 */
public abstract class Runner {

    /**
     * The algorithm to evolve in the enviroinment
     */
    protected GeneticAlgorithm algorithm;

    /**
     * Start evolving the algorithm given as parameter in this enviroinment by 
     * applying to specificated restart flag.
     * 
     * @param algorithm the algorithm to evolve
     * @param restart <tt>restart</tt> flag to pass to {@link GeneticAlgorithm#evolve(boolean)}
     */
    public final void execute(GeneticAlgorithm algorithm, boolean restart) {
        this.algorithm = algorithm;
        this.algorithm.setRunner(this);

        this.algorithm.evolve(restart);
    }

    /**
     * Start evolving the algorithm given as argument restarting its state
     * @param algoritm the algorithm to evolve
     * @see #execute(jenes.GeneticAlgorithm, boolean) 
     */
    public final void execute(GeneticAlgorithm algoritm) {
        this.execute(algoritm, true);
    }

    /**
     * Start evolving the algorithm given and adopting as initial population the
     * one passed as argument
     * @param algorithm
     * @param initialPopulation
     */
    public final void execute(GeneticAlgorithm algorithm, Population<?> initialPopulation) {
        this.algorithm = algorithm;
        this.algorithm.setRunner(this);

        this.algorithm.evolve(initialPopulation);
    }

    /**
     * Return the Genetic Algorithm currently in execution in this enviroinment
     * @return 
     */
    public GeneticAlgorithm getGeneticAlgorithm() {
        return algorithm;
    }

    /**
     * Set the Genetic Algorithm to the runner. This value will be overridden when
     * execute is called.
     * @param algorithm 
     */
    public void setAlgorithm(GeneticAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    /**
     * Call-back invoked by {@link GeneticAlgorithm#start(boolean)}
     * @param reset 
     */
    public void start(boolean reset) {
        //call-back -> do nothing
    }

    /**
     * Call-back invoked soon before {@link GeneticAlgorithm#onInit(long)}
     */
    public void onInit() {
        //call-back -> do nothing
    }

    /**
     * Call-back called soon after {@link GeneticAlgorithm#onStop(long)}
     */
    public void stop() {
        //call-back -> do nothing
    }

    /**
     * Call-back invoked soon before the {@link Population} evaluation starts 
     * using the default {@link Fitness} defined per {@link GeneticAlgorithm}
     * 
     * @param pop the population that will be evaluated
     * @param forced if each individual of the population will be forced to be evaluated
     */
    public void onEvaluationBegin(Population pop, boolean forced) {
        //call-back -> do nothing
    }

    /**
     * Call-back invoked soon after the evaluation phase has been performed.
     * <br/>
     * WARNING: the method is called before the elapsed time per evaluation is computed
     * so a huge work could affect measurements.
     */
    public void onEvaluationEnd() {
        //call-back -> do nothing
    }

    /**
     * Call-back invoked in substitution to {@link GeneticAlgorithm#evaluateIndividual(jenes.population.Individual)}
     * @param individual 
     */
    public abstract void evaluateIndividual(Individual individual);
    
}
