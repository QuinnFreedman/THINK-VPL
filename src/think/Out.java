/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package think;

public class Out{
	public static final boolean isCommandLine = true;
	
	public static void println(String s){
		if(isCommandLine)
			System.out.println(s);
	}
	public static void println(Object s){
		if(isCommandLine)
			System.out.println(s);
	}
	
	public static void println() {
		if(isCommandLine)
			System.out.println();
	}
	
	public static void print(String s){
		if(isCommandLine)
			System.out.print(s);
	}
	
	public static void printStackTrace(Throwable e){
		if(isCommandLine)
			e.printStackTrace();
	}
}