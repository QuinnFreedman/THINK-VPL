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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Executable extends VObject{
	private static final long serialVersionUID = 1L;

	protected Color color;
	protected static enum Mode{
		IN,OUT,BOTH,NONE
	};
	NodeHolder inputNodeHolder;
	NodeHolder outputNodeHolder;
	private ArrayList<Node> inputNodes;
	private ArrayList<Node> outputNodes;
	protected int activeNode;
	ArrayList<VariableData> workingData;
	protected boolean selected = false;
	protected boolean executeOnce;
	protected boolean hasExecuted = false;
	ArrayList<VariableData> outputData;

	protected boolean inputsOptional(int i){
		return false;
	}
	
	//protected boolean isExecuteOnce(){
	//	return false;
	//}
	
	//protected int defaultActiveNode = 1;
	
	/**
	 * Semi-deprecated.
	 * 
	 * Right now, this method is never called for non-primitive functions.
	 * Some day, it might effect which icon is used to represent a function.
	 * 
	 * Returns whether the function is primarily centered around input (ie receives arguments),
	 * output(ie returns values), or both.  Since its only effect is cosmetic, this can be subjective.
	 * 
	 * @return the mode of the function
	 */
	public Mode getPrimairyMode(){return Mode.BOTH;};
	
	/**
	 * @return the module that the executable belongs to, if any; else returns null
	 */
	protected Module getParentMod(){
		Class<? extends Object> a = getClass();
		while(a.getDeclaringClass() != null){
			a = a.getDeclaringClass();
			if(a.getSuperclass() == Module.class){
				for(Module m : Main.modules){
					if(m.getClass() == a){
						return m;
					}
				}
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Gets the inputs of the executable, in the form of data types.
	 * These correspond to input nodes.  When the executable is added to a graph,
	 * input nodes will automatically be created according to this list.
	 * 
	 * Repeat data-types to get multiple nodes of the same type.
	 * Use Variable.DataType.GENERIC for empty (non-data) nodes.
	 * 
	 * @return the list of all input data types.
	 */
	public ArrayList<Variable.DataType> getInputs(){
		/*ArrayList<Variable.DataType> list = new ArrayList<Variable.DataType>();
		for(Node n : getInputNodes()){
			list.add(n.dataType);
		}
		return list;*/
		return null;
	}
	
	/**
	 * Gets the outputs of the executable, in the form of data types.
	 * These correspond to input nodes.  When the executable is added to a graph,
	 * output nodes will automatically be created according to this list.
	 * 
	 * Repeat datatypes to get multiple nodes of the same type.
	 * Use Variable.DataType.GENERIC for empty (non-data) nodes.
	 * 
	 * @return the list of all output data types.
	 */
	public ArrayList<Variable.DataType> getOutputs(){
		/*ArrayList<Variable.DataType> list = new ArrayList<Variable.DataType>();
		for(Node n : getOutputNodes()){
			list.add(n.dataType);
		}
		return list;*/
		return null;
	}
	
	protected ArrayList<Node> getInputNodes(){
		return inputNodes;
	}
	protected void setInputNodes(ArrayList<Node> nodes){
		inputNodes = nodes;
	}
	protected ArrayList<Node> getOutputNodes(){
		return outputNodes;
	}
	protected void setOutputNodes(ArrayList<Node> nodes){
		outputNodes = nodes;
	}
	public void addInputNode(Node node) {
		inputNodes.add(node);
		inputNodeHolder.add(node);
		inputNodeHolder.revalidate();
		inputNodeHolder.repaint();
	}
	public void addOutputNode(Node node) {
		outputNodes.add(node);
		outputNodeHolder.add(node);
		outputNodeHolder.revalidate();
		outputNodeHolder.repaint();
	}

	public void removeInputNode(Node n) {
		Node.clearChildren(n);
		inputNodeHolder.remove(n);
		owner.getNodes().remove(n);
		inputNodes.remove(n);
		inputNodeHolder.revalidate();
		inputNodeHolder.repaint();
	}

	public void removeOutputNode(Node n) {
		Node.clearChildren(n);
		outputNodeHolder.remove(n);
		owner.getNodes().remove(n);
		outputNodes.remove(n);
		outputNodeHolder.revalidate();
		outputNodeHolder.repaint();
	}
	void setSelected(boolean b){
		selected = b;
	}
	 void resetActiveNode() {
		if(!this.getInputNodes().isEmpty() && this.getInputNodes().get(0).dataType == Variable.DataType.GENERIC){
			this.activeNode = 1;
		}else{
			this.activeNode = 0;
		}
	}
	 int getActiveNode() {
		return activeNode;
	}
	 void incrementActiveNode() {
		activeNode++;
	}
	
	/**
	 * Gets a list of tool-tips for the input nodes of an executable.
	 * Override in subclasses to give the user more information about the parameters of a function.
	 * 
	 * @return a list of tool-tips corresponding to the input nodes of the executable.
	 * The length should correspond to the length of Executable.getInputs() (minus one if the first
	 * element of getInputs() is GENERIC).
	 */
	public ArrayList<String> getInputTooltips(){
		return null;
	}
	
	/**
	 * Gets a list of tool-tips for the output nodes of an executable.
	 * Override in subclasses to give the user more information about the parameters of a function.
	 * 
	 * @return a list of tool-tips corresponding to the output nodes of the executable.
	 * The length should correspond to the length of Executable.getOutputs() (minus one if the first
	 * element of getOutputs() is GENERIC).
	 */
	public ArrayList<String> getOutputTooltips(){
		return null;
	}
	
	/**
	 * 
	 * @param pos - the position on the graph where the executable will be created.
	 * @param owner - the graph editor in which the executable will be created.
	 * For example, a blueprint, constructor, or function.
	 */
	protected Executable(Point pos, GraphEditor owner){
		super(owner);
		this.color = Color.BLACK;
		
		inputNodes = new ArrayList<Node>();
		outputNodes = new ArrayList<Node>();
		
		inputNodeHolder = new NodeHolder();
		outputNodeHolder = new NodeHolder();
		
		this.add(inputNodeHolder,BorderLayout.PAGE_START);
		this.add(outputNodeHolder,BorderLayout.PAGE_END);

		if(!(this instanceof EntryPoint || this instanceof Constant
				|| this instanceof FunctionEditor.FunctionIO
				|| this instanceof PrimitiveFunction)){
			body.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			if(!(this instanceof Arithmetic || this instanceof Logic || this instanceof Console.getStr || this instanceof Cast))
				headerLabel.setText(getSimpleName());
			body.add(headerLabel,gbc);
		}
		if(!(this instanceof PrimitiveFunction)){
			if(getInputs() != null){
				for(Variable.DataType dt : getInputs()){
					if(dt == Variable.DataType.NUMBER && this instanceof Arithmetic)
						break;
					else if(dt == Variable.DataType.GENERIC)
						addInputNode(new Node(Node.NodeType.RECIEVING,this,dt,true));
					else
						addInputNode(new Node(Node.NodeType.RECIEVING,this,dt,false));
				}
			}
			if(getOutputs() != null){
				for(Variable.DataType dt : getOutputs()){
					if(dt == Variable.DataType.NUMBER && this instanceof Arithmetic)
						break;
					boolean b = (dt != Variable.DataType.GENERIC);
					addOutputNode(new Node(Node.NodeType.SENDING,this,dt,b));
				}
			}
		}
		if(getInputTooltips() != null){
			int j = (Collections.frequency(getInputs(), Variable.DataType.GENERIC) == 1) ? 1 : 0;
			for(int i = 0; i < getInputTooltips().size(); i++){
				getInputNodes().get(i + j).setToolTipText(getInputTooltips().get(i));
			}
		}
		if(getOutputTooltips() != null){
			int j = (Collections.frequency(getOutputs(), Variable.DataType.GENERIC) == 1) ? 1 : 0;
			for(int i = 0; i < getOutputTooltips().size(); i++){
				getOutputNodes().get(i + j).setToolTipText(getOutputTooltips().get(i));
			}
		}
		
		if(pos != null)
			setBounds(new Rectangle(pos,getSize()));
		try{
			if(!(this instanceof PrimitiveFunction) && getOutputs() != null && this.getOutputs().size() > 1 && 
					this.getOutputs().get(0) == Variable.DataType.GENERIC){
				this.executeOnce = true;
			}
		}catch(Exception e){};
	}
	
	protected Executable() {
		super();
	}
	
	/**
	 * This is the main function that will actually perform the function of an executable
	 * 
	 * Takes and outputs subclasses VariableData objects.  These are basically primitives of undetermined type.
	 * Cast them to a subclass of VariableData to access the `value` field (the value of that piece of data).
	 * See VariableData.java for more.
	 * 
	 * Be conscious of passing live pointers versus values.  Use the `value` field when possible.
	 * For example, writing "return inputs[0]" is concise, but it can have unpredictable results compared
	 * with the more verbose "return new VariableData.Integer(((VariableData.Integer) inputs[0]).value)"
	 * 
	 * @param inputs - takes an array of VaraibleData objects.  The length of this array and the types of its
	 * members should correspond to the output of Executable.getInputs();
	 * @return a single VariableData object.  The type should correspond to the return type of Executable.getOutputs();
	 */
	public VariableData execute(VariableData[] inputs) throws Exception{
		return null;
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(selected){
			 Point2D start = new Point2D.Float(0, 0);
		     Point2D end = new Point2D.Float(this.getWidth()/2, 0);
		     float[] dist = {0.0f, 0.7f};
		     Color[] colors = {new Color(0,0,0,0), Color.YELLOW};
		     LinearGradientPaint p =
		         new LinearGradientPaint(start, end, dist, colors, MultipleGradientPaint.CycleMethod.REFLECT);
			
			g2.setPaint(p);
			g2.setStroke(new BasicStroke(5));
			g2.draw(new RoundRectangle2D.Double(-3, inputNodeHolder.getHeight()-8, this.getSize().width+4, this.body.getSize().height+14, 20, 20));
		}
		GradientPaint gradient = new GradientPaint(0, 
				inputNodeHolder.getHeight()-5, 
				color,
				0,
				this.getHeight()/2,
				new Color(20,20,20,127),true);
		
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, inputNodeHolder.getHeight()-5, this.getSize().width, this.body.getSize().height+10, 20, 20));
	    g2.setPaint(Color.black);
	}
	static class NodeHolder extends JPanel{
		private static final long serialVersionUID = 1L;

		NodeHolder(){
			this.setOpaque(false);
			//this.setPreferredSize(new Dimension(15,15));
			((FlowLayout) this.getLayout()).setVgap(0);
		}
		
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(Math.max((((FlowLayout) this.getLayout()).getHgap()+15)*this.getComponentCount(),15),15);
		}
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
	 String getPathName(){
		String s = "";
		if(this instanceof PrimitiveFunction && this.owner != ((PrimitiveFunction) this).getParentVariable().getOwner()){
			Out.pln("this instanceof PrimitiveFunction");
			//Out.pln(this.owner+" != "+((PrimitiveFunction) this).getParentVar().getOwner());
			if(((PrimitiveFunction) this).getParentVariable().getOwner() instanceof Blueprint){
				s += (((Blueprint) ((PrimitiveFunction) this).getParentVariable().getOwner()).getName()+" > ");
				Out.pln(s);
			}else if(((PrimitiveFunction) this).getParentVariable().getOwner() instanceof FunctionEditor){
				s += (((Blueprint) ((FunctionEditor) ((PrimitiveFunction) this).getParentVariable().getOwner()).getOverseer()).getName()+" > ");
			}
		}else if(this instanceof UserFunc){
			if(((UserFunc) this).getParentVar() instanceof SidebarItem){
				if(this.owner != ((VFunction) ((UserFunc) this).getParentVar()).getOwner()){
					s+= (((Blueprint) ((VFunction) ((UserFunc) this).getParentVar()).getOwner()).getName()+" > ");
				}
				if(/*!((VFunction) ((UserFunc) this).getParentVar()).isStatic && */
						((VFunction) ((UserFunc) this).getParentVar()).parentInstance != null){
					s += (((VFunction) ((UserFunc) this).getParentVar()).parentInstance.getID()+" > ");
				}
				s += ((VFunction) ((UserFunc) this).getParentVar()).getID();
			}else if(((UserFunc) this).getParentVar() instanceof InstantiableBlueprint){
				s += "new "+((InstantiableBlueprint) ((UserFunc) this).getParentVar()).getName();
			}
			
		}
		return s;
	}
	String getSimpleName(){
		return ((getParentMod() == null) ? "" : getParentMod().getModuleName()+" > ") + getFunctionName();
	}
	public String getFunctionName(){
		return this.getClass().getSimpleName().replace('_',' ');
	}

	public String getMenuName() {
		return getSimpleName();
	}
}