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

package think;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

 class VString extends Variable{
	private static final long serialVersionUID = 1L;
	static int idCounter = 0;
	String value = "";
	protected VString getThisVariable(){
		return this;
	}
	VString(GraphEditor owner){
		super(owner);
		this.dataType = DataType.STRING;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		this.nameField.selectAll();
		
		this.valueField.setText(value);
		
		this.functions.add(new Get(this));
		this.functions.add(new Set(this));
		this.functions.add(new Append(this));
		this.functions.add(new Get_Length(this));
		this.functions.add(new Get_Char_At(this));
		this.functions.add(new Replace(this));
		this.functions.add(new Split(this));
		
		resetVariableData();
	}
	@Override
	 void resetVariableData(){
		this.varData = new VariableData.String(value);
	}
	
	@Override
	protected void setValue(String s){
		value = s;
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			return getParentVarData();
			
		}
		Get(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Get(Point pos, Variable parent) {
			super(pos, parent);
		}
		Get(Variable parent){
			super(parent);
		}
		
	}
	static class Set extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.String) parentVarData).value = ((VariableData.String) input[0]).value;
			return null;
			
		}
		Set(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Set(Point pos, Variable parent) {
			super(pos, parent);
		}
		Set(Variable parent){
			super(parent);
		}
		
	}
	static class Append extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.String) parentVarData).value = ((VariableData.String) parentVarData).value.concat(((VariableData.String) input[0]).value);
			return null;
		}
		Append(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Append(Point pos, Variable parent) {
			super(pos, parent);
		}
		Append(Variable parent){
			super(parent);
		}
		
	}
	static class Get_Length extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.INTEGER));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		@Override
		public VariableData execute(VariableData[] input){
			int length = ((VariableData.String) parentVarData).value.length();
			return new VariableData.Integer(length);
		}
		Get_Length(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Get_Length(Point pos, Variable parent) {
			super(pos, parent);
		}
		Get_Length(Variable parent){
			super(parent);
		}
		
	}
	static class Replace extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.STRING,DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			String s = ((VariableData.String) parentVarData).value.replaceAll(((VariableData.String) input[0]).value,((VariableData.String) input[1]).value);
			return new VariableData.String(s);
		}
		Replace(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Replace(Point pos, Variable parent) {
			super(pos, parent);
		}
		Replace(Variable parent){
			super(parent);
		}
		
	}
	static class Get_Char_At extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		@Override
		public VariableData execute(VariableData[] input){
			char c = ((VariableData.String) parentVarData).value.charAt(((VariableData.Integer) input[0]).value);
			String s = Character.toString(c);
			return new VariableData.String(s);
		}
		Get_Char_At(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Get_Char_At(Point pos, Variable parent) {
			super(pos, parent);
		}
		Get_Char_At(Variable parent){
			super(parent);
		}
		
	}
	static class Split extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.ARRAY));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		@Override
		public VariableData execute(VariableData[] input){
			String regex = ((VariableData.String) input[0]).value;
			String s = ((VariableData.String) parentVarData).value;
			String[] strs = s.split(regex);
			VariableData.Array output = new VariableData.Array(Variable.DataType.STRING);
			
			for(String str : strs){
				output.add(new VariableData.String(str));
			}
			
			return output;
		}
		Split(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Split(Point pos, Variable parent) {
			super(pos, parent);
		}
		Split(Variable parent){
			super(parent);
		}
		
	}
	
}