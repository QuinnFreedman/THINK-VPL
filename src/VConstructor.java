import java.awt.Point;
import java.util.ArrayList;

public class VConstructor extends UserFunc{

	VConstructor(Point pos, InstantiableBlueprint parent, GraphEditor owner) {
		super(pos, parent, owner);
		
	}
	
	@Override
	public VariableData execute(VariableData[] inputs){
		
		System.err.println("executing");
		((InstantiableBlueprint) parentFunc).setWorkingInstance(new VInstance(Main.mainBP));
		
		return null;
	}
	
}