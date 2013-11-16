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

public class Statistics {

    public static class Event {

        private String label;
        private int generation;
        private long time;
        private long memory;

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
            return generation + "\t" + label + "\t" + time + "\t" + memory;
        }
    }
    private ArrayList<Event> events = new ArrayList<Event>();

    public void mark(String label, int generation) {
        Event evt = new Event();
        evt.label = label;
        evt.generation = generation;
        evt.time = System.currentTimeMillis();
        Runtime rt = Runtime.getRuntime();
        evt.memory = rt.totalMemory() - rt.freeMemory();
        events.add(evt);
    }

    public void save(String file) {
        try {
            FileWriter stream = new FileWriter(file);
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
}
