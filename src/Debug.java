import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;

public class Debug{
	
	private static ArrayList<Executable> stack;
	private static boolean isStepping = false;
	private static Variable.DataType waitingForInput = null;
	public static Console console;
	private static RunMode mode;
	
	private enum RunMode{
		RUN,FAST,SLOW
	}
	
	public static boolean isStepping() {
		return isStepping;
	}
	public static Variable.DataType waitingForInput() {
		return waitingForInput;
	}
	
	private static void startStep(){
		System.out.println("Start Stepping");
		Main.addVar.setEnabled(false);
		for(Variable var : Main.variables){
			var.setEditable(false);
			var.resetVariableData();
		}
		if(console == null){
			console = new Console();
		}
		console.clear();
		console.setVisible(true);
		Main.window.requestFocus();
		Main.panel.requestFocusInWindow();
		isStepping = true;
		stack = new ArrayList<Executable>(Arrays.asList(Main.entryPoint));
		if(mode != RunMode.RUN)
			stack.get(0).setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				System.out.println(o);
				Executable o2 = ((Executable) o);
				o2.resetActiveNode();
				o2.workingData = new ArrayList<VariableData>();
				System.out.println(o.getClass().getName()+" : "+((Executable) o).workingData);
				
			}
		}
		if(mode == RunMode.RUN){
			step();
			
		}
	}

	
	protected static void exit() {
		isStepping = false;
		Main.addVar.setEnabled(true);
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				((Executable) o).setBorder(null);
			}
		}
		for(Variable var : Main.variables){
			var.setEditable(true);
		}
	}
	
	
	private static boolean moveUpStack(){
		
		if(stack.isEmpty()){
			return false;
		}else if(getTop().getInputNodes().isEmpty()){
			return false;
		}
		System.out.println("try to go up from "+getTop());
		System.out.println("activeNode "+getTop().getActiveNode());
		System.out.println("inputs "+getTop().getInputNodes().size());
		if(getTop().getActiveNode() >= getTop().getInputNodes().size()){
			System.out.println("failed: activeNode > input nodes");
			return false;
		}
		
		ArrayList<Node> parents = getTop().getInputNodes().get(getTop().getActiveNode()).parents;
		System.out.println("parents "+parents);
		
		if(parents.isEmpty()){
			System.out.println("falied: parents is empty; exiting...");
			exit();
			return false;
		}
		Executable next = (Executable) parents.get(0).parentObject;
		getTop().incrementActiveNode();
		
		next.workingData.clear();
		next.resetActiveNode();
		
		if(mode != RunMode.RUN)
			getTop().setBorder(null);
		
		stack.add(next);
		
		if(mode != RunMode.RUN)
			next.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		
		return true;
	}
	
	private static void moveDownStack(){
		if(stack.isEmpty()){
			System.out.println("stack is empty; exiting...");
			exit();
			return;
		}
		System.out.println("executing "+getTop().getClass().getName());
		System.out.print(getTop().getClass().getName()+" workingData = ");
		for(VariableData var : getTop().workingData){
			System.out.print(var.getValueAsString());
		}
		System.out.println();
		VariableData[] array = new VariableData[getTop().workingData.size()];
		array = getTop().workingData.toArray(array);
		VariableData execute = getTop().execute(array);
		
		if(getTop().getClass() == Console.getStr.class){
			waitingForInput = ((Console.getStr) getTop()).getDataType();
			System.out.println("isWaitingForInput = "+waitingForInput);
			console.requestFocus();
			console.input.requestFocusInWindow();
		}else{
			moveDownStack2(execute);
		}
	}
	
	public static void moveDownStack2(VariableData execute){
		waitingForInput = null;
		
		if(mode != RunMode.RUN)
			getTop().setBorder(null);
		
		if(stack.size() == 1){
			Executable next = getNext(getTop());
			stack.remove(stack.size()-1);
			if(next != null){
				next.workingData.clear();
				next.resetActiveNode();
				stack.add(next);
			}else{
				exit();
				return;
			}
		}else{
			stack.remove(stack.size()-1);
		}
		if(execute != null){
			System.out.println("add to "+getTop().workingData);
			getTop().workingData.add(execute);
		}
		if(mode != RunMode.RUN){
			getTop().setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
		}else{
			step();
		}
	}

	private static void step(){
		
		if(!moveUpStack())
			moveDownStack();
		else if(mode == RunMode.RUN)
			step();

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
		}else if(o instanceof Logic.Branch){
			children = o.getOutputNodes().get((((Logic.Branch) o).isTrue()) ? 0 : 1).children;
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
		if(isStepping && waitingForInput == null && mode != RunMode.RUN){
			step();
		}
	}
	public static void f1() {
		if(isStepping()){
			exit();
		}else{
			mode = RunMode.RUN;
			startStep();
		}
	}
	public static void f2() {
		/*if(isStepping()){
			exit();
		}else{
			startStep();
		}*/
	}
	public static void f3() {
		if(isStepping()){
			exit();
		}else{
			mode = RunMode.SLOW;
			startStep();
		}
	}
	
}