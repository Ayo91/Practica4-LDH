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

import jenes.chromosome.Chromosome;
import jenes.population.Population;
import jenes.stage.StageException;

/**
 * This class of operators performs fitness scaling. They can be implemented by overwriting the method <code>scale</code>
 * 
 * @version 2.0
 * @since 2.0
 */
public abstract class Scaling <T extends Chromosome>  extends Operator<T> {

    /**
     * Constructor
     */
    public Scaling() {
        super.statistics = new Statistics();
    }

    public final void process(Population<T> in, Population<T> out) throws StageException {

        long startInstant = System.currentTimeMillis();

        out.setAs(in);
        ga.evaluatePopulation(out, true);
        
        this.scale(out);

        this.statistics.executionTime = System.currentTimeMillis() - startInstant;
    }

    /**
     * Method used to scale the fitness of indivduals belonging to given <code>Population</code>
     * <p>
     * @param pop population of individuals to scale
     */
    public abstract void scale(Population<T> pop);

}
