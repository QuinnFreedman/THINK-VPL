import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VDouble extends Variable{
	private static final long serialVersionUID = 1L;
	static final String name = "Int";
	static int idCounter = 0;
	double value = 0;
	VDouble(GraphEditor owner){
		super(owner);
		this.dataType = DataType.DOUBLE;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new DoubleDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Double.toString(value));
		
		this.functions.add(new Get(this));
		this.functions.add(new Set(this));
		this.functions.add(new Add_To(this));
		this.functions.add(new Multiply_By(this));

		resetVariableData();
	}
	@Override
	public void resetVariableData(){
		this.varData = new VariableData.Double(value);
	}
	
	@Override
	protected void setValue(String s){
		if(s.length() > 0 && !s.equals("-")){
			value = Double.parseDouble(s);
		}else{
			value = 0;
		}
		resetVariableData();
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.DOUBLE));
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.DOUBLE));
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
	static class Add_To extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.DOUBLE));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Double) getParentVar().varData).value += ((VariableData.Double) input[0]).value;
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.DOUBLE));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData[] input){
			((VariableData.Double) getParentVar().varData).value *= ((VariableData.Double) input[0]).value;
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
	static class DoubleDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		
		public DoubleDocumentFilter(AbstractDocument doc) {
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
			String sb = "";
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if(Character.isDigit(c) || (c == '-' && doc.getLength() == 0 && i == 0) || (c == '.' && !(sb.contains(".") || doc.getText(0, doc.getLength()).contains(".")))){
					sb += c;
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
}