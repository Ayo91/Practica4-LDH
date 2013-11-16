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
package jenes.tutorials.old.problem5;


/**
 * Tutorial illustrating the use of object-oriented chromosomes, whose
 * allele set can be defined by the user for each gene.
 *
 * In this example the chromosomes are combinations of colors. We aim at finding
 * the vector of colors closest to a given sequence.
 *
 * This class defines the enumeration of possible colors.
 *
 * @version 1.0
 * @since 1.0
 */
public enum Color {
    
    RED, BLACK, WHITE;
    
}
