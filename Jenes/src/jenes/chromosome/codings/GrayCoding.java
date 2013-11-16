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
package jenes.chromosome.codings;

import jenes.chromosome.BitwiseChromosome.BitCoding;
import jenes.chromosome.BitwiseChromosome.BitSize;

/**
 * Represents a gray coding with 32 bits representation. In this code two successive values differ 
 * in only one digit.
 * 
 * @version 1.2
 * @since 1.0
 *
 * @see ByteCoding
 * @see IntCoding
 * @see ShortCoding
 * @see WordCoding
 * @see BitSize
 * @see BooleanCoding
 */
public class GrayCoding extends BitCoding<Integer> {

    /**
     * Default constructor
     */
    public GrayCoding() {
        super(BitSize.BIT32);
    }

    /**
     * Returns the value of coded bits
     *
     * @param bits coding the value
     * @return the value
     */
    @Override
    public final Integer decode(final int bits) {
        /*
        Let G[n:0] be the input array of bits in Gray code
        Let B[n:0] be the output array of bits in the usual binary representation
        B[n] = G[n]
        for i = n-1 downto 0
        B[i] = B[i+1] XOR G[i]
         */

        int val = bits;// it is useful to make equal the MSBs

        for (int k = this.SIZE.BITS - 2; k >= 0; --k) {
            int gk = (bits >> k) & 1;
            int bk1 = (val >> (k + 1)) & 1;
            val &= ~(1 << k);
            val |= (gk ^ bk1) << k;
        }
        return val;
    }

    /**
     * Returns the bits coding the value
     *
     * @param value the value to be coded
     * @return the coding bits
     */
    @Override
    public final int encode(final Integer value) {
        /*
        Let B[n:0] be the input array of bits in the usual binary representation, [0] being LSB
        Let G[n:0] be the output array of bits in Gray code
        G[n] = B[n]
        for i = n-1 downto 0
        G[i] = B[i+1] XOR B[i]	
         */

        int val = value;
        int gray = value; // it is useful to make equal the MSBs
        for (int k = this.SIZE.BITS - 2; k >= 0; --k) {
            int bk = (val >> k) & 1;
            int bk1 = (val >> (k + 1)) & 1;
            gray &= ~(1 << k);
            gray |= (bk ^ bk1) << k;
        }
        return gray;
    }
}
