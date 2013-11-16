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
 * An <code>AlgorithmException</code> is a runtime exception thrown
 * during the algorithm execution.
 *
 * @version 1.2
 * @since 1.0
 */
public class AlgorithmException extends RuntimeException {
    
    private static final long serialVersionUID = 7816138221965202687L;
    
    /**
     * Creates an <code>AlgorithmException</code> providing an error message and an exception cause.
     *
     * @param msg	the error message
     * @param cause	the error cause
     */
    public AlgorithmException(String msg,Throwable cause) {
        super(msg,cause);
    }
    
    /**
     * Creates an <code>AlgorithmException</code> providing only an error message
     *
     * @param msg	the error message
     */
    public AlgorithmException(String msg){
        this(msg,null);
    }
    
    /**
     * Creates an <code>AlgorithmException</code> providing an exception cause, and no additional message.
     *
     * @param cause the error cause
     */
    public AlgorithmException(Throwable cause){
        this("",cause);
    }
}