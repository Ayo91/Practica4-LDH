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

import java.util.List;
import java.util.Vector;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * A sequence is like a "pipe" of other stages.<br>
 * Stages are executed sequentially in the order they are added to the sequence.
 * Each stage receives an input population (produced as output by the previous stage)
 * and produces an output population.
 *
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 2.0
 * @since 1.0
 *
 */
public class Sequence<T extends Chromosome> extends AbstractStage<T> {

    private List<AbstractStage<T>> stages;
    private Population<T> internal = null;

    /**
     * Constructs a new sequence stage.
     *
     */
    public Sequence() {
        this.stages = new Vector<AbstractStage<T>>();
    }

    /**
     * Adds the specified stage at the end of this sequence
     * <p>
     * @param stage the stage to add
     */
    public void appendStage(AbstractStage<T> stage) {
        this.stages.add(stage);
        if (stage.getFitness() == null) {
            if (this.fitness != null) {
                stage.setFitness(this.fitness, true);
            } else {
                stage.setBiggerIsBetter(this.biggerIsBetter);
            }
        }

    }

    /**
     * Adds the specified stage to the specified position
     * <p>
     * @param stage the stage to add
     * @param pos the position where to insert the new stage
     */
    public void insertStageAt(AbstractStage<T> stage, int pos) {
        this.stages.add(pos, stage);
        if (stage.getFitness() == null) {
            if (this.fitness != null) {
                stage.setFitness(this.fitness, true);
            } else {
                stage.setBiggerIsBetter(this.biggerIsBetter);
            }
        }
    }

    /**
     * Removes all the stages from this sequence
     *
     */
    public void removeAll() {
        this.stages.clear();
    }

    /**
     * Returns the number of stages
     * @return the number of stages
     */
    public int getSize() {
        return stages.size();
    }

    /**
     * Returns the stage at the specified position
     *
     * @param pos index of the stage to return
     * @return The stage at the specified position
     */
    public AbstractStage<T> getStageAt(int pos) {
        return this.stages.get(pos);
    }

    /**
     * Removes the stage at the specified position
     * <p>
     * @param pos the position of the stage to remove
     * @return the AbstractStage instance that has been removed
     */
    public AbstractStage<T> removeAt(int pos) {
        return this.stages.remove(pos);
    }

    /**
     * Initializes all of its internal stages
     */
    @Override
    public void init(GeneticAlgorithm<T> ga) {
        super.init(ga);

        this.internal = new Population<T>();
        this.internal.setPool(ga.getPool());

        //sets the ga to inner stages
        for (AbstractStage<T> stage : this.stages) {
            stage.init(ga);
        }
    }

    /**
     * Disposes all of its internal stages
     */
    @Override
    public void dispose() {
        for (AbstractStage<T> stage : this.stages) {
            stage.dispose();
        }
    }

    /**
     * Invokes the process method on all of its internal stages
     */
    public final void process(Population<T> in, Population<T> out) throws StageException {
        int ns = stages.size();
        if (ns > 0) {
            try {
                // Attention: the first Stage should get as input the internal Population
                // as GeneticAlgorithm invoke the process method, for performance reason, passing as parameters
                // input and output two references to the same population
                GeneticAlgorithm.ResizeStrategy rs = ga.getResizeStrategy();
                switch (rs) {
                    case AUTO:
                        internal.resizeAs(in);
                        break;
                    case EMPTY:
                        internal.clear();
                        break;
                }
                stages.get(0).process(in, internal);

                Population<T> p1 = internal;
                Population<T> p2 = out;
                for (int i = 1; i < ns; i++) {
                    switch (rs) {
                        case AUTO:
                            p2.resizeAs(in);
                            break;
                        case EMPTY:
                            p2.clear();
                            break;
                    }
                    stages.get(i).process(p1, p2);
                    p1.swap(p2);
                }
                p1.swap(out);
            } catch (Exception e) {
                throw new StageException(e.getMessage(), e);
            }
        }
    }

    @Deprecated
    @Override
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        this.biggerIsBetter = flag;
        if (recursively) {
            for (AbstractStage<T> s : stages) {
                s.setBiggerIsBetter(flag, recursively);
            }
        }
    }

    @Override
    public void setFitness(Fitness fit, boolean recursively) {
        super.setFitness(fit, recursively);
        if (recursively) {
            for (AbstractStage<T> s : stages) {
                s.setFitness(fit, recursively);
            }
        }
    }
}