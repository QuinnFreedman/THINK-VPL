import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrimativeFunction extends Function{
	protected Node nodeFromParent;
	Primative.DataType type;
	private JLabel label;
	private String text = "";
	private JPanel nodeHolder;
	public String getFunctionName(){
		return this.getClass().getSimpleName();
	}
	PrimativeFunction(Point pos, Primative.DataType type, Node parentNode, Primative parent, /*String name,*/ ArrayList<Primative.DataType> inputs, ArrayList<Primative.DataType> outputs){
		super();
		this.type = type;
		this.color = Main.colors.get(type);
		this.inputs = inputs;
		this.outputs = outputs;
		this.setBounds(new Rectangle(pos,new Dimension(90,40)));
		this.nodeFromParent = new Node(Node.Direction.WEST, Node.NodeType.INHERITANCE_RECIEVING, this, Node.NodeStyle.INVISIBLE);
		this.body.add(nodeFromParent);
		this.body.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.text = getFunctionName();
		label = new JLabel(text);
		this.body.add(label);
		nodeHolder = new JPanel();
		nodeHolder.setLayout(new BoxLayout(nodeHolder, BoxLayout.Y_AXIS));
		nodeHolder.setOpaque(false);
		if(this.inputs != null){
			this.inputNode = new Node(Node.Direction.EAST,Node.NodeType.RECIEVING,this,inputs);
			Main.nodes.add(inputNode);
			this.nodeHolder.add(inputNode);
		}else{
			JPanel jp = new JPanel();
			jp.setPreferredSize(new Dimension(30,20));
			jp.setOpaque(false);
			this.nodeHolder.add(jp);
		}
		if(this.outputs != null){
			this.outputNode = new Node(Node.Direction.EAST,Node.NodeType.SENDING,this,outputs);
			Main.nodes.add(outputNode);
			this.nodeHolder.add(outputNode);
		}else{
			JPanel jp = new JPanel();
			jp.setPreferredSize(new Dimension(30,20));
			jp.setOpaque(false);
			this.nodeHolder.add(jp);
		}
		this.add(nodeHolder,BorderLayout.LINE_END);
		Main.nodes.add(nodeFromParent);
		Main.curves.add(new Curve(parentNode,nodeFromParent));
		Main.panel.add(this);
	}
	PrimativeFunction() {
		super();
	}
	PrimativeFunction(Point pos, Node parentNode, Primative parent) {
		super();
		System.out.println("you shouldn't be using this");
	}
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint gradient = new GradientPaint(0, 
				0, 
				new Color(Math.min(color.getRed()+64,255),
						Math.min(color.getGreen()+64,255),
						Math.min(color.getBlue()+64,255),
						color.getAlpha()),
				0, 
				this.getHeight()/2,
				new Color(20,20,20,127),true);
		
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, 0, this.getSize().width, this.getSize().height, 20, 20));
	    g2.setPaint(Color.black);
	}
}