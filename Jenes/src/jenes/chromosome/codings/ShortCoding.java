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
 * Represents a short coding with 16 bits. There are two possible representations: the two complement 
 * ond and the module and sign one. The range representable with the former is [-32768,32767]; the one representable
 * with the latter is [-32767,32767] (the difference is the latter provides two different codes to represent the 0).
 * 
 * @version 1.2
 * @since 1.0
 *
 * @see ByteCoding
 * @see IntCoding
 * @see GrayCoding
 * @see WordCoding
 * @see BitSize
 * @see BooleanCoding
 */
public class ShortCoding extends BitCoding<Integer> {

    /**
     * The integer coding representations
     */
    public static enum Mode {

        /**
         * Two's complement
         */
        TWOS_COMPLEMENT,
        /**
         * Module aqnd sign
         */
        MODULE_AND_SIGN
    };
    private Mode mode;

    /**
     * Default constructor. The default mode is two's complement.
     */
    public ShortCoding() {
        this(Mode.TWOS_COMPLEMENT);
    }

    /**
     * Creates a ShortCoding with the specified mode, that is the number representation scheme.
     *
     * @param mode can be two's complement or module and sign.
     */
    public ShortCoding(final Mode mode) {
        super(BitSize.BIT16);
        this.mode = mode;
    }

    /**
     * Returns the value of coded bits
     *
     * @param bits coding the value
     * @return the value
     */
    @Override
    public final Integer decode(final int bits) {
        int value = 0;
        switch (mode) {
            case TWOS_COMPLEMENT: {
                int mask = 0x8000;
                int sign = (bits & mask) >> BitSize.BIT16.BITS - 1;
                value = sign == 1 ? -0x8000 : 0;
                value += bits & ~mask;
                break;
            }
            case MODULE_AND_SIGN: {
                int mask = 0x8000;
                int sign = (bits & mask) >> BitSize.BIT16.BITS - 1;
                int module = bits & ~mask;
                value = sign == 1 ? -module : module;
                break;
            }
        }
        return value;
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

        if (val < (mode == Mode.TWOS_COMPLEMENT ? -32768 : -32767) || val > 32767) {
            throw new IllegalArgumentException();
        }

        if (val < 0) {
            switch (mode) {
                case TWOS_COMPLEMENT:
                    val += 2 << this.SIZE.BITS;
                    break;
                case MODULE_AND_SIGN:
                    val = (2 << this.SIZE.BITS) - val;
                    break;
            }
        }

        return val & this.SIZE.MASK;
    }
}
