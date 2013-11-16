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

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;

/**
 * A parallel is formed by differents branches; each branch receives a subpopolation according to the
 * population dispenser used (see {@link Dispenser#distribute(Population, Population[])}).<br>
 * <p>
 * Each branch is a stage and can be added with the {@link #add(AbstractStage)} method.
 * The output population is obtained merging the output of each branch. 
 * The dispenser is responsable of this merging.
 * <p>
 * Note: generally a dispenser can add the same input individual in different branches. So
 * don't modify the stages input individuals.
 * <p>
 *
 * @param <T> The class chromosomes flowing across the stage.
 *
 * @version 2.0
 * @since 1.0
 * 
 * @see     jenes.stage.Dispenser
 * @see     jenes.stage.ExclusiveDispenser
 */
public class Parallel<T extends Chromosome> extends AbstractStage<T> {

    private Dispenser<T> dispenser;
    private Population<T>[] branchesIn;
    private Population<T>[] branchesOut;
    private List<AbstractStage<T>> stages;

    /**
     * Constructs a new parallel stage with the specified dispenser.
     * <p>
     * @param dispenser the dispenser to use in the distribute and merge operations
     */
    @SuppressWarnings("unchecked")
    public Parallel(Dispenser<T> dispenser) {
        this.dispenser = dispenser;
        int span = dispenser.span();
        this.branchesIn = new Population[span];
        this.branchesOut = new Population[span];
        this.stages = new ArrayList<AbstractStage<T>>(span);
    }

    /**
     * Adds a new branch to this parallel stage.
     * <p>
     * @param stage the stage to be added as branch
     */
    public void add(AbstractStage<T> stage) {
        if (this.stages.size() == this.branchesIn.length) {
            throw new IllegalArgumentException("Cannot add another branch at the parallel operator.");
        }
        this.stages.add(stage);
        stage.setBiggerIsBetter(this.biggerIsBetter);
    }

    /**
     * Removes a stage from the parallel andall the stages it contains.
     * The specified stage is a branch stage container, so after it the branch will be empty.
     * <p>
     * @param stage the stage to remove
     */
    public void remove(AbstractStage<T> stage) {
        this.stages.remove(stage);
    }

    /**
     * Removes a specified branch from the parallel
     * <p>
     * @param index the branch index to remove
     */
    public void remove(int index) {
        if (this.branchesIn.length < index) {
            throw new IllegalArgumentException("Cannot remove branch,index out of bound.");
        }
        this.stages.remove(index);
    }

    /**
     * Sets the specified stage at the specified branch number replacing the
     * stage already present
     * <p>
     * @param index the branch number where to set the new stage
     * @param stage the stage to add
     */
    public void setBranch(int index, AbstractStage<T> stage) {
        if (index >= this.branchesIn.length) {
            throw new IllegalArgumentException("Index is out bound, this parallel has " + this.branchesIn.length + "branches");
        }
        this.stages.set(index, stage);
        stage.setBiggerIsBetter(this.biggerIsBetter);
    }

    /**
     * Removes all the branch stages from this parallel
     */
    public void removeAllBranches() {
        this.stages.clear();
    }

    @Override
    public void init(GeneticAlgorithm<T> ga) throws StageException {
        super.init(ga);

        for (int i = 0; i < branchesIn.length; ++i) {

            branchesIn[i] = new Population<T>();
            branchesIn[i].setPool(ga.getPool());

            branchesOut[i] = new Population<T>();
            branchesOut[i].setPool(ga.getPool());
        }

        for (AbstractStage<T> stage : this.stages) {
            stage.init(ga);
        }
    }

    @Override
    public void dispose() {
        for (AbstractStage<T> stage : this.stages) {
            stage.dispose();
        }
    }

    public final void process(Population<T> in, Population<T> out) throws StageException {
        /*
         * Distributes the initial population between its branchs and invokes their process method.
         * At the end it merges all the branche output populations in the final parallel population
         */
        try {

            for (int i = 0; i < branchesIn.length; i++) {
                this.branchesIn[i].clear();
            }

            // distributes the inCopy population
            this.distribute(in, branchesIn);

            // executes the stage on each branch
            for (int i = 0; i < this.branchesIn.length; ++i) {
                //set branchesOut population to the right size
                this.branchesOut[i].resizeAs(branchesIn[i]);
                this.stages.get(i).process(branchesIn[i], branchesOut[i]);
            }

            // makes the final population from the branch subpopulations
            this.mergePopulation(branchesOut, out);

        } catch (Exception e) {
            throw new StageException(e.getMessage(), e);
        }
    }

    /**
     * Distributes the specified population between those ones in the specified array.
     * If some populations within inStagePop are not empty they will contain the initial
     * individuals too at the end of distribute operation.
     * <p>
     * @param in he population to distribute
     * @param branches the array of sub populations to fill
     */
    protected void distribute(Population<T> in, Population<T>[] branches) {
        this.dispenser.distribute(in, branches);
    }

    /**
     * Merges the populations within the specified array in the specified one.
     * If population is not empty it will contain the initial individuals too at
     * the end of merge operation.
     * <p>
     * @param branches the populations to be merged
     * @param out the final population
     */
    protected void mergePopulation(Population<T>[] branches, Population<T> out) {
        this.dispenser.mergePopulation(branches, out);
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
