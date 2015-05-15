import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Args extends VObject{
	public ArrayList<Node> inputNodes = new ArrayList<Node>();
	private Node actionNode;
	private Node outputNode;
	private int activeNode;//for debug
	public ArrayList<Node> getInputNodes(){
		return inputNodes;
	}
	public Node getActiveNode(){
		return inputNodes.get(activeNode);
	}
	public void setActiveNode(int i){
		activeNode = i;
	}
	Args(Node A, Node B){
		Node recieveNode;
		Node sendNode;
		if(A.type == Node.NodeType.SENDING){
			recieveNode = B;
			sendNode = A;
		}else{
			recieveNode = A;
			sendNode = B;
		}
		this.color = Color.black;
		this.setOpaque(false);
		this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		JPanel inputNodeHolder = new JPanel();
		inputNodeHolder.setBorder(BorderFactory.createEmptyBorder(5, 0, 30, 0));
		((FlowLayout) inputNodeHolder.getLayout()).setHgap(0);
		((FlowLayout) inputNodeHolder.getLayout()).setVgap(0);
		inputNodeHolder.setOpaque(false);
		ArrayList<Primative.DataType> inputDataType = new ArrayList<Primative.DataType>();
			inputDataType.addAll(sendNode.dataType);
		ArrayList<Primative.DataType> outputDataType = recieveNode.dataType;
		actionNode = new Node(Node.Direction.EAST, Node.NodeType.RECIEVING, this, inputDataType);
		actionNode.canHaveMultipleInputs = false;
		outputNode = new Node(Node.Direction.WEST, Node.NodeType.SENDING, this, outputDataType);
		outputNode.canHaveMultipleInputs = false;
		Main.nodes.add(actionNode);
		Main.nodes.add(outputNode);
		this.remove(body);
		this.body = null;
		this.remove(header);
		this.add(outputNode);
		this.add(inputNodeHolder);
		this.add(actionNode);
		
		for(Primative.DataType inp : Node.complement(inputDataType, outputDataType).get(1)){
			ArrayList<Primative.DataType> input = new ArrayList<Primative.DataType>();
			input.add(inp);
			Node n = new Node(Node.Direction.NORTH,Node.NodeType.RECIEVING,this,input);
			n.canHaveMultipleInputs = false;
			this.inputNodes.add(n);
			Main.nodes.add(n);
			inputNodeHolder.add(n,BorderLayout.PAGE_START);
		}
		this.setBounds(
				((Node.getLocationOnPanel(recieveNode).x+(recieveNode.getPreferredSize().width/2))+(Node.getLocationOnPanel(sendNode).x+(sendNode.getPreferredSize().width/2)))/2 - this.getPreferredSize().width/2, 
				((Node.getLocationOnPanel(recieveNode).y+(recieveNode.getPreferredSize().height/2))+(Node.getLocationOnPanel(sendNode).y+(sendNode.getPreferredSize().height/2)))/2 - this.getPreferredSize().height/2, 
				this.getPreferredSize().width, 
				this.getPreferredSize().height);
		Main.objects.add(this);
		Main.panel.add(this);
		this.repaint();
		this.revalidate();
		Node.connect(outputNode, recieveNode);
		Node.connect(actionNode,sendNode);
	//	Main.curves.add(new Curve(outputNode,recieveNode));
	//	Main.curves.add(new Curve(actionNode,sendNode));
	}
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint gradient = new GradientPaint(0, 0, color, 0, header.getPreferredSize().height,
				new Color(20,20,20,127));
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, 0, this.getSize().width, this.getSize().height, 30, 30));
	}
}