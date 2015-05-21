import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VBoolean extends Variable{
	private static final long serialVersionUID = 1L;
	static final String name = "Int";
	static int idCounter = 0;
	boolean value = false;
	VBoolean(){
		super();
		this.dataType = DataType.BOOLEAN;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new BooleanDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Boolean.toString(value));
		
		this.functions.add(new Get());
		this.functions.add(new Set());
		this.functions.add(new Toggle());

		resetVariableData();
	}
	@Override
	public void resetVariableData(){
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
			((VariableData.Boolean) getParentVar().varData).value = !(((VariableData.Boolean) getParentVar().varData).value);
			return null;
		}
		Toggle(Point pos, Variable parent) {
			super(pos, parent);
		}
		Toggle(){
			super();
		}
		
	}
	
	static class BooleanDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		
		public BooleanDocumentFilter(AbstractDocument doc) {
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