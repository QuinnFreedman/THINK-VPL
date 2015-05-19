import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;

public class VFloat extends Variable{
	private static final long serialVersionUID = 1L;
	static final String name = "Int";
	static int idCounter = 0;
	float value = 0;
	VFloat(){
		super();
		this.dataType = DataType.FLOAT;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new VDouble.DoubleDocumentFilter((AbstractDocument) valueField.getDocument());
		
		this.valueField.setText(Float.toString(value));
		
		//this.functions = new ArrayList<Class<? extends PrimitiveFunction>>();
		//this.functions.add(set.class);
		//this.functions.add(subtractFrom.class);
		this.functions.add(new Get());
		this.functions.add(new Set());
		this.functions.add(new Add_To());
		this.functions.add(new Multiply_By());
	}
	
	@Override
	protected void setValue(String s){
		value = Float.parseFloat(s);
		valueField.getDocument().removeDocumentListener(this);
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.FLOAT));
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.FLOAT));
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.FLOAT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData... input){
			((VariableData.Float) getParentVar().varData).value += ((VariableData.Float) input[0]).value;
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
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.FLOAT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		@Override
		public VariableData execute(VariableData... input){
			((VariableData.Float) getParentVar().varData).value *= ((VariableData.Float) input[0]).value;
			return null;
		}
		Multiply_By(Point pos, Variable parent) {
			super(pos, parent);
		}
		Multiply_By(){
			super();
		}
		
	}
}