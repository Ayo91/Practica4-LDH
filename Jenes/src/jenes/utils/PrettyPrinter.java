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

/**
 *
 */
public class PrettyPrinter {
    
    public static String arrayToString ( double[] arr ) {
        return arrayToString(6,3, arr);  
    }
    
    public static String arrayToString ( int digits, int precision, double[] arr ) {
        return arrayToString("%"+digits+"."+precision+"g", arr);  
    }
        
    public static String arrayToString ( String af, double[] arr ) {
        
        StringBuilder str = new StringBuilder();
        
        str.append("[ ");
        str.append(valuesToString(af, arr));
        
        str.append("]");
        
        return str.toString();
        
    }
    
    public static String valuesToString( double ... values ) {               
        return valuesToString( 6,3, values);        
    }

    public static String valuesToString( int digits, int precision, double ... values ) {
        return valuesToString("%"+digits+"."+precision+"g", values);
    }
    
    public static String valuesToString( String af, double ... values ) {
        
        StringBuilder str = new StringBuilder();
        
        for( double d : values ) {
            if( str.length() > 0 ) {
                str.append(" ");
            }
                
            str.append( String.format(af, d) );       
        }
        
        return str.toString();        
    }
    
    
          
    
}
