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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PerformanceStatistics {

    public static class Event {

        public String label;
        public int generation;
        public long time;
        public long memory;

        public String getLabel() {
            return label;
        }

        public int getGeneration() {
            return generation;
        }

        public long getTime() {
            return time;
        }

        public long getMemory() {
            return memory;
        }

        public String toString() {
            return p1 + "|" + p2 + "|" + generation + "|" + label + "|" + time + "|" + memory;
        }
    }
    private static ArrayList<Event> events = new ArrayList<Event>();
    private static long firstEventTime = 0;
    private static int p2 = -1;
    private static String p1 = "";

    public static void setParams(String par1, int par2) {
        p1 = par1;
        p2 = par2;
    }

    public static void mark(Event evt) {
        events.add(evt);
    }

    public static void mark(String label, int generation) {
        Event evt = new Event();
        evt.label = label;
        evt.generation = generation;
        evt.time = System.nanoTime() - firstEventTime;
        if (firstEventTime == 0) {
            firstEventTime = evt.time;
            evt.time = 0;
        }
        Runtime rt = Runtime.getRuntime();
        evt.memory = rt.totalMemory() - rt.freeMemory();
        events.add(evt);
    }

    public static void save(String file) {
        try {
            FileWriter stream = new FileWriter(file, true);
            //stream.write("RUN|POPSIZE|GEN|LABEL|TIME(in ns)|USED_MEM(in byte)\n");
            for (Event evt : events) {
                stream.write(evt + "\n");
            }
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        events.clear();
    }
}
