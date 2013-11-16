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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jenes.GeneticAlgorithm;

/**
 * This class provide the basic implementation of a multi-thread enviroinment 
 * for {@link GeneticAlgorithm}
 * 
 * @since 2.0
 */
public abstract class MultiThreadRunner extends Runner {
    
    protected int nthreads;
    /**
     * Instance of the thread pool
     */
    protected ExecutorService threadGroup;

    /**
     * Default constructor. This will instantiate a MultiThreadRunner envoiroinment
     * wich thread number is equals to the maximum number of processor phisical 
     * avaible to the host Java Virtual Machine.
     * <br/> This approach minimize the overhead in managing the thread because
     * the latest JVMs could try to dispach each thread on a different phisical 
     * processor obtaining the best speed-up in performances.
     * @see #MultiThreadRunner(int) 
     * @see Runtime#availableProcessors()
     */
    public MultiThreadRunner() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * This will instantiate a MultiThreadRunner enviroinment with a fixed thread
     * pool sized as defined by argument
     * @param nthreads the number of threads to use to parallelize algorithm execution
     */
    public MultiThreadRunner(int nthreads) {
        this.nthreads = nthreads;
    }

    /**
     * Get the number of threads currently setted for this enviroinment
     * @return 
     */
    public int getNthreads() {
        return nthreads;
    }

    @Override
    public void start(boolean reset) {
        super.start(reset);
        this.threadGroup = Executors.newFixedThreadPool(this.nthreads);
    }

    @Override
    public void stop() {
        super.stop();
        this.threadGroup.shutdown();
    }
    
}
