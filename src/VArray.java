import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VArray extends Variable{
	private static final long serialVersionUID = 1L;
	static int idCounter = 0;
	String value;
	ArrayList<VariableData.Integer> intData;
	ArrayList<VariableData.Float> floatData;
	ArrayList<VariableData.Double> doubleData;
	ArrayList<VariableData.Boolean> booleanData;
	ArrayList<VariableData.String> stringData;
	ArrayList<VariableData.Character> charData;
	protected VArray getThisVariable(){
		return this;
	}
	VArray(DataType dt,GraphEditor owner){
		super(owner);
		this.dataType = dt;
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
		
		resetVariableData();
	}
	@Override
	public void resetVariableData(){
		ArrayList<String> listOfStrings = new ArrayList<String>(Arrays.asList(value.split(",")));
		if(this.dataType == DataType.INTEGER){
			this.intData = new ArrayList<VariableData.Integer>();
			for(String s : listOfStrings){
				if(s.length() != 0)
					this.intData.add(new VariableData.Integer(Integer.parseInt(s)));
			}
		}else if(this.dataType == DataType.DOUBLE){
			this.doubleData = new ArrayList<VariableData.Double>();
			for(String s : listOfStrings){
				if(s.length() != 0)
					this.doubleData.add(new VariableData.Double(Double.parseDouble(s)));
			}
		}else if(this.dataType == DataType.FLOAT){
			this.floatData = new ArrayList<VariableData.Float>();
			for(String s : listOfStrings){
				if(s.length() != 0)
					this.floatData.add(new VariableData.Float(Float.parseFloat(s)));
			}
		}else if(this.dataType == DataType.BOOLEAN){
			this.booleanData = new ArrayList<VariableData.Boolean>();
			for(String s : listOfStrings){
				if(s.length() != 0)
					this.booleanData.add(new VariableData.Boolean(Boolean.parseBoolean(s)));
			}
		}else if(this.dataType == DataType.STRING){
			this.stringData = new ArrayList<VariableData.String>();
			for(String s : listOfStrings){
				this.stringData.add(new VariableData.String(s));
			}
		}else if(this.dataType == DataType.CHARACTER){
			this.charData = new ArrayList<VariableData.Character>();
			for(String s : listOfStrings){
				this.charData.add(new VariableData.Character((s.length() == 0) ? ' ' : s.charAt(0)));
			}
		}
	}
	
	@Override
	protected void setValue(String s){
		value = s;
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public void resetActiveNode() {
			activeNode = 0;
		};
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(getParentVar().dataType));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			switch(getParentVar().dataType){
			case BOOLEAN:
				return ((VArray) getParentVar()).booleanData.get(((VariableData.Integer) input[0]).value);
			case CHARACTER:
				return ((VArray) getParentVar()).charData.get(((VariableData.Integer) input[0]).value);
			case DOUBLE:
				return ((VArray) getParentVar()).doubleData.get(((VariableData.Integer) input[0]).value);
			case FLOAT:
				return ((VArray) getParentVar()).floatData.get(((VariableData.Integer) input[0]).value);
			case INTEGER:
				return ((VArray) getParentVar()).intData.get(((VariableData.Integer) input[0]).value);
			case STRING:
				return ((VArray) getParentVar()).stringData.get(((VariableData.Integer) input[0]).value);
			default:
				return null;
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
			switch(getParentVar().dataType){
			case BOOLEAN:
				return new VariableData.Integer(((VArray) getParentVar()).booleanData.size());
			case CHARACTER:
				return new VariableData.Integer(((VArray) getParentVar()).charData.size());
			case DOUBLE:
				return new VariableData.Integer(((VArray) getParentVar()).doubleData.size());
			case FLOAT:
				return new VariableData.Integer(((VArray) getParentVar()).floatData.size());
			case INTEGER:
				return new VariableData.Integer(((VArray) getParentVar()).intData.size());
			case STRING:
				return new VariableData.Integer(((VArray) getParentVar()).stringData.size());
			default:
				return null;
			}
			
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,getParentVar().dataType));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			switch(getParentVar().dataType){
			case BOOLEAN:
				((VArray) getParentVar()).booleanData.add((VariableData.Boolean) input[0]);
				return null;
			case CHARACTER:
				((VArray) getParentVar()).charData.add((VariableData.Character) input[0]);
				return null;
			case DOUBLE:
				((VArray) getParentVar()).doubleData.add((VariableData.Double) input[0]);
				return null;
			case FLOAT:
				((VArray) getParentVar()).floatData.add((VariableData.Float) input[0]);
				return null;
			case INTEGER:
				((VArray) getParentVar()).intData.add((VariableData.Integer) input[0]);
				return null;
			case STRING:
				((VArray) getParentVar()).stringData.add((VariableData.String) input[0]);
				return null;
			default:
				return null;
			}
			
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
		public VariableData execute(VariableData[] input){
			switch(getParentVar().dataType){
			case BOOLEAN:
				((VArray) getParentVar()).booleanData.remove(((VariableData.Integer) input[0]).value);
				return null;
			case CHARACTER:
				((VArray) getParentVar()).charData.remove(((VariableData.Integer) input[0]).value);
				return null;
			case DOUBLE:
				((VArray) getParentVar()).doubleData.remove(((VariableData.Integer) input[0]).value);
				return null;
			case FLOAT:
				((VArray) getParentVar()).floatData.remove(((VariableData.Integer) input[0]).value);
				return null;
			case INTEGER:
				((VArray) getParentVar()).intData.remove(((VariableData.Integer) input[0]).value);
				return null;
			case STRING:
				((VArray) getParentVar()).stringData.remove(((VariableData.Integer) input[0]).value);
				return null;
			default:
				return null;
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
	
	static class Set extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,Variable.DataType.INTEGER,getParentVar().dataType));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			switch(getParentVar().dataType){
			case BOOLEAN:
				((VArray) getParentVar()).booleanData.set(((VariableData.Integer) input[0]).value, (VariableData.Boolean) input[1]);
				return null;
			case CHARACTER:
				((VArray) getParentVar()).charData.set(((VariableData.Integer) input[0]).value, (VariableData.Character) input[1]);
				return null;
			case DOUBLE:
				((VArray) getParentVar()).doubleData.set(((VariableData.Integer) input[0]).value, (VariableData.Double) input[1]);
				return null;
			case FLOAT:
				((VArray) getParentVar()).floatData.set(((VariableData.Integer) input[0]).value, (VariableData.Float) input[1]);
				return null;
			case INTEGER:
				((VArray) getParentVar()).intData.set(((VariableData.Integer) input[0]).value, (VariableData.Integer) input[1]);
				return null;
			case STRING:
				((VArray) getParentVar()).stringData.set(((VariableData.Integer) input[0]).value, (VariableData.String) input[1]);
				return null;
			default:
				return null;
			}
			
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
	
	static class ArrayDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		VArray parent;
		
		public ArrayDocumentFilter(AbstractDocument doc, VArray parent) {
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
						if(sb.charAt(sb.length()) != ','){
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