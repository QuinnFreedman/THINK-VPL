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
	VDouble(){
		super();
		this.dataType = DataType.DOUBLE;
		varData = new VariableData.Double();
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new DoubleDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Double.toString(value));
		
		this.functions.add(new Get());
		this.functions.add(new Set());
		this.functions.add(new Add_To());
		this.functions.add(new Multiply_By());
	}
	
	@Override
	protected void setValue(String s){
		value = Double.parseDouble(s);
		if(varData != null)
			((VariableData.Double) varData).value = value;
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
		public VariableData execute(VariableData... input){
			return getParentVar().varData;
		}
		Get(Point pos, Variable parent) {
			super(pos, parent);
		}
		Get(){
			super();
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
		public VariableData execute(VariableData... input){
			getParentVar().varData = input[0];
			return null;
		}
		Set(Point pos, Variable parent) {
			super(pos, parent);
		}
		Set(){
			super();
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
		public VariableData execute(VariableData... input){
			((VariableData.Double) getParentVar().varData).value += ((VariableData.Double) input[0]).value;
			return null;
		}
		Add_To(Point pos, Variable parent) {
			super(pos, parent);
		}
		Add_To(){
			super();
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
		public VariableData execute(VariableData... input){
			((VariableData.Double) getParentVar().varData).value *= ((VariableData.Double) input[0]).value;
			return null;
		}
		Multiply_By(Point pos, Variable parent) {
			super(pos, parent);
		}
		Multiply_By(){
			super();
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
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if(Character.isDigit(c) || c == '.'){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
}