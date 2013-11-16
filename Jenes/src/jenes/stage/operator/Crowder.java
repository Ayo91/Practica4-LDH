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

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;
import jenes.stage.Sequence;
import jenes.stage.StageException;

/**
 * This class provides abstraction to crowders. A crowder perform preselection 
 * before its body processes the population, and controls the replacement after.
 * 
 * @version 2.0
 * @since 2.0
 */
public abstract class Crowder<T extends Chromosome>  extends Operator<T> {

    /** Internal input population */
    private Population<T> int_in;
    /** Internal output population */
    private Population<T> int_out;

    /** Body */
    protected Sequence<T> body = new Sequence<T>();
    /** Elitism */
    protected boolean elitist = false;

    /**
     * Creates a new crowder
     */
    public Crowder() {
        super.statistics = new Statistics();
    }

    /**
     * Says if the crowder is elitist or not.
     * 
     * @return 
     */
    public final boolean isElitist() {
        return this.elitist;
    }

    /**
     * Sets the crowder elistism. If true, offspings replace parents only if they are better.
     * 
     * @param flag 
     */
    public void setElitist(boolean flag) {
        this.elitist = flag;
    }

    @Override
    public void init(GeneticAlgorithm<T> ga) {

        super.init(ga);

        int_in = new Population<T>();
        int_in.setPool(ga.getPool());

        int_out = new Population<T>();
        int_out.setPool(ga.getPool());

        this.body.init(ga);
    }


    /**
     * Performs crowding processing, according the the following scheme:
     * <ol>
     * <li>pre = preselect(in)</li>
     * <li>evo = body.process(pre)</li>
     * <li>out = replace(evo)</li>
     * </ol>
     * @param in
     * @param out
     * @throws StageException 
     */
    @Override
    public  void process(Population<T> in, Population<T> out) throws StageException {

        long startInstant = System.currentTimeMillis();

        preselect(in, int_in);

        body.process(int_in, int_out);
        if( elitist )
            this.ga.evaluatePopulation(int_out);

        replace(in, int_in, int_out, out);

        this.statistics.executionTime = System.currentTimeMillis() - startInstant;
    }

    /**
     * Preselection of individuals before processing.
     * 
     * @param in    input population
     * @param out   output population
     */
    protected abstract void preselect(Population<T> in, Population<T> out);

    /**
     * Implements the replacement policy.
     *
     * @param initial - the initial population
     * @param preselected - the population resulting from preselection
     * @param evolved - the part of population that has evolved
     * @param out - the output population
     */
    protected abstract void replace(Population<T> initial, Population<T> preselected, Population<T> evolved, Population<T> out );

    /**
     * Computes a degree of similarity given the genetic difference vector between two chromosomes.
     * By default, similarity is computed as reprocical of eucledian distance between genes.
     * Other definitions are possible by overriding this method in subclasses.
     *
     * @param diff - the difference vector
     * @return similarity degree
     */
    public double similarity(double[] diff) {
        double s = 0;
        for( int i = 0; i < diff.length; ++i ) {
            if( !Double.isNaN(diff[i]) )
                s += diff[i]*diff[i];
        }
        return 1/Math.sqrt(s);
    }

    @Deprecated
    @Override
    public void setBiggerIsBetter(boolean flag, boolean recursively) {
        this.biggerIsBetter=flag;
        if (recursively) {
            this.body.setBiggerIsBetter(flag, recursively);
        }
    }
    
   @Override
    public void setFitness(Fitness fit, boolean recursively) {
        super.setFitness(fit, recursively);
        if (recursively) {
            this.body.setFitness(fit, recursively);
        }        
    }
    

    /**
     * Return the crowder body
    * 
     * @return the body
     */
    public Sequence<T> getBody() {
        return this.body;
    }


}
