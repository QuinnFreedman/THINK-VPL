import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class ContextualPointer extends Executable{
	private static final long serialVersionUID = 1L;

	ContextualPointer(Point pos, GraphEditor owner){
		super(pos,owner);
		this.color = Main.colors.get(Variable.DataType.OBJECT);
		
		if(!(owner instanceof InstantiableBlueprint)){
			try{
				throw(new IllegalArgumentException());
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public ArrayList<Variable.DataType> getOutputs() {
		return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.OBJECT));
	}
	
	@Override
	public String getSimpleName() {
		return "THIS";
	}
	
	@Override
	public VariableData execute(VariableData[] inputs) {
		VariableData.Instance output;
		
		if(owner instanceof InstantiableBlueprint){
			output = (VariableData.Instance) ((InstantiableBlueprint) owner).getWorkingInstance().varData;
		}else{
			try{
				throw(new IllegalArgumentException());
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			output = null;
		}
		
		return output;	
	}
}