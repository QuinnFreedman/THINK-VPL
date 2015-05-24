import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JLabel;

public class EntryPoint extends Executable{
	public Node startNode;
	EntryPoint(){
		super();
		this.setOpaque(false);
		this.setBounds(350, 50, 80, 80);
		((FlowLayout) this.body.getLayout()).setVgap(30);
		remove(inputNodeHolder);
		remove(outputNodeHolder);
		JLabel text = new JLabel("Start");
		text.setForeground(Color.BLACK);
		this.body.add(text);
		startNode = new StartNode(this);
		Main.nodes.add(startNode);
		this.add(startNode,BorderLayout.AFTER_LAST_LINE);
		Main.objects.add(this);
	}
	@Override
	public void paintComponent(Graphics g){
		if(selected){
			Point2D center = new Point2D.Float(this.getWidth()/2, this.getHeight()/2);
			float radius = this.getHeight()/2;
			float[] dist = {0.8f, 1.0f};
			Color[] colors = {Color.YELLOW, new Color(0,0,0,0f)};
			RadialGradientPaint p =
		    	new RadialGradientPaint(center, radius, dist, colors);
			((Graphics2D) g).setPaint(p);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.red);
		g.fillPolygon(new int[]{0,40,80,40}, new int[]{40,0,40,80}, 4);
	}
	
	private class StartNode extends Node{

		StartNode(VObject parentObj) {
			super(Node.NodeType.SENDING, parentObj, Variable.DataType.GENERIC);
			this.size = new Dimension(80,30);
			this.setOpaque(false);
			this.canHaveMultipleInputs = false;
		}
		
		@Override
		public void paintComponent(Graphics g){
			
		}
	}
}