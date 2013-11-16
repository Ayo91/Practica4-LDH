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
package jenes.tutorials.utils;

import jenes.population.Population;
import jenes.population.Population.Statistics.Group;
import jenes.utils.PrettyPrinter;

/**
 * This class provides some utility methods to tutorials.
 * 
 * @version 2.0
 * @since 1.0
 */
public class Utils {

    public static final void printHeader() {
        System.out.println("JENES, A library for Genetic Algorithms in Java");
        System.out.println("2011, Intelligentia srl");
        System.out.println("Benevento, Italy");
    }

    public static final void printStatistics(Population.Statistics stats) {

        Group legals = stats.getGroup(Population.LEGALS);

        Group illegals = stats.getGroup(Population.ILLEGALS);

        System.out.format("Individuals: %d\n", legals.getNumOfIndividuals() + illegals.getNumOfIndividuals());
        System.out.println();

        System.out.format("Legal Individuals: %d\n", legals.getNumOfIndividuals());
        if (legals.getNumOfIndividuals() > 0) {
            System.out.format(" Highest Score: %s\n", PrettyPrinter.valuesToString(legals.getMax()));
            System.out.format("  Lowest Score: %s\n", PrettyPrinter.valuesToString(legals.getMin()));
            System.out.format("           Avg: %s\n", PrettyPrinter.valuesToString(legals.getMean()));
            System.out.format("           Dev: %s\n", PrettyPrinter.valuesToString(legals.getStDev()));
        }
        System.out.println();

        System.out.format("Illegal Individuals: %d\n", illegals.getNumOfIndividuals());
        if (illegals.getNumOfIndividuals() > 0) {
            System.out.format(" Highest Score: %5.3f\n", PrettyPrinter.valuesToString(illegals.getMax()));
            System.out.format("  Lowest Score: %5.3f\n", PrettyPrinter.valuesToString(illegals.getMin()));
            System.out.format("           Avg: %5.3f\n", PrettyPrinter.valuesToString(illegals.getMean()));
            System.out.format("           Dev: %5.3f\n", PrettyPrinter.valuesToString(illegals.getStDev()));
        }
        System.out.println();
        
        double score = 0;

        for (int i = 0; i < legals.size(); i++) {
            score = legals.get(i).getScore();
            legalScoreAvg = ((legalScoreAvg * i) + score) / (i + 1d);
        }
        for (int i = 0; i < legals.size(); i++) {

            score = legals.get(i).getScore() - legalScoreAvg;
            legalScoreDev = ((legalScoreDev * i) + (score * score)) / (i + 1d);
        }

         System.out.println("DEV--> " + Math.sqrt(legalScoreDev));
    }
    private static double legalScoreDev;
    private static double legalScoreAvg;
}
