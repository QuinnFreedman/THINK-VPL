import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VString extends Variable{
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
		
		resetVariableData();
	}
	@Override
	public void resetVariableData(){
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
			return getParentVar().varData;
			
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
			getParentVar().varData = input[0];
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
			((VariableData.String) getParentVar().varData).value = ((VariableData.String) getParentVar().varData).value.concat(((VariableData.String) input[0]).value);
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
			int length = ((VariableData.String) getParentVar().varData).value.length();
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
			String s = ((VariableData.String) getParentVar().varData).value.replaceAll(((VariableData.String) input[0]).value,((VariableData.String) input[1]).value);
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
			char c = ((VariableData.String) getParentVar().varData).value.charAt(((VariableData.Integer) input[0]).value);
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
	
}//9495 lines, 39 files