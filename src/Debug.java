import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;

public class Debug{
	//private static ArrayList<VObject> currentLowest;
	//private static VObject currentHighest = null;
	private static ArrayList<VObject> stack;
	private static int currentNode = 0;
	private static boolean isStepping = false;
	
	private static void startStep(){
		stack = new ArrayList<VObject>(Arrays.asList(Main.entryPoint));
		isStepping = true;
		stack.get(0).setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
		for(VObject o : Main.objects){
			if(o instanceof Args){
				((Args) o).setActiveNode(0);
			}
		}
	}
	
	private static boolean moveUpStack(){
		if(stack.isEmpty()){
			return false;
		}
		
		VObject last = stack.get(stack.size()-1);
		
		if(last instanceof Args){
			
			if(((Args) last).getActiveNode().parents.isEmpty()){
				return false;
			}else{
				stack.add(((Args) last).getActiveNode().parents.get(0).parentObject);
				return true;
			}
			
		}else if(last instanceof Function){
			if(last instanceof PrimativeFunction){
				return false;
			}else{
				
				if(((PrimativeFunction) last).inputNode.parents.isEmpty()){
					return false;
				}else{
					stack.add(((PrimativeFunction) last).inputNode.parents.get(0).parentObject);
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	private static void moveDownStack(){
		
		
		
	}
	
	private static void step(){
		
		
		
		
	}
	
	public static void tab() {
		if(!isStepping){
			startStep();
		}else{
			step();
		}
		
	}
	
}