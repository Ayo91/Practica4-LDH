
package jenes.algorithms;

import jenes.population.Fitness;
import jenes.GeneticAlgorithm;
import jenes.chromosome.Chromosome;
import jenes.population.Population;
import jenes.stage.AbstractStage;
import jenes.stage.operator.Crowder;

 
public class CrowdingGA<T extends Chromosome> extends GeneticAlgorithm<T> {

     public static final int DEFAULT_GENERATION_LIMIT = 100;
    protected Crowder crowder;

    /**
     * Default constructor
     * @param fitness the Fitness considered for this algorithm
     * @param crowder the crowder to use
     */
    public CrowdingGA(final Fitness fitness, final Crowder crowder) {
        this(fitness, crowder, null, DEFAULT_GENERATION_LIMIT);
    }

    /**
     * Create a new CrowdingGA by setting the initial population and the generation limit
     * @param fitness the Fitness considered for this algorithm
     * @param crowder the crowder to use
     * @param population the initial population to consider
     * @param generations number of generations to evolve
     */
    public CrowdingGA(final Fitness fitness, final Crowder crowder, final Population<T> population, final int generations) {
        super(fitness, population, generations);

        this.crowder = crowder;
        super.getBody().appendStage(crowder);
    }
    
    
    /**
     * A stage is added to the crowder evolution pipeline
     * @param stage 
     */
    @Override
    public void addStage(AbstractStage<T> stage) {
        this.crowder.getBody().appendStage(stage);
    }

    /**
     * Access the current crowder setted for this algorithm
     * @return
     */
    public Crowder getCrowder() {
        return this.crowder;
    }
}
