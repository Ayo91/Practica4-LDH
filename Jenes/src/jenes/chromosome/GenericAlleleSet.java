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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import jenes.utils.Random;

/**
 * A GenericAlleleSet represents a concrete allele set implementation.
 * It is usefull to instantiate a new AlleleSet when the allele values are contained by a list,
 * by an array or by a {@link Set}.
 * At creation time, the aplhabet objects are not cloned so:
 * <p>
 * Note that this implementation is not synchronized. If multiple
 * threads access a GenericAlleleSet concurrently, and at least one of the threads modifies
 * the set, it must be synchronized externally or wrapped using the {@link Collections#synchronizedSet(Set)}
 * method.
 * <p>
 * Note that if values are modified externally, the allele set object also change according at that modification.
 * <p>
 * Some examples of code are provided below.
 * <p><blockquote><pre>
 *    Set s = Collections.synchronizedSet(new HashSet(...));
 *    GenericAlleleSet geneticAlleleSet = new GenericAlleleSet(s);
 * </pre></blockquote>
 * to build a generic allele set with the object value accesses synchronized.
 * <p><blockquote><pre>
 *    GenericAlleleSet generic alleleSet = new GenericAlleleSet<Boolean>(true, false),
 * </pre></blockquote>
 * to build a generic allele set with the boolean values as allele object values.
 * <p>
 * <p><blockquote><pre>
 *    GenericAlleleSet generic alleleSet = new GenericAlleleSet(AnyJavaEnum.values()) );
 * </pre></blockquote>
 * to build a generic allele set with the enum values.
 * <p>
 * @param <T>
 *
 * @version 2.0
 * @since 1.0
 *
 * @see jenes.chromosome.ObjectChromosome
 */
public class GenericAlleleSet<T> implements AlleleSet<T> {

    private T[] set;
    private double[][] diff;
    
    /**
     * Creates a new AlleleSet instance with the alphabet values contained by the specified set
     * <p>
     * @param set the set with the alphabet allele values
     */
    @SuppressWarnings("unchecked")
    public GenericAlleleSet(final Set<T> set){
        this.set=(T[])set.toArray();
    }
    
    /**
     * Creates a new AlleleSet instance with the alphabet values contained by the specified array
     * <p>
     * @param values the alphabet values array
     */
    public GenericAlleleSet(final T ... values) {
        this.set = values;
    }
    
    /**
     * Creates a new AlleleSet instance with the alphabet values contained by the specified list
     * <p>
     * @param list the alphabet values list
     */
    public GenericAlleleSet(final List<T> list) {
        this.set=(T[])list.toArray();
    }

    /**
     * Returns the allele at a given positition.
     * @param pos
     * @return the element
     */
    @Override
    public final T getElementAt(final int pos){
        return(set[pos]);
    }

    /**
     * Returns the index of element a.
     *
     * @param a - the allele to find
     * @return the allele index
     */
    public final int getIndexOf(final T a) {
       for( int i=0; i < this.set.length; ++i ) {
           if( a == set[i] )
               return i;
       }
       return -1;
    }

    /**
     * Returns an allele randomly chosen.
     *
     * @return a value
     */
    @Override
    public final T getRandomValue(){
        return(set[Random.getInstance().nextInt(set.length)]);
    }

    /**
     * Returns the number of alleles held by the allele set.
     *
     * @return the number of alleles
     */
    @Override
    public final int size() {
        return set.length;
    }

    /**
     * Sets the difference matrix between alleles.
     * It is expected that diff is a square matrix according to set size, with diff(i,i) = 0 and diff(i,j) = -diff(j,i).
     *
     * @param diff - the difference matrix
     */
    public void setDifferences(double[][] diff) {

        int sz = this.size();

        if( diff.length == sz )
            throw new RuntimeException("Difference distance has size non-compatible with the allele set.");

        for( int i = 0; i < sz; ++i ) {
            if( diff[i].length == sz )
                throw new RuntimeException("Difference distance has size non-compatible with the allele set.");

            if(diff[i][i] != 0)
                throw new RuntimeException("Diagonal elements in the difference matrix are expected to be zero.");

            for( int j = i+1; j < sz; ++j ) {
                if( diff[i][j] != -diff[j][i] )
                    throw new RuntimeException("Elements in the difference matrix are expected to be diff(i,j) = -diff(j,i).");
            }
        }

        this.diff = diff;
    }

    /**
     * Returns the default difference matrix as given by the difference of allele positions in the set.
     * @return the allele difference matrix.
     */
    public double[][] getDefaultDifferences() {
        int sz = this.size();
        double[][] d = new double[sz][sz];
        for( int i = 0; i < sz; ++i ) {
            for( int j = 0; j < sz; ++j ) {
                d[i][j] = i-j;
            }
        }
        return d;
    }

    @Override
    public double difference(T a0, T a1) {
        
        int i = this.getIndexOf(a0);
        int j = this.getIndexOf(a1);

        return i!=-1 || j!=-1 ? this.diff[i][j] : Double.NaN;
    }
}
