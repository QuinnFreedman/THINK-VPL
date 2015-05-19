import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Executable extends VObject{
	private static final long serialVersionUID = 1L;

	protected Color color;
	protected static enum Mode{
		IN,OUT,BOTH,NONE
	};
	NodeHolder inputNodeHolder;
	NodeHolder outputNodeHolder;
	public ArrayList<Variable.DataType> getInputs(){return null;};
	public ArrayList<Variable.DataType> getOutputs(){return null;};
	public Mode getPrimairyMode(){return Mode.BOTH;};
	private ArrayList<Node> inputNodes;
	private ArrayList<Node> outputNodes;
	public int activeNode;
	public ArrayList<VariableData> workingData;
	protected ArrayList<Node> getInputNodes(){
		return inputNodes;
	}
	protected void setInputNodes(ArrayList<Node> nodes){
		inputNodes = nodes;
		Main.nodes.addAll(nodes);
	}
	protected ArrayList<Node> getOutputNodes(){
		return outputNodes;
	}
	protected void setOutputNodes(ArrayList<Node> nodes){
		outputNodes = nodes;
		Main.nodes.addAll(nodes);
	}
	protected void addInputNode(Node node) {
		inputNodes.add(node);
		inputNodeHolder.add(node);
		inputNodeHolder.revalidate();
		inputNodeHolder.repaint();
		Main.nodes.add(node);
	}
	protected void addOutputNode(Node node) {
		outputNodes.add(node);
		outputNodeHolder.add(node);
		Main.nodes.add(node);
	}
	
	Executable(){
		super();
		inputNodes = new ArrayList<Node>();
		outputNodes = new ArrayList<Node>();
		
		inputNodeHolder = new NodeHolder();
		outputNodeHolder = new NodeHolder();
		
		this.add(inputNodeHolder,BorderLayout.PAGE_START);
		this.add(outputNodeHolder,BorderLayout.PAGE_END);
	}
	
	public VariableData execute(VariableData[] inputs){
		return null;
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
			this.setPreferredSize(new Dimension(15,15));
			((FlowLayout) this.getLayout()).setVgap(0);
		}
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
}