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
package jenes.statistics;

import jenes.utils.AbstractLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.annotation.Retention;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This class provides an utility wrapper for logging the statistics.
 *
 * In order to log statistics, the class has to extend <code>LoggableStatistics<code>
 * in order to automatically retrieve figures. Figures are provided by methods returning
 * <code>double</code> value figures and annotated as <code>@Loggable<code>.
 *
 * For example:
 * <blockquote><pre>
 * class MyStatistics extends LoggableStatistics {
 *    ...
 *    &#064;Loggable("SomeFigure")
 *    double getSomeValue() { ... }
 *    ...
 *    &#064;Loggable("SomeCount")
 *    double getSomeCount() { ... }
 *    ...
 * }
 * </code>
 * </pre></blockquote>
 * Statistics are passed to the logger as specified at instantiation time.
 *
 * @version 2.0
 * @since 1.3
 *
 * @see StatisticsLogger.LoggableStatistics
 */
public class StatisticsLogger {

    /** The logger used for saving the statistics */
    private AbstractLogger logger;
    /** The figures we are interested to save. If null, means all figures */
    private String[] figures;

    /**
     * Creastes a StatisticsLogger. If figures is empty, no filter is applied
     * and all statistics are able to be saved.
     *
     * @param logger - the logger used to save the statistics
     * @param figures - the figures that we are interested to save
     */
    public StatisticsLogger(AbstractLogger logger, String... figures) {
        this.logger = logger;
        this.figures = figures;
    }

    /**
     * Store a new statistic record in the logger.
     *
     * @param statistics - the statistics being logged.
     */
    public void record(LoggableStatistics statistics) {
        this.record("", statistics);
    }

    /**
     * Store a new statistic record in the logger.
     *
     * @param prefix - the prefix of statistics name
     * @param statistics - the statistics being logged.
     */
    public void record(String prefix, LoggableStatistics statistics) {
        this.record(prefix, statistics, true);
    }

    /**
     * Store a new statistic record in the logger.
     *
     * @param prefix - the prefix of statistics name
     * @param statistics - the statistics being logged.
     * @param log - if <code>true</ code> logs a current record by storing it and making the record empty.
     */
    public void record(String prefix, LoggableStatistics statistics, boolean log) {

        String[] schema = logger.getSchema();
        String[] figs = this.figures;

        if (figs == null || figs.length == 0) {
            figs = getFiguresFromSchema(prefix, schema);
        }

        for (String f : figs) {
            store(prefix, statistics, f, schema);
        }

        if (log) {
            this.logger.log();
        }
    }

    /**
     * 
     * Add a value into the current record.
     *
     * @param prefix - the prefix of statistics name
     * @param key - the statistics name
     * @param value - the statistics value
     */
    public void add(String prefix, String key, Object value) {
        this.add(value, prefix + key, logger.getSchema());
    }

    private String[] getFiguresFromSchema(String prefix, String[] schema) {

        Set<String> figs = new HashSet<String>();

        for (String s : schema) {
            if (s.startsWith(prefix)) {

                Pattern p = Pattern.compile("([^\\[\\d\\]]+)(\\[(\\d+)\\])?");
                Matcher m = p.matcher(s.substring(prefix.length()));
                if (m.find()) {
                    figs.add(m.group(0));
                }
            }
        }

        return figs.toArray(new String[figs.size()]);
    }

    private void store(String prefix, LoggableStatistics statistics, String f, String[] schema) {

        Object v = statistics.getValue(f);

        if (v instanceof int[]) {

            int[] u = (int[]) v;
            for (int i = 0; i < u.length; ++i) {
                String fi = f + "[" + i + "]";
                add(u[i], prefix + fi, schema);
            }

        } else if (v instanceof double[]) {

            double[] u = (double[]) v;
            for (int i = 0; i < u.length; ++i) {
                String fi = f + "[" + i + "]";
                add(u[i], prefix + fi, schema);
            }

        } else {
            add(v == null ? "n/a" : v, prefix + f, schema);
        }
    }

    private void add(Object value, String key, String[] schema) {
        if (schema == null) {
            logger.put(key, value);
        } else {
            for (String s : schema) {
                if (s.equals(key)) {
                    logger.put(key, value);
                }
            }
        }
    }

    /**
     * Returns the logger used for storing the statistics.
     *
     * @return the logger
     */
    public AbstractLogger getLogger() {
        return this.logger;
    }

    /**
     * Logs a current record by storing it and making the record empty
     */
    public void log() {
        this.logger.log();
    }

    /**
     * Saves the undelying logger.
     */
    public void save() {
        logger.save();
    }

    /**
     * Closes the undelying logger.
     */
    public void close() {
        logger.close();
    }

    /**
     * This class provides the support for making a statistics loggable.
     *
     * It is simply required to make the own statistics class extending LoggableStatistics.
     *
     * @author Luigi Troiano
     */
    public static abstract class LoggableStatistics {

        private Map<String, Method> statmethods;

        /**
         * Constructs a new LoggableStatistics, retriving the names of figures by reflection.
         */
        protected LoggableStatistics() {
            statmethods = new HashMap<String, Method>();
            for (Method m : this.getClass().getMethods()) {
                Loggable a = m.getAnnotation(Loggable.class);
                if (a != null) {
                    String statname = a.label();
                    this.statmethods.put(statname, m);
                }
            }
        }

        /**
         * Provides the collection of figures made available by the subclass instance.
         * @return the collection of figures
         */
        public final Collection<String> getFigures() {
            return statmethods.keySet();
        }

        /**
         * Returns the figure value.
         * @param figure - the figure to retrieve
         * @return the value if the figure exists, Double.NaN otherwise
         */
        public final Object getValue(String figure) {

            Object value = null;

            Method m = statmethods.get(figure);
            if (m == null) {
                return value;
            }

            try {

                value = m.invoke(this, (Object[]) null);

            } catch (IllegalAccessException ex) {
                Logger.getLogger(StatisticsLogger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(StatisticsLogger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(StatisticsLogger.class.getName()).log(Level.SEVERE, null, ex);
            }

            return value;
        }
    }

    /**
     * Annotation for methods providing some figure in a LoggableStatistics object.
     *
     * The method has to be <code>double foo()</code>. Requires to specify the figure's label.
     * The method name and annotation label are not necessarily related. For example:
     *
     * <pre>
     * &#064;Loggable("SomeStatistics")
     * double getSomeStatistic() {...}
     * </pre>
     */
    @Retention(RUNTIME)
    public @interface Loggable {

        /**
         * Label assigned to the figure
         * 
         * @return the figure's label
         */
        String label();
    }

    @Override
    protected void finalize() {
        logger.close();
    }
}
