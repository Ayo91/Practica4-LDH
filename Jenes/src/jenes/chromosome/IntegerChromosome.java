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

import jenes.utils.Random;

/**
 * An IntegerChromosome is made of an array of integers.
 * Each value (i.e. allele) is within the range [lowerBound,upperBound]. Both bounds are included.
 * These bounds are specified ad the instantiation time.
 * IntegerChromosome is a fixed length chromosome. Thus the numebr of genes is
 * specified at the instantiation time and cannot be modified after.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see jenes.chromosome.Chromosome
 */
public final class IntegerChromosome implements Chromosome<IntegerChromosome> {

    private int[] genes;
    private int upperBound, lowerBound;
    private int defaultValue = 0;

    /**
     * Creates a new IntegerIndividual with the specified chromosome
     * <p>
     * @param chromosome the parameters source chromosome
     */
    public IntegerChromosome(final IntegerChromosome chromosome) {
        this.lowerBound = chromosome.lowerBound;
        this.upperBound = chromosome.upperBound;
        this.defaultValue = chromosome.defaultValue;

        this.genes = new int[chromosome.genes.length];

        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    /**
     * Creates a new IntegerIndividual with each allele in the range
     * [lowerBound, upperBound]
     * <p>
     * @param length the chromosome length
     * @param lowerBound the allele lower bound
     * @param upperBound the allele upper bound
     */
    public IntegerChromosome(final int length, final int lowerBound, final int upperBound) {
        if (lowerBound == Integer.MIN_VALUE || upperBound == Integer.MAX_VALUE) {
            throw new RuntimeException("Illegal argument exception.");
        }

        this.genes = new int[length];
        this.upperBound = upperBound;
        this.lowerBound = this.defaultValue = lowerBound;
    }

    /**
     * Returns the lower bound value for a gene of this chromosome
     *
     * @return the alleles lower bound
     */
    public final int getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the upper bound value for a gene of this chromosome
     *
     * @return the alleles upper bound
     */
    public final int getUpperBound() {
        return upperBound;
    }

    public final void setDefaultValueAt(final int pos) {
        this.genes[pos] = this.defaultValue;
    }

    public final IntegerChromosome clone() {
        return new IntegerChromosome(this);
    }

    public final void randomize() {
        for (int i = 0; i < this.genes.length; i++) {
            this.randomize(i);
        }
    }

    public final void swap(final int pos1, final int pos2) {
        int temp = this.genes[pos1];
        this.genes[pos1] = this.genes[pos2];
        this.genes[pos2] = temp;
    }

    public final void randomize(final int pos) {
        this.genes[pos] = Random.getInstance().nextInt(this.lowerBound, this.upperBound + 1);
    }

    public final void leftShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            int temp = this.genes[from];
            for (int i = from; i < to; ++i) {
                this.genes[i] = this.genes[i + 1];
            }
            this.genes[to] = temp;
        }
    }

    public final void rightShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            int temp = this.genes[to];
            for (int i = to; i > from; --i) {
                this.genes[i] = this.genes[i - 1];
            }
            this.genes[from] = temp;
        }

    }

    public final int length() {
        return this.genes.length;
    }

    public final void setAs(final IntegerChromosome chromosome) {

        this.lowerBound = chromosome.lowerBound;
        this.upperBound = chromosome.upperBound;
        this.defaultValue = chromosome.defaultValue;

        if (chromosome.genes.length != this.genes.length) {
            this.genes = new int[chromosome.genes.length];
        }

        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    public final void cross(final IntegerChromosome chromosome, final int from) {

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (from > minlen) {
            return;
        }

        for (int i = 0; i < from; ++i) {
            int swap = this.genes[i];
            this.genes[i] = chromosome.genes[i];
            chromosome.genes[i] = swap;
        }

        final int[] gtmp = this.genes;
        this.genes = chromosome.genes;
        chromosome.genes = gtmp;
    }

    public final void cross(final IntegerChromosome chromosome, final int from, final int to) {

        final int end = to + 1;

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (end > minlen) {
            this.cross(chromosome, from);
        } else {
            for (int i = from; i < end; ++i) {
                int swap = this.genes[i];
                this.genes[i] = chromosome.genes[i];
                chromosome.genes[i] = swap;
            }
        }

    }

    /**
     * Returns the integer value at the specified position
     * <p>
     * @param pos a position into this population
     * @return the integer value at the specified position
     */
    public final int getValue(final int pos) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        return this.genes[pos];
    }

    /**
     * Returns the integer values
     * <p>
     * @return values
     */
    public final int[] getValues() {
        final int values[] = new int[genes.length];
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * Provides the gene values by the array passed as parameter and returning it on return.
     * If the argument values is null, the array is first created.
     * <p>
     * @param values the array to fill
     * @return the array of values
     */
    public final int[] getValues(final int values[]) {
        if (values == null) {
            int nvalues[] = new int[genes.length];
            System.arraycopy(genes, 0, nvalues, 0, genes.length);
            return (nvalues);
        };
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * Sets the default value of this chromosome
     * <p>
     * @param defaultValue the new default value to be used
     */
    public final void setDefaultValue(final int defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of this chromosome
     * <p>
     * @return the integer default value of this chromosome
     */
    public final int getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the specified value at the specified position
     * <p>
     * @param pos the position to be modify
     * @param value the value to be insert
     */
    public final void setValue(final int pos, final int value) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        if (value < this.lowerBound || value > this.upperBound) {
            throw new IllegalArgumentException("The integer value has to be in the range [" + this.lowerBound + "," + this.upperBound + "]");
        }

        this.genes[pos] = value;
    }

    /**
     * Provides a textual chromosome representation
     * @return the textual chromosome representation
     */
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer("Integer[");
        if (this.genes.length > 0) {
            for (int i = 0; i < this.genes.length - 1; i++) {
                sb.append(genes[i]);
                sb.append(" ");
            }
            sb.append(genes[this.genes.length - 1]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Compares the chromosome with another.
     *
     * @param chromosome the chromosome to compare to.
     * @return True, if the two chromosome are equal.
     */
    @Override
    public final boolean equals(final IntegerChromosome chromosome) {
        if (this.length() != chromosome.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (genes[i] != chromosome.genes[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void difference(final IntegerChromosome chromosome, final double[] diff) {

        int len = this.genes.length;
        if (chromosome.genes.length < len) {
            len = chromosome.genes.length;
        }

        for (int i = 0; i < len; ++i) {
            diff[i] = this.genes[i] - chromosome.genes[i];
        }
    }

    public Object[] toArray() {
        Integer[] toReturn = new Integer[this.genes.length];
        for (int g = 0; g < this.genes.length; g++) {
            toReturn[g] = this.genes[g];
        }
        return toReturn;
    }
}
