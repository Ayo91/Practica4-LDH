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
import java.util.Collection;
import jenes.chromosome.Chromosome;
import jenes.statistics.StatisticsLogger.LoggableStatistics;
import jenes.utils.Random;

/**
 * 
 * This class provides a pool of individuals to which Population can access to 
 * retrieve individuals. 
 * 
 * The pool is highly efficient when the algorithm the overall number of individuals 
 * and the lenght of their chromosome do not change along the algorithm evolution.
 *
 * @version 2.0
 * @since 2.0
 */
public class Pool<T extends Chromosome> {

    /** The default population size */
    public static final int DEFAULT_SIZE = 100;
    /** The vector containing individuals assigned to populations */
    private ArrayList<Individual<T>> assigned;
    /** The vector containing individuals available to be assigned */
    private ArrayList<Individual<T>> available;

    /**
     * Constructs a new empty pool
     *
     */
    public Pool() {
        this.assigned = new ArrayList<Individual<T>>();
        this.available = new ArrayList<Individual<T>>();
    }

    /**
     * Constructs a new pool with a given number of (initially no genome) individuals
     * <p>
     * @param n - the initial number of individuals
     */
    public Pool(int n) {
        this();
        for (int i = 0; i < n; ++i) {
            Individual<T> ind = new Individual<T>();
            available.add(ind);
        }
    }

    /**
     * Constructs a new pool with a given number of individuals, clones of sample.
     * <p>
     * @param n - the initial number of individuals
     * @param sample - the prototype individual
     */
    public Pool(int n, Individual<T> sample) {
        this();
        for (int i = 0; i < n; ++i) {
            Individual<T> clone = sample.clone();
            clone.setRank(Individual.UNRANKED);
            available.add(clone);
        }
    }

    /**
     * Returns an {@link Individual} from the set of available individuals. 
     * If individuals are not available, a new individual is created
     * 
     * @return an available individual 
     */
    public final Individual<T> getIndividual() {
        int last = this.available.size() - 1;
        return last == -1 ? new Individual<T>() : this.available.remove(last);
    }

    /**
     * Adds the individual given as argument to the list of assigned individuals. 
     * If individual population makes use of a different pool, the individual is discarded.
     * <p>
     * @param ind to be assigned
     */
    public final void assign(Individual<T> ind) {
        if (ind.getPopulation().getPool() == this) {
            ind.setRank(Individual.UNRANKED);
            this.assigned.add(ind);
        }
    }

    /**
     * Removes the {@link Individual}, given as argument, from the list of assigned 
     * individuals and makes it available
     * 
     * @param ind - individual to be released
     */
    public final void release(Individual<T> ind) {
        if (this.assigned.remove(ind)) {
            this.available.add(ind);
        }
    }

    /**
     * Resizes the pool.
     *
     * If the new size is larger, new individuals will be added cloning
     * random individuals, thus requiring that at least one individual is held;
     * on contrary individuals are randomly generated.
     * <p>
     * If the new size is smaller than the old size, individuals are randomly removed from the pool.
     * <p>
     * @param size the pool new size
     */
    public final void setAvailability(int size) {

        final int av = available.size();
        final int sz = assigned.size();
        final Random rand = Random.getInstance();

        if (av < size) {
            if (sz > 0) {
                for (int i = av; i < size; ++i) {
                    int k = rand.nextInt(sz);
                    available.add(this.available.get(k).clone());
                }
            } else {
                for (int i = av; i < size; ++i) {
                    available.add(new Individual());
                }
            }
        } else {
            for (int s = av; s > size; --s) {
                int k = rand.nextInt(s);
                available.remove(k);
            }
        }

    }
    /** The Pool statistics used by resize() */
    private Statistics stats = new Statistics(this);

    /**
     * Performs an automic pool resizing.
     */
    public final void resize() {
        stats.update();
        int d = (int) Math.ceil(2 * stats.getSizeDev());
        if (this.availability() > d) {
            this.setAvailability(d);
        }
    }

    /**
     * Removes all the individuals from this population.
     */
    public final void clear() {
        this.available.clear();
    }

    /**
     * Returns the pool size.
     * <p>
     * @return the number of individuals in the pool.
     */
    public final int size() {
        return this.available.size() + this.assigned.size();
    }

    /**
     * Returns the number of available individuals
     * 
     * @return the number of available individuals
     */
    public final int availability() {
        return this.available.size();
    }

    /**
     * Removes the collection of individuals given as argument from the list of
     * available individuals and from the list of assigned individuals
     * @param individuals
     */
    public final void remove(Collection<Individual<T>> individuals) {
        this.available.removeAll(individuals);
        this.assigned.removeAll(individuals);
    }

    /**
     * Adds a collection of individuals to the pool. If the individual belongs to a population with a different pool, the individual is discarded.
     * <p>
     * @param individuals   to add
     */
    public final void add(Collection<Individual<T>> individuals) {
        for (Individual<T> i : individuals) {
            i.setRank(Individual.UNRANKED);
            if (i.getPopulation() != null) {
                if (i.getPopulation().getPool() == this) {
                    this.assigned.add(i);
                } // else it is discarded
            } else {
                this.available.add(i);
            }
        }
    }

    /**
     * Returns a new instance of pool (@link Statistics).
     * <p>
     * @return pool statistics
     */
    public final Statistics getStatistics() {
        return new Statistics(this);
    }

    /**
     * Updates pool statistics
     * <p>
     * @param stats  the stastistics to update
     */
    public final void updateStatistics(Statistics stats) {
        if (stats.subject == this) {
            stats.update();
        }
    }

    /**
     * Returns a string complete pool representation.
     * <p>
     * @return a new String complete string representing this pool.
     */
    public final String toCompleteString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            sb.append("Position=");
            sb.append(i);
            sb.append(" individual=");
            sb.append(available.get(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a string representation of this pool.
     * <p>
     * @return a new <code>String</code> object representing this pool.
     */
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer(", Number Individuals: ");
        sb.append(available.size());
        return sb.toString();
    }

    /**
     *  
     * This class provides some basic statistics regarding the <code> Pool </code>.
     * 
     */
    public static final class Statistics extends LoggableStatistics {

        /** The default lookback memory */
        public static final int DEFAULT_MEMORY = 20;
        /** The pool statitistics are referred to */
        private Pool subject;
        /** The lookback memory of series */
        private int memory;
        /** The number of data points collected */
        private int horizon;
        /** The time series */
        private long[] time;
        /** The series of number of individuals collected by the pool */
        private int[] size;
        /** The series of number of avilable individuals */
        private int[] availability;
        /** Arithmetic mean of size */
        private double sizeAvg;
        /** Arithmetic mean of availability **/
        private double availabilityAvg;
        /** Standard deviation of size */
        private double sizeDev;
        /** Standard deviation of availability */
        private double availabilityDev;

        /**
         * Creates <code>Statistics<code> with default lookback memory 
         */
        private Statistics(Pool pool) {
            this(pool, DEFAULT_MEMORY);
        }

        /**
         * Creates <code>Statististics</code< with given lookback memory
         * <p>
         * @param pool      the subject
         * @param memory    lookback memory 
         */
        private Statistics(Pool pool, int memory) {
            this.memory = memory;
            this.horizon = 0;
            this.size = new int[memory];
            this.availability = new int[memory];
            this.time = new long[memory];
            this.sizeAvg = Double.NaN;
            this.sizeDev = Double.NaN;
            this.availabilityAvg = Double.NaN;
            this.availabilityDev = Double.NaN;
        }

        /**
         * Creates <code>Statistics</code> as copy of given one. Used by clone.
         * <p>
         * @param stats     statistics to copy
         */
        private Statistics(Statistics stats) {
            this(stats.subject, stats.memory);
            this.horizon = stats.horizon;
            System.arraycopy(stats.size, 0, this.size, 0, stats.size.length);
            System.arraycopy(stats.availability, 0, this.availability, 0, stats.availability.length);
            System.arraycopy(stats.time, 0, this.time, 0, stats.time.length);
            this.sizeAvg = stats.sizeAvg;
            this.availabilityAvg = stats.availabilityAvg;
            this.sizeDev = stats.sizeDev;
            this.availabilityDev = stats.availabilityDev;
        }

        /**
         * Returns the pool statistics are referred to
         * <p>
         * @return pool 
         */
        public Pool getSubject() {
            return subject;
        }

        /**
         * Clones the statistics
         * <p>
         * @return a clone
         */
        public Statistics clone() {
            return new Statistics(this);
        }

        /**
         * Updates the statistics with the given pool
         * <p>
         * @param pool 
         */
        public final void update() {
            if (horizon < memory) {
                horizon += 1;
            }

            shift(time, System.currentTimeMillis());
            shift(size, subject.size());
            shift(availability, subject.availability());

            recompute();
        }

        /**
         * Recomoputes the key figures
         */
        private void recompute() {
            double[] sz = update(size);
            sizeAvg = sz[0];
            sizeDev = sz[1];

            double[] av = update(availability);
            availabilityAvg = av[0];
            availabilityDev = av[1];
        }

        /**
         * Performs the shit of time series and inserts the new data
         * @param data
         * @param d 
         */
        private void shift(int[] data, int d) {

            for (int i = horizon - 1; i > 0; --i) {
                data[i] = data[i - 1];
            }
            data[0] = d;
        }

        /**
         * Performs the shit of time series and inserts the new data
         * @param data
         * @param d 
         */
        private void shift(long[] data, long d) {

            for (int i = horizon - 1; i > 0; --i) {
                data[i] = data[i - 1];
            }
            data[0] = d;
        }
        /** Internal variable used to return key figures */
        private double[] out = new double[2];

        /**
         * Computes data arithmetice mean and standard deviation
         * @param data
         * @return 
         */
        private double[] update(int[] data) {

            double avg = 0;
            for (int i = 0; i < horizon; ++i) {
                avg += data[i];
            }
            avg /= horizon;

            double sd = 0;
            for (int i = 0; i < horizon; ++i) {
                double z = data[i] - avg;
                sd += z * z;
            }
            sd = Math.sqrt(sd / horizon);

            out[0] = avg;
            out[1] = sd;

            return out;
        }

        /**
         * Returns the lookback memory
         * <p>
         * @return the look back memory
         */
        public int getMemory() {
            return memory;
        }

        /**
         * Sets the new lookback memory
         * <p>
         * @param memory    new lookback memory
         */
        public void setMemory(int memory) {
            int[] sz = new int[memory];
            System.arraycopy(this.size, 0, sz, 0, memory);
            this.size = sz;

            int[] av = new int[memory];
            System.arraycopy(this.availability, 0, sz, 0, memory);
            this.availability = av;

            if (horizon > memory) {
                horizon = memory;
                this.recompute();
            }
        }

        /**
         * Returns the number of data points collected
         * <p>
         * @return the number of data points 
         */
        public int getHorizon() {
            return horizon;
        }

        /**
         * Returns the time series at which statistics have been collected
         * 
         * @return  time series 
         */
        public long[] getTime() {
            return this.time;
        }

        /**
         * Returns the series of number of individuals held by the pool
         * <p>
         * @return  the number of individuals over time
         */
        public int[] getSize() {
            return this.size;
        }

        /**
         * Returns the arithmetic mean of size series
         * <p>
         * @return  size mean
         */
        public double getSizeAvg() {
            return this.sizeAvg;
        }

        /**
         * Returns the standard deviation of size series
         * <p>
         * @return size standard deviation
         */
        public double getSizeDev() {
            return this.sizeDev;
        }

        /**
         * Returns the series of number of available individuals
         * <p>
         * @return  avialable individuals over time
         */
        public int[] getAvailability() {
            return this.availability;
        }

        /**
         * Returns the arithmetic mean of availability series
         * <p>
         * @return availability mean 
         */
        public double getAvailabilityAvg() {
            return this.availabilityAvg;
        }

        /**
         * Returns the standard deviation of availability series
         * <p>
         * @return availability standard deviation
         */
        public double getAvailabilityDev() {
            return this.availabilityDev;
        }
    }
}
