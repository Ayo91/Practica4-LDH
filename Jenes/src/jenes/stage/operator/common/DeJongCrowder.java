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
import jenes.stage.operator.Crossover;
import jenes.stage.operator.Crowder;
import jenes.stage.operator.Mutator;
import jenes.stage.operator.Selector;

/**
 * This class implements De Jong crowding. 
 * 
 * @version 2.0
 * @since 2.0
 */
public class DeJongCrowder<T extends Chromosome> extends Crowder<T> {

    /** Provides the available selection methods */
    public static enum SelectionMethod {

        ROULETTE, TOURNAMENT
    }

    /** Provides the available crossover methods */
    public static enum CrossoverMethod {

        SINGLEPOINT, TWOPOINTS
    }

    /** Provides standard mutation methods */
    public static enum MutationMethod {

        NONE, SIMPLE
    }
    
    /** The default selection method */
    public static final SelectionMethod DEFAULT_SELECTION_METHOD = SelectionMethod.TOURNAMENT;
    /** The default crossover method */
    public static final CrossoverMethod DEFAULT_CROSSOVER_METHOD = CrossoverMethod.SINGLEPOINT;
    /** The default crossover probability */
    public static final double DEFAULT_CROSSOVER_PROBABILITY = 0.8;
    /** The default mutation probability */
    public static final double DEFAULT_MUTATION_PROBABILITY = 0.02;
    /** The default crowding selectionRate */
    public static final int DEFAULT_CROWDING_FACTOR = 2;
    
    /** The selctor used */
    protected Selector<T> selector = null;
    
    /** The crowding factor */
    protected int crowdingFactor;

    /**
     * Creates DeJongCrowder
     * 
     * @param sr    selection rate
     * @param cf    crowding rate
     */
    public DeJongCrowder(final int sr, final int cf) {
        this(sr,
                cf,
                DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY);
    }

    /**
     * Creates DeJongCrowder
     * 
     * @param sr            selection rate
     * @param cf            crowding rate
     * @param selmethod     selection method 
     */
    public DeJongCrowder(final int sr, final int cf, final SelectionMethod selmethod) {
        this(sr,
                cf,
                selmethod,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY);
    }

    /**
     * Creates DeJongCrowder
     * 
     * @param sr            selection rate
     * @param cf            crowding rate
     * @param selmethod     selection method 
     * @param crossmethod   crossover method
     * @param crossover     crossover probability
     */
    public DeJongCrowder(final int sr, final int cf, final SelectionMethod selmethod, final CrossoverMethod crossmethod) {
        this(sr,
                cf,
                selmethod,
                crossmethod,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY);
    }

    /**
     * Creates DeJongCrowder
     * 
     * @param sr            selection rate
     * @param cf            crowding rate
     * @param selmethod     selection method 
     * @param crossmethod   crossover method
     * @param crossover     crossover probability
     * @param mutation      mutation probability
     */
    public DeJongCrowder(final int sr, final int cf, final SelectionMethod selmethod, final CrossoverMethod crossmethod, final double crossover, final double mutation) {

        this.selector =
                selmethod == SelectionMethod.TOURNAMENT ? new TournamentSelector<T>(2) : new RouletteWheelSelector<T>();

        this.selector.setSelectionRate(sr);

        Crossover<T> crossover_op =
                crossmethod == CrossoverMethod.SINGLEPOINT ? new OnePointCrossover<T>(crossover) : new TwoPointsCrossover<T>(crossover);

        Mutator<T> mutator = mutation > 0
                ? new SimpleMutator<T>(mutation) : null;

        this.crowdingFactor = cf;

        this.body.appendStage(crossover_op);
        if (mutator != null) {
            this.body.appendStage(mutator);
        }

        this.elitist = false;
    }

    /**
     * Creates DeJongCrowder
     * 
     * @param sr        selection rate
     * @param cf        crowding rate
     * @param selector  selector
     * @param stages    the body stages  
     */
    public DeJongCrowder(final int sr, final int cf, final Selector<T> selector, final AbstractStage<T>... stages) {

        this.selector = selector;
        this.selector.setSelectionRate(sr);

        this.crowdingFactor = cf;

        for (AbstractStage<T> s : stages) {
            this.body.appendStage(s);
        }

        this.elitist = false;
    }

    /**
     * Returns the crowding factor
     * 
     * @return 
     */
    public final int getCrowdingFactor() {
        return this.crowdingFactor;
    }

    /**
     * Sets the crouding factor
     * 
     * @param cf    crowding factor
     */
    public void setCrowdingFactor(int cf) {
        this.crowdingFactor = cf;
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
     * @param sr 
     */
    public void setSelectionRate(int sr) {
        this.selector.setSelectionRate(sr);
    }

    /**
     * Returns the selector
     * @return 
     */
    public final Selector<T> getSelector() {
        return this.selector;
    }

    /**
     * Sets the selector
     * @param selector 
     */
    public void setSelector(Selector<T> selector) {
        this.selector = selector;
    }

    @Override
    protected void preselect(Population<T> in, Population<T> out) {
        selector.process(in, out);
    }

    @Override
    protected void replace(Population<T> initial, Population<T> preselected, Population<T> evolved, Population<T> out) {

        out.setAs(initial);

        int len = out.size();

        double[] diff = null;

        for (Individual<T> offspring : evolved) {

            Individual<T> r = null;
            double s = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < crowdingFactor; ++i) {
                int k = this.random.nextInt(len);
                Individual<T> ind = out.getIndividual(k);

                diff = Chromosome.Util.getDifference(offspring.getChromosome(), ind.getChromosome(), diff);
                double sm = similarity(diff);

                if (sm > s) {
                    r = ind;
                    s = sm;
                }
            }

            if (elitist) {

                Fitness fit = this.getFitness();

                boolean better = fit != null ? fit.dominates(offspring, r) : Fitness.dominates(offspring, r, this.biggerIsBetter);

                if (better) {
                    r.setAs(offspring);
                }
            } else {
                r.setAs(offspring);
            }

        }
    }

    @Deprecated
    @Override
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        super.setBiggerIsBetter(flag, recursively);
        this.selector.setBiggerIsBetter(flag);
    }

    /**
     * XXX
     * @param fit
     * @param recursively 
     */
    @Override
    public void setFitness(Fitness fit, boolean recursively) {
        super.setFitness(fit, recursively);
        this.selector.setFitness(fit);
    }
}
