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
 * This class provides a chromosome able to model permutations. An array of integer values is its
 * genoma. Each value is in the range [0,length-1] where length is the chromosome length.
 * The most importatnt property is that each value is never duplicated: each value is contained
 * just once. All the chromosome operations preserve this chromosome property.
 *
 * @version 2.0
 * @since 1.0
 *
 */
public final class PermutationChromosome implements Chromosome<PermutationChromosome> {

    /**
     * This data structure keeps the permutation order
     */
    protected int[] permutation;
    /**
     * This data structure keeps the position of elements in the permuted order
     */
    protected int[] position;
    /**
     * This structure is used by crossover to keep original elements when the crossover begins.
     */
    private int temp_permutation[];

    /**
     * Creates a new PermutationChromosome using the specified one as model
     *
     * @param chromosome the chromosome model
     */
    public PermutationChromosome(final PermutationChromosome chromosome) {
        this.permutation = new int[chromosome.permutation.length];
        System.arraycopy(chromosome.permutation, 0, this.permutation, 0, chromosome.permutation.length);

        this.position = new int[chromosome.position.length];
        System.arraycopy(chromosome.position, 0, this.position, 0, chromosome.position.length);

        this.temp_permutation = new int[chromosome.permutation.length];
    }

    /**
     * Creates a new PermutationChromosome with the specified length.
     *
     * @param length the chromosome length
     */
    public PermutationChromosome(final int length) {
        this.permutation = new int[length];
        this.position = new int[length];
        for (int i = 0; i < length; ++i) {
            permutation[i] = i;
            position[i] = i;
        }

        this.temp_permutation = new int[length];
    }

    public final int length() {
        return permutation.length;
    }

    /**
     * Returns the element at the specified position
     *
     * @param index of the element to return
     * @return the desired allele value
     */
    public final int getElementAt(final int index) {
        return permutation[index];
    }

    /**
     * Provides the element position
     *
     * @param element a chromosome element
     * @return the position of the specified element
     */
    public final int getPositionOf(final int element) {
        return position[element];
    }

    /**
     * Sets the default value at the specified position.
     * For the permutation chromosome it is the original value in that position, that is pos itself.
     *
     * @param pos the position where to set the default value
     */
    public final void setDefaultValueAt(final int pos) {
        int pos2 = position[pos];
        this.swap(pos, pos2);
    }

    public final void cross(final PermutationChromosome chromosome, final int from) {
        this.cross(chromosome, from, this.permutation.length - 1);
    }

    public final void cross(final PermutationChromosome chromosome, final int from, int to) {

        // This operation is possible only if both chromosomes entail
        // the same number of permutation elements
        if (chromosome.permutation.length != this.permutation.length) {
            throw new RuntimeException("Incompatible chromosome length.");
        }

        to += 1;
        if (to > this.permutation.length) {
            to = this.permutation.length;
        }

        // The original values in the crossover area are
        // saved in the temporary structures
        for (int i = from; i < to; ++i) {
            this.temp_permutation[i] = this.permutation[i];
            chromosome.temp_permutation[i] = chromosome.permutation[i];
        }

        // The elements out the crossover area are left unchanged
        for (int i = from; i < to; ++i) {

            // This is the element of this chromosome at position i
            // within the crossover area
            int this_elem = this.temp_permutation[i];

            // We look for the position of this element in the other chromosome
            int chrm_pos = chromosome.position[this_elem];

            // If this_elem is also in the crossover area for the other chromosome
            if (chrm_pos >= from && chrm_pos < to) {
                // We take the element in position i, as provided
                // by this chromosome schema. The position is exchanged with
                // the element that currently occupies that position
                chromosome.swap(chrm_pos, i);
            }

            // In dual way, we apply the same strategy to the other chromosome.
            // This is the element of the other chromosome in position i
            int chrm_elem = chromosome.temp_permutation[i];

            // And this is the position held by this element in this chromosome.
            int this_pos = this.position[chrm_elem];

            // If it is within the crossover area
            if (this_pos >= from && chrm_pos < to) {
                // We take this element in position i
                this.swap(this_pos, i);
            }
        }
    }

    public final void leftShift(final int from, int to) {

        int len = this.permutation.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            int first = this.permutation[from];
            for (int i = from; i < to; ++i) {
                int el = this.permutation[i + 1];
                this.permutation[i] = el;
                this.position[el] = i;
            }
            this.permutation[to] = first;
            this.position[first] = to;
        }
    }

    public final void rightShift(final int from, int to) {
        int len = this.permutation.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            int last = this.permutation[to];
            for (int i = to; i > from; --i) {
                int el = this.permutation[i - 1];
                this.permutation[i] = el;
                this.position[el] = i;
            }
            this.permutation[from] = last;
            this.position[last] = from;
        }
    }

    /**
     * Perform a random transformation of gene at the specified positions, by exchanging it with another element randomly chosen.
     *
     * @param pos the position to randomize
     */
    public final void randomize(final int pos) {
        int pos2 = Random.getInstance().nextInt(this.permutation.length);
        this.swap(pos, pos2);
    }

    public final void randomize() {
        for (int i = 0; i < this.permutation.length; i++) {
            this.randomize(i);
        }
    }

    public final void setAs(final PermutationChromosome chromosome) {
        int length = chromosome.permutation.length;
        if (length != this.permutation.length) {
            this.permutation = new int[length];
            this.position = new int[length];
            this.temp_permutation = new int[length];
        }

        System.arraycopy(chromosome.permutation, 0, this.permutation, 0, chromosome.permutation.length);
        System.arraycopy(chromosome.position, 0, this.position, 0, chromosome.position.length);
    }

    public final void swap(final int pos1, final int pos2) {

        int e1 = permutation[pos1];
        int e2 = permutation[pos2];

        permutation[pos1] = e2;
        position[e2] = pos1;

        permutation[pos2] = e1;
        position[e1] = pos2;

    }

    public final boolean equals(final PermutationChromosome chromosome) {
        if (this.length() != chromosome.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (permutation[i] != chromosome.permutation[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final PermutationChromosome clone() {
        return new PermutationChromosome(this);
    }

    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer("Integer[");
        if (this.permutation.length > 0) {
            for (int i = 0; i < this.permutation.length - 1; i++) {
                sb.append(permutation[i]);
                sb.append(" ");
            }
            sb.append(permutation[this.permutation.length - 1]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Provides the gene offsets to reach the natural order, according to equation p = n + o, where
     * p is the permutation vector, n the natural order (i.e. 0,1,2..) and o the offset vector.
     *
     * In other words, if we sum to permutation the offsets, we get the natural ordering,
     * or if we subtract the offsets to the natural order we get the permutation.
     *
     * @return - gene offsets;
     */
    public final int[] toNaturalOrdering() {
        int out[] = new int[this.position.length];
        for (int i = 0; i < out.length; ++i) {
            out[i] = this.permutation[i] - i;
        }
        return out;
    }

    @Override
    public void difference(final PermutationChromosome chromosome, final double[] diff) {

        int len = this.permutation.length;
        if (chromosome.permutation.length < len) {
            len = chromosome.permutation.length;
        }

        for (int i = 0; i < len; ++i) {
            diff[i] = this.permutation[i] - chromosome.permutation[i];
        }
    }

    public Object[] toArray() {
        Integer[] toReturn = new Integer[this.permutation.length];
        for (int g = 0; g < this.permutation.length; g++) {
            toReturn[g] = this.permutation[g];
        }
        return toReturn;
    }
}
