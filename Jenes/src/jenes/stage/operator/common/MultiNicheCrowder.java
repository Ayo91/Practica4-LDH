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
import jenes.stage.operator.Crossover;
import jenes.stage.operator.Crowder;
import jenes.stage.operator.Mutator;
import jenes.stage.operator.Selector;

/**
 * Implementation of multi-niche crowding.
 * 
 * @version 2.0
 * @since 2.0
 */
public class MultiNicheCrowder<T extends Chromosome> extends Crowder<T> {

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
    /** The default selection factor */
    public static final int DEFAULT_SELECTION_FACTOR = 5;
    /** The default crowding factor */
    public static final int DEFAULT_CROWDING_FACTOR = 5;
    /** The default replacement factor */
    public static final int DEFAULT_REPLACEMENT_FACTOR = 5;
    /** Selection factor */
    protected int selectionFactor;
    /** Crowding factor */
    protected int crowdingFactor;
    /** Replacement factor */
    protected int replacementFactor;
    /** Crossover operator */
    protected Crossover<T> crossover;
    /**
     * Selector operator
     */
    protected Selector<T> selector;

    /**
     * Creates MultiNicheCrowder using default options.
     */
    public MultiNicheCrowder() {
        this(DEFAULT_SELECTION_METHOD,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_SELECTION_FACTOR,
                DEFAULT_CROWDING_FACTOR,
                DEFAULT_REPLACEMENT_FACTOR);
    }

    /**
     * Creates a MultiNicheCrowder instance
     * 
     * @param crossmethod   crossover method
     */
    public MultiNicheCrowder(final SelectionMethod selection) {
        this(selection,
                DEFAULT_CROSSOVER_METHOD,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_SELECTION_FACTOR,
                DEFAULT_CROWDING_FACTOR,
                DEFAULT_REPLACEMENT_FACTOR);
    }

    /**
     * Creates a MultiNicheCrowder instance
     * 
     * @param crossmethod   crossover method
     */
    public MultiNicheCrowder(final SelectionMethod selection, final CrossoverMethod crossmethod) {
        this(selection,
                crossmethod,
                DEFAULT_CROSSOVER_PROBABILITY,
                DEFAULT_MUTATION_PROBABILITY,
                DEFAULT_SELECTION_FACTOR,
                DEFAULT_CROWDING_FACTOR,
                DEFAULT_REPLACEMENT_FACTOR);
    }

    /**
     * Creates a MultiNicheCrowder instance
     * 
     * @param crossmethod   crossover method
     * @param crossover     crossover probability
     * @param mutation      mutation probability
     */
    public MultiNicheCrowder(final SelectionMethod selection, final CrossoverMethod crossmethod, final double crossover, final double mutation) {
        this(selection,
                crossmethod,
                crossover,
                mutation,
                DEFAULT_SELECTION_FACTOR,
                DEFAULT_CROWDING_FACTOR,
                DEFAULT_REPLACEMENT_FACTOR);
    }

    /**
     * Creates a MultiNicheCrowder instance
     * 
     * @param crossmethod   crossover method
     * @param crossover     crossover probability
     * @param mutation      mutation probability
     * @param sf            selection factor
     * @param cf            crowding factor
     * @param rf            replacement factor
     */
    public MultiNicheCrowder(final SelectionMethod selectionmethod, final CrossoverMethod crossmethod, final double crossover, final double mutation, final int sf, final int cf, final int rf) {

        this.selector = selectionmethod == SelectionMethod.ROULETTE ? new RouletteWheelSelector<T>() : new TournamentSelector<T>(3);
        this.crossover = crossmethod == CrossoverMethod.SINGLEPOINT ? new OnePointCrossover<T>(crossover) : new TwoPointsCrossover<T>(crossover);

        Mutator<T> mutator = mutation > 0 ? new SimpleMutator<T>(mutation) : null;

        this.body.appendStage(this.crossover);
        if (mutator != null) {
            this.body.appendStage(mutator);
        }

        this.selectionFactor = sf;
        this.crowdingFactor = cf;
        this.replacementFactor = rf;

        this.elitist = true;
    }

    /**
     * Returns the crowding factor
     * 
     * @return 
     */
    public final int getCrowdingFactor() {
        return crowdingFactor;
    }

    /**
     * Sets the crowding factor
     * 
     * @param crowdingFactor    the factor 
     */
    public void setCrowdingFactor(int crowdingFactor) {
        this.crowdingFactor = crowdingFactor;
    }

    /**
     * Returns the replacement factor
     * 
     * @return 
     */
    public final int getReplacementFactor() {
        return replacementFactor;
    }

    /**
     * Sets the replacement factor
     * 
     * @param replacementFactor the factor 
     */
    public void setReplacementFactor(int replacementFactor) {
        this.replacementFactor = replacementFactor;
    }

    /**
     * Return the selection factor
     * 
     * @return 
     */
    public final int getSelectionFactor() {
        return selectionFactor;
    }

    /**
     * Sets the replacement factor
     * 
     * @param selectionFactor   the factor 
     */
    public void setSelectionFactor(int selectionFactor) {
        this.selectionFactor = selectionFactor;
    }

    @Override
    protected void preselect(Population<T> in, Population<T> out) {

        out.resizeAs(in);

        int len = in.size();

        int spread = this.crossover.spread();

        for (int i = 0; i < len; i += spread) {

            Individual<T> parent = this.selector.select(in);
            out.getIndividual(i).setAs(parent);

            int rm = spread <= len - i - 1 ? spread : len - i - 1;
            for (int j = 1; j < rm; j++) {
                Individual<T> mate = this.getMostSimilar(parent, in, selectionFactor);
                out.getIndividual(i + j).setAs(mate);
            }
        }
    }

    @Override
    protected void replace(Population<T> initial, Population<T> preselected, Population<T> evolved, Population<T> out) {

        out.setAs(initial);

        boolean[] biggerBetter = this.fitness != null ? this.fitness.getBiggerIsBetter() : new boolean[]{this.biggerIsBetter};
        for (Individual<T> ind : evolved) {

            Individual<T> candidate = null;
            for (int i = 0; i < crowdingFactor; ++i) {

                Individual<T> sibling = this.getMostSimilar(ind, out, replacementFactor);
                if (candidate == null || Fitness.dominates(candidate, sibling, biggerBetter)) {
                    candidate = sibling;
                }
            }

            //checks if ind dominates candidates
            boolean better = Fitness.dominates(ind, candidate, biggerBetter);

            if (!elitist || better) {
                candidate.setAs(ind);
            }

        }
    }

    /**
     * Finds the most similar individual to sample
     * 
     * @param sample    the individual to compare 
     * @param pop       the population to search
     * @param trials    the number of trials to perform
     * @return          the most similar individual
     */
    private Individual<T> getMostSimilar(Individual<T> sample, Population<T> pop, int trials) {
        int len = pop.size();

        double[] diff = null;

        Individual<T> sibling = null;
        double s = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < trials; ++j) {

            int h = this.random.nextInt(len);
            Individual<T> ind = pop.getIndividual(h);

            diff = Chromosome.Util.getDifference(sample.getChromosome(), ind.getChromosome(), diff);
            double sm = similarity(diff);

            if (sm > s) {
                sibling = ind;
                s = sm;
            }
        }

        return sibling;
    }
}
