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

/**
 * An AlleleSet represents an alphabet of object gene allele values. Each {@link ObjectChromosome.Gene} of an 
 * {@link ObjectChromosome} has an allele set containing all the object values it can assume.
 * A custom set of values can be determined by implementing this interface.
 * 
 * @param <T> class of elements held by AlleleSet
 * 
 * @version 2.0
 * @since 1.0
 * 
 * @see jenes.chromosome.ObjectChromosome
 */
public interface AlleleSet<T> {

    /**
     * Gets the allele value at the specified position
     * <p>
     * @param pos the index of the desidered allele value
     * @return the allele value at the specified position
     */
    public abstract T getElementAt(int pos);

    /**
     * Gets a random allele value within this alphabet.
     * The allele value returned has to be a copy of the value in the allele set.
     * <p>
     * @return the random value selected
     */
    public abstract T getRandomValue();

    /**
     * Returns the alphabet size
     * <p>
     * @return the alphabet size
     */
    public abstract int size();

    /**
     * Provides the genetic difference between two alleles.
     *
     * @param a0 - first allele
     * @param a1 - second allele
     * @return - the genetic difeerence
     */
    public abstract double difference(T a0, T a1);
    
}