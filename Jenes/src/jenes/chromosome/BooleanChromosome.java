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
 * A BooleanChromosome is made of an array of booleans. Each gene can assume the value true or false.
 * A faster and less memory demanding alternative to BooleanChromosome is provided by {@link BitwiseChromosome}
 * with {@link jenes.chromosome.codings.BooleanCoding}.
 *
 * @version 2.0
 * @since 1.0
 *
 */
public final class BooleanChromosome implements Chromosome<BooleanChromosome> {

    private boolean[] genes;
    private boolean defaultValue = false;

    /**
     * Creates a new BooleanChromosome with the specified chromosome
     * <p>
     * @param chromosome the parameters source chromosome
     */
    public BooleanChromosome(final BooleanChromosome chromosome) {
        this.defaultValue = chromosome.defaultValue;
        this.genes = new boolean[chromosome.genes.length];
        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    /**
     * Creates a new BooleanChromosome with the specified chromosome length
     * <p>
     * @param size the chromosome length
     */
    public BooleanChromosome(final int size) {
        this.genes = new boolean[size];
    }

    public final void setDefaultValueAt(final int pos) {
        this.genes[pos] = this.defaultValue;
    }

    @Override
    public final BooleanChromosome clone() {
        return new BooleanChromosome(this);
    }

    public final void randomize() {
        for (int i = 0; i < this.genes.length; i++) {
            this.randomize(i);
        }
    }

    public final void randomize(final int pos) {
        this.genes[pos] = Random.getInstance().nextBoolean();
    }

    public final void swap(final int pos1, final int pos2) {
        boolean temp = this.genes[pos1];
        this.genes[pos1] = this.genes[pos2];
        this.genes[pos2] = temp;
    }

    public final void leftShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            boolean temp = this.genes[from];
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
            boolean temp = this.genes[to];
            for (int i = to; i > from; --i) {
                this.genes[i] = this.genes[i - 1];
            }
            this.genes[from] = temp;
        }

    }

    public final int length() {
        return this.genes.length;
    }

    public final void setAs(final BooleanChromosome chromosome) {

        this.defaultValue = chromosome.defaultValue;

        if (chromosome.genes.length != this.genes.length) {
            this.genes = new boolean[chromosome.genes.length];
        }

        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
    }

    public final void cross(final BooleanChromosome chromosome, final int from) {

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (from > minlen) {
            return;
        }

        for (int i = 0; i < from; ++i) {
            boolean swap = this.genes[i];
            this.genes[i] = chromosome.genes[i];
            chromosome.genes[i] = swap;
        }

        boolean[] gtmp = this.genes;
        this.genes = chromosome.genes;
        chromosome.genes = gtmp;
    }

    public final void cross(final BooleanChromosome chromosome, final int from, final int to) {

        int end = to + 1;

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (end > minlen) {
            this.cross(chromosome, from);
        } else {
            for (int i = from; i < end; ++i) {
                boolean swap = this.genes[i];
                this.genes[i] = chromosome.genes[i];
                chromosome.genes[i] = swap;
            }
        }

    }

    /**
     * Returns the boolean value at the specified position
     * <p>
     * @param pos a gene position
     * @return the boolean value at the specified position
     */
    public final boolean getValue(final int pos) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        return this.genes[pos];
    }

    /**
     * Returns the boolean values
     * <p>
     * @return values
     */
    public final boolean[] getValues() {
        boolean values[] = new boolean[genes.length];
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * Sets a new boolean value at the specified position
     * <p>
     * @param value the value to be set
     * @param position the position to modify
     */
    public final void setValueAt(boolean value, int position) {
        this.genes[position] = value;
    }

    /**
     * Provides the gene values by the array passed as parameter and returning it on return.
     * If the argument values is null, the array is first created.
     * <p>
     * @param values the array to fill
     * @return the array of values
     */
    public final boolean[] getValues(final boolean values[]) {
        if (values == null) {
            boolean nvalues[] = new boolean[genes.length];
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
    public final void setDefaultValue(final boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of this chromosome
     * <p>
     * @return the boolean default value of this chromosome
     */
    public final boolean getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the specified value at the specified position
     * <p>
     * @param pos the position to be modify
     * @param value the value to be set
     */
    public final void setValue(final int pos, final boolean value) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        this.genes[pos] = value;
    }

    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer("Boolean[");
        if (this.genes.length > 0) {
            for (int i = 0; i < this.genes.length - 1; i++) {
                sb.append(this.genes[i] ? "T" : "F");
                sb.append(" ");
            }
            sb.append(this.genes[this.genes.length - 1] ? "T]" : "F]");
        } else {
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public final boolean equals(final BooleanChromosome chromosome) {
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
    public void difference(BooleanChromosome chromosome, double[] diff) {
        int len = this.genes.length;
        if (chromosome.genes.length < len) {
            len = chromosome.genes.length;
        }

        for (int i = 0; i < len; ++i) {
            diff[i] = 0;
            if (this.genes[i] && !chromosome.genes[i]) {
                diff[i] = 1;
            } else if (!this.genes[i] && chromosome.genes[i]) {
                diff[i] = -1;
            }
        }
    }

    @Override
    public Object[] toArray() {
        Boolean[] toReturn = new Boolean[this.genes.length];
        for (int g = 0; g < this.genes.length; g++) {
            toReturn[g] = this.genes[g];
        }
        return toReturn;
    }
}