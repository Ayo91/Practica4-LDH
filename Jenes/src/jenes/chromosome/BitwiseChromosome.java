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
 * This class provides chromosomes made of bits. Its genome contains objects
 * coded according to a specified {@link BitCoding}. Typically objects coded by this chromosome
 * are numeric values. The default integer representation is used
 * when no BitCoding is specified.
 * <p>
 * A BitwiseChromosome has size and length attributes. The size is the number of
 * coded value contained by its genoma; the length is the number of bits.
 * The length depends on which coding is used for translating objects into bits.
 * The relation between size and length is shown below:
 * <p><blockquote><pre>
 *  aBitwiseChromosome.length() = aBitwiseChromosome.getSize() *  aCoding.SIZE.BITS
 * </pre></blockquote>
 * where aCoding.SIZE.BITS is the number of bits required for coding one object.
 * <p>
 * This Chromosome performs genetic operations at bit level, processing an array of integer,
 * thus ensuring a high throughput and minimal memory occupation.
 * Using a 16 bit representation, a chromosome holding 4 objects (size)
 * will be represented by 32 bits (length), thus by 2 integers (as each of integer
 * is represented by 32 bit)
 *
 * @version 2.0
 * @since 1.0
 */
public final class BitwiseChromosome implements Chromosome<BitwiseChromosome> {

    /**
     * Definition of the number bit length and mask to use in the coding operations.
     *
     * @author Luigi Troiano
     * @author Pierpaolo Lombardi
     * @author Giuseppe Pascale
     * @author Thierry Bodhuin
     *
     * @version 1.2
     *
     * @since 1.0
     *
     */
    public static enum BitSize {

        /**
         * Standard 1 bit strucure
         */
        BIT1(1, 0x1),
        /**
         * Standard 8 bits strucure
         */
        BIT8(8, 0xFF),
        /**
         * Standard 16 bits strucure
         */
        BIT16(16, 0xFFFF),
        /**
         * Standard 32 bits strucure
         */
        BIT32(32, 0xFFFFFFFF);
        /**
         * The number of bits
         */
        public final int BITS;
        /**
         * The bit mask for retrieving and setting
         */
        public final int MASK;

        /**
         * Constructs a new BitSize with the specified number length and bit mask.
         *
         * @param bits the number bit length of the coding
         * @param mask the mask of the coding
         */
        BitSize(int bits, int mask) {
            this.BITS = bits;
            this.MASK = mask;
        }
    }

    /**
     * Represents the coding used by a {@link BitwiseChromosome}. A BitCoding can be used by subclassing
     * this class and providing implementation for {@link BitCoding#encode(Object)} and
     * {@link #decode(int)} methods. A {@link BitSize} object is useful to specify the number of bits
     * to use foreach coded number.
     *
     * @param <T> The type entailed by enconding/deconding operations.
     *
     * @author Luigi Troiano
     * @author Pierpaolo Lombardi
     * @author Giuseppe Pascale
     * @author Thierry Bodhuin
     *
     * @version 1.2
     *
     * @since 1.0
     *
     */
    public static abstract class BitCoding<T> {

        /**
         * The bit structure characteristics.
         */
        public final BitSize SIZE;

        /**
         * Constructs a new BitCoding with the specified {@link BitSize}
         *
         * @param size the bitSize object to use
         */
        protected BitCoding(final BitSize size) {
            this.SIZE = size;
        }

        /**
         * Returns the value of coded bits.
         *
         * @param bits coding the object
         * @return the value
         */
        public abstract T decode(int bits);

        /**
         * Returns the bits coding the object
         *
         * @param obj the object to be coded
         * @return the coding bits
         */
        public abstract int encode(T obj);
    }
    private int genes[];
    private BitCoding coding;	//the coding used
    private int slots;			//number of available positions within an integer
    private int size;			//number of objects encoded by the chromosome
    private int bits; 			//number of bits for coding an object
    private int load;           //number of bits used within each integer

    /**
     * Creates a new BitwiseChromosome with the specified number of objects.
     * The chromosome is made of a bit string encoding objects.
     *
     * @param size the number of objects the chromosone represents
     */
    public BitwiseChromosome(final int size) {
        this(size, null);
    }

    /**
     * Creates a new BitwiseChromosome with the specified number of objects
     * and coding.
     *
     * @param size the number of chromosome coded objects
     * @param coding the coding to use
     */
    public BitwiseChromosome(final int size, final BitCoding coding) {
        this.coding = coding;
        this.bits = (this.coding != null ? this.coding.SIZE.BITS : Integer.SIZE);
        this.slots = Integer.SIZE / this.bits;
        this.size = size;
        this.load = this.bits * this.slots;
        this.genes = new int[size / slots + (size % slots > 0 ? 1 : 0)];
    }

    /**
     * Creates a new BitwiseChromosome using the specified one as prototype
     *
     * @param chromosome the chromosome to copy
     */
    public BitwiseChromosome(final BitwiseChromosome chromosome) {
        this.genes = new int[chromosome.genes.length];
        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);

        this.bits = chromosome.bits;
        this.slots = chromosome.slots;
        this.size = chromosome.size;
        this.load = chromosome.load;
        this.coding = chromosome.coding;
    }

    /**
     * Returns the {@link BitCoding} used by this chromosome
     *
     * @return the bit coding used
     */
    public final BitCoding getType() {
        return this.coding;
    }

    /**
     * Returns the number of coded objects contained by this chromosome.
     *
     * @return the number of coded objects
     */
    public final int getSize() {
        return this.size;
    }

    /**
     * Returns the int value at the specified position
     *
     * @param index the index of the value to return
     * @return the int value at the specified position
     */
    public final int getIntValueAt(final int index) {
        return genes[index];
    }

    /**
     * Sets the int value at the specified position
     *
     * @param index the index of the element to be modify
     * @param value the value to set
     */
    public final void setIntValueAt(final int index, final int value) {
        this.genes[index] = value;
    }

    /**
     * Returns the number of integer used by the chromosome for coding the objects
     *
     * @return the number of integers
     */
    public final int getIntSize() {
        return this.genes.length;
    }

    /**
     * Returns the object value at the specified position in the chromosome.
     * The value is decoded and returned.
     *
     * @param index the position
     * @return the object
     */
    public final Object getValueAt(final int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        if (coding != null) {
            int k = index / slots;
            int h = index % slots;

            // WARNING: The following solution does not preserve the sign. 
            // For example if we consider the chromosome xx xx F1 xx
            // this becomes 00 00 00 F1 that is positive. Instead it should be FF FF FF F1
            // int v = (genes[k] >> (slots - h -1)*coding.SIZE.BITS) & coding.SIZE.MASK; 

            // The following solution preserves the sign.
            // Indeed the first line moves the value at the head: xx xx F1 xx becomes F1 00 00 00
            // The second line moves the head value to the bottom preserving the sign: F1 00 00 00 -> FF FF FF F1
            int v = genes[k] << h * coding.SIZE.BITS;
            v >>= (slots - 1) * coding.SIZE.BITS;
            return coding.decode(v);
        } else {
            return genes[index];
        }
    }

    /**
     * Sets the specified object value at the given position.
     * The object value is encoded and then placed in the chromosome.
     *
     * @param index the position
     * @param value the object value to be placed
     */
    public final void setValue(final int index, final Object value) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        if (coding != null) {
            int k = index / slots;
            int h = index % slots;
            int v = coding.encode(value);
            int mask = coding.SIZE.MASK << ((slots - 1 - h) * coding.SIZE.BITS);
            genes[k] &= ~mask;
            v = v << ((slots - 1 - h) * coding.SIZE.BITS);
            genes[k] |= (mask & v);
        } else {
            genes[index] = ((Integer) value).intValue();
        }
    }

    /**
     * Returns the bit value at the specified position. This position takes
     * into account the usage of integers made by the coding specification.
     * For instance, if the coding requires 5 bits for each value, index 31 does
     * not point to the last bit of the first integer, but to the second bit of
     * the second integer, as described below
     *
     * [01101 01110 00011 11011 10101 10010 --] [01100 11010 ...
     *
     * @param index the position
     * @return the bit value
     */
    public final int getBitValueAt(final int index) {
        if (index < 0 || index >= this.length()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int i1 = genes[index / load];
        int offset = load - (index % load) - 1;
        return (i1 >> (offset)) & 1;
    }

    /**
     * Sets the bit at a given position. Position is related to the bits actually used
     * and not to those occupied by the chromosome data structure.
     *
     * @param index the position
     * @param bit the bit value
     */
    public final void setBitValueAt(final int index, final int bit) {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException();
        }

        int i1 = genes[index / load];
        int offset = load - (index % load) - 1;
        int mask = 1 << offset;

        this.genes[i1] = (this.genes[i1] & (~mask)) | (bit << offset);
    }

    /**
     * Exchanges the chromosome bits in the range [from,to]. The range is referred to
     * positions of bits effectively used by the chromosome and not to thoae occupied by
     * the underlying data structure.
     * 
     * @param chromosome the chromosome to cross with
     * @param from the initial cross site
     * @param to the final cross site
     */
    public final void cross(final BitwiseChromosome chromosome, final int from, final int to) {

        if (to <= from) {
            return;
        }

        int end = to + 1;

        int minlen = this.length();
        if (minlen > chromosome.length()) {
            minlen = chromosome.length();
        }

        if (end > minlen) {
            this.cross(chromosome, from);
        } else {
            int i1 = from / load;
            int i2 = end / load;

            int offset_from = load - (from % load);
            int offset_to = load - (end % load);

            this.bitcross(offset_from, i1, chromosome, true);

            for (int i = i1 + 1; i < i2; ++i) {
                int v = chromosome.genes[i];
                chromosome.genes[i] = this.genes[i];
                this.genes[i] = v;
            }

            this.bitcross(offset_to, i2, chromosome, false);

            // this is  necessary when the cross points fall on the same index
            if (i1 == i2) {
                int v = chromosome.genes[i1];
                chromosome.genes[i1] = this.genes[i1];
                this.genes[i1] = v;
            }
        }

    }

    /**
     * Exchanges the chromosome bits from the specified cross site to the final position
     *
     * @param chromosome the chromosome to cross with
     * @param from the initial cross site
     */
    public final void cross(final BitwiseChromosome chromosome, final int from) {

        int minlen = this.length();
        if (minlen > chromosome.length()) {
            minlen = chromosome.length();
        }

        if (from > minlen) {
            return;
        }

        int i1 = from / load;

        int offset = load - (from % load);

        for (int i = 0; i < i1; ++i) {
            int v = chromosome.genes[i];
            chromosome.genes[i] = this.genes[i];
            this.genes[i] = v;
        }

        this.bitcross(offset, i1, chromosome, false);

        int[] gtmp = this.genes;
        this.genes = chromosome.genes;
        chromosome.genes = gtmp;
    }

    /**
     * Executes the bit exchange starting from the specified point; it is the start point or
     * the final one according to the after flag value.
     * The cross will involve only the bits of the int value containing the bit at the
     * point position
     *
     * @param point the cross site
     * @param chromosome the chromosome to cross with
     * @param after direction, true for crossing after, false for crossing before the specified point
     */
    private final void bitcross(final int offset, final int index, final BitwiseChromosome chromosome, final boolean after) {

        // If offset is 0 and the crossover is on left,
        // or the offset is the last bit and the crossover is on right
        // there is nothing to cross;
        if (offset == 0 && !after || offset == bits - 1 && after) {
            return;
        }

        int mask = 0xFFFFFFFF >>> (Integer.SIZE - offset);

        if (!after) {
            mask = ~mask;
        }

        int v_this = this.genes[index];
        int v_chrom = chromosome.genes[index];

        this.genes[index] = (v_this & ~mask) | (v_chrom & mask);
        chromosome.genes[index] = (v_chrom & ~mask) | (v_this & mask);
    }

    /**
     * Compares the chromosome with another.
     *
     * @param chromosome the chromosome to compare to.
     * @return true, if the two chromosome are equal.
     */
    public final boolean equals(final BitwiseChromosome chromosome) {
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

    /**
     * Returns the chromosome length expressed in bits. This value can be different from
     * the effective chromosome length (the latter can contain bits used to make the former
     * multiple of {@link Integer#SIZE})
     *
     * @return the chromosome length
     */
    public final int length() {
        return this.size * this.bits;
    }

    /**
     * Returns the chromosome length expressed in integers
     *
     * @return the number of integers
     */
    public final int getIntLength() {
        return this.genes.length;
    }

    /**
     * Randomizes the bit at the given position
     *
     * @param pos the position of bit to alter
     */
    public final void randomize(final int pos) {
        int index = pos / load;
        int offset = load - (pos % load) - 1;
        int mask = 1 << offset;
        int i = Random.getInstance().nextInt(2);
        this.genes[index] = (this.genes[index] & (~mask)) | (i << offset);
    }

    /**
     * Randomizes each chromosome bit
     */
    public final void randomize() {
        int len = this.length();
        for (int i = 0; i < len; i++) {
            this.randomize(i);
        }
    }

    /**
     * Executes the left shift of bits within the specified range. The shift is circular, so the
     * most right-side bit becomes the first bit on left.
     *
     * @param from the lower range limit
     * @param to the upper range limit
     */
    public final void leftShift(final int from, final int to) {

        if (to <= from) {
            return;
        }

        int i1 = from / load;
        int offset1 = load - (from % load) - 1; //(Integer.SIZE-from-1) % Integer.SIZE;

        int i2 = to / load;
        int offset2 = load - (to % load) - 1; //(Integer.SIZE-to-1) % Integer.SIZE;

        int bit = (this.genes[i1] >> offset1) & 1;

        if (i1 == i2) {
            this.bitshift(i1, offset2, offset1, bit);
        } else {
            bit = this.bitshift(i2, offset2, load - 1, bit);
            for (int i = i2 - 1; i > i1; --i) {
                bit = this.bitshift(i1, load - 1, 0, bit);
            }
            this.bitshift(i1, 0, offset1, bit);
        }

    }

    /**
     * Executes the right shift of bits within the specified range. The shift is circular, so the
     * most left-side bit becomes the last bit on right.
     *
     * @param from the lower range limit
     * @param to the upper range limit
     */
    public final void rightShift(final int from, final int to) {

        if (to <= from) {
            return;
        }

        int i1 = from / Integer.SIZE;
        int offset1 = Integer.SIZE - (from % Integer.SIZE) - 1; //(Integer.SIZE-from-1) % Integer.SIZE;

        int i2 = to / Integer.SIZE;
        int offset2 = Integer.SIZE - (to % Integer.SIZE) - 1; //(Integer.SIZE-to-1) % Integer.SIZE;

        int bit = (this.genes[i2] >> offset2) & 1;

        if (i1 == i2) {
            this.bitshift(i1, offset1, offset2, bit);
        } else {
            bit = this.bitshift(i1, offset1, 0, bit);
            for (int i = i1 + 1; i < i2; ++i) {
                bit = this.bitshift(i1, 0, Integer.SIZE - 1, bit);
            }
            this.bitshift(i2, Integer.SIZE - 1, offset2, bit);
        }
    }

    /**
     * Performs right or left shift in a single integer value
     *
     * @param index the index of the integer where to perform the shift
     * @param from the integer start bit position for the shift (in this position wil lbe copied the bit value)
     * @param to the integer final bit position for the shift
     * @param bit the bit to insert for execute the round shift
     * @return the bit value to give at the next integer value
     */
    private final int bitshift(final int index, final int from, final int to, final int bit) {
        if (from > to) {
            // right shift
            int submask_1 = to == 0 ? 0 : (0xFFFFFFFF >>> (bits - to));
            int submask_2 = (from + 1) == bits ? 0 : (0xFFFFFFFF << (from + 1));
            int mask = ~(submask_1 ^ submask_2);

            int value = this.genes[index] & mask;
            int b = (this.genes[index] >>> (to)) & 1;
            this.genes[index] = ((value >>> 1 | bit << from) & mask) | (this.genes[index] & ~mask);

            return b;
        } else {
            // left shift
            int submask_1 = from == 0 ? 0 : (0xFFFFFFFF >>> (bits - from));
            int submask_2 = (to + 1) == bits ? 0 : (0xFFFFFFFF << (to + 1));

            int mask = ~(submask_1 ^ submask_2);
            int value = this.genes[index] & mask;
            int b = (value >> to) & 1;

            this.genes[index] = ((value << 1 | bit << from) & mask) | (this.genes[index] & ~mask);

            return b;
        }
    }

    /**
     * Sets this chromosome as a copy of another.
     *
     * @param chromosome the chromosome to copy
     */
    public final void setAs(final BitwiseChromosome chromosome) {
        if (chromosome.genes.length != this.genes.length) {
            this.genes = new int[chromosome.genes.length];
        }
        System.arraycopy(chromosome.genes, 0, this.genes, 0, chromosome.genes.length);
        this.bits = chromosome.bits;
        this.slots = chromosome.slots;
        this.size = chromosome.size;
        this.coding = chromosome.coding;
        this.load = chromosome.load;
    }

    /**
     * Sets the default bit value at the a given position
     *
     * @param pos bit position
     */
    public final void setDefaultValueAt(final int pos) {
        int index = pos / load;
        int offset = load - (pos % load) - 1;

        int mask = 1 >> offset;
        this.genes[index] = (this.genes[index] & (~mask));
    }

    /**
     * Swaps two bits at given positions
     *
     * @param pos1 first bit position
     * @param pos2 second bit position
     */
    public final void swap(final int pos1, final int pos2) {

        int i1 = pos1 / load;
        int offset1 = load - (pos1 % load) - 1;
        int v1 = this.genes[i1];
        int m1 = 1 << offset1;
        int b1 = (this.genes[i1] >> offset1) & 1;

        int i2 = pos2 / load;
        int offset2 = load - (pos2 % load) - 1;
        int v2 = this.genes[i2];
        int m2 = 1 << offset2;
        int b2 = (this.genes[i2] >> offset2) & 1;

        if (i1 == i2) {
            this.genes[i1] = (v1 & ~(m1 | m2)) | (b1 << offset2) | (b2 << offset1);
        } else {
            this.genes[i1] = (v1 & ~m1) | (b2 << offset1);
            this.genes[i2] = (v2 & ~m2) | (b1 << offset2);
        }
    }

    /**
     * Makes a chromosome copy
     *
     * @return the chromsome clone
     */
    @Override
    public final BitwiseChromosome clone() {
        return new BitwiseChromosome(this);
    }

    @Override
    public void difference(BitwiseChromosome chromosome, double[] diff) {
        int len = this.genes.length;
        if (chromosome.genes.length < len) {
            len = chromosome.genes.length;
        }

        for (int i = 0; i < len; ++i) {
            diff[i] = this.genes[i] - chromosome.genes[i];
        }

    }

    @Override
    public Object[] toArray() {
        Integer[] toReturn = new Integer[this.genes.length];
        for (int g = 0; g < this.genes.length; g++) {
            toReturn[g] = this.genes[g];
        }
        return toReturn;
    }
    /**
     * XXX spostare questo test in una junit
     * @param args
     */
//    public static void main(String[] args) {
    //		BitwiseChromosome chrom1 = new BitwiseChromosome(4,new ByteCoding());
    //		chrom1.setIntValueAt(0, 0);
    //		chrom1.setBitValueAt(31,1);
    //		chrom1.setBitValueAt(30,1);
    //		chrom1.setBitValueAt(29,1);
    //		chrom1.setBitValueAt(28,1);
    //		System.out.println(chrom1.getIntValueAt(0));
/*
     * chrom1:001,10101 01111001 01010110 10111010 00110011 00011101 01100101 01101001 00110101 01111001 010,10110 10111010
     * chrom2:001,10011 00011101 01100101 01101001 00110101 01111001 01010110 10111010 00110011 00011101 011,00101 01101001
     * mi aspetto:
     * 001,10011 00011101 01100101 01101001 00110101 01111001 01010110 10111010 00110011 00011101 011,10110 10111010
     * 001,10101 01111001 01010110 10111010 00110011 00011101 01100101 01101001 00110101 01111001 010,00101 01101001
     */
    //		BitwiseChromosome chrom1 = new BitwiseChromosome(12,new ByteCoding());
    //		chrom1.setIntValueAt(0, 897144506);
    //		chrom1.setIntValueAt(1, 857564521);
    //		chrom1.setIntValueAt(2, 897144506);
    //
    //		BitwiseChromosome chrom2 = new BitwiseChromosome(12,new ByteCoding());
    //		chrom2.setIntValueAt(0, 857564521);
    //		chrom2.setIntValueAt(1, 897144506);
    //		chrom2.setIntValueAt(2, 857564521);
    //
    //		chrom1.cross(chrom2, 3,82);
    //		System.out.println(chrom1.getIntValueAt(0));
    //		System.out.println(chrom1.getIntValueAt(1));
    //		System.out.println(chrom1.getIntValueAt(2));
    //		System.out.println();
    //		System.out.println(chrom2.getIntValueAt(0));
    //		System.out.println(chrom2.getIntValueAt(1));
    //		System.out.println(chrom2.getIntValueAt(2));      
//    }
}
