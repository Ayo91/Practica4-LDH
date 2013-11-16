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
package jenes;

import jenes.population.Fitness;
import jenes.utils.Random;
import java.util.ArrayList;
import java.util.List;

import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Pool;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.Sequence;
import jenes.stage.StageException;
import jenes.statistics.StatisticsLogger.LoggableStatistics;
import jenes.utils.multitasking.Runner;

/**
 * This is the main class of JENES, providing the skeleton for implementing
 * genetic algorithms.
 * <p>
 * A genetic algorithm can be implemented by providing a <code>Fitness</code> function. 
 * An alternative way (but deprecated) is to subclass
 * <code>GeneticAlgorithm</code> and to overwrite the method
 * {@link #evaluateIndividual(Individual)}. 
 * <p>
 * The genetic algorithm body is made
 * of a sequence of stages. Which stages and in which order to consider is left
 * to the algorithm needs. Generally, the sequence is made of a selection
 * operator, followed by a a crossover operator, and then by a mutation
 * operator. This schema is implemented by {@link jenes.algorithms.SimpleGA}.
 * <p>
 * An example of code is provided below. First, the initial population has to be
 * created.
 * <p>
 * <blockquote>
 *
 * <pre>
 * BooleanChromosome chrom = new BooleanChromosome(CHROMOSOME_LENGTH);
 * Individual&lt;BooleanChromosome&gt; ind = new Individual&lt;BooleanChromosome&gt;(chrom);
 * Population&lt;BooleanChromosome&gt; pop = new Population&lt;BooleanChromosome&gt;(ind,
 * 		POPULATION_SIZE);
 * </pre>
 *
 * </blockquote>
 * <p>
 * A fitness function is implmented.
 * 
 * <blockquote>
 * <pre>
 * Fitness<BooleanChromosome> fit = new Fitness<BooleanChromosome>(true) {
 *
 *     @Override
 *     public void evaluate(Individual<BooleanChromosome> individual) {
 *         BooleanChromosome chrom = individual.getChromosome();
 *         int count = 0;
 *         int length=chrom.length();
 *         for(int i=0;i<length;i++)
 *             if(chrom.getValue(i))
 *                 count++;
 *               
 *             individual.setScore(count);
 *          }           
 *     };
 * </pre>
 * </blockquote>
 *
 * <p>
 * Then, the genetic algorithm is instanced.
 * <p>
 * <blockquote>
 *
 * <pre>
 * GeneticAlgorithm&lt;BooleanChromosome&gt; ga = new GeneticAlgorithm&lt;BooleanChromosome&gt;(fit, pop, GENERATION_LIMIT);
 * </pre>
 *
 * </blockquote>
 * <p>
 * In this example, we used an anonymous subclass, but other subclassing methods
 * can be used. After, stages (operators in particular) are added to the
 * algorithm's body.
 * <p>
 * <blockquote>
 *
 * <pre>
 * AbstractStage&lt;BooleanChromosome&gt; selection = new TournamentSelector&lt;BooleanChromosome&gt;(3);
 * AbstractStage&lt;BooleanChromosome&gt; crossover = new OnePointCrossover&lt;BooleanChromosome&gt;(0.8);
 * AbstractStage&lt;BooleanChromosome&gt; mutation = new SimpleMutator&lt;BooleanChromosome&gt;(0.2);
 *
 * ga.addStage(selection);
 * ga.addStage(crossover);
 * ga.addStage(mutation);
 * </pre>
 *
 * </blockquote>
 * <p>
 * Finally, the algorithm is executed.
 * <p>
 * <blockquote>
 *
 * <pre>
 * ga.evolve();
 * </pre>
 *
 * </blockquote>
 * <p>
 * A genetic algorithm processes a {@link Population} of {@link Individual}s.
 * At each generation there are an input and output population. The reference to
 * these populations can be respectively obtained by the methods
 * {@link #getCurrentPopulation()} and {@link #getNextPopulation()}. Past
 * populations are buffered in the algorithm's history. An history population
 * can be retrieve by the method {@link #getHistoryAt(int)}. The reference to
 * an history population is valid for the history length. After, populations are
 * collected for reuse, so references to them are not anymore valid.
 * <p>
 * The genetic algorithm execution is invoked by the method {@link #evolve()}.
 * The algorithm execution passes through the following events:
 * <ul>
 * <li>Start: the algorithm is just created.
 * <li>Init: internal structures, such as the population given as input at
 * generation 0 and history, are initialized.
 * <li>Generation: a generation has been just performed.
 * <li>Stop: the algorithm terminates its executions.
 * </ul>
 * Each of these events can be captured by {@link AlgorithmEventListener}s and
 * {@link GenerationEventListener}s. They can also be caputured by the
 * <code>GeneticAlgorithm</code> subclass, by overriding the methods
 * {@link #onStart}, {@link #onInit}, {@link #onGeneration}, and
 * {@link #onStop}. Capturing events is useful to collect statistics and to
 * perform analyses. Evolution terminates when the maximum number of generations
 * is reached. It is possible to terminate the execution on the basis of some
 * condition (e.g. precision level, variance of population, ecc.) by overriding
 * the method {@link #end()}.
 * <p>
 * <code>GeneticAlgorithm</code> include a support for elitism, that is best
 * individuals at each generation are assured to be at the next generation. The
 * number of elite individuals is set by the method {@link #setElitism(int)}.
 * These individuals are substituted to some individuals to the processed
 * population according to the following strategies:
 * <ul>
 * <li> {@link ElitismStrategy#RANDOM}: next population individuals are
 * randomly selected and substituted by elite.
 * <li> {@link ElitismStrategy#WORST}: next population worst individuals are
 * substituted by elite.
 * </ul>
 * The first strategy is more efficient as it does not require to order the
 * population. The drawback is that individuals with a good fitness could be
 * substituted by elite. The second strategy is slower, but assures that only
 * worst individuals are substituted.
 * <p>
 * The genetic algorithm can be used for both maximizing or minimizing the
 * fitness function. This can be accomplished by setting the fitness.
 * <p>
 * <code>GeneticAlgorithm</code> can process populations with a variable
 * number of individuals at each generation.
 * <p>
 * <code>GeneticAlgorithm</code> performs by default an initial randomization
 * of the population.
 * <p>
 *
 * @param <T> The class of chromosomes to work with.
 *
 * @version 2.0
 * @since 1.0
 */
public class GeneticAlgorithm<T extends Chromosome> {

    /** The default maximum number of generations */
    public static final int DEFAULT_GENERATION_LIMIT = 100;
    /** The default history size */
    public static final int DEFAULT_HISTORY_SIZE = 2;
    /** The minimum history size */
    public static final int MIN_HISTORY_SIZE = 2;
    /** The maximum history size */
    public static final int MAX_HISTORY_SIZE = 100;
    /** The main algorithm sequence */
    protected Sequence<T> body;
    /**
     * The statistics object responsible for storing statistics about this
     * genetic algorithm.
     */
    protected Statistics statistics = null;
    /** Current generation count */
    protected int generation = 0;
    /** Rate of randomization of initial population */
    protected double randomization = 1.0;
    /** Maximum number of generations */
    protected int generationLimit;
    /** 
     * Jenes could now support performance optimization in executing fitness evaluation 
     * by delegating this task to the performance-optimizer support
     * @since 2.0
     */
    private Runner runner;

    /** The elitism strategy enumeration */
    public static enum ElitismStrategy {

        /** A number of individuals is randomly chosen, and substituted */
        RANDOM,
        /** Worst individuals are substituted */
        WORST
    };
    /** The elitism number */
    protected int elitism = 0;
    /** The elitism strategy used by this genetic algorithm */
    protected ElitismStrategy elitismStrategy = ElitismStrategy.WORST;

    /*
     * These fields are kept private as they are critical for the algorithm
     * evolution
     */
    private int historySize;
    private Population<T> history[];
    /** The initial population */
    protected Population<T> initialPopulation = null;

    /*
     * These fields are kept private as they are critical for the algorithm
     * evolution
     */
    private Population<T> currentPopulation = null;
    private Population<T> nextPopulation = null;
    /** The individuals pool used by populations */
    private Pool<T> pool = null;
    /** The genetic algorithm listeners */
    protected List<AlgorithmEventListener<T>> algorithmListeners;
    /** The generation listeners */
    protected List<GenerationEventListener<T>> generationListeners;

    /** The resize stategy enumeration. It is used in @link jenes.stage.Sequence */
    public static enum ResizeStrategy {

        /** No resize is perfomed. */
        NONE,
        /** If there is a need to expand, additional individuals are added, else they are removed. */
        AUTO,
        /** Population is made empty, and then individuals are added as needed. */
        EMPTY
    };
    /** The resize strategy used by this genetic algorithm */
    protected ResizeStrategy resizeStrategy = ResizeStrategy.AUTO;
    /** The flag controlling the evalution. If true, individuals are evaluated in any case. */
    protected boolean fullEvaluationForced = false;
    /** The random instance */
    protected Random random = Random.getInstance();

    /**
     * Constructs a new genetic algorithm with no initial population and the
     * default generation limit.
     *
     */
    public GeneticAlgorithm() {
        this(null, null, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Constructs a new genetic algorithm with the specified population and the
     * default generation limit.
     * <p>
     *
     * @param pop
     *            the sample population
     */
    public GeneticAlgorithm(final Population<T> pop) {
        this(null, pop, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Constructs a new genetic algorithm with the specified population and the
     * specified generation limit.
     * <p>
     *
     * @param pop
     *            the sample population
     * @param genlimit
     *            the generations upper bound
     */
    public GeneticAlgorithm(final Population<T> pop, final int genlimit) {
        this(null, pop, genlimit);
    }

    /**
     * Constructs a new genetic algorithm with no initial population and the
     * default generation limit.
     * 
     */
    public GeneticAlgorithm(final Fitness fitness) {
        this(fitness, null, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Constructs a new genetic algorithm with the specified population and the
     * default generation limit.
     * <p>
     *
     * @param pop
     *            the sample population
     */
    public GeneticAlgorithm(final Fitness fitness, final Population<T> pop) {
        this(fitness, pop, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Constructs a new genetic algorithm with the specified population and the
     * specified generation limit.
     * <p>
     *
     * @param pop
     *            the sample population
     * @param genlimit
     *            the generations upper bound
     */
    public GeneticAlgorithm(final Fitness fitness, final Population<T> pop, final int genlimit) {

        this.initialPopulation = pop;
        if (pop != null) {
            pool = pop.getPool();
        }

        this.generationLimit = genlimit;

        this.body = new Sequence<T>();
        this.setFitness(fitness);

        // historySize provides the number of elements used by history
        // history size is set to maximum in order to avoid the array
        // re-allocation in memory
        this.historySize = DEFAULT_HISTORY_SIZE;
        this.history = new Population[MAX_HISTORY_SIZE];

        for (int i = 0; i < this.historySize; ++i) {
            this.history[i] = new Population<T>();
            this.history[i].setPool(pool);
        }

        this.algorithmListeners = new ArrayList<AlgorithmEventListener<T>>();
        this.generationListeners = new ArrayList<GenerationEventListener<T>>();

        //XXX fixes some issue in TimSorter for ArrayList in jdk 7.0
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    /**
     * Returns the {@link Fitness} from this genetic algorithm's body
     * <p>
     *
     * @return fitness
     */
    public final Fitness getFitness() {
        return this.body.getFitness();
    }

    /**
     * Sets the {@link Fitness}
     *
     * @param fitness   new fitness
     */
    public final void setFitness(Fitness fitness) {
        this.body.setFitness(fitness);
    }

    /**
     * Says if the fitness is changed or not. 
     * @return  <code>true</code>, if fitness is changed
     */
    public final boolean isFitnessChanged() {
        return this.body.isFitnessChanged();
    }

    /**
     * Get current runner implementation
     * @return 
     */
    public Runner getRunner() {
        return runner;
    }

    /**
     * Set the {@link Runner}
     * 
     * @param runner
     */
    public void setRunner(Runner runner) {
        this.runner = runner;
        this.runner.setAlgorithm(this);
    }

    /**
     * Impose {@link Random} seed in order to reproduce an execution producing 
     * the same enviroinment
     * 
     * @param seed the seed to set
     */
    public void setRandomSeed(long seed) {
        this.random.setSeed(seed); // XXX check for production mode: in a same JDK env. different parallels execution may be affected by this requirement
    }

    /**
     * Returns the history size that is the number of populations kept by
     * history
     * <p>
     *
     * @return the history length
     */
    public final int getHistorySize() {
        return this.historySize;
    }

    /**
     * Returns the individuals pool used by populations
     *
     * @return pool
     */
    public final Pool<T> getPool() {
        return this.pool;
    }

    /**
     * Sets the history size, that is the number of populations kept by history.
     *
     * @param hs the new number of history populations
     */
    public void setHistorySize(int hs) {
        if (hs < MIN_HISTORY_SIZE) {
            hs = MIN_HISTORY_SIZE;
        } else if (hs > MAX_HISTORY_SIZE) {
            hs = MAX_HISTORY_SIZE;
        }

        // If there is a need, new populations are created
        for (int i = historySize; i < hs; ++i) {
            history[i] = new Population<T>();
        }

        // Exceeding populations are discarded
        for (int i = hs; i < historySize; ++i) {
            history[i] = null;
        }

        this.historySize = hs;
    }

    /**
     * Returns the history population at the specified generation. The
     * <code>pos</code> value can be relative or absolute. If relative, it
     * must be a negative number that specifies how many generations to go back
     * in order to get the population. So that, 0 means the current generation,
     * -1 means one generation back, -2 means two generations back, and so on.
     * If positive, the generation index is absolute. If the population at the
     * specified generation is not available, the method returns null.
     * <p>
     *
     * @param pos the population position
     * @return the history population, if available. Otherwise it returns null.
     */
    public final Population<T> getHistoryAt(int pos) {
        pos = pos < 0 ? -pos : this.generation - pos;
        return pos >= 0 && pos < this.historySize ? history[pos] : null;
    }

    /**
     * Returns the initial population. This is the population given as input to
     * the genetic algorithm, thus it is not affected by the initial population
     * randomization.
     * <p>
     *
     * @return the initial population
     */
    public final Population<T> getInitialPopulation() {
        return this.initialPopulation;
    }

    /**
     * Returns the current population. This is the population given as input to
     * the algorithm's body at the current generation. When the algorithm
     * starts, it is a copy of the initial population, and it is eventually
     * affected by the randomization process.
     * <p>
     *
     * @return the current population.
     */
    public final Population<T> getCurrentPopulation() {
        return this.currentPopulation;
    }

    /**
     * Returns the genetic algorithm next population. This is the population
     * being processed by the algorithm's body at the current generation. It is
     * the population that will be given as input to the algorithm's body
     * sequence at the following generation.
     * <p>
     *
     * @return the next population
     */
    public final Population<T> getNextPopulation() {
        return this.nextPopulation;
    }

    /**
     * Evolves the algorithms by restarting the algorithm from the initial population.
     *
     * @throws jenes.AlgorithmException
     */
    public final void evolve() throws AlgorithmException {
        evolve(true);
    }

    /**
     * Evolves the algorithms by resetting the initial population and restarting the algorithm.
     *
     * @throws jenes.AlgorithmException
     */
    public final void evolve(final Population<T> pop) {
        this.initialPopulation = pop;
        evolve(true);
    }

    /**
     * Evolves the algorithm until the termination condition or the generation
     * limit is reached. Depending on the argument, the algorithm restarts or
     * continues from the last population.
     *
     * @param restart if true, the initial population is reset;
     * if false, the algorithm continues from the last population.
     *
     * @throws jenes.AlgorithmException
     */
    @SuppressWarnings("unchecked")
    public final void evolve(boolean restart) throws AlgorithmException {

        this.body.init(this);

        this.start(restart);

        final int limit = this.generationLimit;
        for (generation = 0; generation < limit && !end(); generation++) {

            this.statistics.generations = generation + 1;
            try {
                currentPopulation = history[0];
                nextPopulation = history[this.historySize - 1];
                nextPopulation.setAs(currentPopulation);

                // Because Sequence uses a temporary population, it can be used
                // nextPopulation
                // for both input and output parameters, in this way
                // memory is saved by avoiding the allocation of an additional
                // population.
                // (see Sequence.process)
                this.body.process(nextPopulation, nextPopulation);

                this.evaluatePopulation(nextPopulation, fullEvaluationForced);

                for (int i = this.historySize - 1; i > 0; i--) {
                    history[i] = history[i - 1];
                }
                history[0] = nextPopulation;
            } catch (StageException e) {
                this.statistics.exceptionTerminated = true;
                throw new AlgorithmException(
                        "An error occured during the ga evolution", e);
            }
            if (elitism > 0) {
                this.applyElitism();
            }
            final long now = System.currentTimeMillis();
            this.statistics.generationEndTimes[generation] = now;
            this.onGeneration(now);
            for (GenerationEventListener gel : generationListeners) {
                gel.onGeneration(this, now);
            }

            Pool<T> p = history[0].getPool();
            if (p != null) {
                p.resize();
            }
        }
        this.stop();
    }

    /**
     * Provides the algorithm termination condition. By default it returns false
     * as reaching the generation limit is the sole ending criterion Subclasses
     * can override this method in order to provide a problem specific
     * termination condition.
     * <p>
     *
     * @return true if the ga evolution reached the termination condition, false
     *         otherwise
     */
    protected boolean end() {
        // override it for specific termination conditions
        return false;
    }

    /**
     * Starts this genetic algorithm. This method performs the algorithm
     * initialization and notifies the start and init events. It is
     * automatically invoked by the method {@link #evolve()}, thus it should
     * not be explicity invoked.
     *
     * @param reset if true, the algorithm reset the initial population.
     */
    protected final void start(boolean reset) {

        if (this.initialPopulation == null) {
            throw new AlgorithmException("[Jenes]: It is not possible to start an algorithm with no initial population.");
        }

        if (this.runner != null) {
            this.runner.start(reset);
        }

        this.statistics = new Statistics(this.generationLimit);

        this.generation = 0;

        long now = System.currentTimeMillis();
        this.statistics.startTime = now;


        this.onStart(now);
        for (AlgorithmEventListener<T> ael : algorithmListeners) {
            ael.onAlgorithmStart(this, now);
        }

        if (reset || this.currentPopulation == null) {

            for (int i = 0; i < this.historySize; ++i) {
                this.history[i].setPool(this.initialPopulation.getPool());
            }

            this.currentPopulation = history[0];
            this.currentPopulation.setAs(this.initialPopulation);
            if (this.randomization > 0) {
                this.randomizePopulation(this.currentPopulation);
            }
        }
        this.nextPopulation = null;

        this.evaluatePopulation(this.currentPopulation);

        final int body_length = this.body.getSize();
        for (int i = 0; i < body_length; ++i) {
            this.body.getStageAt(i).init(this);
        }

        now = System.currentTimeMillis();
        this.statistics.initTime = now;

        if (this.runner != null) {
            this.runner.onInit();
        }

        this.statistics.randomSeed = this.random.getSeed();

        this.onInit(now);
        for (AlgorithmEventListener<T> ael : algorithmListeners) {
            ael.onAlgorithmInit(this, now);
        }
    }

    /**
     * Terminates the genetic algorithm, notifying the stop event to listeners.
     * It is automatically invoked by the method {@link #evolve()}, thus it
     * should not be explicity invoked.
     */
    protected final void stop() {

        this.body.dispose();

        long now = System.currentTimeMillis();
        this.statistics.stopTime = now;
        this.statistics.executionTime = now - this.statistics.startTime;

        this.onStop(now);
        for (AlgorithmEventListener<T> ael : algorithmListeners) {
            ael.onAlgorithmStop(this, now);
        }

        if (this.runner != null) {
            this.runner.stop();
        }
    }

    /**
     * Invoked when a start ga event occurs. By default, no action is performed.
     * Override this method to make the <code>GeneticAlgorithm</code> subclass
     * able to be notfied of the start event.
     *
     * @param time
     *            the start event time expressed in milliseconds
     */
    protected void onStart(long time) {
        // do nothing; override it for a specific behavior
    }

    /**
     * Invoked when an init end ga event occurs. By default, no action is
     * performed. Override this method to make the <code>GeneticAlgorithm</code>
     * subclass able to be notfied of the init event.
     *
     * @param time
     *            the init event time expressed in milliseconds
     */
    protected void onInit(long time) {
        // do nothing; override it for a specific behavior
    }

    /**
     * Invoked when a stop event occurs. By default, no action is performed.
     * Override this method to make the <code>GeneticAlgorithm</code> subclass
     * able to be notfied of the stop event.
     *
     * @param time
     *            the stop event time expressed in milliseconds
     */
    protected void onStop(long time) {
        // do nothing; override it for a specific behavior
    }

    /**
     * Invoked when a generation ga end event occurs. By default, no action is
     * performed. Override this method to make the <code>GeneticAlgorithm</code>
     * subclass able to be notfied of the generation event.
     *
     * @param time
     *            the generation event time expressed in milliseconds
     */
    protected void onGeneration(long time) {
        // do nothing; override it for a specific behavior
    }

    /**
     * Evaluates the population. The method iterates the evaluation on each
     * individual. Evalution is performed according to <code>fullEvaluationForced</code>
     * <p>
     *
     * @param population
     *            the population to be evaluated
     */
    public final void evaluatePopulation(final Population<T> population) {
        this.evaluatePopulation(population, fullEvaluationForced);
    }

    /**
     * Evaluates the population. The method iterates the evaluation on each
     * individual. Evalution is performed according to flag
     * <p>
     *
     * @param population
     *            the population to be evaluated
     * @paran forced
     *            if true, all individuals are evaluated.
     */
    public final void evaluatePopulation(final Population<T> population, final boolean forced) {

        if (this.runner != null) {
            this.runner.onEvaluationBegin(population, forced);
        }

        // notify to the population that
        if (this.getFitness() != null) {
            population.setEvaluatedBy(this.getFitness());
            population.setSortingBy(this.getFitness().getBiggerIsBetter());
        } else {
            //this is done for backward compatibility
            population.setEvaluatedBy(null);
            population.setSortingBy(this.isBiggerBetter());
        }

        long now = System.currentTimeMillis();
        this.statistics.fitnessEvalStageBegin[this.generation] = now;

        for (Individual individual : population) {
            if (!individual.isEvaluated() || forced || this.isFitnessChanged()) {
                if (this.runner != null) {
                    this.runner.evaluateIndividual(individual);
                } else {
                    this.evaluateIndividual(individual);
                }

                this.statistics.fitnessEvaluationNumbers++;
            }
        }

        if (this.runner != null) {
            this.runner.onEvaluationEnd();
        }

        now = System.currentTimeMillis();
        this.statistics.fitnessEvalStageEnd[this.generation] = now;
        this.statistics.timeSpentInFitnessEval += now - statistics.fitnessEvalStageBegin[this.generation];

    }

    /**
     * Evaluates a single individual. This evaluation of individuals is
     * specifically related to the problem to solve. If the genetic algorithm's body
     * has a {@link Fitness}, this method calls {@link Fitness#evaluate(jenes.population.Individual)}
     * method, otherwise method requiring an implementation by the sublass.
     * <p>
     *
     * @param individual
     *            the individual to be evaluated
     */
    public void evaluateIndividual(final Individual<T> individual) {
        if (this.getFitness() != null) {
            this.getFitness().evaluate(individual);
        }
    }

    /**
     * Perform a population randomization, by itering on individuals. This
     * process is generally useful to enrich the population diversity, necessary
     * to the genetic algorithm for exploiting the search space, especially the
     * initial population is created by cloning a sample individual.
     * <p>
     * The percentage of individuals to be randomized can be finely controlled
     * by the randomization rate. This is useful in many problems where a
     * dominant solution should be kept in the population, but still providing a
     * random genetic variety. The randomization rate is controlled by the
     * {@link #setRandomization(double) } method.
     * <p>
     * This method is automatically invoked by the {@link #start(boolean)} method, so
     * no explicit invokation is required.
     *
     * @param pop
     *            the population to randomize
     */
    protected final void randomizePopulation(final Population<T> pop) {
        final long popSize = Math.round(pop.size() * this.randomization);
        for (int i = 0; i < popSize; i++) {
            randomizeIndividual(pop.getIndividual(i));
        }
    }

    /**
     * Performs an individual randomization. It is invoked by
     * {@link #randomizePopulation(Population)}. By default randomization is
     * delegated to the individual. In some problems, it would be useful to
     * control the randomization process of individual. This is especially the
     * case of when there are some constraints on genes in order to make the
     * individual valid.
     * <p>
     *
     * @param individual
     *            the individual to be randomize
     */
    protected void randomizeIndividual(final Individual<T> individual) {
        individual.randomize();
    }

    /**
     * Sets the randomization rate to 0 or 1, according to the flag. By this
     * method is possible to apply the randomization process to the whole
     * population or not at all.
     * <p>
     *
     * @param value
     *            if true the rate is set to 1, otherwise to 0.
     */
    public final void setRandomization(final boolean value) {
        this.randomization = value ? 1.0 : 0.0;
    }

    /**
     * Sets the randomization rate. The randomization rate represents the
     * percentage of individuals that will be randomized. The value should be
     * within [0,1]. However the method automatically trims values outside the
     * unary range, so that negative values are trimmed to 0 and values bigger
     * than 1, are trimmed to 1. Therefore the method is consistent for any
     * value.
     * <p>
     *
     * @param rate
     *            the randomization rate to use during the randomization phase
     */
    public void setRandomization(final double rate) {
        if (rate < 0) {
            this.randomization = 0;
        } else if (rate > 1) {
            this.randomization = 1;
        } else {
            randomization = rate;
        }
    }

    /**
     * Provides the randomization rate, that is the percentage of individuals
     * being randomized by the algorithm.
     * <p>
     *
     * @return the randomization rate.
     */
    public final double getRandomization() {
        return this.randomization;
    }

    /**
     * Returns the current generation counter
     *
     * @return the current generation counter
     */
    public final int getGeneration() {
        return this.generation;
    }

    /**
     * Returns the generation limit
     * <p>
     *
     * @return the maximum number of generations
     */
    public final int getGenerationLimit() {
        return this.generationLimit;
    }

    /**
     * Sets the generation limit
     * <p>
     *
     * @param limit
     *            the new generation limit
     */
    public void setGenerationLimit(final int limit) {
        this.generationLimit = limit;
    }

    /**
     * Adds a new stage at the genetic algorithm's body
     * <p>
     *
     * @param stage
     *            the stage to be add
     */
    public void addStage(final AbstractStage<T> stage) {
        this.body.appendStage(stage);
    }

    /**
     * Says if the algorithm's body objective is set to maximize or minimize
     * individual fitness.
     * <p>
     *
     * @return true is the body objective is to maximize the fitness false
     *         otherwise
     */
    @Deprecated
    public final boolean isBiggerBetter() {
        return this.body.isBiggerBetter();
    }

    /**
     * Sets the algorithm's body objective to maximize (true) or minimize
     * (false) the individual fitness. All stages belonging to the body sequence
     * are recursevely set according to the flag.
     * <p>
     *
     * @param flag
     *            true to set the body to maximize the fitness, false to
     *            minimize
     */
    @Deprecated
    public void setBiggerIsBetter(final boolean flag) {
        this.body.setBiggerIsBetter(flag);
    }

    /**
     * Returns the current evaluation mode. By defualt it is false.
     * @return true, if evaluation is referred to the whole popuation.
     */
    public boolean isFullEvaluationForced() {
        return this.fullEvaluationForced;
    }

    /**
     * Sets the evalaution mode.
     * @param flag - true if evaluation is referred to the whole popuation.
     * False if evaluation is only referred to unevaluated individuals. By default it is false.
     */
    public void setFullEvaluationForced(boolean flag) {
        this.fullEvaluationForced = flag;
    }

    /**
     * Applies the elitism to the current population, according to the chosen
     * strategy. This method is authomatically invoked by {@link #evolve()},
     * and should not be explicitaly invoked.
     */
    protected final void applyElitism() {
        int currentSize = currentPopulation.size();
        int nextSize = nextPopulation.size();

        int len = currentSize < nextSize ? currentSize : nextSize;
        if (elitism < len) {
            len = elitism;
        }

        Fitness fit = this.getFitness();

        if (fit != null) {
            fit.sort(currentPopulation);
        } else {
            Fitness.sort(currentPopulation, this.isBiggerBetter());
        }

        if (this.elitismStrategy == ElitismStrategy.WORST) {
            if (fit != null) {
                fit.sort(nextPopulation);
            } else {
                Fitness.sort(nextPopulation, this.isBiggerBetter());
            }

            for (int i = 0; i < len; i++) {
                nextPopulation.getIndividual(nextSize - i - 1).setAs(
                        currentPopulation.getIndividual(i));
            }
        } else {
            // ElitismStrategy.RANDOM
            for (int i = 0; i < len; i++) {
                nextPopulation.getIndividual(random.nextInt(nextSize)).setAs(
                        currentPopulation.getIndividual(i));
            }
        }
    }

    /**
     * Returns the number of individuals considered for elitism by the genetic
     * algorithm.
     * <p>
     *
     * @return the elitism parameter
     */
    public final int getElitism() {
        return elitism;
    }

    /**
     * Sets the number of individuals considered for elitism by the genetic
     * algorithm. If it is 0, elitism has no place in this algorithm.
     * <p>
     *
     * @param elitism
     *            the new elitism parameter
     */
    public void setElitism(final int elitism) {
        this.elitism = elitism;
    }

    /**
     * Returns the elitism strategy used by this genetic algorithm
     * <p>
     *
     * @return the elitism strategy
     */
    public final ElitismStrategy getElitismStrategy() {
        return this.elitismStrategy;
    }

    /**
     * Sets the elitism strategy to used by this genetic algorithm. This
     * stratehy can be RANDOM or WORST: in the first case the random individuals
     * will be replaced by the elite individuals; in the latter the algorithm
     * will replace the worst individuals.
     * <p>
     *
     * @param es
     *            the elitism strategy
     */
    public void setElitismStrategy(final ElitismStrategy es) {
        this.elitismStrategy = es;
    }

    /**
     * Sets the resize stragety. This is for advanced use. The genetic algorithm
     * should have a need to resize an existing population, in particular to
     * expand it. Expanding the population would eventually require to create
     * new <code>Individual</code>. The resize strategy specifies the way to
     * perform such a task. Possible strategies are:
     * <ul>
     * <li> {@link ResizeStrategy#AUTO}: entails the creation of new
     * individuals, generally by cloning.
     * <li> {@link ResizeStrategy#EMPTY}: expands the population, without
     * creating new individuals.
     * <li> {@link ResizeStrategy#NONE}: disables the automatic resize of
     * populations.
     * </ul>
     * <p>
     *
     * @param rs
     *            the new resize strategy
     */
    public void setResizeStrategy(final ResizeStrategy rs) {
        this.resizeStrategy = rs;
    }

    /**
     * Returns the resize strategy used by this genetic algorithm.
     * <p>
     *
     * @return the resize strategy
     */
    public final ResizeStrategy getResizeStrategy() {
        return this.resizeStrategy;
    }

    /**
     * Returns the algorithm's body sequence.
     * <p>
     *
     * @return the body stage sequence
     */
    public final Sequence<T> getBody() {
        return this.body;
    }

    /**
     * Adds a new algorthm event listener
     * <p>
     *
     * @param ael
     *            the listener to add
     */
    public final void addAlgorithmEventListener(final AlgorithmEventListener<T> ael) {
        this.algorithmListeners.add(ael);
    }

    /**
     * Removes an algorithm event listener
     * <p>
     *
     * @param ael
     *            the listener to remove
     */
    public final void removeAlgorithmEventListener(
            final AlgorithmEventListener ael) {
        this.algorithmListeners.remove(ael);
    }

    /**
     * Adds a new generation event listener
     * <p>
     *
     * @param gel
     *            the generation listener to add
     */
    public final void addGenerationEventListener(
            final GenerationEventListener<T> gel) {
        this.generationListeners.add(gel);
    }

    /**
     * Removes the generation event listener
     * <p>
     *
     * @param gel
     *            the listener to remove
     */
    public final void removeGenerationEventListener(
            final GenerationEventListener gel) {
        this.generationListeners.remove(gel);
    }

    /**
     * Returns algoritm statistics at the moment of invokation.
     * <p>
     *
     * @return an object containing the algorithm statistics
     */
    public final Statistics getStatistics() {
        Statistics stats = new Statistics(statistics.generationLimit);
        this.updateStatistics(stats);
        return stats;
    }

    /**
     * Updates the algoritm statistics at the moment of invokation.
     * <p>
     *
     * @param stats
     *            the statistics object to be updated
     */
    public final void updateStatistics(final Statistics stats) {
        if (stats != null) {
            statistics.copyTo(stats);
        }
    }

    @Override
    public final String toString() {
        return (getClass().getName());
    }

    /**
     * This class provides some basic statistics regarding the algorithm
     * execution.
     */
    public static final class Statistics extends LoggableStatistics {

        private long startTime;
        private long stopTime;
        private long initTime;
        private long executionTime;
        private int generations;
        private int generationLimit;
        private long[] generationEndTimes;
        private boolean exceptionTerminated;
        private int fitnessEvaluationNumbers;
        private long[] fitnessEvalStageBegin;
        private long[] fitnessEvalStageEnd;
        private long timeSpentInFitnessEval;
        private long randomSeed;

        /**
         * Constructs a new GeneticAlgorithm.Statistics.
         * <p>
         *
         * @param generationLimit
         *            the max number of generations
         */
        private Statistics(final int generationLimit) {
            this.generationLimit = generationLimit;
            this.generationEndTimes = new long[generationLimit];
            this.fitnessEvalStageBegin = new long[generationLimit];
            this.fitnessEvalStageEnd = new long[generationLimit];
            this.exceptionTerminated = false;
        }

        /**
         * Returns the algorithm starting time.
         * <p>
         *
         * @return the starting time expressed in milliseconds
         */
        public final long getStartTime() {
            return this.startTime;
        }

        /**
         * Returns the algorithm stoppping time.
         * <p>
         *
         * @return the stopping time expressed in milliseconds
         */
        public final long getStopTime() {
            return this.stopTime;
        }

        /**
         * Returns the algorithm init time.
         * <p>
         *
         * @return the init time expressed in milliseconds
         */
        public final long getInitTime() {
            return this.initTime;
        }

        /**
         * Returns the algorithm execution time.
         * <p>
         *
         * @return the algorithm execution time expressed in milliseconds
         */
        public final long getExecutionTime() {
            return this.executionTime;
        }

        /**
         * Returns the last generation counter.
         * <p>
         *
         * @return the generation counter
         */
        public final int getGenerations() {
            return this.generations;
        }

        /**
         * Returns the algorithm generation limit.
         * <p>
         *
         * @return the generations limit
         */
        public final int getGenerationLimit() {
            return this.generationLimit;
        }

        /**
         * Returns the generation end time of the specified generation.
         * <p>
         *
         * @param gen
         *            the generation with end time is desidered
         * @return the end time of the generation gen
         */
        public final long getGenerationEndTime(final int gen) {
            return this.generationEndTimes[gen];
        }

        /**
         * Says if an exception terminated the evolution.
         * <p>
         *
         * @return true if an exception terminated the evolution, false
         *         otherwise
         */
        public final boolean isExceptionTerminated() {
            return this.exceptionTerminated;
        }

        /**
         * Returns the fitness evaluation number.
         * <p>
         *
         * @return the fitness evaluation number
         */
        public int getFitnessEvaluationNumbers() {
            return fitnessEvaluationNumbers;
        }

        /**
         * Returns the timestamp of the last fitness evaluation stage begin.
         * <p>
         *
         * @return the starting time expressed in milliseconds
         */
        public long getFitnessEvalStageBegin() {
            int last = this.generations - 1;
            return last >= 0 ? this.fitnessEvalStageBegin[this.generations - 1] : -1;
        }

        /**
         * Returns the timestamp (in millisecond) in wich the fitness evaluation 
         * has begin for the given generation.
         * <p>
         * 
         * @param the generation to query for starting from <tt>1</tt>
         *
         * @return the starting time timestamp expressed in milliseconds
         */
        public long getFitnessEvalStageBegin(int gen) {
            return this.fitnessEvalStageBegin[gen - 1];
        }

        /**
         * Returns the timestamp of the last fitness evaluation stage end.
         * <p>
         *
         * @return the end time timestamp expressed in milliseconds
         */
        public long getFitnessEvalStageEnd() {
            int last = this.generations - 1;
            return last >= 0 ? this.fitnessEvalStageEnd[this.generations - 1] : -1;
        }

        /**
         * Returns the timestamp at wich the fitness evaluation stage has ended
         * for the given generation.
         * <p>
         * @param the generation to query for starting from <tt>1</tt>
         *
         * @return the stopping time expressed in milliseconds
         */
        public long getFitnessEvalStageEnd(int gen) {
            return this.fitnessEvalStageEnd[gen - 1];
        }

        /**
         * Returns the current time spent in fitness evaluation.
         * <p>
         *
         * @return the execution time expressed in milliseconds
         */
        public long getTimeSpentForFitnessEval() {
            return timeSpentInFitnessEval;
        }

        /**
         * Returns the random seed used during the execution of the algorithm instance
         * @return 
         * 
         * @see Random#getSeed()
         */
        public long getRandomSeed() {
            return randomSeed;
        }

        /**
         * Copies the statistics data to the target object.
         * <p>
         *
         * @param stats
         *            the statistics object to be filled
         */
        private void copyTo(final Statistics stats) {
            stats.initTime = this.initTime;
            stats.startTime = this.startTime;
            stats.stopTime = this.stopTime;
            stats.executionTime = this.executionTime;
            stats.generationLimit = this.generationLimit;
            stats.generations = this.generations;
            stats.fitnessEvaluationNumbers = this.fitnessEvaluationNumbers;

            stats.timeSpentInFitnessEval = this.timeSpentInFitnessEval;
            stats.randomSeed = this.randomSeed;
            System.arraycopy(this.fitnessEvalStageBegin, 0, stats.fitnessEvalStageBegin, 0, this.fitnessEvalStageBegin.length);
            System.arraycopy(this.fitnessEvalStageEnd, 0, stats.fitnessEvalStageEnd, 0, this.fitnessEvalStageEnd.length);
            System.arraycopy(this.generationEndTimes, 0, stats.generationEndTimes, 0, this.generationEndTimes.length);
        }
    }
}
