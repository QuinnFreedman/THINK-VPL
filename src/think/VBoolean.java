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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

 class VBoolean extends Variable{
	private static final long serialVersionUID = 1L;
	static final String name = "Int";
	static int idCounter = 0;
	boolean value = false;
	VBoolean(GraphEditor owner){
		super(owner);
		this.dataType = DataType.BOOLEAN;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new BooleanDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Boolean.toString(value));
		
		this.functions.add(new Get(this));
		this.functions.add(new Set(this));
		this.functions.add(new Toggle(this));

		resetVariableData();
	}
	@Override
	 void resetVariableData(){
		this.varData = new VariableData.Boolean(value);
	}
	
	@Override
	protected void setValue(String s){
		value = s.equals("true");
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.BOOLEAN));
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.BOOLEAN));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Boolean) parentVarData).value = ((VariableData.Boolean) input[0]).value;
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
	static class Toggle extends PrimitiveFunction{
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
			((VariableData.Boolean) parentVarData).value = !(((VariableData.Boolean) parentVarData).value);
			return null;
		}
		Toggle(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Toggle(Point pos, Variable parent) {
			super(pos, parent);
		}
		Toggle(Variable parent){
			super(parent);
		}
		
	}
	
	static class BooleanDocumentFilter extends DocumentFilter implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		AbstractDocument doc;
		
		BooleanDocumentFilter(AbstractDocument doc) {
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
		    super.remove(fb, 0, doc.getLength());
		}
		  
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
			//String curentText = doc.getText(0, Math.min(4, doc.getLength()));
			/*if(doc.getLength() >= 4 && curentText.equals("true")){
				text = "true";
				offset = 0;
				length = doc.getLength();
			}else if(doc.getLength() >= 5){
				text = "false";
				offset = 0;
				length = doc.getLength();
			}*/
			if(doc.getText(0,Math.min(1, doc.getLength())).equals("t") || doc.getText(0,Math.min(1, doc.getLength())).equals("T") || (text.equals("t")) || (text.equals("T"))){
				text = "true";
				offset = 0;
				length = doc.getLength();
			}else{
				text = "false";
				offset = 0;
				length = doc.getLength();
			}
			super.replace(fb, offset, length, text, attrs);
		}
	}
}