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

import jenes.performance.PerformanceStatistics.Event;

public class JENESTest {

    protected static int POPULATION_SIZE = 100;
    protected static int GENERATION_LIMIT = 20;
    protected static String RUN = "1";
    protected static String TYPE = "dj1";

    public static void main(String[] args) {

        loadArgs(args);
        Runtime runtime = Runtime.getRuntime();
        String fileName = "";
        PerformanceStatistics.setParams(RUN, POPULATION_SIZE);

        long timeStart = -1;

        //de jong functions
        if (TYPE.equals("dj1")) {
            timeStart = System.currentTimeMillis();
            //System.out.println("######\n De Jong's Problem #1");
            DeJongTest problem = new DeJongTest(1, POPULATION_SIZE, GENERATION_LIMIT);
            problem.solve();
            fileName = "DEJOND_1.txt";
        } else if (TYPE.equals("dj2")) {
            timeStart = System.currentTimeMillis();
            //System.out.println("######\n De Jong's Problem #2");
            DeJongTest problem = new DeJongTest(2, POPULATION_SIZE, GENERATION_LIMIT);
            problem.solve();
            fileName = "DEJOND_2.txt";
        } else if (TYPE.equals("dj3")) {
            timeStart = System.currentTimeMillis();
            //System.out.println("######\n De Jong's Problem #3");
            DeJongTest problem = new DeJongTest(3, POPULATION_SIZE, GENERATION_LIMIT);
            problem.solve();
            fileName = "DEJOND_3.txt";
        } else if (TYPE.equals("dj4")) {
            timeStart = System.currentTimeMillis();
            //System.out.println("######\n De Jong's Problem #4");
            DeJongTest problem = new DeJongTest(4, POPULATION_SIZE, GENERATION_LIMIT);
            problem.solve();
            fileName = "DEJOND_4.txt";
        } else if (TYPE.equals("dj5")) {
            timeStart = System.currentTimeMillis();
            //System.out.println("######\n De Jong's Problem #5");
            DeJongTest problem = new DeJongTest(5, POPULATION_SIZE, GENERATION_LIMIT);
            problem.solve();
            fileName = "DEJOND_5.txt";
        } else if (TYPE.equals("tsp")) {
            timeStart = System.currentTimeMillis();
            //tsp
            //System.out.println("######\n TSP Problem");
            double[][] m1 = getMap();
            TravelSalesmanProblem tspt = new TravelSalesmanProblem(m1, POPULATION_SIZE, GENERATION_LIMIT);
            tspt.solve();
            fileName = "TSP.txt";
        } else if (TYPE.equals("royal")) {
            timeStart = System.currentTimeMillis();
            //royal
            //System.out.println("######\n Royalroad Problem");
            RoyalTest rt = new RoyalTest(POPULATION_SIZE, GENERATION_LIMIT);
            rt.solve();
            fileName = "ROYAL.txt";
        }

        Event evt = new Event();
        evt.label = "EndGA";
        evt.generation = -1;
        evt.time = System.currentTimeMillis() - timeStart;
        evt.memory = runtime.totalMemory() - runtime.freeMemory();
        PerformanceStatistics.mark(evt);

        PerformanceStatistics.save(fileName);
        PerformanceStatistics.reset();
    }

    private static void loadArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i++];
            if (arg.equals("ps")) {
                POPULATION_SIZE = Integer.parseInt(args[i]);
            } else if (arg.equals("gn")) {
                GENERATION_LIMIT = Integer.parseInt(args[i]);
            } else if (arg.equals("run")) {
                RUN = args[i];
            } else if (arg.equals("problem")) {
                TYPE = args[i];
            }

        }

    }

    public static double[][] getMap() {
        double[][] matrix = new double[20][20];
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 20; ++j) {
                matrix[i][j] = getDistance(i, j);
            }
        }
        return matrix;
    }

    private static double getDistance(int i, int j) {
        int xa = positions[i][0];
        int ya = positions[i][1];

        int xb = positions[j][0];
        int yb = positions[j][1];

        return Math.sqrt(Math.pow((xa - xb), 2) + Math.pow((ya - yb), 2));
    }
    //valori usati anche da galib
    private static int[][] positions = {
        {1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {3, 1}, {3, 2}, {3, 3},
        {3, 4}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {5, 1}, {5, 2}, {5, 3}, {5, 4}
    };
}
