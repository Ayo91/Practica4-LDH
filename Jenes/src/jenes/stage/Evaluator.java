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

import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * This stage performs a population evaluation. In general, individuals that have been alread avaluated,
 * do not require be evaluated again. However, in order to provide full control to the user, the class
 * has the property <code>force</code> that if set, forces the evaluation of all individuals, despite
 * the fact that the some individuals cold have been already evaluated.
 * <p>
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 1.3
 * @since 1.0
 */
public class Evaluator<T extends Chromosome> extends AbstractStage<T> {

    /**
     * The property controlling if evaluation should be extened also to individuals already evaluated.
     */
    protected boolean force;

    /**
     * Creates a new Evaluator instance. By default <colde>force</code> is false.
     */
    public Evaluator() {
        this(false);
    }

    /**
     * Creates a new Evaluator instance, specifying the value of <colde>force</code>.
     *
     * @param force - if true, all individuals are forced to be evaluated in any case.
     */
    public Evaluator(boolean force) {
        this.force = force;
    }

    /**
     * Returns the current value of <code>force</code>. By default this value is false.
     * @return the value of <code>force</code>.
     */
    public boolean isForce() {
        return force;
    }

    /**
     * Sets the value of <code>force</code>.
     * @param force - if true, all individuals are forced to be evaluated in any case.
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * Performs an evaluation of input population.
     * @param in - the input population.
     * @param out - equals the input population
     * @throws StageException
     */
    @Override
    public void process(Population<T> in, Population<T> out) throws StageException {
        super.ga.evaluatePopulation(in);
        in.swap(out);
    }

}
