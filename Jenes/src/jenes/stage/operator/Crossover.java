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

import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.StageException;


/**
 * A genetic class representing a crossover operator. This implementation represents a generic crossover operation with {@link Crossover#spread}
 * parent and the same number of children; the operation is executed according to a crossover probability specified at the creation time.
 * <p>
 * The actual crossover is implemented by subclassing this abstract class and providing the {@link Crossover#spread} and {@link Crossover#cross(Individual[])}
 * implementations: the former is required to specify the number of parents and children involved in each crossover operation; the
 * second is required to specify what crossover algorithm to use.
 * <p>
 * Offsprings have not to be created in the {@link Crossover#cross(Individual[])} method implementation: they
 * are provided by the specified array; these ones have to be modify according to the crossover strategy.
 * At the {@link Crossover#cross(Individual[])} invocation time, the abstract crossover makes each of the array individuals
 * equals to each one of the parents.
 * <p>
 * A {@link Crossover.Statistics} is associated to each crossover operator.
 *
 * @param <T> The class of chromosomes to work with.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see Individual
 * @see Population
 */
public abstract class Crossover<T extends Chromosome>  extends Operator<T> {

    /** Crossover probability */
    protected double probability;
    
    /* Internal data structures */
    private Individual[] offsprings;
    
    private int spread;
    
    /**
     * Constructs a new crossover instance with the specified crossover probability
     *
     * @param probability
     */
    public Crossover(final double probability) {
        this.probability = probability;
        super.statistics = new Statistics();
    }
    
    /**
     * Returns the crossover probability
     *
     * @return the probability
     */
    public final double getProbability() {
        return this.probability;
    }
    
    /**
     * Sets the crossover probability
     *
     * @param probability the new crossover probability
     */
    public final void setProbability(final double probability) {
        this.probability = probability;
    }
    
    /**
     * Returns the number of individuals involved by this crossover operator
     * <p>
     * @return the number of individuals required by crossover
     */
    public abstract int spread();
    
    
    @Override
    public final void init(final GeneticAlgorithm<T> ga) throws StageException {
        super.init(ga);
        spread = this.spread();
        offsprings = new Individual[spread];
    }
    
    public final void process(final Population<T> in, final Population<T> out) throws StageException {
        final long startTime=System.currentTimeMillis();
        ((Statistics)this.statistics).crossovers=0;
        out.setAs(in);
        final int size=out.size();
        for (int k=0; k<size-(spread-1); k+=spread  ) {   // -(spread-1) is necessary for avoiding to exceed
            for (int i=0; i<spread; i++) {
                offsprings[i]=out.getIndividual(k+i);
            }
            if (random.nextBoolean(this.probability)) {
                cross(offsprings);
                ((Statistics) this.statistics).crossovers++;
                for (int i=0; i<spread; i++) {
                    offsprings[i].setNotEvaluated();
                }
            }
        }
        this.statistics.executionTime=System.currentTimeMillis()-startTime;
    }
    
    /**
     * Executes the crossover. At the invocation time the specified array contains the individuals to be
     * modify by cross; at the return time it contains the output crossover individuals.
     *
     * @param offsprings the individuals to be modified.
     */
    protected abstract void cross(Individual<T> offsprings[]);
    
    /**
     * A statistics object holding the number of crossover performed and the time spent to execute them.
     * The statistics is available by two methods:
     * {@link Crossover#getStatistics()} to have a new statistics setted according to the crossover state or
     * {@link Crossover#updateStatistics(jenes.stage.operator.Operator.Statistics)} to modify an existing statistics according
     * to the crossover state.
     * <p>
     * Esamples of use are showed below.
     * <p><blockquote><pre>
     * Crossover.Statistics stat = a_crossover.getStatistics();
     * </pre></blockquote>
     * <p>
     * returns a new statistics object setted according to the specified crossover state.
     * <p><blockquote><pre>
     * Crossover.Statistics stat = new Crossover.Statistics();
     * a_crossover.updateStatistics(stat);
     * </pre></blockquote>
     * <p>
     * modifies the existing statistics according to the specified crossover state.
     */
    public final class Statistics extends Operator<T>.Statistics {
        /** Number of crossovers performed. */
        protected long crossovers;
        
        /**
         * Returns the number of crossovers performed.
         *
         * @return the number of crossovers performed.
         */
        public final long getCrossovers() {
            return this.crossovers;
        }
        
        @Override
        protected final void fill(final Operator<T>.Statistics stats) {
            super.fill(stats);
            ((Statistics)stats).crossovers=this.crossovers;
        }
    }
}