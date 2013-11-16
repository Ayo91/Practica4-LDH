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
package jenes.tutorials.problem3;

import jenes.utils.Random;
import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.stage.operator.Mutator;

/**
 * Tutorial showing how to implement problem specific operators.
 * The problem faced in this example is the well known Tavel Salesman Problem (TSP)
 *
 * This class implements a specific mutations aimed at preserving permutations.
 *
 * Algorithm description:
 * Two random indexes, i1 and i2, are choosed; the order of the elements within the
 * range [i1,i2] changes randomly. For example:
 * <pre>
 *       i1=0; i2=3
 *       position:    0 1 2 3 4 5
 *	 start_chrom: 5 2 1 4 6 3
 *       end_chrom:   2 5 4 1 6 3
 * </pre>
 *
 * @version 2.0
 * @since 1.0
 */
public class TSPScrambleMutator extends Mutator<IntegerChromosome> {
    
    public TSPScrambleMutator(double pMut) {
        super(pMut);
    }
    
    @Override
    protected void mutate(Individual<IntegerChromosome> t) {
        int size = t.getChromosomeLength();
        int index1,index2;
        do{
            index1 = Random.getInstance().nextInt(0,size);
            index2 = Random.getInstance().nextInt(0,size);
        }while(index2==index1);
        
        int min,max;
        if(index1<index2){
            min=index1;
            max=index2;
        }else{
            min=index2;
            max=index1;
        }
        
        randomize(t.getChromosome(),min, max);
    }
    
    /**
     * Randomizes the elements chromosome within the range [min,max]
     * <p>
     * @param chrom the individual to mutate
     * @param min the lower bound
     * @param max the upper bound
     */
    public void randomize(IntegerChromosome chrom, int min, int max) {

        //we create a temporany array
        int len = max-min+1;
        int[] base = new int[len];

        //we fill it with the elements within [min,max]
        for(int i=0;i<len;i++)
            base[i]= chrom.getValue(min+i);
        
        //the loop ends when the temporany array is empty
        for( int i = 0; len > 0; --len, ++i) {
            //we choose a random position pos in the array and copy the element at pos in the chromosome
            int pos = Random.getInstance().nextInt(0,len);
            chrom.setValue(min+i,base[pos]);
            //we removes the chosen element from the temporany array
            for(int j=pos;j<(len-1);j++){
                base[j]=base[j+1];
            }
        }
    }
}
