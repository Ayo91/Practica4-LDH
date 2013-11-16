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
 * Represents a word coding with 16 bits unsigned representation. The range representable
 * by this coding is [0,65535]. 
 * 
 * @version 1.2
 * @since 1.0
 *
 * @see GrayCoding
 * @see IntCoding
 * @see ShortCoding
 * @see ByteCoding
 * @see BitSize
 * @see BooleanCoding
 */
public class WordCoding extends BitCoding<Integer> {

    /**
     * Default constructor
     */
    public WordCoding() {
        super(BitSize.BIT16);
    }

    /**
     * Returns the value of coded bits
     *
     * @param bits coding the value
     * @return the value
     */
    @Override
    public final Integer decode(final int bits) {
        return 0x0000FFFF & bits;
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
        if (val < 0 || val > 65535) {
            throw new IllegalArgumentException();
        }

        return val;
    }
}
