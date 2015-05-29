import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class EntryPoint extends Executable{
	public Node startNode;
	EntryPoint(GraphEditor owner){
		super(owner);
		this.setOpaque(false);
		this.setBounds(80, 30, 80, 80);
		((FlowLayout) this.body.getLayout()).setVgap(30);
		remove(inputNodeHolder);
		remove(outputNodeHolder);
		JLabel text = new JLabel("Start");
		text.setForeground(Color.BLACK);
		text.setFont(text.getFont().deriveFont(Font.PLAIN, text.getFont().getSize()+2));
		text.setBorder(new EmptyBorder(new Insets(-2,-1,-1,-1)));
		this.body.add(text);
		startNode = new StartNode(this);
		this.add(startNode,BorderLayout.AFTER_LAST_LINE);
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