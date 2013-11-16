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
package jenes.chromosome;

/**
 * The interface Chromosome is provided by all chromosomes, specifying
 * genoma operations used by genetic operators during the algorithm evolution.
 * Concretizations of the Chromosome class are required to actually implement an algorithm.
 * There are some concrete chromosome types based on boolean, double, integer and object values.
 * All of these are fixed length chromosomes, as the number of genes dosen't change during
 * the algorithm iterations.
 * <p>
 * If necessary, a variable length chromosome can be implemented by subclassing
 * this abstract class and specifying the abstract methods.
 * 
 * @param <T> The type of Chromosomes accepted as input and returned by operations.
 *
 * @version 2.0
 * @since 1.0
 * 
 */
public interface Chromosome<T extends Chromosome> extends Cloneable {

    /**
     * Exchanges two genes at the specified positions
     * <p>
     * @param pos1 the position of first gene
     * @param pos2 the position of second gene
     */
    public void swap(int pos1, int pos2);

    /**
     * Sets a random value in the specified position
     * <p>
     * @param pos a position where to set the random value
     */
    public void randomize(int pos);

    /**
     * Makes a left-shift of the genes between two positions.
     * The shift is circular, so the most left-side gene becomes the last gene on right.
     * <p>
     * @param from the start position to be shifted
     * @param to the final position to be shifted
     */
    public void leftShift(int from, int to);

    /**
     * Makes a right-shift of the genes between two positions.
     * The shift is circular, so the most right-side gene becomes the first gene on left.
     * <p>
     * @param from the start position to be shifted
     * @param to the final position to be shifted
     */
    public void rightShift(int from, int to);

    /**
     * Sets the default value at a given position
     * <p>
     * @param pos the position where to set the default value
     */
    public void setDefaultValueAt(int pos);

    /**
     * Performs a chromosome deep-cloning.
     * <p>
     * @return a chromosome clone.
     */
    public T clone();

    /**
     * Returns the number of genes contained in the chromosome.
     * <p>
     * @return the length of chromosome.
     */
    public int length();

    /**
     * Fills the chromosome with random value.
     */
    public void randomize();

    /**
     * Makes the chromosome a copy of another chromosome.
     * <p>
     * @param chromosome the chromosome to be copied
     */
    public void setAs(T chromosome);

    /**
     * Exchanges the genes chromosome within the range [from,to]
     * <p>
     * @param chromosome the chromosome to cross with
     * @param from the start position of the genes to exchange
     * @param to the final position of the genes to exchange
     */
    public void cross(T chromosome, int from, int to);

    /**
     * Exchanges the genes chromosome starting from a given position
     * <p>
     * @param chromosome the chromosome to cross with
     * @param from the start position of the genes to exchange
     */
    public void cross(T chromosome, int from);

    /**
     * Compares the chromosome with another.
     *
     * @param chromosome the cromosome to compare to.
     * @return true if the two chromosome are equal, false otherwise.
     */
    public boolean equals(T chromosome);

    /**
     * Computes the genetic difference between two chromosomes.
     * NOTICE: This  should not be invoked directly. Please, use #getDifference instead.
     *
     * @param diff - the variable to be filled by the minus values
     * @param chromosome - the chromosome to quantify the minus from
     */
    public void difference(T chromosome, double[] diff);
    
    /**
     * Returns an array containing all of the genes in chromosome.
     * 
     * @return an array containing all of the genes in chromosome.
     */
    public Object[] toArray();


    public final class Util {

        /**
         * Provides the gene-by-gene quantitative difference vector.
         *
         * @param c0 - the first chromosome to compare
         * @param c1 - the second chromosome to compare
         * @return the vector of gene-by-gene getDifference
         */
        public static final double[] getDifference(Chromosome c0, Chromosome c1) {
            return getDifference(c0, c1, null);
        }

        /**
         * Provides the gene-by-gene quantitative difference vector.
         * This version fills and returns the vecor provided externally in order to optimize the memory usage.
         * If <code>dist</code> is <code>null</code> or not properly sized, then the vector is first created.
         *
         * @param c0 - the first chromosome to compare
         * @param c1 - the second chromosome to compare
         * @return the vector of gene-by-gene getDifference
         */
        public static final double[] getDifference(Chromosome c0, Chromosome c1, double[] dist) {
            int s0 = c0.length();
            int s1 = c1.length();

            int sz = s0 > s1 ? s0 : s1;
            if (dist == null || sz != dist.length) {
                dist = new double[sz];
            }

            for (int i = 0; i < sz; ++i) {
                dist[i] = Double.NaN;
            }

            c0.difference(c1, dist);

            return dist;
        }
    }
}
