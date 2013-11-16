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
 * This class implements the deterministic crowding.
 * 
 * @version 2.0
 * @since 2.0
 */
public class DeterministicCrowder<T extends Chromosome> extends Crowder<T> {

    /** Provides the available selection methods */
    public static enum SelectionMethod {
        NONE, ROULETTE, TOURNAMENT
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
    public static final double DEFAULT_MUTATION_PROBABILITY = 0.0;

    /** Selector */
    private Selector<T> selector;
    /** Crossover */
    private Crossover<T> crossover;
    
    /** 
     * Creates a DeterministicCrowder with default options.
     */
    public DeterministicCrowder() {
        this( DEFAULT_SELECTION_METHOD, DEFAULT_CROSSOVER_METHOD, DEFAULT_CROSSOVER_PROBABILITY, DEFAULT_MUTATION_PROBABILITY );
    }

    
    public DeterministicCrowder(final SelectionMethod selmethod, final CrossoverMethod  crossmethod) {
        this( selmethod, crossmethod, DEFAULT_CROSSOVER_PROBABILITY, DEFAULT_MUTATION_PROBABILITY );
    }

    /**
     * Creates a DeterministicCrowder
     * 
     * @param selmethod     selection method
     * @param crossmethod   crossover method
     * @param cp            croosover probability
     * @param mp            mutation probability
     */
    public DeterministicCrowder(final SelectionMethod selmethod, final CrossoverMethod  crossmethod, final double cp, final double mp) {

        switch( selmethod ) {
            case TOURNAMENT: selector = new TournamentSelector<T>(2); break;
            case ROULETTE: selector = new RouletteWheelSelector<T>(); break;
            case NONE:
            default:
                selector = null;
        }

        crossover =
                crossmethod == CrossoverMethod.SINGLEPOINT ? new OnePointCrossover<T>(cp) : new TwoPointsCrossover<T>(cp);

        Mutator<T> mutator = mp > 0 ?
                new SimpleMutator<T>(mp) : null;

        this.body.appendStage(crossover);
        if( mutator != null )
            this.body.appendStage(mutator);

        this.elitist = true;
    }

    /**
     * Creates DeterministicCrowder providing selector and crossover operator
     * 
     * @param selector      selection operator
     * @param crossover     crossover operator
     */
    public DeterministicCrowder(final Selector<T> selector, final Crossover<T>  crossover) {
        this(selector, crossover, null);
    }
    
    /**
     * Creates DeterministicCrowder providing selector, crossover and mutator operators
     * 
     * @param selector      selection operator
     * @param crossover     crossover operator
     * @param mutator       mutation operator
     */
    public DeterministicCrowder(final Selector<T> selector, final Crossover<T>  crossover, final Mutator<T> mutator) {

        this.selector = selector;
        this.crossover = crossover;

        this.body.appendStage(crossover);
        if( mutator != null )
            this.body.appendStage(mutator);

        this.elitist = true;
    }

    @Override
    protected void preselect(Population<T> in, Population<T> out) {
        if( selector!=null )
            selector.process(in, out);
    }

    @Override
    protected void replace(Population<T> initial, Population<T> preselected, Population<T> evolved, Population<T> out) {

        out.setAs(preselected);

        int len = evolved.size();

        int cf = crossover.spread();

        double[] diff = null;
        for (int i = 0; i < len; i += cf) {
            
            // This is for the remainder of last group that could be smaller than crowdingFactor
            int rm = cf < len-i-1 ? cf : len-i-1;
            for (int j = 0; j < rm; ++j) {

                Individual<T> offspring = out.getIndividual(i + j);

                Individual<T> r = null;
                double s = Double.NEGATIVE_INFINITY;

                for (int k = 0; k < rm; ++k) {

                    Individual<T> ind = evolved.getIndividual(i + k);

                    diff = Chromosome.Util.getDifference(offspring.getChromosome(), ind.getChromosome(), diff);
                    double sm = similarity(diff);

                    if (sm > s) {
                        r = ind;
                        s = sm;
                    }

                }

                if (elitist) {
                    
                    Fitness<T> fit = this.getFitness();
                                        
                    boolean better = fit != null ? fit.dominates(r, offspring) : Fitness.dominates(r, offspring, this.biggerIsBetter);
                    
                    if( better ) {
                        offspring.setAs(r);
                    }
                } else {
                    offspring.setAs(r);
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
