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
 * Represents a byte coding with 8 bits unsigned representation. The range representable
 * by this coding is [0,255]. 
 * 
 * @version 1.2
 * @since 1.0
 *
 * @see GrayCoding
 * @see IntCoding
 * @see ShortCoding
 * @see WordCoding
 * @see BitSize
 * @see BooleanCoding
 */
public class ByteCoding extends BitCoding<Integer> {

    /**
     * Default constructor
     */
    public ByteCoding() {
        super(BitSize.BIT8);
    }

    /**
     * Returns the value of coded bits
     *
     * @param bits coding the value
     * @return the value
     */
    @Override
    public final Integer decode(final int bits) {
        return bits;
    }

    /**
     * Returns the bits coding the value
     *
     * @param value the value to be coded
     * @return the coding bits
     */
    @Override
    public final int encode(final Integer value) {

        int val = value;

        if (val < 0 || val > 255) {
            throw new IllegalArgumentException();
        }

        return val;
    }
}
