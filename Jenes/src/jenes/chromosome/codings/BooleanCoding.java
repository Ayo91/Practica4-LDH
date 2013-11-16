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
 * Represents a boolean coding with 1 bit representation. The value false is associated to 0, and true to 1.
 *
 * @version 1.2
 * @since 1.2
 *
 * @see ByteCoding
 * @see IntCoding
 * @see ShortCoding
 * @see WordCoding
 * @see BitSize
 */
public class BooleanCoding extends BitCoding<Boolean> {

    /**
     * Default constructor
     */
    public BooleanCoding() {
		super(BitSize.BIT1);
	}

    /**
     * Returns the value of coded bits.
     * If the argument is 1 it returns true, if 0 then false.
     *
     * @param bits coding the object
     * @return the value
     */
    @Override
    public Boolean decode(int bits) {
        return bits == 1;
    }

    /**
     * Returns the bits coding the value.
     * It returns 0 if the argument is false, 1 if true.
     *
     * @param obj the boolean value to be coded
     * @return the coding bits
     */
    @Override
    public int encode(Boolean obj) {
        return obj ? 1 : 0;
    }
    
}
