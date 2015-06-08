import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Debug{
	
	private static ArrayList<Executable> stack;
	private static ArrayList<Executable> remember;
	private static boolean isStepping = false;
	private static Variable.DataType waitingForInput = null;
	public static Console console;
	private static RunMode mode;
	
	static Status running;
	
	public static enum RunMode{
		RUN,FAST,SLOW
	}
	
	public static void setRunMode(RunMode rm){
		mode = rm;
	}
	
	public static RunMode getRunMode(){
		return mode;
	}
	
	public static boolean isStepping() {
		return isStepping;
	}
	public static Variable.DataType waitingForInput() {
		return waitingForInput;
	}
	
	private static void startStep(){
		System.out.println("Start Stepping");
		for(Blueprint bp : Main.blueprints){
			bp.addVar.setEnabled(false);
			for(Variable var : bp.getVariables()){
				var.setEditable(false);
				var.resetVariableData();
			}
		}
		if(console == null){
			console = new Console();
		}
		console.clear();
		console.setVisible(true);
		console.setAlwaysOnTop(true);
		
		for(Module m : Main.modules){
			m.run();
		}
		
		Main.window.requestFocus();
		Main.getOpenClass().getPanel().requestFocusInWindow();
		isStepping = true;
		stack = new ArrayList<Executable>(Arrays.asList(Main.entryPoint));
		remember = new ArrayList<Executable>();
		if(mode != RunMode.RUN){
			stack.get(0).setSelected(true);
			stack.get(0).repaint();
		}
		for(Blueprint bp : Main.blueprints){
			for(VObject o : bp.getObjects()){
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
			for(VFunction f : bp.getFunctions()){
				if(f == null || f.editor == null || f.editor.getObjects() == null){
					System.out.println("!WARNING: null Pointer");
				}
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
		}
		stepForever();
	}
	
	public static void stepForever(){
		if(mode == RunMode.RUN){
			Thread t = new Thread() {
				public void run() {
					while(step()){};
			}};
	        t.start();
			
		}
	}
	
	protected static void exit() {
		System.out.println("exiting");
		isStepping = false;
		for(Blueprint bp : Main.blueprints){
			bp.addVar.setEnabled(true);
			for(VObject o : bp.getObjects()){
				if(o instanceof Executable){
					((Executable) o).setSelected(false);
					((Executable) o).repaint();
				}
			}
			for(Variable var : bp.getVariables()){
				var.setEditable(true);
			}
		}
		if(running != null){
			running.setVisible(false);
		}
		console.setAlwaysOnTop(false);
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
		System.out.println("next = "+next);
		if(getTop() instanceof FunctionEditor.FunctionIO && !((FunctionEditor.FunctionIO) getTop()).getOverseer().isStatic()){
			System.out.println("next is non-static function :: changing all primitive funcs");
			FunctionOverseer fo = ((FunctionEditor.FunctionIO) getTop()).getOverseer();
			for(VObject e : fo.getEditor().getObjects()){
				if(e instanceof PrimitiveFunction &&
						(((PrimitiveFunction) e).getParentVar().getOwner() == ((VFunction) fo).getOwner() ||
						(((PrimitiveFunction) e).getParentVar().parentInstance != null && ((PrimitiveFunction) e).getParentVar().parentInstance.parentBlueprint == ((VFunction) fo).getOwner()))
					){
					System.out.println(((PrimitiveFunction) e));
					System.out.println("	parentInstance = "+((PrimitiveFunction) e).getParentVar().parentInstance);
					((PrimitiveFunction) e).setParentVar(
							VFunction.getVariable( ((VFunction) ((VFunction) fo).getCurrentlyExecuting().getParentVar()).parentInstance.childVariables,((PrimitiveFunction) e).getParentVar().getID())
						);
					System.out.println("	parentInstance = "+((PrimitiveFunction) e).getParentVar().parentInstance);
				}
			}
		}
		if(!(next instanceof FunctionEditor.FunctionIO && ((FunctionEditor.FunctionIO) next).mode == FunctionEditor.FunctionIO.Mode.INPUT)){
			getTop().incrementActiveNode();
			
			if(!next.executeOnce && next.hasExecuted){
				next.workingData.clear();
				next.resetActiveNode();
			}
			
			if(mode != RunMode.RUN){
				getTop().setSelected(false);
				getTop().repaint();
			}
			
			if(next instanceof UserFunc){
				((UserFunc) next).getGrandparent().setCurrentlyExecuting(((UserFunc) next));
			}//TODO
			
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
	//EXECUTE
		System.out.println("executing "+getTop().getClass().getName());
		System.out.print(getTop().getClass().getName()+" workingData = ");
		for(VariableData var : getTop().workingData){
			System.out.print(var.getValueAsString());
		}
		System.out.println();
		System.out.println(getTop().getClass().getName()+" executeOnce = "+getTop().executeOnce+"; hasExecuted = "+getTop().hasExecuted);
		
		VariableData execute;
		if(getTop().executeOnce && getTop().hasExecuted){
			execute = null;//getTop().outputData;
		}else{
			VariableData[] array = new VariableData[getTop().workingData.size()];
			array = getTop().workingData.toArray(array);
			execute = getTop().execute(array);
		}
		
		if(getTop().getClass() == Console.getStr.class){
			waitingForInput = ((Console.getStr) getTop()).getDataType();
			System.out.println("isWaitingForInput = "+waitingForInput);
			console.requestFocus();
			console.input.requestFocusInWindow();
			running.pause();
			return false;
		}else{
			return moveDownStack2(execute);
		}
	}
	
	public static boolean moveDownStack2(VariableData execute){
		waitingForInput = null;
		
	//SET OUTPUT DATA
		ArrayList<VariableData> multiExecute = null;
		if(!(getTop() instanceof FunctionEditor.FunctionIO)){
			if(execute != null){
				getTop().outputData = new ArrayList<VariableData>(Arrays.asList(execute));
			}else{
				if(getTop() instanceof UserFunc){
					System.out.println("execute == null, getTop().outputData = "+getTop().outputData);
					multiExecute = getTop().outputData;
				}
			}
		}else if(((FunctionEditor.FunctionIO) getTop()).mode == FunctionEditor.FunctionIO.Mode.OUTPUT){
			System.out.println("overseer = "+((FunctionEditor.FunctionIO) getTop()).getOverseer());
			System.out.println("currently executing = "+((FunctionEditor.FunctionIO) getTop()).getOverseer().getCurrentlyExecuting());
			
			((FunctionEditor.FunctionIO) getTop()).getOverseer().getCurrentlyExecuting().outputData = 
					new ArrayList<VariableData>(Arrays.asList(execute));
		}
		
		if(mode != RunMode.RUN){
			getTop().setSelected(false);
			getTop().repaint();
		}

		getTop().hasExecuted = true;
		
	//FIND NEXT
		if(stack.size() == 1){
			
			Executable next = getNext(getTop());
			System.out.println("next = "+((next == null) ? "null" : next.getClass().getName()));
			
			if(next instanceof UserFunc){
				((UserFunc) next).getGrandparent().setCurrentlyExecuting(((UserFunc) next));
			}
			
			if(next instanceof FunctionEditor.FunctionIO && !((FunctionEditor.FunctionIO) next).getOverseer().isStatic()){
				System.out.println("next is non-static function :: changing all primitive funcs");
				FunctionOverseer fo = ((FunctionEditor.FunctionIO) next).getOverseer();
				for(VObject e : fo.getEditor().getObjects()){
					if(e instanceof PrimitiveFunction &&
							(((PrimitiveFunction) e).getParentVar().getOwner() == ((VFunction) fo).getOwner() ||
							(((PrimitiveFunction) e).getParentVar().parentInstance != null && ((PrimitiveFunction) e).getParentVar().parentInstance.parentBlueprint == ((VFunction) fo).getOwner()))
						){
						System.out.println(((PrimitiveFunction) e));
						System.out.println("	parentInstance = "+((PrimitiveFunction) e).getParentVar().parentInstance);
						((PrimitiveFunction) e).setParentVar(
								VFunction.getVariable( ((VFunction) ((VFunction) fo).getCurrentlyExecuting().getParentVar()).parentInstance.childVariables,((PrimitiveFunction) e).getParentVar().getID())
							);
						System.out.println("	parentInstance = "+((PrimitiveFunction) e).getParentVar().parentInstance);
					}
				}
			}
			
			stack.remove(stack.size()-1);
			if(next != null){
				next.hasExecuted = false;
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
			if(getTop() instanceof UserFunc && !(getTop().executeOnce && getTop().hasExecuted)){
				UserFunc f = (UserFunc) getTop();
				stack.remove(stack.size()-1);
				stack.add(f.getParentVar().getOutputObject());
				f.getParentVar().getOutputObject().resetActiveNode();
				f.getParentVar().getOutputObject().outputData = new ArrayList<VariableData>();
				f.getParentVar().getOutputObject().workingData = new ArrayList<VariableData>();
				f.getParentVar().getInputObject().outputData = new ArrayList<VariableData>(f.workingData);
				f.outputData = new ArrayList<VariableData>();
			}else{
				stack.remove(stack.size()-1);
			}
		}
		if(execute != null){
			System.out.println("add to "+getTop().workingData);
			getTop().workingData.add(execute);
		}else if(multiExecute != null){
			getTop().workingData = new ArrayList<VariableData>(multiExecute);
		}
		if(mode != RunMode.RUN){
			getTop().setSelected(true);
			getTop().repaint();
		}
		
		return true;
	}

	private static boolean step(){
		
		if(!isStepping){
			System.out.println("ABORTED");
			console.post("ERROR: program aborted by user");
			return false;
		}
		
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
			String s = "ERROR: an unexpected error occured";
			if(stack != null && !stack.isEmpty() && stack.get(stack.size()-1) != null && stack.get(stack.size()-1).headerLabel != null){
				s += " when executing \""+stack.get(stack.size()-1).headerLabel.getText()+"\"";
			}
			s += ". The program will now exit.";
			console.post(s);
			e.printStackTrace(System.err);
			exit();
			return false;
		}
	}
	
	private static Executable getTop(){
		return stack.get(stack.size()-1);
	}
	
	private static Executable getNext(Executable o){
		System.out.println("getNext "+o.getClass().getName());
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
			System.out.println("userfunc going to fIO");
			FunctionEditor.FunctionIO inputObj = ((UserFunc) o).getParentVar().getInputObject();
			inputObj.outputData = new ArrayList<VariableData>(o.workingData);
			System.out.println("inputObj.outputData = "+inputObj.outputData);
			((UserFunc) o).getGrandparent().setCurrentlyExecuting((UserFunc) o);
			return inputObj;
			
		}else if(o instanceof FunctionEditor.FunctionIO && ((FunctionEditor.FunctionIO) o).mode == FunctionEditor.FunctionIO.Mode.OUTPUT){
			children = ((FunctionEditor.FunctionIO) o).getOverseer().getCurrentlyExecuting().getOutputNodes().get(0).children;
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
		if(running == null){
			running = new Status(Main.mainBP);
		}else{
			running.setVisible(true);
		}
		
		if(!isStepping()){
    		Main.blueprints.get(0).getPanel().requestFocusInWindow();
			mode = RunMode.RUN;
			startStep();
    	}else{
    		setRunMode(RunMode.RUN);
    		stepForever();
    	}
	}
	public static void f2() {
		if(!isStepping()){
			Main.blueprints.get(0).getPanel().requestFocusInWindow();
			mode = RunMode.FAST;
			startStep();
    	}else{
    		setRunMode(RunMode.FAST);
    		step();
    	}
	}
	public static void f3() {
    	if(!isStepping()){
    		Main.blueprints.get(0).getPanel().requestFocusInWindow();
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
	public static class Status extends VObject{
		private static final long serialVersionUID = 1L;
		private ImageIcon run;
		private ImageIcon paused;
		
		public void pause(){
			headerLabel.setIcon(paused);
		}
		
		public void unpause(){
			headerLabel.setIcon(run);
		}
		
		@Override
		public Dimension getSize(){
			return new Dimension(Math.max(60,this.getPreferredSize().width),
					30);
		}
		
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2.setColor(new Color(20,20,20,127));
		    g2.fill(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 10, 10));
		    g2.setPaint(Color.black);
		}
		
		Status(GraphEditor owner){
			super(owner);
			URL url = null;
			try{
				url = Main.class.getResource("/images/loading2.gif");
			}catch (Exception e){
				e.printStackTrace();
			}
			run = new ImageIcon(url);
			
			try{
				url = Main.class.getResource("/images/loading_paused.png");
			}catch (Exception e){
				e.printStackTrace();
			}
			paused = new ImageIcon(url);
			
			headerLabel = new JLabel("Running...", run, JLabel.LEADING);
			headerLabel.setOpaque(false);
			//headerLabel.setBorder(BorderFactory.createEmptyBorder(4, 7, 2, 7));
			body.add(headerLabel);
			
			this.setBounds(10, 10, getPreferredSize().width, getPreferredSize().height);
		}
	};
}