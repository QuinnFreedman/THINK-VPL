import java.util.ArrayList;

public interface FunctionOverseer{
	public FunctionEditor.FunctionIO getInputObject();
	public FunctionEditor.FunctionIO getOutputObject();
	
	public void removeOutput(int i);
	public void removeInput(int i);
	public void addOutput(Variable.DataType dataType);
	public void addInput(Variable.DataType dataType);
	
	public ArrayList<Variable.DataType> getInput();
	public ArrayList<Variable.DataType> getOutput();
	
	public void removeChild(UserFunc f);
	public void addChild(UserFunc f);
	public void clearChildren();
	
	public void setCurrentlyExecuting(UserFunc f);
	public UserFunc getCurrentlyExecuting();
	
	public boolean isStatic();
	
	void setInput(ArrayList<Variable.DataType> dt);
	void setOutput(ArrayList<Variable.DataType> dt);
	public boolean isEcexuteOnce();
	
	public GraphEditor getEditor();
	
	public VInstance getWorkingInstance();
}