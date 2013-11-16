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
package jenes;

/**
 * The default exception used in Jenes
 */
public class JenesException extends RuntimeException {

    public enum Context {
        ALGORITHM,
        STATISTICS,
        IO
    }

    private Object source;
    private Context context;
    private String description;
    private boolean fatal;

    public JenesException( Object source, Context context, String desc ) {
        this( source, context, desc, true );
    }

    public JenesException( Object source, Context context, String desc, boolean fatal ) {
        super( context.name() + ": " + desc );
        this.source = source;
        this.description = desc;
        this.fatal = fatal;
        this.context = context;
    }

    public Object getSource() {
        return source;
    }

    public Context getContext() {
        return context;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFatal() {
        return fatal;
    }

}
