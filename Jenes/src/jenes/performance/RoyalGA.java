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

import jenes.GeneticAlgorithm;
import jenes.chromosome.BitwiseChromosome;
import jenes.chromosome.codings.ByteCoding;
import jenes.population.Individual;
import jenes.population.Population;

public class RoyalGA extends GeneticAlgorithm<BitwiseChromosome> {

    /**
     * Number of blocks
     */
    private int NBLOCKS;
    /**
     * Block size, i.e, length of target schemata.
     */
    private int BLOCKSIZE;
    /**
     * Offset between the start of two blocks.
     * <p>
     * Equals (BLOCK_SIZE + GAPSIZE)
     */
    private int BLOCK_DELTA;
    /**
     * Holland's m*, up to this many bits in low level block gets reward
     */
    private int BLOCK_THRESHOLD = 4;
    /**
     * Holland's U* reward parameter (first block earns this)
     */
    private double FIRST_BONUS = 1.0;
    /**
     * Holland's u increment for lowest level match
     */
    private double FOLLOWING_BONUS = 0.3;
    /**
     * Holland's v reward/penalty per bit
     */
    private double PART_CALCULATION_STEP = 0.02;
    private int[] block_array;

    /**
     * 
     * @param pop
     * @param gen
     * @param sectionSize the length of each section
     * @param blockSize the length of each block (each of them is contained by a section)
     * @param numBlocks the number of blocks
     */
    public RoyalGA(Population<BitwiseChromosome> pop, int gen, int sectionSize, int blockSize, int numBlocks) {
        super(pop, gen);

        BLOCK_DELTA = sectionSize;
        BLOCKSIZE = blockSize;
        NBLOCKS = numBlocks;

        this.block_array = new int[NBLOCKS];
    }

    @Override
    public void evaluateIndividual(Individual<BitwiseChromosome> individual) {
        BitwiseChromosome chrom = individual.getChromosome();
        double score = 0.0;

        /** part calculation and first level bonus block */
        int foundFirstLevelBlocks = 0;
        for (int block = 0; block < NBLOCKS; block++) { // for each first level block

            int num_bits = 0;

            int block_start = block * BLOCK_DELTA;
            int next_block = block_start + BLOCKSIZE;

            // count the bits in the current block
            for (int j = block_start; j < next_block; j++) {
                if (chrom.getBitValueAt(j) == 1) {
                    num_bits++;
                }
            }

            // assign the score according to the number of bits found
            if (num_bits > BLOCK_THRESHOLD && num_bits < BLOCKSIZE) {
                score -= ((num_bits - BLOCK_THRESHOLD) * PART_CALCULATION_STEP);
            } else if (num_bits <= BLOCK_THRESHOLD) {
                score += (num_bits * PART_CALCULATION_STEP);
            }

            // mark which lowest level blocks were found
            if (num_bits == BLOCKSIZE) {
                block_array[block] = 1;
                foundFirstLevelBlocks++;
            } else {
                block_array[block] = 0;
            }
        }

        // assign bonus for filled first level blocks
        if (foundFirstLevelBlocks > 0) {
            score += (FIRST_BONUS + ((foundFirstLevelBlocks - 1) * FOLLOWING_BONUS));
        }


        /** higher level bonus blocks */
        // max blocks in current level
        int mx_blocks = NBLOCKS;  // now number of filled low level blocks

        boolean proceed = true; // should we look at the next higher level?
        int level = 0;

        while ((mx_blocks > 1) && proceed) {
            proceed = false;

            int blocks_found = 0; //number of blocks found with size equal to mx_blocks 

            // there are mx_blocks valid blocks in the blockarray each time
            // round, so mx_blocks=2 is the last.

            for (int block = 0, index = 0; block < (mx_blocks / 2) * 2; block += 2, index++) {
                if (block_array[block] == 1 && block_array[block + 1] == 1) {
                    blocks_found++;
                    block_array[index] = 1; //there is a block with size equal to mx_block
                    proceed = true;
                } else {
                    block_array[index] = 0;
                }
            }
            if (blocks_found > 0) {
                score += FIRST_BONUS + (blocks_found - 1) * FOLLOWING_BONUS;
                level++;
            }
            //max blocks in next level decrease
            mx_blocks /= 2;
        }

        individual.setScore(score);
    }

    public static void main(String[] args) {
        BitwiseChromosome c = new BitwiseChromosome(16, new ByteCoding());
        Individual<BitwiseChromosome> ind = new Individual<BitwiseChromosome>(c);
        Population<BitwiseChromosome> pop = new Population<BitwiseChromosome>(ind, 5);

        c.setIntValueAt(0, 16777215);
        c.setIntValueAt(1, 16777215);
        c.setIntValueAt(2, 16777215);
        c.setIntValueAt(3, 16777215);
//		for(int i=0; i < 16; i++)
//			c.setValueAt(i,255);

        RoyalGA g = new RoyalGA(pop, 4, 8, 8, 16);
        g.evaluateIndividual(ind);
        System.out.println(ind.getScore());

    }
}
