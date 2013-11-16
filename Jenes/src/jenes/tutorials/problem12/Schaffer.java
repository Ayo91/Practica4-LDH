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
package jenes.tutorials.problem12;

/**
 *
 */
public class Schaffer extends MultiObjectiveProblem.Function {

    public Schaffer() {
        super( "Schaffer", new double[][]{{0, 2}}, 2 );
    }


    public double[] evaluate(double... x) {
        double out[] = new double[2];
        out[0] = Math.pow(x[0],2);
        out[1] = Math.pow(x[0]-2,2);
        return out;
    }

}
