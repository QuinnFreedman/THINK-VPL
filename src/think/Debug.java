/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General  License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General  License for more details.
 *
 *  You should have received a copy of the GNU General  License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package think;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation;

class Debug{
	
	private static ArrayList<Executable> stack;
	private static ArrayList<Executable> remember;
	private static boolean isStepping = false;
	private static Variable.DataType waitingForInput = null;
	static Console console;
	private static RunMode mode;
	
	static Status running;
	
	static enum RunMode{
		RUN,FAST,SLOW
	}
	
	static void setRunMode(RunMode rm){
		mode = rm;
	}
	
	static RunMode getRunMode(){
		return mode;
	}
	
	static boolean isStepping() {
		return isStepping;
	}
	static Variable.DataType waitingForInput() {
		return waitingForInput;
	}
	private static void reset(Variable var){
		var.setEditable(false);
		var.resetVariableData();
		Out.pln(var.getID()+" "+var.varData.getValueAsString());
		for(PrimitiveFunction child : var.getChildren()){//TODO: should be unnecessary??
			child.setParentVarData(var.varData);
			Out.pln("	"+child.getSimpleName()+" "+child.getParentVarData().getValueAsString());
			
		}
	}
	private static void startStep(){
		Out.pln("Start Stepping");
		
		for(Blueprint bp : Main.blueprints){
			
			int i = 0;
			while(i < bp.getObjects().size()){
				VObject o = bp.getObjects().get(i);
				if(o instanceof FunctionSelector || o instanceof PrimitiveFunctionSelector){
					o.delete();
				}else{
					i++;
					if(o instanceof Executable)
						((Executable) o).isError = false;
				}
			}
			
			bp.addVar.setEnabled(false);
			for(Variable var : bp.getVariables()){
				reset(var);
				ArrayList<VFunction> funcs =  bp.getFunctions();
				for(VFunction func : funcs){
					for(Variable v : func.getEditor().getVariables()){
						reset(v);
					}
				}
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
					//Out.pln(o);
					Executable o2 = ((Executable) o);
					o2.resetActiveNode();
					o2.workingData = new ArrayList<VariableData>();
					o2.outputData = new ArrayList<VariableData>();
					o2.hasExecuted = false;
					if(o2 instanceof FlowControl.For)
						((FlowControl.For) o2).resetIndex();
					else if(o2 instanceof FlowControl.AdvancedFor)
						((FlowControl.AdvancedFor) o2).resetIndex();
					//Out.pln(o.getClass().getName()+" : "+((Executable) o).workingData);
					
				}
			}
			for(VFunction f : bp.getFunctions()){
				if(f == null || f.editor == null || f.editor.getObjects() == null){
					Out.pln("!WARNING: null Pointer");
				}
				for(VObject o : f.editor.getObjects()){
					if(o instanceof Executable){
						//Out.pln(o);
						Executable o2 = ((Executable) o);
						o2.resetActiveNode();
						o2.workingData = new ArrayList<VariableData>();
						o2.outputData = new ArrayList<VariableData>();
						o2.hasExecuted = false;
						//Out.pln(o.getClass().getName()+" : "+((Executable) o).workingData);
						
					}
				}
			}
		}
		stepForever();
	}
	
	 static void stepForever(){
		if(mode == RunMode.RUN){
			Thread t = new Thread() {
				public void run() {
					while(step()){};
			}};
	        t.start();
			
		}
	}
	
	protected static void exit() {
		Out.pln("exiting");
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
			for(VFunction f : bp.getFunctions()){
				for(Variable var : f.editor.getVariables()){
					var.setEditable(true);
				}
			}
		}
		if(running != null){
			running.setVisible(false);
		}
		console.setAlwaysOnTop(false);
	}
	
	
	private static boolean moveUpStack() throws Exception{
		
		if(stack.isEmpty()){
			return false;
		}else if(getTop().getInputNodes().isEmpty()){
			return false;
		}
		Out.pln("try to go up from "+getTop());
		Out.pln("activeNode "+getTop().getActiveNode());
		Out.pln("inputs "+getTop().getInputNodes().size());
		if(getTop().getActiveNode() >= getTop().getInputNodes().size() || (getTop().executeOnce && getTop().hasExecuted)){
			Out.pln("failed: activeNode > input nodes");
			return false;
		}
		
		ArrayList<Node> parents = getTop().getInputNodes().get(getTop().getActiveNode()).parents;
		Out.pln("parents "+parents);
		
		boolean hasGeneric = (getTop().getInputNodes().get(0).dataType == Variable.DataType.GENERIC);
		
		if((getTop().getInputNodes().size() - (hasGeneric ? 1 : 0) > 0) && parents.isEmpty() && !getTop().inputsOptional(getTop().activeNode)){
			Out.pln("failed: parents are missing; exiting...");
			getTop().isError = true;
			throw new BadInputException("Error: Missing inputs ("+getTop().getFunctionName()+").");
			
		}
		
		if(parents.isEmpty()){
			Out.pln("falied: parents is empty");
			//exit();
			return false;
		}
		
		Executable next = (Executable) parents.get(0).parentObject;
		Out.pln("next = "+next);
		
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
				((UserFunc) next).getParentVar().setCurrentlyExecuting(((UserFunc) next));
			}
			
			stack.add(next);
			
			if(mode != RunMode.RUN){
				next.setSelected(true);
				next.repaint();
			}
		}else{
			Out.pln(next.getOutputNodes().indexOf(
									parents.get(0)
									));
			getTop().workingData.add(
					next.outputData.get(
							next.getOutputNodes().indexOf(
									parents.get(0)
									)
							-((next.getOutputNodes().get(0).dataType == Variable.DataType.GENERIC/* && next.outputData.get(0) != null*/) ? 1 : 0)
							)
					);
			getTop().incrementActiveNode();
		}
		
		return true;
	}
	
	private static boolean moveDownStack(){
		if(stack.isEmpty()){
			Out.pln("stack is empty; exiting...");
			exit();
			return false;
		}
	//EXECUTE
		Out.pln("executing "+getTop().getClass().getName());
		Out.print(getTop().getClass().getName()+" workingData = ");
		for(VariableData var : getTop().workingData){
			Out.print(var.getValueAsString()+" ");
		}
		Out.pln();
		Out.pln(getTop().getClass().getName()+" executeOnce = "+getTop().executeOnce+"; hasExecuted = "+getTop().hasExecuted);
		
		if(getTop() instanceof PrimitiveFunction && !((PrimitiveFunction) getTop()).isStatic()){
			if(getTop().workingData.get(0) instanceof VariableData.Instance){
				((PrimitiveFunction) getTop()).setParentVarData(
						((VariableData.Instance) getTop().workingData.get(0)).getVariableDataByName( ((PrimitiveFunction) getTop()).getParentVariable().getID() )
						);
				getTop().workingData.remove(0);
			}else{
				Debug.console.postError("internal error");
				exit();
				return false;
			}
		}
		
		VariableData execute;
		if(getTop().executeOnce && getTop().hasExecuted){
			Out.pln("is executeOnce has executed.  Output = "+getTop().outputData);
			execute = null;
		}else{
			VariableData[] array = new VariableData[getTop().workingData.size()];
			array = getTop().workingData.toArray(array);
			try {
				execute = getTop().execute(array);
			} catch (Exception e) {
				if(getTop() != null){
					getTop().isError = true;
				}
				exit();
				console.postError(e.getMessage());
				Out.printStackTrace(e);
				return false;
			}
			Out.pln("execute = "+execute);
		}
		
		if(getTop().getClass() == Console.getStr.class){
			waitingForInput = ((Console.getStr) getTop()).getDataType();
			Out.pln("isWaitingForInput = "+waitingForInput);
			console.requestFocus();
			console.input.requestFocusInWindow();
			if(running != null)
				running.pause();
			return false;
		}else{
			return moveDownStack2(execute);
		}
	}
	
	 static boolean moveDownStack2(VariableData execute){
		waitingForInput = null;
		
	//SET OUTPUT DATA
		ArrayList<VariableData> multiExecute = null;
		if(!(getTop() instanceof FunctionEditor.FunctionIO)){
			if(execute != null){
				getTop().outputData = new ArrayList<VariableData>(Arrays.asList(execute));
				Out.pln("outputData = "+getTop().outputData);
			}else{
				Out.pln("execute == null, getTop().outputData = "+getTop().outputData);
				multiExecute = getTop().outputData;
			}
		}else if(((FunctionEditor.FunctionIO) getTop()).mode == FunctionEditor.FunctionIO.Mode.OUTPUT){
			FunctionOverseer overseer = ((FunctionEditor.FunctionIO) getTop()).getOverseer();
			Out.pln("overseer = "+overseer);
			Out.pln("currently executing = "+((FunctionEditor.FunctionIO) getTop()).getOverseer().getCurrentlyExecuting());
			
			if(overseer instanceof InstantiableBlueprint){
				execute = ((InstantiableBlueprint) overseer).getWorkingInstance();//TODO clone or point?
			}
			
			((FunctionEditor.FunctionIO) getTop()).getOverseer().getCurrentlyExecuting().outputData = 
					new ArrayList<VariableData>(Arrays.asList(execute));
			
		}
		
		if(!getTop().getOutputNodes().isEmpty() && 
				getTop().getOutputNodes().get(0).dataType == Variable.DataType.GENERIC){
			Out.pln("is generic output > clearing execute");
			execute = null;//TODO
		}
		
		if(mode != RunMode.RUN){
			getTop().setSelected(false);
			getTop().repaint();
		}
		
		getTop().hasExecuted = true;
		
	//FIND NEXT
		if(stack.size() == 1){
			
			Executable next = getNext(getTop());
			
			while(next instanceof Rerout){
				next = getNext(next);
			}
			
			Out.pln("next = "+((next == null) ? "null" : next.getClass().getName()));
			
			if(next instanceof UserFunc){
				((UserFunc) next).getParentVar().setCurrentlyExecuting(((UserFunc) next));
			}
			
			stack.remove(stack.size()-1);
			if(next != null){
				next.hasExecuted = false;
				if(next instanceof Repeater){
					remember.add(next);
					if(next instanceof FlowControl.Sequence){
						((FlowControl.Sequence) next).activeOutNode = 0;
					}
				}
				
				next.workingData.clear();
				next.resetActiveNode();
				
				
				stack.add(next);
			}else{
				if(!remember.isEmpty()){
					stack.add(remember.get(remember.size()-1));
					getTop().hasExecuted = false;
					if(getTop() instanceof FlowControl.For || getTop() instanceof FlowControl.AdvancedFor){
						getTop().activeNode = 999;
					}
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
				f.getParentVar().getInputObject().outputData = new ArrayList<VariableData>(f.workingData);
				f.getParentVar().getOutputObject().workingData = new ArrayList<VariableData>();
				f.outputData = new ArrayList<VariableData>();
			}else{
				stack.remove(stack.size()-1);
			}
		}
		
		if(execute != null){ 
			Out.pln("add to "+getTop().workingData);
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
			Out.pln("ABORTED");
			Debug.console.postError("program aborted by user");
			return false;
		}
		
		Out.pln();
		Out.pln("stack:");
		for(VObject o : stack){
			Out.pln(o.getClass().getSimpleName());
		}
		Out.pln();
		
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
			String s;
			if(e instanceof BadInputException){
				s = e.getMessage();
			}else{
				s = "An unexpected error occured";
				if(stack != null && !stack.isEmpty() && stack.get(stack.size()-1) != null && stack.get(stack.size()-1).headerLabel != null){
					s += " when executing \""+stack.get(stack.size()-1).headerLabel.getText()+"\"";
				}
				s += ".";
			}
			s += "  The program will now exit.";
			console.postError(s);
			Out.printStackTrace(e);
			exit();
			return false;
		}
	}
	
	private static Executable getTop(){
		if(stack.isEmpty())
			return null;
		return stack.get(stack.size()-1);
	}
	
	private static Executable getNext(Executable o){
		Out.pln("getNext "+o.getClass().getName());
		ArrayList<Node> children;
		if(o instanceof EntryPoint){
			children = ((EntryPoint) o).startNode.children;
		}else if(o instanceof FlowControl.Branch){
			children = o.getOutputNodes().get((((FlowControl.Branch) o).isTrue()) ? 0 : 1).children;
		}else if(o instanceof FlowControl.Sequence){
			if(((FlowControl.Sequence) o).activeOutNode < o.getOutputNodes().size()){
				children = o.getOutputNodes().get(((FlowControl.Sequence) o).activeOutNode).children;
				((FlowControl.Sequence) o).activeOutNode++;
			}else{
				children = new ArrayList<Node>();
				removeFromEnd(remember,o);
			}
		}else if(o instanceof Repeater){
			if(((Repeater) o).isContinue()){
				o.resetActiveNode();
				if(!(o.executeOnce && o.hasExecuted)){
					o.workingData = new ArrayList<VariableData>();
					o.outputData = new ArrayList<VariableData>();
				}
				children = o.getOutputNodes().get(0).children;
			}else{
				children = o.getOutputNodes().get(o.getOutputNodes().size()-1).children;
				removeFromEnd(remember,o);
			}
		}else if(o instanceof UserFunc){
			Out.pln("userfunc going to fIO");
			FunctionEditor.FunctionIO inputObj = ((UserFunc) o).getParentVar().getInputObject();
			inputObj.outputData = new ArrayList<VariableData>(o.workingData);
			Out.pln("inputObj.outputData = "+inputObj.outputData);
			((UserFunc) o).getParentVar().setCurrentlyExecuting((UserFunc) o);
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
	
	 static void tab() {
		if(isStepping && waitingForInput == null && mode != RunMode.RUN){
			do{
				step();
			}while(getTop() != null && (!getTop().isShowing() || (getTop() instanceof Rerout)));
		}
	}
	 static void f1() {
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
	 static void f2() {
		if(!isStepping()){
			Main.blueprints.get(0).getPanel().requestFocusInWindow();
			mode = RunMode.FAST;
			startStep();
    	}else{
    		setRunMode(RunMode.FAST);
    		step();
    	}
	}
	 static void f3() {
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
	 static class Status extends VObject{
		private static final long serialVersionUID = 1L;
		private ImageIcon run;
		private ImageIcon paused;
		
		 void pause(){
			headerLabel.setIcon(paused);
		}
		
		 void unpause(){
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
				Out.printStackTrace(e);
			}
			run = new ImageIcon(url);
			
			try{
				url = Main.class.getResource("/images/loading_paused.png");
			}catch (Exception e){
				Out.printStackTrace(e);
			}
			paused = new ImageIcon(url);
			
			headerLabel = new JLabel("Running...", run, JLabel.LEADING);
			headerLabel.setOpaque(false);
			//headerLabel.setBorder(BorderFactory.createEmptyBorder(4, 7, 2, 7));
			body.add(headerLabel);
			
			this.setBounds(10, 10, getPreferredSize().width, getPreferredSize().height);
			owner.getPanel().setComponentZOrder(this,0);
			for(MouseListener listener : this.getMouseListeners()){
				this.removeMouseListener(listener);
			}
		}
	}
	private static class BadInputException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public BadInputException(String string) {
			super(string);
		}
		
	}
}