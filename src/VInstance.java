import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class VInstance extends Variable{
	private static final long serialVersionUID = 1L;
	
	Random rng = new Random();
	
	Blueprint parentBlueprint;
	ArrayList<Variable> childVariables = new ArrayList<Variable>();
	ArrayList<VFunction> childFunctions = new ArrayList<VFunction>();
	
	JPanel body;
	
	VInstance(GraphEditor owner, Blueprint bp) {
		super(owner);
		parentBlueprint = bp;
		
		header.remove(valueField);
		fields.remove(valueField);
		valueField = null;
		
		this.dataType = DataType.OBJECT;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		this.nameField.selectAll();
		
		header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.add(Box.createHorizontalGlue());
		body = new JPanel();
		body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));
		
		this.add(body);
		
		for(Variable v : bp.getVariables()){
			addChildVariable(v);
		}
		for(VFunction f : bp.getFunctions()){
			addChildFunction(f);
		}
		
		System.out.println(this.varData);
		
		this.functions.add(new Get(this));
		this.functions.add(new Set(this));
		//this.functions.add(new Get_Name());
		//this.functions.add(new Get_JSON());
	}
	
	VInstance(Blueprint bp) {
		super(null);
		
		parentBlueprint = bp;
		
		this.dataType = DataType.OBJECT;
		
		this.name = parentBlueprint.getName()+"::"+rng.nextInt(Integer.MAX_VALUE);
		
		this.varData = new VariableData.Instance(this);
	}
	
	public void addChildVariable(Variable v){
		if(v.dataType == null || v.isStatic)
			return;
		
		DataType dt = v.dataType;
		Variable v2 = null;
		switch(dt){
		case BOOLEAN:
			v2 = new VBoolean(getOwner());
			((VBoolean) v2).value = ((VBoolean) v).value;
			v2.valueField.setText(Boolean.toString(((VBoolean) v).value));
			break;
		case DOUBLE:
			v2 = new VDouble(getOwner());
			((VDouble) v2).value = ((VDouble) v).value;
			v2.valueField.setText(Double.toString(((VDouble) v).value));
			break;
		case FLOAT:
			v2 = new VFloat(getOwner());
			((VFloat) v2).value = ((VFloat) v).value;
			v2.valueField.setText(Float.toString(((VFloat) v).value));
			break;
		case INTEGER:
			v2 = new VInt(getOwner());
			((VInt) v2).value = ((VInt) v).value;
			v2.valueField.setText(Integer.toString(((VInt) v).value));
			break;
		case STRING:
			v2 = new VString(getOwner());
			((VString) v2).value = ""+((VString) v).value;
			v2.valueField.setText(((VString) v).value);
			break;
		case OBJECT:
			v2 = new VInstance(getOwner(),((VInstance) v).parentBlueprint);
			//((VInstance) v2).value = ((VInstance) v).value;
			//v2.valueField.setText(((VInstance) v).value);
			break;
		default:
			break;
		
		}
		v2.resetVariableData();
		v2.setID(v.getID());
		v2.nameField.setText(v2.getID());
		v2.nameField.setEnabled(false);
		if(v2.valueField != null)
			v2.valueField.setEnabled(false);
		v2.isStatic = false;
		v2.setOriginalVar(v);
		v2.setParentInstance(this);
		
		childVariables.add(v2);
		body.add(v2, Component.LEFT_ALIGNMENT);
	}
	
	public void addChildFunction(VFunction f){
		if(f.isStatic)
			return;
		
		VFunction f2 = new VFunction(getOwner());
		f2.setID(f.getID());
		f2.nameField.setText(f2.getID());
		f2.nameField.setEnabled(false);
		f2.setInput(f.getInput());
		f2.setOutput(f.getOutput());
		f2.editor = f.editor;
		f2.isStatic = false;//f.isStatic();
		f2.setParentInstance(this);
		f2.setOriginalFunc(f);
		f2.setExecuteOnce(f.isEcexuteOnce());
		
		childFunctions.add(f2);
		body.add(f2, Component.LEFT_ALIGNMENT);
	}
	
	public void removeChildFunction(VFunction f){
		for(VFunction f2 : childFunctions){
			if(f2.getID().equals(f.getID())){
				f2.clearChildren();
				childVariables.remove(f2);
				body.remove(f2);
				return;
			}
		}
	}
	
	public void removeChildVariable(Variable v){
		for(Variable v2 : childVariables){
			if(v2.getID().equals(v.getID())){
				v2.clearChildren();
				childVariables.remove(v2);
				body.remove(v2);
				return;
			}
		}
	}
	
	public Variable getVariable(String s){
		for(Variable v : childVariables){
			if(v.getID().equals(s))
				return v;
		}
		return null;
	}
	
	static class Get extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			System.out.println("parentVar = "+getParentVar());
			System.out.println("returning "+getParentVar().varData);
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
	
	@Override
	public void resetVariableData(){
		this.varData = new VariableData.Instance(this);
		for(Variable v : this.childVariables){
			v.resetVariableData();
		}
	}
	
	static class Set extends PrimitiveFunction{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC,DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.GENERIC));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			if(((VInstance) getParentVar()).parentBlueprint != ((VariableData.Instance) input[0]).value.parentBlueprint){
				try {
					throw new Exception();
				} catch (Exception e) {
					Debug.console.post("ERROR: type missmatch");
					e.printStackTrace();
				}
			}else{
				getParentVar().varData = input[0];
				for(int i = 0; i < ((VInstance) getParentVar()).childVariables.size(); i++){
					Variable v = ((VInstance) getParentVar()).childVariables.get(i);
					Variable v2 = ((VariableData.Instance) input[0]).value.childVariables.get(i);
					System.out.println("\""+v.name+"\" : "+v.varData.getValueAsString()+" < "+"\""+v2.name+"\" : "+v2.varData.getValueAsString());
					v.varData = v2.varData;
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
	
	static class Get_Name extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			String s = ((VariableData.Instance) input[0]).value.getID()
					+":"
					+((VInstance) ((VariableData.Instance) input[0]).value).parentBlueprint.getName()
					+"@"
					+java.lang.System.identityHashCode(((VariableData.Instance) input[0]).value);
			
			return new VariableData.String(s);
		}
		Get_Name(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.defaultActiveNode = 0;
		}
		Get_Name(){
			super();
		}
		
	}
	
	static class Get_JSON extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.OBJECT));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.String(((VariableData.Instance) input[0]).getJSON());
		}
		Get_JSON(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.defaultActiveNode = 0;
		}
		Get_JSON(){
			super();
		}
		
	}
}