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
package jenes.performance;

import java.util.Vector;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.stage.operator.Mutator;

public class TSPMutator extends Mutator<IntegerChromosome> {

    public TSPMutator(double probability) {
        super(probability);
    }

    @Override
    protected void mutate(Individual<IntegerChromosome> t) {
        IntegerChromosome chrom = t.getChromosome();
        int size = chrom.length();
        if (this.random.nextBoolean(0.5)) {
            //swap only one time
            chrom.swap(this.random.nextInt(size), this.random.nextInt(size));
        } else {
            Vector<Integer> v = new Vector<Integer>();
            for (int i = 0; i < size; i++) {
                v.add(chrom.getValue(i));
            }

            int nNodes = this.random.nextInt(size / 2);
            int[] tmp = new int[nNodes];
            int startPos = this.random.nextInt(size);

            for (int i = 0; i < nNodes; i++) {
                tmp[i] = v.remove(startPos);
                if (v.size() == startPos) {
                    startPos = 0;
                }
            }
            boolean invert = this.random.nextBoolean(0.5);
            int insertPos = this.random.nextInt(size - nNodes + 1);
            for (int i = 0; i < nNodes; i++) {
                int pos = invert ? i : nNodes - 1 - i;
                v.add(insertPos, tmp[pos]);
            }

            for (int i = 0; i < size; i++) {
                chrom.setValue(i, v.get(i));
            }
        }
    }
}
