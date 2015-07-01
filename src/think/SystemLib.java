/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015 Quinn Freedman
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

package think;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import think.Variable.DataType;

class SystemLib{
	static class Get_JSON extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.String(((VariableData.Instance) input[0]).getJSON());
		}
		@Override
		public String getMenuName(){
			return "Get Object JSON";
		}
		Get_JSON(Point pos, GraphEditor owner) {
			super(pos, owner);
		}
		Get_JSON(){
			super();
		}
		
	}
	
	static class Get_Name extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.String(((VariableData.Instance) input[0]).getName());
		}
		@Override
		public String getMenuName(){
			return "Get Object Name";
		}
		Get_Name(Point pos, GraphEditor owner) {
			super(pos, owner);
		}
		Get_Name(){
			super();
		}
		
	}
	
	static class Get_Type extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.String(((VariableData.Instance) input[0]).parentBlueprint.getName());
		}
		@Override
		public String getMenuName(){
			return "Get Object Type";
		}
		Get_Type(Point pos, GraphEditor owner) {
			super(pos, owner);
		}
		Get_Type(){
			super();
		}
		
	}
}