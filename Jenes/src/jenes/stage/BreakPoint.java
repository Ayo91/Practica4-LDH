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

import java.util.ArrayList;
import java.util.List;

import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * Stage representing a break point.
 * It notifies its listeners when its {@link #process(Population, Population)} method is invocated. 
 * The population isn't alterated so the output population is equals to the input one.
 * 
 * @param <T> The class chromosomes flowing across the stage.
 * 
 * @version 1.2
 * @since 1.0
 * 
 */
public class BreakPoint<T extends Chromosome> extends AbstractStage<T> {

	/**
	 * Abstract class representing a break point listener.
	 * When the process method of a break point stage is inciked by a Genetic Algorithm
	 * the {@link #onBreak(int, long)} method of each registred listener is invoked.
	 * 
	 * @author Luigi Troiano
	 * @author Pierpaolo Lombardi
	 * @author Giuseppe Pascale
	 * @author Thierry Bodhuin
	 * 
	 * @version 1.2
	 * 
	 * @since 1.0
	 * 
	 * @see jenes.stage.AbstractStage
	 * 
	 */
	public static abstract class Listener {
		/**
		 * Notify a break point time
		 * 
		 * @param generation the current number generation
		 * @param time the break point time
		 */
		public abstract void onBreak(int generation, long time);
	}
	
	/** the list of the break point listeners */
	private List<Listener> listeners;
	
	/**
	 * Constructs a new BreakPoint stage
	 */
	public BreakPoint() {
		this.listeners = new ArrayList<Listener>();
	}
	
	/**
	 * Adds the specified listener at this break point stage
	 * 
	 * @param listener the listener to notify when the break point process time occurs. 
	 */
	public void add(Listener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes the specified listener between the ones 
	 * 
	 * @param listener the listener to remove
	 */
	public void remove(Listener listener) {
		this.listeners.remove(listener);
	}
	
	public void process(Population<T> in, Population<T> out) throws StageException {
		long now = System.currentTimeMillis();
		for(Listener l : listeners)
			l.onBreak(ga.getGeneration(), now);
		in.swap(out);
	}
}
