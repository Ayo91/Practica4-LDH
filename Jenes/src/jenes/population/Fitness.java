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
package jenes.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jenes.chromosome.Chromosome;

/**
 * This class aims at implementing the scoring and sorting of individuals.
 * <p>
 * Socring of individuals is obtained by implementing the abstract method <code>evaluate</code>.
 * The class supports multi-objective optimizazion, so that a vector of scores can be associated to each individual.
 * Flags have to be given at construction. Each flag should be <b>true</b> if the related objective is to maximize, otherwise false.
 * <p>
 * Sorting is performed according to objectives, so that better individuals come first. In order to keep sorting efficient, there are
 * four modes, namely <code>PARTIAL</code>, <code>HIERACHICAL</code>, <code>DOMINANCE</code>, and <code>CROWDING</code>.
 * <p>
 * In <code>PARTIAL</code> mode, sorting is perfomed using only the first objective.
 * <code>HIERARCHICAL</code> mode considers objectives in sequence. 
 * <code>DOMINANCE</code> is used to sort individuals according to dominance.
 * <code>CROWDING</code> sorts elements by dominance first, and by crowding distance after, as suggested by Deb for NSGA2.
 * <p>
 * Cloning is necessary for multithreading.
 * <p>
 * 
 * @version 2.0
 * @since 2.0
 */
public abstract class Fitness<C extends Chromosome> implements Cloneable {

    /** Sorter used by static methods */
    private static Sorter DEFAULTSORTER;
    /** Sorting mode used by static methods */
    private static SortingMode DEFAULTSORTINGMODE = SortingMode.PARTIAL;

    static {
        DEFAULTSORTER = new Sorter();
        DEFAULTSORTER.mode = DEFAULTSORTINGMODE;
    }
    /** Objective flag vector */
    private boolean[] biggerIsBetter;
    /** Sorter */
    private Sorter sorter = new Sorter();

    /**
     * Creates a new fitness with the defined number of objectives imposing all 
     * to maximize or minimize solutions.
     * 
     * @param m             numbers of objective
     * @param maximize      when <code>true</code> the algorithm will find solution
     * with maximum score; <code>false</code> otherwise
     */
    public Fitness(int m, boolean maximize) {
        assert m > 0 : "Jenes: at least one objective must be declared for Fitness constructor";

        boolean[] bis = new boolean[m];
        for (int i = 0; i < m; ++i) {
            bis[i] = maximize;
        }
        this.biggerIsBetter = bis;
    }

    /**
     * Creates a new fitness where the number of objectives is derived by the 
     * lenght of array given as argument. Each cell of array represent a flag 
     * for maximize or minimize that objective
     * 
     * @param bis     a boolean array. When a cell contains a <code>true</code> 
     * value the algorithm will find solution with maximum score; 
     * <code>false</code> otherwise
     */
    public Fitness(boolean... bis) {
        assert bis.length > 0 : "Jenes: at least one objective must be declared for Fitness constructor";

        this.biggerIsBetter = new boolean[bis.length];
        System.arraycopy(bis, 0, biggerIsBetter, 0, bis.length);
    }

    @Override
    public final Fitness<C> clone() {
        try {
            Fitness<C> copy = this.duplicate();

            copy.biggerIsBetter = new boolean[this.biggerIsBetter.length];
            System.arraycopy(this.biggerIsBetter, 0, copy.biggerIsBetter, 0, this.biggerIsBetter.length);

            copy.sorter = new Sorter();
            copy.sorter.mode = this.sorter.mode;

            return copy;

        } catch (Exception ex) {
            throw new RuntimeException("Jenes: problem in cloning fitness", ex);
        }
    }

    /**
     * Builds a copy instance. It can be overwritten by subclasses.
     * <p>
     * @return copy instance
     */
    protected Fitness<C> duplicate() throws CloneNotSupportedException {
        return (Fitness<C>) super.clone();
    }

    /**
     * Evaluates a single {@link Individual}. This evaluation of individuals is
     * specifically related to the problem to solve. 
     * <p>
     *
     * @param individual
     *            the individual to be evaluated
     */
    public abstract void evaluate(Individual<C> individual);

    /**
     * Initializes the {@link Individual} given as parameter by resetting its scores.
     * <p>
     * @param ind       individual to be initialize
     */
    public void init(Individual<C> ind) {
        ind.resetScores(this.getNumOfObjectives());
    }

    /**
     * Initializes the {@link Population} given as parameter by resetting the 
     * scores of all individuals.
     * <p>
     * @param pop       pupolation to be initialize
     */
    public void init(Population<C> pop) {
        for (Individual<C> ind : pop) {
            this.init(ind);
        }
    }

    /**
     * Sets the {@link SortingMode} of {@link Population}
     * <p>
     * @param sortingMode       sorting mode
     */
    public final SortingMode setSortingMode(SortingMode sortingMode) {
        SortingMode old = this.sorter.mode;
        this.sorter.mode = sortingMode;
        return old;
    }

    /**
     * Sorts the {@link Population} given as argument 
     * <p>
     * @param pop       population to be sorted
     */
    public final void sort(final Population<C> pop) {
        pop.setSortingBy(this.biggerIsBetter);
        List<Individual> sort = this.sorter.sort(pop, this.biggerIsBetter);
        pop.reset(sort, true);
    }

    /**
     * Sorts the {@link Population} given as argument using the {@link SortingMode}
     * given as argument
     * <p>
     * @param mode      sorting mode
     * @param pop       population to be sorted
     */
    public final void sort(final SortingMode mode, final Population<C> pop) {
        SortingMode sm = this.setSortingMode(mode);
        this.sort(pop);
        this.setSortingMode(sm);
    }

    /**
     * Sorts the list of individuals given as argument
     * 
     * @param list      list of individual to be sorted
     */
    public final void sort(final List<Individual<C>> list) {
        List sorted = this.sorter.sort(list, this.biggerIsBetter);
        list.clear();
        list.addAll(sorted);
    }

    /**
     * Sorts the list of individuals  given as argument using the {@link SortingMode}
     * given as argument
     * <p>
     * @param mode      sorting mode
     * @param list      list of individual to be sorted
     */
    public final void sort(final SortingMode mode, final List<Individual<C>> list) {
        SortingMode sm = this.setSortingMode(mode);
        this.sort(list);
        this.setSortingMode(sm);
    }

    /**
     * Sorts the {@link Population} given as argument using the array given as 
     * argument. Each cell of array represent a flag for maximize or minimize
     * that objective
     * <p>
     * @param pop       population to be sorted
     * @param bis       a boolean array. Each cell contains a <code>true</code> 
     * if the corresponding score to be maximized; <code>false</code> otherwise
     */
    public static <K extends Chromosome> void sort(final Population<K> pop, final boolean... bis) {
        pop.setSortingBy(bis);
        List<Individual> sorted = Fitness.DEFAULTSORTER.sort(pop, bis);
        pop.reset(sorted, true);
    }

    /**
     * Sorts the {@link Population} given as argument using the {@link SortingMode} 
     * and the array given as arguments. Each cell of array represent a flag for 
     * maximize or minimize that objective
     * <p>
     * @param sortingMode   sorting mode
     * @param pop           population to be sorted
     * @param bis           a boolean array. Each cell contains a <code>true</code> 
     * if the corresponding score to be maximized; <code>false</code> otherwise
     */
    public static <K extends Chromosome> void sort(final SortingMode sortingMode, final Population<K> pop, final boolean... bis) {
        Fitness.DEFAULTSORTER.mode = sortingMode;
        Fitness.sort(pop, bis);
        Fitness.DEFAULTSORTER.mode = Fitness.DEFAULTSORTINGMODE;
    }

    /**
     * Sorts the list given as argument using the array given as 
     * argument. Each cell of array represent a flag for maximize or minimize
     * that objective
     * 
     * @param pop       population to be sorted
     * @param bis       a boolean array. Each cell contains a <code>true</code> 
     * if the corresponding score to be maximized; <code>false</code> otherwise
     */
    public static <K extends Chromosome> void sort(final List<Individual<K>> list, final boolean... bis) {

        List<Individual> sorted = Fitness.DEFAULTSORTER.sort(list, bis);

        //clear and reset input-output list
        list.clear();
        for (Individual i : sorted) {
            list.add(i);
        }
    }

    /**
     * Sorts the list given as argument using the {@link SortingMode} 
     * and the array given as arguments. Each cell of array represent a flag for 
     * maximize or minimize that objective
     * 
     * @param sortingMode   sorting mode
     * @param pop           population to be sorted
     * @param bis           a boolean array. Each cell contains a <code>true</code> 
     * if the corresponding score to be maximized; <code>false</code> otherwise
     */
    public static <K extends Chromosome> void sort(final SortingMode sortingMode, final List<Individual<K>> list, final boolean... bis) {
        Fitness.DEFAULTSORTER.mode = sortingMode;
        Fitness.sort(list, bis);
        Fitness.DEFAULTSORTER.mode = Fitness.DEFAULTSORTINGMODE;
    }

    /**
     * Sorts the list of {@link Individual} given as argument. Individuals are 
     * sorted by score in position <code> dim </code>. 
     * 
     * @param list      list to be ordered
     * @param dim       index score to sort
     */
    public final void partialsort(final List<Individual<C>> list, final int dim) {
        partialsort(list, dim, this.biggerIsBetter[dim]);
    }

    /**
     * Sorts the list of {@link Individual} given as argument. Individuals are 
     * sorted by score in position <code> dim </code>. Sort direction is set by 
     * <code> bib </code>
     * 
     * @param <K>       extends Chromosome
     * @param list      list to be ordered
     * @param dim       index score to sort
     * @param bib       Sorting is ascending if <code> bib </code> is 
     * <code> true </code>; sorting is descending if <code> bib </code> is 
     * <code> false </code>  
     */
    public static <K extends Chromosome> void partialsort(final List<Individual<K>> list, final int dim, final boolean bib) {

        Collections.sort(list, new Comparator<Individual<?>>() {

            public int compare(Individual<?> i1, Individual<?> i2) {
                int res = 0;
                if (i1.getScore(dim) > i2.getScore(dim)) {
                    res = 1;
                } else if (i1.getScore(dim) > i2.getScore(dim)) {
                    res = -1;
                }

                if (!bib) {
                    res *= -1;
                }

                return res;
            }
        });

    }

    /**
     * Returns the number of objectives
     * 
     * @return number of objective
     */
    public final int getNumOfObjectives() {
        return this.biggerIsBetter.length;
    }

    /**
     * Returns the objective flag array. Each element is true is the corresponding objective is to maximize, false if it is to minimize.
     * Values should not be changed.
     * 
     * @return the  array
     */
    public final boolean[] getBiggerIsBetter() {
        return this.biggerIsBetter;
    }

    /**
     * Returns a positive, negative or zero value according to dominance relation between individuals i1 and i2
     * 
     * @param i1    individual
     * @param i2    individual
     * @return +1 if i1 dominates i2, -1 if i2 dominates i2, 0 otherwise
     */
    public final int dominance(final Individual<C> i1, final Individual<C> i2) {
        return dominance(i1, i2, this.biggerIsBetter);
    }

    /**
     * Checks if individuals i1 dominates i2.
     * 
     * @param i1    individual
     * @param i2    individual
     * @return  true, if i1 dominates i2
     */
    public boolean dominates(final Individual<C> i1, final Individual<C> i2) {
        return dominates(i1, i2, this.biggerIsBetter);
    }

    /**
     * Returns a positive, negative or zero value according to dominance relation between individuals i1 and i2, given the objective flag array.
     * 
     * @param i1    individual
     * @param i2    individual
     * @param bis   objective flag array
     * @return +1 if i1 dominates i2, -1 if i2 dominates i2, 0 otherwise
     */
    public static int dominance(final Individual<?> i1, final Individual<?> i2, final boolean... bis) {

        if (dominates(i1, i2, bis)) {
            return 1;
        } else if (dominates(i2, i1, bis)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Checks if individuals i1 dominates i2.
     * 
     * @param i1    individual
     * @param i2    individual
     * @param bis   objective flag array
     * @return  true, if i1 dominates i2
     */
    public static boolean dominates(final Individual<?> i1, final Individual<?> i2, final boolean... bis) {
        double[] s1 = i1.getAllScores();
        double[] s2 = i2.getAllScores();
        int m = bis.length;

        if (s1.length != m || s2.length != m) {
            throw new IllegalStateException("[Jenes]: individuals must have scores compatible with the number of objectives!");
        }

        for (int i = 0; i < m; ++i) {

            boolean gt = bis[i];

            if (gt && !(s1[i] > s2[i]) || !gt && !(s1[i] < s2[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sorting modes
     */
    public static enum SortingMode {

        PARTIAL, HIERACHICAL, DOMINANCE, CROWDING
    }

    /**
     * Class delegate to individual sorting
     */
    private static class Sorter {

        /**
         * Elements used by CROWDING mode.
         */
        private static class CrowdingRef {

            /** Individual */
            private Individual<? extends Chromosome> ref;
            /** Front assigned to the individual */
            private int front;
            /** Crowding distance */
            private double distance;
        }
        
        private SortingMode mode = SortingMode.DOMINANCE;
        private CrowdingRef[] refs = null;
        private int segments[] = null;
        private int ordering[] = null;
        private List<Individual> list = new ArrayList<Individual>();
        private List<Individual> listForRank = new ArrayList<Individual>();

        /**
         * Sorts the given population, according to the given flag array.
         *
         * @param pop   population to sort
         * @param bis   objective flag array
         * @return  sorted list of individuals
         */
        private List<Individual> sort(Population<?> pop, boolean[] bis) {
            List<Individual> sorted = sort(pop.getIndividuals(), bis);
            return sorted;
        }

        /**
         * Sorts the given list, according to the given flag array.
         * 
         * @param in    list of individuals to sort
         * @param bis   objective flag array
         * @return list sorted list
         */
        private <K extends Chromosome> List<Individual> sort(List<Individual<K>> in, final boolean[] bis) {

            this.list.clear();
            this.list.addAll(in);

            if (this.mode == SortingMode.PARTIAL) {
                if (bis[0]) {
                    Collections.sort(this.list, Collections.reverseOrder());
                } else {
                    Collections.sort(this.list);
                }
            } else if (this.mode == SortingMode.HIERACHICAL) {
                Collections.sort(this.list, new Comparator<Individual>() {

                    public int compare(Individual i1, Individual i2) {

                        double[] s1 = i1.getAllScores();
                        double[] s2 = i2.getAllScores();
                        int m = bis.length;

                        if (s1.length != m || s2.length != m) {
                            throw new IllegalStateException("[Jenes]: individuals must have scores compatible with the number of objectives!");
                        }

                        int i = 0;
                        DIFFERENT:
                        {
                            for (i = 0; i < m; ++i) {
                                if (s1[i] != s2[i]) {
                                    break DIFFERENT;
                                }
                            }
                            return 0;
                        }
                        //minus servs to obtain the ascending order
                        return -(bis[i] && s1[i] > s2[i] || !bis[i] && s1[i] < s2[i] ? -1 : 1);
                    }
                });
            } else if (this.mode == SortingMode.DOMINANCE) {
                Collections.sort(this.list, new Comparator<Individual>() {

                    public int compare(Individual i1, Individual i2) {
                        //minus servs to obtain the ascending order
                        return -(Fitness.dominance(i1, i2, bis));
                    }
                });

            } else { // this.mode == SortingMode.CROWDING
                reset(list);

                sortByDominance(bis);
                int r = assignRanks(bis);

                for (int k = 0; k < r; ++k) {
                    sortByDistance(segments[k], segments[k + 1], bis.length);
                }

                list.clear();
                for (CrowdingRef cr : refs) {
                    list.add(cr.ref);
                }
            }

            this.setIndividualsRank(list, bis);
            return list;
        }

        /**
         * Calculates the rank for each individual
         * @param list      list of individuals
         * @param bis       objective flag array
         */
        private void setIndividualsRank(List<Individual> list, final boolean[] bis) {
            this.listForRank.clear();
            this.listForRank.addAll(list);

            //Sort by dominance
            Collections.sort(this.listForRank, new Comparator<Individual>() {

                public int compare(Individual i1, Individual i2) {
                    //minus servs to obtain the ascending order
                    return -(Fitness.dominance(i1, i2, bis));
                }
            });

            int rank = 0;

            if (!this.listForRank.isEmpty()) {
                this.listForRank.get(0).setRank(rank);
            }

            for (int i = 1; i < this.listForRank.size(); i++) {
                Individual previousIndividual = this.listForRank.get(i - 1);
                Individual currentIndividual = this.listForRank.get(i);
                
                if (Fitness.dominates(previousIndividual, currentIndividual, bis)) {
                    rank++;
                }
                currentIndividual.setRank(rank);
            }
        }

        /**
         * Resets data structures
         * @param pop 
         */
        private void reset(List<Individual> pop) {

            int sz = pop.size();

            if (refs == null || refs.length != sz) {
                refs = new CrowdingRef[sz];
                for (int i = 0; i < refs.length; ++i) {
                    refs[i] = new CrowdingRef();
                }
            }

            if (ordering == null || ordering.length != sz) {
                ordering = new int[sz];
            }

            if (segments == null || segments.length != sz + 1) {
                segments = new int[sz + 1];
            }

            int i = 0;
            for (Individual ind : pop) {
                refs[i].ref = ind;
                refs[i].front = 0;
                refs[i].distance = 0;
                i++;
            }

        }

        /**
         * Comparator based on dominance, used to sort the refs.
         */
        private class DominanceComparator implements Comparator<CrowdingRef> {

            boolean[] biggerIsBetter;

            public int compare(CrowdingRef r1, CrowdingRef r2) {
                return -Fitness.dominance(r1.ref, r2.ref, this.biggerIsBetter);
            }
        }
        private DominanceComparator domcomp = new DominanceComparator();

        /**
         * Sorts refs by dominance according to the given flag array.
         * 
         * @param bis   objective flag array 
         */
        private void sortByDominance(boolean[] bis) {
            domcomp.biggerIsBetter = bis;    
            Arrays.sort(refs, domcomp);
        }

        /**
         * Assigns ranks (fronts) to refs
         * 
         * @return the number of fronts. 
         */
        private int assignRanks(boolean[] bis) {

            int rank = 1;
            refs[0].front = rank;

            segments[rank - 1] = 0;
            for (int i = 1; i < refs.length; ++i) {

                CrowdingRef rh = refs[i - 1];
                CrowdingRef ri = refs[i];

                Individual<?> ph = rh.ref;
                Individual<?> pi = ri.ref;

                if (dominates(ph, pi, bis)) {
                    rank++;
                    segments[rank - 1] = i;
                }

                ri.front = rank;
            }
            segments[rank] = refs.length;
            return rank;
        }

        /**
         * Sorts refs belonging to the same front (between from and to) by crowding distance
         * 
         * @param from  lower index
         * @param to    upper index
         */
        private void sortByDistance(int from, int to, int m) {

            for (int h = 0; h < m; ++h) {
                updateDistance(h, from, to);
            }

            for (int i = to; i > from; --i) {
                for (int j = from; j < i - 1; ++j) {
                    CrowdingRef rj = refs[j];
                    CrowdingRef rk = refs[j + 1];

                    if (rj.distance < rk.distance) {
                        refs[j] = rk;
                        refs[j + 1] = rj;
                    }
                }
            }
        }

        /**
         * Crowding distance is updated with respect to a given objective
         * 
         * @param h     objective
         * @param from  lower index
         * @param to    upper index
         */
        private void updateDistance(int h, int from, int to) {

            for (int i = from; i < to; ++i) {
                ordering[i] = i;
            }

            //bouble sort...
            for (int i = to; i > from; --i) {
                for (int j = from; j < i - 1; ++j) {

                    Individual<?> indj = refs[ordering[j]].ref;
                    Individual<?> indk = refs[ordering[j + 1]].ref;

                    double fj = indj.getScore(h);
                    double fk = indk.getScore(h);

                    if (fj < fk) {
                        int sw = ordering[j + 1];
                        ordering[j + 1] = ordering[j];
                        ordering[j] = sw;
                    }
                }
            }

            //retrieve max and min
            double fmin = refs[ordering[to - 1]].ref.getScore(h);
            double fmax = refs[ordering[from]].ref.getScore(h);

            /*
             * We look for an individual (if any) that is already the lower-bound for the given goal,
             * in order to avoid duplication of bounds with gaining infinite distance
             */
            if (h > 0 && !Double.isInfinite(refs[ordering[to - 1]].distance)) {
                for (int i = to - 2; i >= 0 && refs[ ordering[i]].ref.getScore(h) == fmin; --i) {
                    if (Double.isInfinite(refs[ ordering[i]].distance)) {
                        int sw = ordering[i];
                        ordering[i] = ordering[to - 1];
                        ordering[to - 1] = sw;
                    }
                }
            }

            /*
             * We look for an individual (if any) that is already the upper-bound for the given goal
             */
            if (h > 0 && !Double.isInfinite(refs[ordering[from]].distance)) {
                for (int i = from + 1; i < ordering.length && refs[ ordering[i]].ref.getScore(h) == fmax; ++i) {
                    if (Double.isInfinite(refs[ ordering[i]].distance)) {
                        int sw = ordering[i];
                        ordering[i] = ordering[from];
                        ordering[from] = sw;
                    }
                }
            }

            refs[ordering[to - 1]].distance = Double.POSITIVE_INFINITY;
            refs[ordering[from]].distance = Double.POSITIVE_INFINITY;

            for (int i = from + 1; i < to - 1; ++i) {
                refs[ordering[i]].distance += fmax != fmin ? (refs[ordering[i - 1]].ref.getScore(h) - refs[ordering[i + 1]].ref.getScore(h)) / (fmax - fmin) : Double.POSITIVE_INFINITY;
            }
        }
    }
}
