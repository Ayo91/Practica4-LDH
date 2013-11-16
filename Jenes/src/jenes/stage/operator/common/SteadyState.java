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
package jenes.stage.operator.common;

import jenes.population.Fitness;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.operator.Crowder;
import jenes.stage.operator.Selector;

/**
 * This class implements a steady-state stage.
 * 
 * @version 2.0
 * @since 2.0
 */
public class SteadyState<T extends Chromosome> extends Crowder<T> {

    /**
     * Replacement strategies
     */
    public static enum ReplacementStrategy {
        WORST, RANDOM
    }
    
    /** Default replace strategy */
    public static final ReplacementStrategy DEFAULT_REPLACEMENT_STRATEGY = ReplacementStrategy.WORST;
    
    /** Defualt selection rate */
    public static final int DEFAULT_SELECTION_RATE = 2;
    
    /** Default replacement rate */
    public static final int DEFAULT_REPLACEMENT_RATE = 1;
    
    /** Selector */
    protected Selector<T> selector = null;
    
    /** Replacement strategy */
    protected ReplacementStrategy replacementStrategy = DEFAULT_REPLACEMENT_STRATEGY;
    
    /** Replacement rate */
    protected int replacementRate = DEFAULT_REPLACEMENT_RATE;

    /** 
     * Creates a SteadySate instance
     * 
     * @param selector  selector
     * @param stages    body stages 
     */
    public SteadyState(final Selector<T> selector, final AbstractStage<T>... stages) {
        this(DEFAULT_REPLACEMENT_RATE, DEFAULT_SELECTION_RATE, selector, stages);
    }

    /**
     * Creates a SteadyState instance
     * 
     * @param rr        replacement rate
     * @param sr        selection rate
     * @param selector  selector
     * @param stages    body statges
     */
    public SteadyState(final int rr, final int sr, final Selector<T> selector, final AbstractStage<T>... stages) {

        this.selector = selector;
        this.selector.setSelectionRate(sr);

        for (AbstractStage<T> s : stages) {
            this.body.appendStage(s);
        }

        this.elitist = false;
    }

    /**
     * Returns the selector
     * 
     * @return 
     */
    public final Selector<T> getSelector() {
        return this.selector;
    }

    /**
     * Sets the selector
     * 
     * @param selector 
     */
    public void setSelector(Selector<T> selector) {
        this.selector = selector;
    }

    /**
     * Returns the selection rate
     * 
     * @return 
     */
    public final int getSelectionRate() {
        return this.selector.getSelectionRate();
    }

    /**
     * Sets the selection rate
     * 
     * @param sr    rate 
     */
    public void setSelectionRate(int sr) {
        this.selector.setSelectionRate(sr);
    }

    /**
     * Returns the replacement policy
     * 
     * @return 
     */
    public final ReplacementStrategy getReplacementPolicy() {
        return this.replacementStrategy;
    }

    /**
     * Sets the replacement policy
     * 
     * @param replacement 
     */
    public void setReplacementPolicy(ReplacementStrategy replacement) {
        this.replacementStrategy = replacement;
    }

    /**
     * Returns the replacement rate
     * 
     * @return 
     */
    public final int getReplacementRate() {
        return this.replacementRate;
    }

    /**
     * Sets the replacement rate
     * 
     * @param rate 
     */
    public void setReplacementRate(int rate) {
        this.replacementRate = rate;
    }

    @Override
    protected void preselect(Population<T> in, Population<T> out) {
        selector.process(in, out);
    }

    @Override
    protected void replace(Population<T> initial, Population<T> preselected, Population<T> evolved, Population<T> out) {

        out.setAs(initial);

        int evolvedSize = evolved.size();
        int outSize = out.size();

        int len = evolvedSize < outSize ? evolvedSize : outSize;
        if (replacementRate < len) {
            len = replacementRate;
        }

        if (this.fitness != null) {
            this.fitness.sort(evolved);
        } else {
            Fitness.sort(evolved, this.biggerIsBetter);
        }

        if (this.replacementStrategy == ReplacementStrategy.WORST) {
            out.sort();

            for (int i = 0; i < len; i++) {

                Individual<T> sibling = evolved.getIndividual(i);
                Individual<T> candidate = out.getIndividual(outSize - i - 1);

                boolean better = this.fitness != null ? this.fitness.dominates(sibling, candidate) : Fitness.dominates(sibling, candidate, this.biggerIsBetter);

                if (this.elitist && !better) {
                    break;
                }

                candidate.setAs(sibling);

            }
        } else {
            // RANDOM
            for (int i = 0; i < len; i++) {

                Individual<T> sibling = evolved.getIndividual(i);

                for (int h = 0; h < 3; ++h) {
                    Individual<T> candidate = out.getIndividual(random.nextInt(outSize));
                    boolean better = this.fitness != null ? this.fitness.dominates(sibling, candidate) : Fitness.dominates(sibling, candidate, this.biggerIsBetter);

                    if (this.elitist && !better) {
                        continue;
                    }

                    candidate.setAs(sibling);
                }

            }
        }
    }

    @Deprecated
    @Override
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        super.setBiggerIsBetter(flag, recursively);
        this.selector.setBiggerIsBetter(flag);
    }
}
