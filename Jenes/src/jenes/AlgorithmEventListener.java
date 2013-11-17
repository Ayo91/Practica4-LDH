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
package jenes;

import jenes.chromosome.Chromosome;

/**
 * <code>AlgorithmEventListener</code> provides the interface for capturing events at algorithm level.
 * This iterface should be implemented by all classes interested in being notified by
 * algorithm events, that are:
 * <ul>
 * <li>Start, when the algorithm evolution starts. The first step consists in initiliazing data structures, in particular populations.
 * <li>Init, when the initilization task is accomplished.
 * <li>Stop, when the evolution terminates.
 * </ul>
 * <p>
 * An <code>AlgorithmEventListener</code> is registered to the algorithm by the method {@link GeneticAlgorithm#addAlgorithmEventListener(AlgorithmEventListener) }.
 * The listener is removed by invoking the method {@link GeneticAlgorithm#removeAlgorithmEventListener(AlgorithmEventListener)}.
 * </p>
 * <p>
 * An <code>AlgorithmEventListener<code> can be registered to multiple different algorithms, thus being notified by all of them.
 * <p>
 * Another way to get notified of algorithms events is to override methods {@link GeneticAlgorithm#onStart(long)}, {@link GeneticAlgorithm#onInit(long)} and {@link GeneticAlgorithm#onStop(long)} when subclassing the <code>GeneticAlgorithm</code> class.
 * </p>
 *
 * @param <T> extends Chromosome
 *
 * @version 1.2
 * @since 1.0
 *
 * @see GeneticAlgorithm
 */
public interface AlgorithmEventListener<T extends Chromosome> {
    
    /**
     * Invoked when the genetic algorithm starts
     *
     * @param ga the genetic algorithm that generated this event.
     * @param time the event time expressed in milliseconds
     */
    public void onAlgorithmStart(GeneticAlgorithm<T> ga, long time);
    
    /**
     * Invoked when the genetic algorithm ends
     *
     * @param ga the genetic algorithm that generated this event.
     * @param time the event time expressed in milliseconds
     */
    public void onAlgorithmStop(GeneticAlgorithm<T> ga, long time);
    
    /**
     * Invoked when the genetic algorithm is initialized
     *
     * @param ga the genetic algorithm that generated this event.
     * @param time the event time expressed in milliseconds
     */
    public void onAlgorithmInit(GeneticAlgorithm<T> ga, long time);
    
}
