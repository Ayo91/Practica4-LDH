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
package jenes.stage;

/**
 * An exception thrown during a stage processing.
 * <p>
 * 
 * @version 1.2
 * @since 1.0
 * 
 * @see     jenes.stage.AbstractStage
 */
public class StageException extends RuntimeException {

    private static final long serialVersionUID = -6449902532700947172L;

    /**
     * Constructs a new StageException with the specified message and the specified cause
     * <p>
     * @param message the exception message
     * @param cause the exception cause
     */
    public StageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new StageException with the specified message
     * <p>
     * @param message the exception message
     */
    public StageException(String message) {
        this(message, null);
    }

    /**
     * Constructs a new StageException with the specified cause
     * <p>
     * @param cause the exception cause
     */
    public StageException(Throwable cause) {
        this("", cause);
    }
}