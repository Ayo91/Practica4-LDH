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

import jenes.chromosome.IntegerChromosome;
import jenes.population.Individual;
import jenes.stage.operator.Crossover;

public class TSPCrossover extends Crossover<IntegerChromosome> {

    public TSPCrossover(double probability) {
        super(probability);
    }

    @Override
    public int spread() {
        return 2;
    }

    @Override
    protected void cross(Individual<IntegerChromosome>[] offsprings) {
        //c1 is a copy of p1; c2 is a copy of p2
        IntegerChromosome c1 = offsprings[0].getChromosome();
        IntegerChromosome c2 = offsprings[1].getChromosome();

        // The i-th city is in the i-th row;
        // -1 indicates the end of the list
        // The city are numbered from 0 to n-1
        int[] lengths = new int[c1.length()];
        int[][] list = buildNeighborsList(c1, c2, lengths);
        int[][] list2 = new int[list.length][list.length];
        for (int i = 0; i < list.length; i++) {
            list2[i] = list[i].clone();
        }

        int size = c1.length();
        for (int i = 0; i < size; i++) {
            c1.setValue(i, -1);
            c2.setValue(i, -1);
        }

        fill(c1, lengths.clone(), list);
        fill(c2, lengths, list2);
    }

    private void fill(IntegerChromosome child, int[] lengths, int[][] list) {
        int size = child.length();
        int value = this.random.nextInt(size);
        for (int i = 0; i < size; ++i) {
            child.setValue(i, value);
            removeFromList(list, value, lengths);

            // The list of the selected city isn't empty
            if (lengths[value] != 0) {
                value = getSmallerLength(lengths, list[value]);
            } else {
                // One taken randomly not contained in c1. We take the first that is not contained.
                int pos = 0;
                boolean stop = false;
                while (pos < size && !stop) {
                    // We see if pos is contained in child. The filled positions are from 0 to i.
                    boolean thereIs = false;
                    for (int j = 0; j <= i; j++) {
                        thereIs = thereIs | (pos == child.getValue(j));
                    }
                    if (thereIs) {
                        pos++;
                    } else {
                        stop = true;
                    }
                }
                value = pos;
            }
        }


    }

    /** prende l'elemento in values con la lunghezza minore */
    private int getSmallerLength(int[] lengths, int[] values) {
        int currentPos = 0;

        while (values[currentPos] == -1) {
            currentPos++;
        }

        int currentSmallerLength = values[currentPos];

        for (currentPos++; currentPos < values.length; currentPos++) {
            if (values[currentPos] != -1) {
                if (lengths[values[currentSmallerLength]] > lengths[values[currentPos]]) {
                    currentSmallerLength = currentPos;
                    currentPos++;
                }
            }
        }
        return currentSmallerLength;
    }

    /** elimina value dalla lista list; le lunghezze sn aggiornate */
    private void removeFromList(int[][] list, int value, int[] lengths) {
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[0].length; j++) {
                if (i != value) {
                    if (list[i][j] == value) {
                        list[i][j] = -1;
                        lengths[i]--;
                    }
                }
            }
        }
    }

    /** costruisce la matrice dei vicini; e' una matrice numCitta x 4 */
    private int[][] buildNeighborsList(IntegerChromosome p1, IntegerChromosome p2, int[] lengths) {
        int size = p1.length();
        int[][] list = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                list[i][j] = -1;
            }
        }

        for (int i = 0; i < size; i++) {
            //prendo i vicini da p1
            int currentValue = p1.getValue(i);
            int nextPos = i == size - 1 ? 0 : i + 1;
            int lastPos = i == 0 ? size - 1 : i - 1;
            list[currentValue][p1.getValue(nextPos)] = p1.getValue(nextPos);
            list[currentValue][p1.getValue(lastPos)] = p1.getValue(lastPos);
            lengths[currentValue] = 2;

            //cerco currentValue in p2 e ne prendo i vicini
            int k = 0;
            while (p2.getValue(k) != currentValue) {
                k++;
            }

            int _nextPos = k == size - 1 ? 0 : k + 1;
            int _lastPos = k == 0 ? size - 1 : k - 1;
            if (p2.getValue(_nextPos) != list[currentValue][p1.getValue(nextPos)] && p2.getValue(_nextPos) != list[currentValue][p1.getValue(lastPos)]) {
                list[currentValue][p2.getValue(_nextPos)] = p2.getValue(_nextPos);
                lengths[currentValue]++;
            }

            if (p2.getValue(_lastPos) != list[currentValue][p1.getValue(nextPos)] && p2.getValue(_lastPos) != list[currentValue][p1.getValue(lastPos)] && ((lengths[currentValue] == 3) && (p2.getValue(_lastPos) != list[currentValue][p2.getValue(_nextPos)]))) {
                list[currentValue][p2.getValue(_lastPos)] = p2.getValue(_lastPos);
                lengths[currentValue]++;
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        IntegerChromosome p1 = new IntegerChromosome(20, -1, 19);

        p1.setValue(0, 13);
        p1.setValue(1, 1);
        p1.setValue(2, 3);
        p1.setValue(3, 7);
        p1.setValue(4, 2);
        p1.setValue(5, 8);
        p1.setValue(6, 4);
        p1.setValue(7, 10);
        p1.setValue(8, 14);
        p1.setValue(9, 5);
        p1.setValue(10, 17);
        p1.setValue(11, 9);
        p1.setValue(12, 0);
        p1.setValue(13, 12);
        p1.setValue(14, 16);
        p1.setValue(15, 6);
        p1.setValue(16, 11);
        p1.setValue(17, 18);
        p1.setValue(18, 19);
        p1.setValue(19, 15);

        IntegerChromosome p2 = p1.clone();

        p2.setValue(0, 5);
        p2.setValue(1, 8);
        p2.setValue(2, 16);
        p2.setValue(3, 6);
        p2.setValue(4, 4);
        p2.setValue(5, 9);
        p2.setValue(6, 13);
        p2.setValue(7, 19);
        p2.setValue(8, 0);
        p2.setValue(9, 2);
        p2.setValue(10, 10);
        p2.setValue(11, 3);
        p2.setValue(12, 18);
        p2.setValue(13, 17);
        p2.setValue(14, 12);
        p2.setValue(15, 14);
        p2.setValue(16, 11);
        p2.setValue(17, 15);
        p2.setValue(18, 7);
        p2.setValue(19, 1);

        Individual<IntegerChromosome>[] inds = new Individual[2];
        inds[0] = new Individual<IntegerChromosome>(p1);
        inds[1] = new Individual<IntegerChromosome>(p2);

        TSPCrossover tsp = new TSPCrossover(1);
        tsp.cross(inds);
    }
}
