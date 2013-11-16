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
package jenes.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides basic support and interface for logging on different media.
 * A log is made of records. Each record is an associative table of properties.
 *
 * @version 2.0
 * @since 1.3
 */
public abstract class AbstractLogger {

    /** The statistics schema */
    protected List<String> schema = null;

    /** The table of value to store. It is the current record to fill. */
    protected Map<String, Object> record = new HashMap<String, Object>();

    protected boolean closed;

    /**
     * Creates a new logger providing a schema
     *
     * @param schema - the logging schema
     */
    public AbstractLogger(String[] schema) {
        this.schema = new ArrayList<String>();
        for( String field : schema )
            this.schema.add(field);
        this.closed = false;
    }

    /** Returns the logging schema
     *
     * @returns the array of fields
     */
    public String[] getSchema() {
        String[] fields = new String[this.schema.size()];
        return this.schema.toArray(fields);
    }

    /** Returns the list of schema fields
     * 
     * @returns the iterator of fields
     */
    public Iterator<String> getFields() {
        return this.schema.iterator();
    }

    /**
     * Puts a value into the current record.
     *
     * @param key - the statistics name
     * @param value - the statistics value
     */
    public final void put(String key, Object value) {
        if( closed )
            throw new RuntimeException("LOGGER: Closed. Operation not allowed.");

        if(schema.contains(key))
            record.put(key, value);
        else
            throw new RuntimeException("LOGGER: The field " + key + " does not exist.");
    }

    /**
     * Retrieves a statistics from the current record.
     *
     * @param key - the statistics name
     * @return the statistics value
     */
    public final Object get(String key) {
        return record.get(key);
    }

    /** 
     * Returns true if the record is complete
     *
     * @return true if the record is complete
     */
    public boolean isRecordComplete() {
        for( String field : schema ) {
            if( record.get(field) == null )
                return false;
        }
        return true;
    }

    /**
     * Logs a current record by storing it and making the record empty.
     * This is the method to invoke to store the current recorrd.
     */
    public final void log() {
        if( closed )
            throw new RuntimeException("LOGGER: Closed. Operation not allowed.");

        store();
        record.clear();
    }

    /**
     * Saves cached records on media. If the logger is closed this operation is not allowed.
     */
    public void save() {
        if( closed )
            throw new RuntimeException("LOGGER: Closed. Operation not allowed.");

        doSave();
    }

    /**
     * Saves cached records on media and closes the logger. If the logger is closed this operation is not allowed.
     */
    public void close() {
        
        if( closed )
            throw new RuntimeException("LOGGER: Closed. Operation not allowed.");

        doSave();
        doClose();

        this.closed = true;
    }

    /**
     * Stores the current record.
     */
    protected abstract void store();

    /**
     * Saves cached records on media
     */
    protected abstract void doSave();

    /**
     * Closes the logger. Any further log is not allowed.
     */
    protected abstract void doClose();

    @Override
    protected void finalize() {
        doClose();
    }

}
