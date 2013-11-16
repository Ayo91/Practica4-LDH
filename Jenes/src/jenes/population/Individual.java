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
package jenes.population;

import java.io.Serializable;

import java.util.Arrays;
import jenes.chromosome.Chromosome;
import jenes.statistics.StatisticsLogger.Loggable;
import jenes.statistics.StatisticsLogger.LoggableStatistics;

/**
 * Individual represents a solution processed by a genetic algorithm.
 * It is made of a {@link Chromosome} representing its genoma, and a score representing its fitness.
 * To be valid, the score must be non-negative.
 * <p>
 * A key point is how to compare individuals.
 * There two ways in comparing individuals: by fitness and chromosome.
 * The {@link #compareTo(Individual)} method tests if scores are equal or which one is bigger
 * regardless at their genoma.
 * The {@link #equals(Individual)} methods compares the chromosome. In order to gain efficiency in
 * comparing individuals, the {@link Individual#isSameChromosomeSameFitness() } property is used to shortcut
 * the procedure.
 * If this property is true, <code>equals</code> returns true according to the score, thus avoiding to
 * perform an expensive chromosome comparison.
 * Indeed, by setting the <code>sameChromosomeSameFitness</code> property to true, we assume that is not possible
 * to have two different chromosome with the same score. Appliability fo this property depends on the
 * problem and search landscape characteristics.
 * Obviously, we assume that individuals with the same chromosome must show the fitness value.
 * <p>
 * In complex search spaces, not every solution is feasible. The solution feasability is controlled
 * by the <code>legal</code> property.
 *
 * @param <T> The type of chromosome.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see jenes.population.Population
 * @see jenes.chromosome.Chromosome
 */
public final class Individual<T extends Chromosome> implements Cloneable, Serializable, Comparable<Individual<T>> {

    private static final long serialVersionUID = -7644577572400307010L;
    /** the default individual's rank **/
    public static final int UNRANKED = -1;
    /** flag saying if equal chromosomes have the same fitness */
    private static boolean sameChromosomeSameFitness = true;
    /** the individual's chromosome */
    private T chromosome;
    /** the individual's score */
    private double score[];
    /** it says if the individual is evaluated or not */
    private boolean evaluated;
    /** it says if the individual is legal or not */
    private boolean legal = true;
    /** the population it belongs to */
    private Population<T> population;
    /** the subpopulation the individual belongs to */
    private int speciem = 0;
    /** the individual's rank */
    private int rank = UNRANKED;
    /** the statistics of individual */
    private Statistics<T> statistics = null;

    /**
     * Creates an <code>Individual</code> with no genome.
     */
    public Individual() {
        this(null, null);
    }

    /**
     * Creates an <code>Individual</code> with a specific chromosome and a number of scores.
     * @param n             number of scores
     * @param chromosome    individual's chromosome
     */
    public Individual(final int n, final T chromosome) {
        this.chromosome = chromosome;
        this.resetScores(n);
    }

    /**
     * Creates an <code>Individual</code> with the specified genome and a given score array(fitness value).
     *
     * @param chromosome	individual's genome
     * @param score		individual's score array
     */
    public Individual(final T chromosome, final double... score) {
        this.chromosome = chromosome;
        this.setScore(score);
    }

    /**
     * Constructs a new <code>Individual</code> as a copy of another <code>Individual</code>.
     * <p>
     * @param ind the source <code>Individual</code>.
     */
    @SuppressWarnings("unchecked")
    public Individual(final Individual<T> ind) {
        this.chromosome = (T) ind.getChromosome().clone();

        this.score = new double[ind.score.length];
        System.arraycopy(ind.score, 0, this.score, 0, this.score.length);

        this.evaluated = ind.evaluated;
        this.legal = ind.legal;
        this.speciem = ind.speciem;
        this.population = null;
    }

    /**
     * Return the {@link Population} that contains this <code>Individual</code>
     * <p>
     * @return individual's population
     */
    public final Population<T> getPopulation() {
        return this.population;
    }

    /**
     * Sets the <code>Individual</code>'s population
     * <p>
     * @param pop   individual's population
     */
    final void setPopulation(Population<T> pop) {
        this.population = pop;
    }

    /**
     * Returns the <code>Individual</code>'s genome
     * <p>
     * @return the individual's genome
     */
    public final T getChromosome() {
        return chromosome;
    }

    /**
     * Sets the <code>Individual</code>'s genome
     * <p>
     * @param chromosome the <code>Individual</code>'s genome
     */
    public final void setChromosome(final T chromosome) {
        this.chromosome = chromosome;
        this.setNotEvaluated();
    }

    /**
     * Returns the number of objectivies for which this <code>Individual</code> is evaluated
     * <p>
     * @return the score array's lengh
     */
    public final int getNumOfObjectives() {
        return this.score.length;
    }

    /**
     * Returns the <code>Individual</code>'s fitness value
     * This score is meaningful only if <code>isEvaluated()</code> returns true.
     * Otherwise the score is equal to <code>Double.NaN</code>.
     * <p>
     * @return	the <code>Individual</code>'s fitness value
     */
    public final double getScore() {
        return this.score == null ? Double.NaN : this.score[0];
    }

    /**
     * Return the score at a specific position
     * 
     * @param i index of array's score
     * @return score
     */
    public final double getScore(int i) {
        return this.score[i];
    }

    /**
     * Returns all scores
     * 
     * @return the array of scores
     */
    public final double[] getAllScores() {
        return this.score;
    }

    /**
     * Reset the array of scores by setting each value to <code>Double.NaN</code>. 
     * If the value given as argument is different from the actual length 
     * of scores' array, a new array is created.
     * 
     * @param m     lenght of scores' array
     */
    public final void resetScores(int m) {
        if (this.score == null || m != this.score.length) {
            this.score = new double[m];
        }

        for (int i = 0; i < m; ++i) {
            this.score[i] = Double.NaN;
        }

        this.evaluated = false;
    }

    /**
     * Sets the <code>Individual</code>'s fitness value.
     * 
     * @param score	the <code>Individual</code>'s fitness value
     */
    public final void setScore(final double... score) {

        if (score != null && score.length > 0) {

            if (this.score == null || this.score.length != score.length) {
                this.score = new double[score.length];
                System.arraycopy(score, 0, this.score, 0, this.score.length);
            }

            for (int i = 0; i < this.score.length; ++i) {
                this.score[i] = score[i];
            }

            this.evaluated = checkEvaluated();

        } else {
            this.resetScores(1);
        }
    }

    /**
     * Set the score at specific position and check if this <code>Individual</code> is evaluated
     * 
     * @param score     value of score
     * @param i         array's index
     */
    public final void setScore(final double score, final int i) {
        this.score[i] = score;
        this.evaluated = checkEvaluated();
    }

    /**
     * Says if this <code>Individual</code> is evaluated.
     * <p>
     * @return <code> false </code> if array's score contains at least 
     * a <code>Double.NaN</code> value. 
     * <code> true </code> otherwise
     */
    private boolean checkEvaluated() {
        for (double s : score) {
            if (s == Double.NaN) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this individual as not evaluated.
     */
    public final void setNotEvaluated() {
        this.evaluated = false;
        for (int i = 0; i < this.score.length; ++i) {
            this.score[i] = Double.NaN;
        }
    }

    /**
     * Returns the individual's rank
     * 
     * @return the individual's rank
     */
    public int getRank() {
        if (this.population == null || !this.population.isSorted()) {
            return Individual.UNRANKED;
        }
        return rank;
    }

    /**
     * Set the individual's rank
     * 
     * @param individual's rank
     */
    void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns the subpopulation the individual belongs to
     * 
     * @return the subpopulation id
     */
    public int getSpeciem() {
        return speciem;
    }

    /**
     * Sets the subpopulation the individual belongs to
     * 
     * @param speciem - the subpopulation id
     */
    public void setSpeciem(int speciem) {
        this.speciem = speciem;
    }

    /**
     * Returns a cloned instance of this class
     * <p>
     * @return a clone of this Individual
     */
    @Override
    public final Individual<T> clone() {
        return new Individual<T>(this);
    }

    /**
     * Randomizes the genome (chromosome) of this <code>Individual</code>
     */
    public final void randomize() {
        this.chromosome.randomize();
        this.setNotEvaluated();
    }

    /**
     * Sets this <code>Individual</code> as the specified one.
     * <p>
     * @param individual        the model individual
     */
    public final void setAs(final Individual<T> individual) {
        this.setAs(individual, false);
    }

    /**
     * Sets this <code>Individual</code>'s genome equal
     * to the specified <code>Individual</code>'s one
     *
     * @param individual the instance to copy as
     */
    @SuppressWarnings("unchecked")
    public final void setAs(final Individual<T> individual, final boolean full) {

        if (this.score == null || this.score.length != individual.score.length) {
            this.score = new double[individual.score.length];
        }

        System.arraycopy(individual.score, 0, this.score, 0, this.score.length);

        if (this.chromosome != null) {
            this.chromosome.setAs(individual.getChromosome());
        } else {
            this.chromosome = (T) individual.getChromosome().clone();
        }

        this.evaluated = individual.evaluated;
        this.legal = individual.legal;
        this.speciem = individual.speciem;

        this.rank = individual.getRank();
        if (full) {
            this.population = individual.population;
        }

    }

    /**
     * Tests if the <code>Individual</code> is evaluated
     *
     * @return <code>true</code> if the <code>Individual</code> is evaluated; <code>false</code> otherwise
     */
    public final boolean isEvaluated() {
        return this.evaluated;
    }

    /**
     * Returns a full textual representation of this Individual
     *
     * @return	representation
     */
    public final String toCompleteString() {

        return String.format("%s: scored %s, legal = %b, speciem = %d",
                chromosome != null ? chromosome.toString() : "[]", Arrays.toString(this.score),
                this.isLegal(),
                this.getSpeciem());
    }

    @Override
    public final String toString() {
        return String.format("%s: scored %s",
                chromosome != null ? chromosome.toString() : "[]", Arrays.toString(this.score));
    }

    /**
     * Tests if this Individual is a legal individual
     * i.e is a feasible solution.
     *
     * @return	<code>true</code> if this individual is a legal solution; <code>flase</code> otherwise.
     */
    public final boolean isLegal() {
        return this.legal;
    }

    /**
     * Sets the legal state of this individual
     *
     * @param legal <code>true</code> if legal; <code>false</code> otherwise.
     */
    public final void setLegal(final boolean legal) {
        this.legal = legal;
    }

    /**
     * Compares this individual with the specified individual.
     * Returns a negative, zero or positive value as
     * the individual is lesser than, equal to, or greater than the specified individual.
     * The comparison is based on the chromosome score.
     *
     * Note: this class has a natural ordering that is inconsistent with equals.
     * @param ind the individual to be compared.
     */
    public final int compareTo(final Individual<T> ind) {

        if (this.getScore() > ind.getScore()) {
            return 1;
        }
        if (ind.getScore() > this.getScore()) {
            return -1;
        }

        return 0;
    }

    /**
     * Indicates whether the other individual is "equal to" this one.
     * i.e if they have the same fitness and the same chromosome
     *
     * @param ind the reference individual with which to compare.
     * @return <code>true</code> if the individual has the same chromosome and score as the other; <code>false</code> otherwise.
     *
     */
    @SuppressWarnings("unchecked")
    public final boolean equals(final Individual<T> ind) {

        if (this == ind) {
            return true;
        }

        if (ind.chromosome != null && this.chromosome != null && ind.chromosome.equals(this.chromosome)) {
            if (this.isScoreEqual(ind.score)) {
                return true;
            } else {
                if (sameChromosomeSameFitness) {
                    throw new IllegalStateException("[Jenes]: when sameChromosomeSameFitness=true individuals with the same genome have to be equal fitted!");
                } else {
                    return false;
                }
            }

        } else {
            // Equivalent to return ind.chromosome == null && this.chromosome == null;            
            return ind.chromosome == this.chromosome;
        }

    }

    /**
     * Says if the score' array of this <code> Individual </code> has the same lenght of the array given as argument
     * 
     * @param s     array to compare
     * @return <code> true </code> if the arrays have the same lenght. Otherwise throws {@link IllegalStateException} 
     */
    private boolean isScoreEqual(double[] s) {

        if (this.score.length != s.length) {
            throw new IllegalStateException("[Jenes]: the array passed as argument and the array of scores must have the same lenght !");
        }

        return Arrays.equals(this.score, s);
    }

    /**
     * Returns the chromosome's length
     *
     * @return the chromosome's length
     */
    public final int getChromosomeLength() {
        return this.chromosome != null ? this.chromosome.length() : 0;
    }

    /**
     * Returns true if individual with the same chromosome must have also the same fitness (if evaluated),
     * false otherwise
     *
     * @return the sameChromosomeSameFitness field value
     */
    public static final boolean isSameChromosomeSameFitness() {
        return sameChromosomeSameFitness;
    }

    /**
     * Set to true if individual with the same chromosome must have also the same fitness (if evaluated),
     * false otherwise
     *
     * @param sameChromosomeSameFitness the sameChromosomeSameFitness value
     */
    public static final void setSameChromosomeSameFitness(final boolean sameChromosomeSameFitness) {
        Individual.sameChromosomeSameFitness = sameChromosomeSameFitness;
    }

    /**
     * Returns the {@link Statistics} of this <code> Individual </code>
     * 
     * @return the {@link Statistics} object
     */
    public Statistics<T> getStatistics() {
        if (this.statistics == null) {
            this.statistics = new Statistics<T>(this);
        }
        return this.statistics;
    }

    /**
     * This class provides some basic statistics regarding an <code> Individual </code>.
     * 
     * @param <T> extends {@link Chromosome}
     */
    public static final class Statistics<T extends Chromosome> extends LoggableStatistics {

        private Individual<T> individual;
        private double[] score;

        private Statistics(Individual ind) {
            this.individual = ind;
        }

        /**
         * Returns the scores of this <code> Individual </code>
         * 
         * @return the array of scores
         */
        @Loggable(label = "I.Score")
        public final double[] getScore() {
            if (this.score == null || !Arrays.equals(this.score, this.individual.score)) {
                this.score = this.individual.getAllScores();
            }
            return this.score;
        }

        /**
         * Says if this <code> Individual </code> is legal
         * 
         * @return <code> true </code> if this individual is legal. 
         * <code> false </code> otherwise
         */
        @Loggable(label = "I.Legal")
        public final boolean isLegal() {
            return this.individual.isLegal();
        }

        /**
         * Says if this <code> Individual </code> is evaluated
         * 
         * @return <code> true </code> if this individual is evaluated. 
         * <code> false </code> otherwise
         */
        @Loggable(label = "I.Evaluated")
        public final boolean isEvaluated() {
            return this.individual.isEvaluated();
        }

        /**
         * Returns the subpopulation the <code> Individual </code> belongs to
         * 
         * @return the subpopulation id
         */
        @Loggable(label = "I.Speciem")
        public final int getSpeciem() {
            return this.individual.getSpeciem();
        }

        /**
         * Returns the number of objectivies for which this <code>Individual</code> is evaluated
         * <p>
         * @return the score array's lengh
         */
        @Loggable(label = "I.NumOfObjectives")
        public final int getNumOfObjectives() {
            return this.individual.getNumOfObjectives();
        }

        /**
         * Returns the <code>Individual</code>'s genome
         * <p>
         * @return the individual's genome
         */
        @Loggable(label = "I.Chromosome")
        public final T getChromosome() {
            return this.individual.getChromosome();
        }
    }
}
