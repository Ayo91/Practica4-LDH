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
package jenes.stage.operator;

import jenes.utils.Random;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.AbstractStage;

/**
 * A genetic operator used in the evolution process. It is made part of the genetic algorithm structure at the
 * genetic algorithm set up. An operator represents the ultimate stage executing some trasformation on
 * the input {@link Population} in order to obtain the output one.
 * <p>
 * To implement an operator it is necessary to subclass this abstract class.
 *
 * @param <T> The class of chromosomes to work with.
 *
 * @version 1.2
 * @since 1.0
 *
 * @see Individual
 * @see Population
 */
public abstract class Operator<T extends Chromosome> extends AbstractStage<T> {

    /** the operator statistics */
    protected Statistics statistics;
    
    /** the random used by the operator */
    protected Random random;
    
    /**
     * Constructs a new operator
     *
     */
    protected Operator(){
        this.random = Random.getInstance();
    }
    
    /**
     * Returns the operator statistics
     *
     * @return statistics
     */
    public Statistics getStatistics() {
        Statistics stats = new Statistics();
        updateStatistics(stats);
        return statistics;
    }
    
    /**
     * Updates the specified statistics at the statistics operator state
     * <p>
     * @param stats
     */
    public void updateStatistics(Statistics stats) {
        if (statistics == null) throw new IllegalArgumentException("The statistics has not to be null");
        
        statistics.fill(stats);
    }
    
    /**
     * A statistics object holding the time spent to execute the operator.
     * The statistics is available by invoking the {@link Statistics#fill(jenes.stage.operator.Operator.Statistics)}
     * method: it modifies the specified statistics stage according to that of the statistics associated at the operator.
     */
    public class Statistics {
        
        /** the excecution time of the last procssing of the operator */
        protected long executionTime;
        
        /**
         * Constructs a new statistics operator
         *
         */
        public Statistics() {
        }
        
        /**
         * Returns the execution of the last processing of the operator
         *
         * @return the time of the last processing of the operator
         */
        public long getExecutionTime() {
            return executionTime;
        }
        
        /**
         * Fills the specified statistics with the data of
         * the operator statistics. At the end, these statistics
         * will have the same state
         *
         * @param stats the statistics to fill
         */
        protected void fill(Statistics stats) {
            stats.executionTime = this.executionTime;
        }
    }
}