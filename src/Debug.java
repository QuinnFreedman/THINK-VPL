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
				o2.outputData = new ArrayList<VariableData>();
				o2.hasExecuted = false;
				System.out.println(o.getClass().getName()+" : "+((Executable) o).workingData);
				
			}
		}
		for(VFunction f : Main.functions){
			for(VObject o : f.editor.getObjects()){
				if(o instanceof Executable){
					System.out.println(o);
					Executable o2 = ((Executable) o);
					o2.resetActiveNode();
					o2.workingData = new ArrayList<VariableData>();
					o2.outputData = new ArrayList<VariableData>();
					o2.hasExecuted = false;
					System.out.println(o.getClass().getName()+" : "+((Executable) o).workingData);
					
				}
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
		if(getTop().getActiveNode() >= getTop().getInputNodes().size() || (getTop().executeOnce && getTop().hasExecuted)){
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
		if(!(next instanceof FunctionEditor.FunctionIO && ((FunctionEditor.FunctionIO) next).mode == FunctionEditor.FunctionIO.Mode.INPUT)){
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
		}else{
			System.out.println(next.getOutputNodes().indexOf(
									parents.get(0)
									));
			getTop().workingData.add(
					next.outputData.get(
							next.getOutputNodes().indexOf(
									parents.get(0)
									)
							-((next.getOutputNodes().get(0).dataType == Variable.DataType.GENERIC) ? 1 : 0)
							)
					);
			getTop().incrementActiveNode();
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
		
		VariableData execute;
		if(getTop().executeOnce && getTop().hasExecuted){
			execute = getTop().workingData.get(0);
		}else{
			VariableData[] array = new VariableData[getTop().workingData.size()];
			array = getTop().workingData.toArray(array);
			execute = getTop().execute(array);
			getTop().hasExecuted = true;
		}
		
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
		
		if(!(getTop() instanceof FunctionEditor.FunctionIO)){
			getTop().outputData = new ArrayList<VariableData>(Arrays.asList(execute));
		}else if(((FunctionEditor.FunctionIO) getTop()).mode == FunctionEditor.FunctionIO.Mode.OUTPUT){
			((FunctionEditor) ((FunctionEditor.FunctionIO) getTop()).owner).parent.currentlyExecuting.outputData = 
					new ArrayList<VariableData>(Arrays.asList(execute));
		}
		
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
			if(getTop() instanceof UserFunc){
				UserFunc f = (UserFunc) getTop();
				stack.remove(stack.size()-1);
				stack.add(f.getParentVar().editor.outputObject);
				f.getParentVar().editor.outputObject.resetActiveNode();
				f.getParentVar().editor.outputObject.outputData = new ArrayList<VariableData>();
				f.getParentVar().editor.outputObject.workingData = new ArrayList<VariableData>();
				f.getParentVar().editor.inputObject.outputData = new ArrayList<VariableData>(f.workingData);
				f.getParentVar().editor.parent.currentlyExecuting = f;
				f.outputData = new ArrayList<VariableData>();
			}else{
				stack.remove(stack.size()-1);
			}
		}
		if(execute != null){
			System.out.println("add to "+getTop().workingData);
			getTop().workingData.add(execute);
		}
		if(mode != RunMode.RUN){
			getTop().setSelected(true);
			getTop().repaint();
		}
		
		return true;
	}

	private static boolean step(){
		
		System.out.println();
		System.out.println("stack:");
		for(VObject o : stack){
			System.out.println(o.getClass().getSimpleName());
		}
		System.out.println();
		
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
			//console.post("ERROR: an unexpected error occured"+((!stack.isEmpty() && stack.get(stack.size()-1).headerLabel != null) ? "" : " when executing \""+stack.get(stack.size()-1).headerLabel.getText()+"\"")+". The program will now exit.");
			e.printStackTrace(System.err);
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
				o.outputData = new ArrayList<VariableData>();
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
		}else if(o instanceof UserFunc){
			FunctionEditor.FunctionIO inputObj = ((UserFunc) o).getParentVar().editor.inputObject;
			inputObj.outputData = new ArrayList<VariableData>(o.workingData);
			System.out.println(inputObj.outputData);
			((UserFunc) o).getParentVar().currentlyExecuting = (UserFunc) o;
			return inputObj;
		}else if(o instanceof FunctionEditor.FunctionIO && ((FunctionEditor.FunctionIO) o).mode == FunctionEditor.FunctionIO.Mode.OUTPUT){
			children = ((FunctionEditor) ((FunctionEditor.FunctionIO) o).owner).parent.currentlyExecuting.getOutputNodes().get(0).children;
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