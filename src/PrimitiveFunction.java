import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PrimitiveFunction extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private JLabel label;
	private String text = "";
	private Variable parentVar;
	public Variable getParentVar(){
		return parentVar;
	}
	public String getFunctionName(){
		return this.getClass().getSimpleName();
	}
	protected PrimitiveFunction getThis(){
		return this;
	}
	PrimitiveFunction(Point pos, Variable parent){
		super();
		this.type = parent.dataType;
		this.parentVar = parent;
		this.parentVar.addChild(this);
		this.color = Main.colors.get(parent.dataType);
		
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
				body.setLayout(new GridBagLayout());
		        GridBagConstraints gbc = new GridBagConstraints();
		        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
				label = new JLabel();
				setText(parentVar.nameField.getText());
				body.add(label,gbc);
				
				if(getInputs() != null){
					for(Variable.DataType dt : getInputs()){
						addInputNode(new Node(Node.NodeType.RECIEVING,getThis(),dt,false));
					}
				}
				if(getOutputs() != null){
					for(Variable.DataType dt : getOutputs()){
						boolean b = (dt != Variable.DataType.GENERIC);
						addOutputNode(new Node(Node.NodeType.SENDING,getThis(),dt,b));
					}
				}
				
				setBounds(new Rectangle(pos,getSize()));
				Main.panel.add(getThis());
	        }});
	}
	
	public void setText(String s) {
		this.text = crop(s,15)+": "+crop(getFunctionName(),15);
		label.setText(text);
		this.setSize(this.getSize());
	}
	private String crop(String s, int i){
		if(s.length() <= i){
			return s;
		}
		return s.substring(0, i-2)+"...";
	}
	PrimitiveFunction() {
		super();
	}

	public void removeFromParent(){
		parentVar.removeChild(this);
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
}