import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JLabel;

public class EntryPoint extends VObject{
	public Node startNode;
	EntryPoint(){
		this.setOpaque(false);
		this.color = Color.black;
		this.setBounds(350, 50, 80, 80);
		((FlowLayout) this.body.getLayout()).setVgap(27);
		JLabel text = new JLabel("Start");
		this.body.add(text);
		startNode = new StartNode(this);
		Main.nodes.add(startNode);
		this.add(startNode,BorderLayout.AFTER_LAST_LINE);
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.red);
		g.fillPolygon(new int[]{0,40,80,40}, new int[]{40,0,40,80}, 4);
	}
	
	private class StartNode extends Node{

		StartNode(VObject parentObj) {
			super(Node.Direction.SOUTH, Node.NodeType.SENDING, parentObj);
			this.size = new Dimension(80,30);
			this.setOpaque(false);
			this.canHaveMultipleInputs = false;
		}
		
		@Override
		public void paintComponent(Graphics g){
			
		}
	}
}