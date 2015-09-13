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

import java.awt.Color;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

 class VArray extends Variable{
	private static final long serialVersionUID = 1L;
	static int idCounter = 0;
	String value;
	
	 InstantiableBlueprint objectType;
	
	protected VArray getThisVariable(){
		return this;
	}
	VArray(DataType dt,GraphEditor owner){
		super(owner);
		this.dataType = dt;
		this.varData = new VariableData.Array(dt);
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		this.value = "";
		
		this.nameField.selectAll();
		
		new ArrayDocumentFilter((AbstractDocument) valueField.getDocument(),this);
		
		this.functions.add(new Get(this));
		this.functions.add(new Get_Length(this));
		this.functions.add(new Add(this));
		this.functions.add(new Set(this));
		this.functions.add(new Remove(this));
		this.functions.add(new To_String(this));
		this.functions.add(new Get_Array(this));
		this.functions.add(new Set_Array(this));
		
		resetVariableData();
	}
	 VArray(DataType object, InstantiableBlueprint bp, GraphEditor owner) {
		this(object, owner);
		this.objectType = bp;
	}
	@Override
	 void resetVariableData(){
		value = valueField.getText();
		
		VariableData.Array data = new VariableData.Array(this.dataType);
		
		ArrayList<String> listOfStrings = new ArrayList<String>(Arrays.asList(value.split(",")));
		if(!(listOfStrings.size() == 1 && listOfStrings.get(0).equals("")) || this.dataType == Variable.DataType.OBJECT){
			
			if(this.dataType == DataType.INTEGER){
				for(String s : listOfStrings){
					try{
						data.add(new VariableData.Integer(Integer.parseInt(s)));
						
					}catch(Exception e){
						valueField.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
					}
				}
			}else if(this.dataType == DataType.DOUBLE){
				for(String s : listOfStrings){
					try{
						data.add(new VariableData.Double(Double.parseDouble(s)));
						
					}catch(Exception e){
						valueField.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
					}
				}
			}else if(this.dataType == DataType.FLOAT){
				for(String s : listOfStrings){
					try{
						data.add(new VariableData.Float(Float.parseFloat(s)));
						
					}catch(Exception e){
						valueField.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
					}
				}
			}else if(this.dataType == DataType.BOOLEAN){
				for(String s : listOfStrings){
					try{
						data.add(new VariableData.Boolean(Boolean.parseBoolean(s)));
						
					}catch(Exception e){
						valueField.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
					}
				}
			}else if(this.dataType == DataType.STRING){
				for(String s : listOfStrings){
					try{
						data.add(new VariableData.String(s));
						
					}catch(Exception e){
						valueField.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
					}
				}
			}
		}
		varData = data;
	}
	
	@Override
	protected void setValue(String s){
		value = s;
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(((VariableData.Array) getParentVarData()).dataType));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			try{
				return ((VariableData.Array) getParentVarData()).value.get(((VariableData.Integer) input[0]).value);
			}catch(IndexOutOfBoundsException e){
				Out.printStackTrace(e);
				throw new Exception("in \""+this.getParentVariable().getFullName()+" : "+this.getSimpleName()+"\"; index out of bounds; trying to get element "+input[0].getValueAsString()+" of "+((VariableData.Array) getParentVarData()).value.size());
			}
			
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
			return new VariableData.Integer(((VariableData.Array) getParentVarData()).value.size());
			
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
	
	static class Add extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,((VariableData.Array) getParentVarData()).dataType));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Array) getParentVarData()).add(VariableData.clone(input[0]));
			
			return null;
		}
		Add(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Add(Point pos, Variable parent) {
			super(pos, parent);
		}
		Add(Variable parent){
			super(parent);
		}
		
	}
	
	static class Remove extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC, Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			try{
				((VariableData.Array) getParentVarData()).value.remove(((VariableData.Integer) input[0]).value);
				return null;
				
			}catch(IndexOutOfBoundsException e){
				Out.printStackTrace(e);
				throw new Exception("in \""+this.getParentVariable().getFullName()+" : "+this.getSimpleName()+"\"; index out of bounds; trying to get element "+input[0].getValueAsString()+" of "+((VariableData.Array) getParentVarData()).value.size());
			}
			
		}
		Remove(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Remove(Point pos, Variable parent) {
			super(pos, parent);
		}
		Remove(Variable parent){
			super(parent);
		}
		
	}
	
	static class Clear extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Array) getParentVarData()).value.clear();
				
			return null;
			
		}
		Clear(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Clear(Point pos, Variable parent) {
			super(pos, parent);
		}
		Clear(Variable parent){
			super(parent);
		}
		
	}
	
	static class Set extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,Variable.DataType.INTEGER,((VariableData.Array) getParentVarData()).dataType));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			int index = ((VariableData.Integer) input[1]).value;
			int size = ((VariableData.Array) getParentVarData()).value.size();
			if(index == 0){
				((VariableData.Array) getParentVarData()).add(VariableData.clone(input[0]));
			}else{
				try{
					((VariableData.Array) getParentVarData()).value.set(index,VariableData.clone(input[0]));
				}catch(IndexOutOfBoundsException e){
					Out.printStackTrace(e);
					throw new Exception("in \""+this.getParentVariable().getFullName()+" : "+this.getSimpleName()+"\"; index out of bounds; trying to get element "+input[1].getValueAsString()+" of "+size);
				}
			}
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
	
	static class To_String extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return null;
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			return new VariableData.String(((VariableData.Array) getParentVarData()).getValueAsString());
			
		}
		To_String(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		To_String(Point pos, Variable parent) {
			super(pos, parent);
		}
		To_String(Variable parent){
			super(parent);
		}
		
	}
	
	static class Set_Array extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC, Variable.DataType.ARRAY));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			if(((VariableData.Array) parentVarData).dataType == ((VariableData.Array) input[0]).dataType){
				((VariableData.Array) parentVarData).value = ((VariableData.Array) input[0]).value;
			}else{
				throw new Exception("in \""+this.getParentVariable().getFullName()+" : "+this.getSimpleName()+"\"; type mismatch: can't convert from ["+((VariableData.Array) input[0]).dataType+"] to ["+((VariableData.Array) getParentVarData()).dataType+"]");
			}
			return null;
			
		}
		Set_Array(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Set_Array(Point pos, Variable parent) {
			super(pos, parent);
		}
		Set_Array(Variable parent){
			super(parent);
		}
		
	}
	
	static class Get_Array extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return null;
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.ARRAY));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			return getParentVarData();
			
		}
		Get_Array(Point pos, Variable parent, GraphEditor owner) {
			super(pos, parent, owner);
		}
		Get_Array(Point pos, Variable parent) {
			super(pos, parent);
		}
		Get_Array(Variable parent){
			super(parent);
		}
		
	}
	
	@Override
	public void changedUpdate(DocumentEvent e){
		resetVariableData();
	}
	@Override
	public void insertUpdate(DocumentEvent e){
		resetVariableData();
	}
	@Override
	public void removeUpdate(DocumentEvent e){
		resetVariableData();
	}
	
	static class ArrayDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		VArray parent;
		
		 ArrayDocumentFilter(AbstractDocument doc, VArray parent) {
			this.doc = doc;
			this.doc.setDocumentFilter(this);
			this.parent = parent;
		}
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String text,
			AttributeSet attr) throws BadLocationException {
		    super.insertString(fb, offset, text, attr);
		}
		
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
			if(parent.dataType == Variable.DataType.BOOLEAN){
				while(doc.getText(offset, 1) != "," && offset > 0){
					offset++;//TODO
					length++;
				}
			}
		    super.remove(fb, offset, length);
		}
		  
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
			String sb = "";
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if(c == ','){
					if(sb.length() == 0){
						if(!doc.getText(offset-1, 1).equals(",")){
							sb += c;
						}
					}else{
						if(sb.charAt(sb.length()-1) != ','){
							sb += c;
						}
					}
				}else if(parent.dataType == Variable.DataType.STRING){
					sb += c;
				}else if(parent.dataType == Variable.DataType.CHARACTER){
					if(sb.length() == 0){
						if(doc.getText(doc.getLength()-1, 1).equals(",")){
							sb += c;
						}
					}else{
						if(sb.charAt(sb.length()) == ','){
							sb += c;
						}
					}
				}else if(parent.dataType == Variable.DataType.DOUBLE || parent.dataType == Variable.DataType.FLOAT){
					if(Character.isDigit(c)){
						sb += c;
					}else if(c == '.'){
						ArrayList<String> strings = new ArrayList<String>(Arrays.asList(doc.getText(0, doc.getLength()).concat(sb).split(",")));
						if(strings.isEmpty() || !strings.get(strings.size()-1).contains(".")){
							sb += c;
						}
					}
				}else if(parent.dataType == Variable.DataType.BOOLEAN){
					if(sb.length() == 0){
						if(offset == 0 || doc.getText(offset-1, 1).equals(",")){
							if(c == 't'){
								sb += "true";
							}else{
								sb += "false";
							}
						}
					}else{
						if(sb.charAt(sb.length()) == ','){
							if(c == 't'){
								sb += "true";
							}else{
								sb += "false";
							}
						}
					}
				}else{
					if(Character.isDigit(c)){
						sb += c;
					}
				}
			}
			text = sb;
			super.replace(fb, offset, length, text, attrs);
		}
	}
}