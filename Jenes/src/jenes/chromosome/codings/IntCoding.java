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
 * Represents an int coding with 32 bits two-complement representation. 
 * The coding is direct. The range representable is [-2147483648,2147483647].
 *  
 * @version 1.2
 * @since 1.0
 *
 * @see ByteCoding
 * @see GrayCoding
 * @see ShortCoding
 * @see WordCoding
 * @see BitSize
 * @see BooleanCoding
 */
public class IntCoding extends BitCoding<Integer> {

    /**
     * Default constructor
     */
    public IntCoding() {
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
        return value;
    }
}
