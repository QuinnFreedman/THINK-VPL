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

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

 class VInt extends Variable{
	private static final long serialVersionUID = 1L;
	static int idCounter = 0;
	int value = 0;
	protected VInt getThisVariable(){
		return this;
	}
	VInt(GraphEditor owner){
		super(owner);
		Out.pln("new int "+type);
		this.dataType = DataType.INTEGER;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		this.nameField.selectAll();
		
		new IntDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Integer.toString(value));
		
		this.functions.add(new Get(this));
		this.functions.add(new Set(this));
		this.functions.add(new Add_To(this));
		this.functions.add(new Multiply_By(this));
		this.functions.add(new Increment(this));
		
		resetVariableData();
	}
	@Override
	 void resetVariableData(){
		this.varData = new VariableData.Integer(value);
	}
	
	@Override
	protected void setValue(String s){
		if(s.length() > 0 && !s.equals("-")){
			value = Integer.parseInt(s);
		}else{
			value = 0;
		}
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.INTEGER));
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Integer) parentVarData).value = ((VariableData.Integer) input[0]).value;
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
	static class Add_To extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Integer) parentVarData).value += ((VariableData.Integer) input[0]).value;
			return null;
		}
		Add_To(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Add_To(Point pos, Variable parent) {
			super(pos, parent);
		}
		Add_To(Variable parent){
			super(parent);
		}
		
	}
	static class Multiply_By extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Integer) parentVarData).value *= ((VariableData.Integer) input[0]).value;
			return null;
		}
		Multiply_By(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Multiply_By(Point pos, Variable parent) {
			super(pos, parent);
		}
		Multiply_By(Variable parent){
			super(parent);
		}
		
	}
	static class Increment extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(DataType.GENERIC));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Integer) parentVarData).value++;
			return null;
		}
		Increment(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Increment(Point pos, Variable parent) {
			super(pos, parent);
		}
		Increment(Variable parent){
			super(parent);
		}
		
	}
	static class IntDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		
		 IntDocumentFilter(AbstractDocument doc) {
			this.doc = doc;
			this.doc.setDocumentFilter(this);
		}
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {
		    super.insertString(fb, offset, string, attr);
		}
		
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
			
		    super.remove(fb, offset, length);
		}
		  
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if(Character.isDigit(c) || (c == '-' && doc.getLength() == 0 && i == 0)){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
}