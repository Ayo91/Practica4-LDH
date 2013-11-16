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

import java.util.List;

/**
 * An ObjectChromosome is made of objects. An {@link AlleleSet} is
 * the {@link Gene} allele values alphabet.
 * One or more allele sets are required to instantiate an ObjectChromosome. All of genes can
 * have the same allele set or a different one according to ObjectChromosome constructor invoked.
 * An example of code is provided below.
 * <p><blockquote><pre>
 * ObjectChromosome chrom = new ObjectChromosome(
 * 		new GenericAlleleSet<Integer>(1, 2, 3),
 *		new GenericAlleleSet<Boolean>(true, false),
 * 		new AnyDoubleGenericAlleleSetSubtype.createRandom(10, 0, 1)  );
 * </pre></blockquote>
 * <p>
 * We instantiated an ObjectChromosome with three genes: the first one with {@link Integer} allele
 * values 1, 2 and 3; the second one with {@link Boolean} allele values and the last one with {@link Double}
 * values randomly distributed in the range [0,1].
 * {@link GenericAlleleSet} is a concrete subclass of {@link AlleleSet} interface; AnyDoubleGenericAlleleSetSubtype
 * is a subclass of GenericAlleleSet providing a static method to create randomly double values within a
 * specified range.
 *
 * @version 2.0
 * @since 1.0
 *
 * @see jenes.chromosome.AlleleSet
 * @see jenes.chromosome.GenericAlleleSet
 */
public final class ObjectChromosome implements Chromosome<ObjectChromosome> {

    /**
     * An <code>ObjectChromosome</code>'s gene. Each <code>Gene</code> has an <code>Object</code> value
     * and an <code>AlleleSet</code> (that is the alphabet of its own value).
     *
     */
    public static class Gene implements Cloneable {

        /**
         * The value (i.e. allele) assumed by the gene
         */
        protected Object value;
        /**
         * The set of possible values for the gene
         */
        protected AlleleSet alleleSet;

        /**
         * Creates a new Gene with the specified <code>AlleleSet</code>.
         * The new gene will contain a random allele value.
         * <p>
         * @param alleleSet the gene's <code>AlleleSet</code>
         */
        private Gene(final AlleleSet alleleSet) {
            this.alleleSet = alleleSet;
            this.value = this.alleleSet.getRandomValue();
        }

        /**
         * Returns the alleleSet of this gene.
         * <p>
         * @return the alleleSet of this gene
         */
        public final AlleleSet getAlleleSet() {
            return alleleSet;
        }

        /**
         * Sets the alleleSet of this Gene
         * <p>
         * @param alleleSet the alleleSet to be setted to this gene
         */
        public final void setAlleleSet(final AlleleSet alleleSet) {
            this.alleleSet = alleleSet;
        }

        /**
         * Returns the object value of this gene
         * <p>
         * @return the pbject value of this gene
         */
        public final Object getValue() {
            return value;
        }

        /**
         * Sets the gene value to this gene
         * <p>
         * @param value the value to be setted to this gene
         */
        public final void setValue(final Object value) {
            this.value = value;
        }

        public final Object clone() {
            Gene gene = new Gene(this.alleleSet);
            gene.value = this.value;
            return gene;
        }
    }
    /**
     * The chromosome data structure
     */
    protected Gene[] genes;
    /**
     * The default value for this chromosome.
     * The default value must be valid for all genes, therefore it should be
     * be <code>null</code> or belonging to every <code>AlleleSet</code>.
     */
    protected Object defaultValue = null;

    /**
     * Creates a new ObjectChromosome with the specified length and with the same
     * <code>AlleleSet</code> for each <code>Gene</code>.
     * <p>
     * @param size the chromosome length
     * @param set the genes' <code>AlleleSet</code>
     */
    public ObjectChromosome(final AlleleSet set, final int size) {
        this.genes = new Gene[size];
        for (int i = 0; i < size; i++) {
            this.genes[i] = new Gene(set);
        }
    }

    /**
     * Creates a new ObjectChromosome; its genes will be equal to the specified ObjectChromosome's ones.
     * <p>
     * @param chromosome the source <code>ObjectChromosome</code>
     */
    public ObjectChromosome(final ObjectChromosome chromosome) {
        this.genes = new Gene[chromosome.genes.length];
        for (int i = 0; i < chromosome.genes.length; i++) {
            this.genes[i] = (Gene) chromosome.genes[i].clone();
        }
    }

    /**
     * Creates a new ObjectChromosome with one gene for each <code>AlleleSet</code> within the
     * specified array.
     * The chromosome genes will contain random allele values.
     * <p>
     * @param sets the alleleset array
     */
    public ObjectChromosome(final AlleleSet... sets) {
        this.genes = new Gene[sets.length];
        for (int i = 0; i < this.genes.length; i++) {
            this.genes[i] = new Gene(sets[i]);
        }
    }

    /**
     * Creates a new ObjectChromosome with one gene for each <code>AlleleSet</code> within the
     * specified list.
     * The chromosome genes will contain random allele values.
     * <p>
     * @param list the <code>java.util.List</code> with all of <code>AlleleSet</code>
     */
    public ObjectChromosome(final List<AlleleSet> list) {
        this.genes = new Gene[list.size()];
        for (int i = 0; i < this.genes.length; i++) {
            this.genes[i] = new Gene(list.get(i));
        }
    }

    public final void setDefaultValueAt(final int pos) {
        this.genes[pos].value = this.defaultValue;
    }

    public final ObjectChromosome clone() {
        return new ObjectChromosome(this);
    }

    public final void randomize() {
        for (int i = 0; i < this.genes.length; i++) {
            this.randomize(i);
        }
    }

    public final void randomize(final int pos) {
        this.genes[pos].value = this.genes[pos].alleleSet.getRandomValue();
    }

    public final void swap(final int pos1, final int pos2) {
        Object temp = this.genes[pos1].value;
        this.genes[pos1].value = this.genes[pos2].value;
        this.genes[pos2].value = temp;
    }

    public final void leftShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            Object temp = this.genes[from].value;
            for (int i = from; i < to; ++i) {
                this.genes[i].value = this.genes[i + 1].value;
            }
            this.genes[to].value = temp;
        }

    }

    public final void rightShift(final int from, int to) {

        int len = this.genes.length - 1;
        if (len < to) {
            to = len;
        }

        if (from < to) {
            Object temp = this.genes[to].value;
            for (int i = to; i > from; --i) {
                this.genes[i].value = this.genes[i - 1].value;
            }
            this.genes[from].value = temp;
        }

    }

    public final int length() {
        return this.genes.length;
    }

    public final void setAs(final ObjectChromosome chromosome) {

        this.defaultValue = chromosome.defaultValue;

        int len = this.genes.length;
        if (chromosome.genes.length != len) {
            Gene[] oldgenes = this.genes;
            this.genes = new Gene[chromosome.genes.length];

            if (chromosome.genes.length < len) {
                len = this.genes.length;
            }

            for (int i = 0; i < len; ++i) {
                this.genes[i] = oldgenes[i];
                this.genes[i].value = chromosome.genes[i].value;
            }

            for (int i = len; i < this.genes.length; i++) {
                this.genes[i] = (Gene) chromosome.genes[i].clone();
            }
        } else {
            for (int i = 0; i < len; ++i) {
                this.genes[i].value = chromosome.genes[i].value;
            }
        }

    }

    public final void cross(final ObjectChromosome chromosome, final int from) {

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (from > minlen) {
            return;
        }

        for (int i = 0; i < from; ++i) {
            Object swap = this.genes[i].value;
            this.genes[i].value = chromosome.genes[i].value;
            chromosome.genes[i].value = swap;
        }

        Gene[] gtmp = this.genes;
        this.genes = chromosome.genes;
        chromosome.genes = gtmp;
    }

    public final void cross(final ObjectChromosome chromosome, final int from, final int to) {

        int end = to + 1;

        int minlen = this.genes.length;
        if (minlen < chromosome.genes.length) {
            minlen = chromosome.genes.length;
        }

        if (end > minlen) {
            this.cross(chromosome, from);
        } else {
            for (int i = from; i < end; ++i) {
                Object swap = this.genes[i].value;
                this.genes[i].value = chromosome.genes[i].value;
                chromosome.genes[i].value = swap;
            }
        }

    }

    /**
     * Returns the object allele value at the specified position
     * <p>
     * @param pos a position into this population
     * @return the object allele value at the specified position
     */
    public final Object getValue(final int pos) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        return this.genes[pos].value;
    }

    /**
     * Returns the Object values
     * <p>
     * @return the Object values
     */
    public final Object[] getValues() {
        Object values[] = new Object[genes.length];
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * gets the array of double values by filling the double values passed as parameters
     * <p>
     * @param values 
     * @return the array of chromosome genes
     */
    public final Object[] getValues(final Object values[]) {
        if (values == null) {
            Object nvalues[] = new Object[genes.length];
            System.arraycopy(genes, 0, nvalues, 0, genes.length);
            return (nvalues);
        };
        System.arraycopy(genes, 0, values, 0, genes.length);
        return (values);
    }

    /**
     * Sets the default value of this chromosome.
     * The default value must be valid for all genes, therefore it should be
     * be <code>null</code> or belonging to every <code>AlleleSet</code>.
     * <p>
     * @param defaultValue the new default value to be used
     */
    public final void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of this chromosome
     * <p>
     * @return the object default value of this chromosome
     */
    public final Object getDeafultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the specified value at the specified position
     * <p>
     * @param pos the position to be modify
     * @param value the value to be set
     */
    public final void setGene(final int pos, final Object value) {
        if (pos < 0 || pos >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        this.genes[pos].value = value;
    }

    /**
     * Returns the gene at the specified position
     * <p>
     * @param index the index of the gene
     * @return the gene at the specified position
     */
    public final Gene getGene(final int index) {
        if (index < 0 || index >= this.genes.length) {
            throw new IllegalArgumentException("Out of chromosome length.");
        }

        return genes[index];
    }

    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer("Object[");
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

    @Override
    public final boolean equals(final ObjectChromosome chromosome) {
        if (this.length() != chromosome.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (!genes[i].equals(chromosome.genes[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void difference(ObjectChromosome chromosome, double[] diff) {
        int len = this.genes.length;
        if (chromosome.genes.length < len) {
            len = chromosome.genes.length;
        }

        for (int i = 0; i < len; ++i) {
            if (this.genes[i].alleleSet == chromosome.genes[i].alleleSet) {
                diff[i] = this.genes[i].alleleSet.difference(this.genes[i].value, chromosome.genes[i].value);
            } else {
                diff[i] = Double.NaN;
            }
        }
    }

    @Override
    public Object[] toArray() {
        Object toReturn[] = new Object[genes.length];
        System.arraycopy(genes, 0, toReturn, 0, genes.length);
        return (toReturn);
    }
}