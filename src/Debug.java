import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;

public class Debug{
	
	private static ArrayList<Executable> stack;
	private static ArrayList<Executable> remember;
	private static boolean isStepping = false;
	private static Variable.DataType waitingForInput = null;
	public static Console console;
	private static RunMode mode;
	
	public static enum RunMode{
		RUN,FAST,SLOW
	}
	
	public static void setRunMode(RunMode rm){
		mode = rm;
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
		remember = new ArrayList<Executable>();
		if(mode != RunMode.RUN){
			stack.get(0).setSelected(true);
			stack.get(0).repaint();
		}
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				System.out.println(o);
				Executable o2 = ((Executable) o);
				o2.resetActiveNode();
				o2.workingData = new ArrayList<VariableData>();
				System.out.println(o.getClass().getName()+" : "+((Executable) o).workingData);
				
			}
		}
		stepForever();
	}
	
	public static void stepForever(){
		if(mode == RunMode.RUN){
			while(step()){};
			
		}
	}
	
	protected static void exit() {
		isStepping = false;
		Main.addVar.setEnabled(true);
		for(VObject o : Main.objects){
			if(o instanceof Executable){
				((Executable) o).setSelected(false);
				((Executable) o).repaint();
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
		
		if(mode != RunMode.RUN){
			getTop().setSelected(false);
			getTop().repaint();
		}
		
		stack.add(next);
		
		if(mode != RunMode.RUN){
			next.setSelected(true);
			next.repaint();
		}
		
		return true;
	}
	
	private static boolean moveDownStack(){
		if(stack.isEmpty()){
			System.out.println("stack is empty; exiting...");
			exit();
			return false;
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
			return false;
		}else{
			return moveDownStack2(execute);
		}
	}
	
	public static boolean moveDownStack2(VariableData execute){
		waitingForInput = null;
		
		if(mode != RunMode.RUN){
			getTop().setSelected(false);
			getTop().repaint();
		}
		
		if(stack.size() == 1){
			Executable next = getNext(getTop());
			stack.remove(stack.size()-1);
			if(next != null){
				if(next instanceof Repeater){
					remember.add(next);
					if(next instanceof Logic.Sequence){
						((Logic.Sequence) next).activeOutNode = 0;
					}
				}
				next.workingData.clear();
				next.resetActiveNode();
				stack.add(next);
			}else{
				if(!remember.isEmpty()){
					stack.add(remember.get(remember.size()-1));
				}else{
					exit();
					return false;
				}
			}
		}else{
			stack.remove(stack.size()-1);
		}
		if(execute != null){
			System.out.println("add to "+getTop().workingData);
			getTop().workingData.add(execute);
		}
		if(mode != RunMode.RUN){
			getTop().setSelected(true);
			getTop().repaint();
		}else{
			//step();
		}
		return true;
	}

	private static boolean step(){
		
		/*System.out.println();
		System.out.println("stack:");
		for(VObject o : stack){
			System.out.println(o.getClass().getSimpleName());
		}
		System.out.println();*/
		
		try{
			
			boolean b;
			
			if(mode == RunMode.FAST){
				b = moveUpStack();
				
				if(!b){
					return moveDownStack();
				}else{
					do{
						b = moveUpStack();
					}while(b);
					return b;
				}
				
			}else{
			
				b = moveUpStack();
				
				if(!b){
					return moveDownStack();
				}else{
					return b;
				}
			}
			
		}catch(Exception e){
			console.post("ERROR: an unexpected error occured"+((stack.isEmpty() && stack.get(stack.size()-1).headerLabel != null) ? "" : " when executing \""+stack.get(stack.size()-1).headerLabel.getText()+"\"")+". The program will now exit.");
			return false;
		}
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
		}else if(o instanceof Logic.While){
			if(((Repeater) o).isContinue()){
				o.resetActiveNode();
				o.workingData = new ArrayList<VariableData>();
				children = o.getOutputNodes().get(0).children;
			}else{
				children = o.getOutputNodes().get(1).children;
				removeFromEnd(remember,o);
			}
		}else if(o instanceof Logic.Sequence){
			if(((Logic.Sequence) o).activeOutNode < o.getOutputNodes().size()){
				children = o.getOutputNodes().get(((Logic.Sequence) o).activeOutNode).children;
				((Logic.Sequence) o).activeOutNode++;
			}else{
				children = new ArrayList<Node>();
				removeFromEnd(remember,o);
			}
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
		if(!isStepping()){
    		Main.panel.requestFocusInWindow();
			mode = RunMode.RUN;
			startStep();
    	}else{
    		setRunMode(RunMode.RUN);
    		stepForever();
    	}

	}
	public static void f2() {
		if(!isStepping()){
    		Main.panel.requestFocusInWindow();
			mode = RunMode.FAST;
			startStep();
    	}else{
    		setRunMode(RunMode.FAST);
    		step();
    	}
	}
	public static void f3() {
    	if(!isStepping()){
    		Main.panel.requestFocusInWindow();
    		mode = RunMode.SLOW;
			startStep();
    	}else{
    		exit();
    	}
	}
	private static void removeFromEnd(ArrayList<Executable> list, Executable o){
		ArrayList<Executable> reverseList = new ArrayList<Executable>(list);
		Collections.reverse(reverseList);
		list.remove(list.size() - 1 - reverseList.indexOf(o));
	}
}