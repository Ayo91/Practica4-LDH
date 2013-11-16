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
 * A DoubleChromosome is made of an array of double values. Each value is in the range [lowerBound,upperBound[.
 * The lower bound is include included, the upper bound not.
 * Both the bounds are specified ad the instantiation time.
 * DoubleChromosome is a fixed length chromosome class, thus number of genes is
 * specified at instantiation time and cannot be modified after.
 *
 * @version 2.0
 * @since 1.0
 *
 */
public final class DoubleChromosome implements Chromosome<DoubleChromosome> {

    protected double[] genes;
    protected double upperBound, lowerBound;
    protected double defaultValue = 0;

    /**
     * Creates a new DoubleChromosome as a copy of the specified chromosome
     * <p>
     * @param chromosome the chromosome to be copied
     */
    public DoubleChromosome(final DoubleChromosome chromosome) {
        this.lowerBound = chromosome.lowerBound;
        this.upperBound = chromosome.upperBound;
        this.defaultValue = chromosome.defaultValue;

        this.genes = new double[chromosome.genes.length];

        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    /**
     * Creates a DoubleChromosome with each allele in the range
     * [lowerBound, upperBound[
     * <p>
     * @param length the chromosome length
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public DoubleChromosome(final int length, final double lowerBound, final double upperBound) {
        if (lowerBound == Double.MIN_VALUE || upperBound == Double.MAX_VALUE) {
            throw new RuntimeException("Illegal argument exception.");
        }

        this.genes = new double[length];
        this.upperBound = upperBound;
        this.lowerBound = this.defaultValue = lowerBound;
    }

    public final void setDefaultValueAt(final int pos) {
        this.genes[pos] = this.defaultValue;
    }

    @Override
    public final DoubleChromosome clone() {
        return new DoubleChromosome(this);
    }

    public final void swap(final int pos1, final int pos2) {
        double temp = this.genes[pos1];
        this.genes[pos1] = this.genes[pos2];
        this.genes[pos2] = temp;
    }

    public final void randomize() {
        for (int i = 0; i < this.genes.length; i++) {
            this.randomize(i);
        }
    }

    public final void randomize(final int pos) {
        this.genes[pos] = Random.getInstance().nextDouble(this.lowerBound, this.upperBound + Double.MIN_VALUE);
    }

    public final void leftShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            double temp = this.genes[from];
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
            double temp = this.genes[to];
            for (int i = to; i > from; --i) {
                this.genes[i] = this.genes[i - 1];
            }
            this.genes[from] = temp;
        }

    }

    public final int length() {
        return this.genes.length;
    }

    public final void setAs(final DoubleChromosome chromosome) {

        this.lowerBound = chromosome.lowerBound;
        this.upperBound = chromosome.upperBound;
        this.defaultValue = chromosome.defaultValue;

        if (chromosome.genes.length != this.genes.length) {
            this.genes = new double[chromosome.genes.length];
        }

        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    public final void cross(final DoubleChromosome chromosome, final int from) {

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (from > minlen) {
            return;
        }

        for (int i = 0; i < from; ++i) {
            double swap = this.genes[i];
            this.genes[i] = chromosome.genes[i];
            chromosome.genes[i] = swap;
        }

        double[] gtmp = this.genes;
        this.genes = chromosome.genes;
        chromosome.genes = gtmp;
    }

    public final void cross(final DoubleChromosome chromosome, final int from, final int to) {

        int end = to + 1;

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (end > minlen) {
            this.cross(chromosome, from);
        } else {
            for (int i = from; i < end; ++i) {
                double swap = this.genes[i];
                this.genes[i] = chromosome.genes[i];
                chromosome.genes[i] = swap;
            }
        }

    }

    /**
     * Performs the weighted average between the genes in the two chromosomes.
     * If ratio = 1, the chromosomes, do not change. If ratio = 0, chromosomes are swapped.
     * The operation is performed until the end of shortest chromosome is reached.
     *
     * @param chromosome - the chromosome to combine
     * @param ratio - the coefficient used in the weighted average
     */
    public final void average(final DoubleChromosome chromosome, double ratio) {
        if (ratio < 0) {
            ratio = 0;
        } else if (ratio > 1) {
            ratio = 1;
        }

        int len = this.length();
        if (len > chromosome.length()) {
            len = chromosome.length();
        }

        for (int i = 0; i < len; ++i) {
            double g0 = this.genes[i];
            double g1 = chromosome.genes[i];

            this.genes[i] = g1 + ratio * (g0 - g1);
            chromosome.genes[i] = g0 + ratio * (g1 - g0);
        }
    }

    /**
     * Performs the ordered weighted average (OWA) between the genes in the two chromosomes.
     * If ratio = 1, the second chromosome holds the maximal values, the first the minimal.
     * The opposite in case of ratio = 0.
     * The operation is performed until the end of shortest chromosome is reached.
     *
     * @param chromosome - the chromosome to combine
     * @param ratio - the coefficient used in the weighted average
     */
    public final void owa(final DoubleChromosome chromosome, double ratio) {
        if (ratio < 0) {
            ratio = 0;
        } else if (ratio > 1) {
            ratio = 1;
        }

        int len = this.length();
        if (len > chromosome.length()) {
            len = chromosome.length();
        }

        for (int i = 0; i < len; ++i) {
            double g0 = this.genes[i];
            double g1 = chromosome.genes[i];

            if (g0 > g1) {
                double tm = g0;
                g0 = g1;
                g1 = tm;
            }

            this.genes[i] = g1 + ratio * (g0 - g1);
            chromosome.genes[i] = g0 + ratio * (g1 - g0);
        }
    }

    /**
     * Returns the double value at the specified position
     * <p>
     * @param pos a position of this chromosome
     * @return the double value at the specified position
     */
    public final double getValue(final int pos) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        return this.genes[pos];
    }

    /**
     * Returns the double values
     * <p>
     * @return values
     */
    public final double[] getValues() {
        double values[] = new double[genes.length];
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
    public final double[] getValues(final double values[]) {
        if (values == null) {
            double nvalues[] = new double[genes.length];
            System.arraycopy(genes, 0, nvalues, 0, genes.length);
            return (nvalues);
        }
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * Sets the default value of this chromosome
     * <p>
     * @param defaultValue the new default value to be used
     */
    public final void setDefaultValue(final double defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of this chromosome
     * <p>
     * @return the double default value of this chromosome
     */
    public final double getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the specified value at the specified position
     * <p>
     * @param pos the position to be modify
     * @param value the value to be insert
     */
    public final void setValue(final int pos, final double value) {
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
        StringBuffer sb = new StringBuffer("Double[");
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
     * Returns the lower bound value for a gene of this chromosome
     *
     * @return the alleles lower bound
     */
    public final double getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the upper bound value for a gene of this chromosome
     *
     * @return the alleles upper bound
     */
    public final double getUpperBound() {
        return upperBound;
    }

    /**
     * Compares the chromosome with another.
     *
     * @param chromosome the chromosome to compare to.
     * @return True, if the two chromosome are equal.
     */
    public final boolean equals(final DoubleChromosome chromosome) {
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
    public void difference(final DoubleChromosome chromosome, final double[] diff) {

        int len = this.genes.length;
        if (chromosome.genes.length < len) {
            len = chromosome.genes.length;
        }

        for (int i = 0; i < len; ++i) {
            diff[i] = this.genes[i] - chromosome.genes[i];
        }
    }

    public Object[] toArray() {
        Double[] toReturn = new Double[this.genes.length];
        for (int g = 0; g < this.genes.length; g++) {
            toReturn[g] = this.genes[g];
        }
        return toReturn;
    }
}