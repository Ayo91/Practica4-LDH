/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.old.problem12;

/**
 * This class represent a function for this problem
 */
public abstract class Function {

    protected double[][] bounds;
    protected int goals;
    protected String name;

    public Function(String name, double[][] bounds, int goals) {
        this.name = name;
        this.bounds = bounds;
        this.goals = goals;
    }

    public double[][] getBounds() {
        return bounds;
    }

    public int getNVars() {
        return this.bounds.length;
    }

    public int getGoals() {
        return this.goals;
    }

    public String getName() {
        return this.name;
    }

    public abstract double[] evaluate(double... x);

    /**
     * Shaffer function
     */
    public static class Schaffer extends Function {

        public Schaffer() {
            super("Schaffer", new double[][]{{0, 2}}, 2);
        }

        public double[] evaluate(double... x) {
            double out[] = new double[2];
            out[0] = Math.pow(x[0], 2);
            out[1] = Math.pow(x[0] - 2, 2);
            return out;
        }
    }
}
