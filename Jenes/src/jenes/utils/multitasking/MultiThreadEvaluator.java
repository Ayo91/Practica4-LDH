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

import java.util.Stack;
import jenes.population.Fitness;
import jenes.population.Individual;
import jenes.population.Population;
import jxl.common.Logger;

/**
 * This class represent a simple implementation of a multi thread runner
 * 
 * @since 2.0
 * 
 * @see MultiThreadRunner
 */
public class MultiThreadEvaluator extends MultiThreadRunner {

    /**
     * shared counter... number of tasks currently in queue
     */
    private int todo = 0;
    /**
     * Due to concurrency the multi thread evaluator clones a fixed number of 
     * {@link Fitness} instances
     */
    private Stack<Fitness> fitnessPool = new Stack<Fitness>();
    /**
     * The evaluation Task cache pool
     */
    private Stack<EvaluationTask> tasksPool = new Stack<EvaluationTask>();

    /**
     * Default constructor that define the thread number to use
     * @param nthreads 
     * @see MultiThreadRunner#MultiThreadRunner(int) 
     */
    public MultiThreadEvaluator(int nthreads) {
        super(nthreads);
    }

    /**
     * Default constructor that generates an execution enviroinment with a number
     * of threads equals to the number of phisical cores.
     * @see MultiThreadRunner#MultiThreadRunner()
     */
    public MultiThreadEvaluator() {
        super();
    }

    @Override
    public void onEvaluationBegin(Population pop, boolean forced) {
        this.todo = 0;

        if (super.algorithm.getFitness() == null) {
            throw new IllegalStateException("Jenes: fitness must be not null to be used by MultiThreadEvaluator");
        }

        if (super.algorithm.isFitnessChanged() || this.fitnessPool.isEmpty()) {
            this.refreshFitnessPool();
        }

        //check for task pool dimension
        int size = this.tasksPool.size();
        int required = pop.size();

        int offset = required - size;
        for (int i = offset; i > 0; i--) {
            this.tasksPool.push(new EvaluationTask());
        }

    }

    @Override
    public synchronized void evaluateIndividual(Individual individual) {

        EvaluationTask task = this.tasksPool.pop(); //recycle
        task.individual = individual;

        this.todo++;
        super.threadGroup.submit(task);

    }

    private void refreshFitnessPool() {
        int size = super.getNthreads();

        Fitness fit = super.algorithm.getFitness();

        this.fitnessPool.clear();
        for (int i = 0; i < size; ++i) {
            fitnessPool.add(fit.clone());
        }
    }

    private synchronized Fitness getFitness() {
        assert !this.fitnessPool.isEmpty() : "Jenes: fitness could not be empty at this point";
        return this.fitnessPool.remove(this.fitnessPool.size() - 1);
    }

    private synchronized void releaseFitness(Fitness fit) {
        this.fitnessPool.add(fit);
        this.todo--;
        if (this.todo <= 0) {
            this.notifyAll(); //wait until all tasks have been completed
        }
    }

    @Override
    public synchronized void onEvaluationEnd() {
        try {
            
            if (this.todo > 0) { //called only by producer thread (main thread)
                this.wait(); //wait until all tasks are been completed
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass()).error(this, ex);
        }
    }
    
    /**
     * Evaluation task
     */
    private class EvaluationTask implements Runnable {

        private Individual individual;

        public void run() {
            Fitness fit = MultiThreadEvaluator.this.getFitness();
            fit.evaluate(this.individual);

            //relase fitness to enable others thread to start
            MultiThreadEvaluator.this.releaseFitness(fit);

            //release task for caching
            MultiThreadEvaluator.this.tasksPool.push(this);
        }
    }
}
