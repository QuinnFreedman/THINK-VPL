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

import think.Executable.Mode;
import think.Variable.DataType;

@SuppressWarnings("serial")
abstract class StringFuncs{
	
	static class Parse_Int extends Executable{
		
		Parse_Int(){
			super();
		}
		
		Parse_Int(Point pos, GraphEditor owner){
			super(pos, owner);
		}
		
		@Override
		public ArrayList<DataType> getInputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		
		@Override
		public ArrayList<DataType> getOutputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.INTEGER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs) throws Exception {
			
			return new VariableData.Integer(
					Integer.parseInt(((VariableData.String) inputs[0]).value)
				);
		}
	}
	
	static class Parse_Float extends Executable{
		
		Parse_Float(){
			super();
		}
		
		Parse_Float(Point pos, GraphEditor owner){
			super(pos, owner);
		}
		
		@Override
		public ArrayList<DataType> getInputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		
		@Override
		public ArrayList<DataType> getOutputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.FLOAT));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs) throws Exception {
			return new VariableData.Float(
					Float.parseFloat(((VariableData.String) inputs[0]).value)
				);
		}
	}
	
	static class Parse_Double extends Executable{
		
		Parse_Double(){
			super();
		}
		
		Parse_Double(Point pos, GraphEditor owner){
			super(pos, owner);
		}
		
		@Override
		public ArrayList<DataType> getInputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		
		@Override
		public ArrayList<DataType> getOutputs() {
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs) throws Exception {
			return new VariableData.Double(
					Double.parseDouble(((VariableData.String) inputs[0]).value)
				);
		}
	}
	
	static class Split extends Executable{
		
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList("String","regex"));
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(DataType.STRING,DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.ARRAY));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			String regex = ((VariableData.String) input[1]).value;
			String s = ((VariableData.String) input[0]).value;
			String[] strs = s.split(regex);
			VariableData.Array output = new VariableData.Array(Variable.DataType.STRING);
			
			for(String str : strs){
				output.add(new VariableData.String(str));
			}
			
			return output;
		}
		Split(Point pos, GraphEditor owner) {
			super(pos, owner);
		}
		Split(){
			super();
		}
	}
}