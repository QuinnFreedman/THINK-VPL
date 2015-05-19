import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;

public class Debug{
	
	private static ArrayList<Executable> stack;
	private static boolean isStepping = false;
	
	private static VariableData varData = null;
	
	public static boolean isStepping() {
		return isStepping;
	}
	
	private static void startStep(){
		stack = new ArrayList<Executable>(Arrays.asList(Main.entryPoint));
		isStepping = true;
		stack.get(0).setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				((Executable) o).activeNode = 1;
			}
		}
	}
	
	private static void exit() {
		isStepping = false;
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				((Executable) o).setBorder(null);
			}
		}
	}
	
	
	private static boolean moveUpStack(){
		
		if(stack.isEmpty()){
			return false;
		}else if(getTop().getInputNodes().isEmpty()){
			return false;
		}
		
		if(getTop().activeNode >= getTop().getInputNodes().size()){
			return false;
		}
		
		ArrayList<Node> parents = getTop().getInputNodes().get(getTop().activeNode).parents;
		if(parents.isEmpty()){
			exit();
			return false;
		}
		Executable next = (Executable) parents.get(0).parentObject;
		getTop().activeNode++;
				
		getTop().setBorder(null);
		stack.add(next);
		next.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
		return true;
	}
	
	private static void moveDownStack(){
		if(stack.isEmpty()){
			exit();
			return;
		}
		
		varData = getTop().execute(varData);
		
		getTop().setBorder(null);
		
		if(stack.size() == 1){
			Executable next = getNext(getTop());
			stack.remove(stack.size()-1);
			if(next != null){
				stack.add(next);
			}else{
				exit();
				return;
			}
		}else{
			stack.remove(stack.size()-1);
		}
	
		getTop().setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
	}

	private static void step(){
		
		if(!moveUpStack())
			moveDownStack();

		System.out.println("stack:");
		for(VObject o : stack){
			System.out.println(o.getClass().getSimpleName());
		}
		System.out.println();
		
	}
	
	private static Executable getTop(){
		return stack.get(stack.size()-1);
	}
	
	private static Executable getNext(Executable o){
		ArrayList<Node> children;
		if(o instanceof EntryPoint){
			children = ((EntryPoint) o).startNode.children;
		}else{
			children = o.getOutputNodes().get(0).children;
		}
		
		if(children.isEmpty()){
			return null;
		}else{
			return (Executable) children.get(0).parentObject;
		}
	}
	
	public static void tab() {
		if(!isStepping){
			startStep();
		}else{
			step();
		}
		
	}
	
}