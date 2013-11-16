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
 * A listener of the genetic algorithm generation event. Such a listener is notified after a
 * generation step is executed.
 * <p>
 * A <code>GenerationEventListener</code> is registered to the algorithm by the method {@link GeneticAlgorithm#addGenerationEventListener(GenerationEventListener)}.
 * The listener is removed by invoking the method {@link GeneticAlgorithm#removeGenerationEventListener(GenerationEventListener)}.
 * </p>
 * <p>
 * A <code>GenerationEventListener</code> can be registered to multiple different algorithms, thus being notified by all of them.
 * <p>
 * Another way to get notified of algorithms events is to override method {@link GeneticAlgorithm#onGeneration(long)} when subclassing the <code>GeneticAlgorithm</code> class.
 * </p>
 *
 * @param <T> extends Chromosome
 *
 * @version 1.2
 * @since 1.0
 *
 * @see GeneticAlgorithm
 */
public interface GenerationEventListener<T extends Chromosome> {
    
    /**
     * Invoked when at the end of one generation step
     *
     * @param ga the genetic algorithm generating the event
     * @param time the event time expressed in milliseconds
     */
    public void onGeneration(GeneticAlgorithm<T> ga, long time);
}
