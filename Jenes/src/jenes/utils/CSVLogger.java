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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class defines a StatisticsLogger based on CSV (comma separated values) file.
 * The logger has a schema, made of the fields the user means to record. If the schema
 * is not specified, the logger use the set of keys making a record as schema.
 * The default separator is tab, but a different separator can be specified at instantiation time.
 * The default logfile is named log.csv and placed into the working directory.
 * The logger can record or not the first line with the header made of the field names.
 *
 * @version 1.3
 * @since 1.3
 */
public class CSVLogger extends AbstractLogger {

    private boolean header = true;
    private StringWriter buffer;
    private PrintWriter out;
    private String separator;

    private boolean started = false;

    /**
     * Creates a logger with the specified schema, and
     * log.csv as default filename, tab as default separator.
     * The header is placed on the first line.
     *
     * @param schema - the array making the schema of records
     * @throws java.io.FileNotFoundException
     */
    public CSVLogger(String[] schema) throws FileNotFoundException {
       this(schema, "jenes.log.csv", "\t", true);
    }

    /**
     * Creates a logger with the specified schema and filename, tab as default separator.
     * The header is placed on the first line.
     *
     * @param schema - the array making the schema of records
     * @param filename - the output filename
     * @throws java.io.FileNotFoundException
     */
    public CSVLogger(String[] schema, String filename) throws FileNotFoundException {
       this(schema, filename, "\t", true);
    }

    /**
     * Creates a logger with the specified schema, filename and separator.
     * The header is placed on the first line.
     *
     * @param schema - the array making the schema of records
     * @param filename - the output filename
     * @param separator - the values separator
     * @throws java.io.FileNotFoundException
     */
    public CSVLogger(String[] schema, String filename, String separator) throws FileNotFoundException {
       this(schema, filename, separator, true);
    }


    /**
     * Creates a logger with the specified schema, filename and separator.
     * The header is placed on the first line if required.
     *
     * @param schema - the array making the schema of records
     * @param filename - the output filename
     * @param separator - the values separator
     * @param header - the header is included if true, otherwise not.
     * @throws java.io.FileNotFoundException
     */
    public CSVLogger(String[] schema, String filename, String separator, boolean header) throws FileNotFoundException {
       super(schema);
       this.out = new PrintWriter(filename);
       this.separator = separator;
       this.header = header;
       this.buffer = new StringWriter();
    }

    /**
     * Return the output stream used for logging.
     *
     * @return the log writer
     */
    public PrintWriter getOut() {
        return this.out;
    }

    @Override
    protected void store() {

        if( !started ) {
            if(this.header) this.writeHeader();
            this.started = true;
        }

        String line = "";
        boolean first = true;
        for( String key : record.keySet()  ) {
            line += (first ? "" : separator+" ") + record.get(key);
            first = false;
        }

        buffer.write(line + "\n");
    }

    @Override
    protected void doSave() {
        out.print( buffer );
        out.flush();
        buffer = new StringWriter();
    }

    @Override
    protected void doClose() {
        this.out.close();
    }

    private void writeHeader() {

        String hd = "";
        boolean first = true;
        for( String s : schema ) {
            hd += (first ? "" : separator+" ") + s;
            first = false;
        }

        out.println(hd);
    }
    
}