import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrimitiveFunction extends VObject{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private JLabel label;
	private String text = "";
	private Variable parentVar;
	private Color color;
	public String getFunctionName(){
		return this.getClass().getSimpleName();
	}
	PrimitiveFunction(Point pos, Variable parent){
		super();
		this.type = parent.dataType;
		this.parentVar = parent;
		this.color = Main.colors.get(parent.dataType);
		this.body.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.text = parent.nameField.getText()+": "+getFunctionName();
		label = new JLabel(text);
		this.body.add(label);
		
		if(getInputs() != null){
			for(Variable.DataType dt : getInputs()){
				this.inputNodes.add(new Node(Node.Direction.NORTH,Node.NodeType.RECIEVING,this,dt));
			}
		}
		if(getOutputs() != null){
			for(Variable.DataType dt : getOutputs()){
				this.outputNodes.add(new Node(Node.Direction.NORTH,Node.NodeType.RECIEVING,this,dt));
			}
		}
		
		this.setBounds(new Rectangle(pos,this.getSize()));
		Main.panel.add(this);
	}
	PrimitiveFunction() {
		super();
	}

	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),40);
	}
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint gradient = new GradientPaint(0, 
				0, 
				color,
				0, 
				this.getHeight()/2,
				new Color(20,20,20,127),true);
		
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, 0, this.getSize().width, this.getSize().height, 20, 20));
	    g2.setPaint(Color.black);
	}
}