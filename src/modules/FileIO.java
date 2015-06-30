/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General  License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General  License for more details.
 *
 *  You should have received a copy of the GNU General  License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package modules;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import think.Executable;
import think.GraphEditor;
import think.Module;
import think.Variable.DataType;
import think.VariableData;

public class FileIO extends Module{
	
	
	@Override
	public void setup(){
		addFunction(new Make_Directory());
	}
	
	public static class Make_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			return null;
		}
		
		public Make_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Make_Directory(){
			
		}
	}

}