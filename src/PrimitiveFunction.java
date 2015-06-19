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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PrimitiveFunction extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private JLabel label;
	private String text = "";
	protected Variable parentVar;
	public ArrayList<Variable.DataType> getInputs(){return null;};
	public ArrayList<Variable.DataType> getOutputs(){return null;};
	public Variable getParentVar(){
		return parentVar;
	}
	public void setParentVar(Variable v) {
		parentVar = v;
		
	}
	PrimitiveFunction(Point pos, Variable parent){
		this(pos,parent,parent.getOwner());
	}
	PrimitiveFunction(Point pos, Variable parent, GraphEditor owner){
		super(null, owner);
		this.type = parent.dataType;
		this.parentVar = parent;
		this.parentVar.addChild(this);
		this.color = Main.colors.get(parent.dataType);
		
		body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		label = new JLabel();
		setText(parentVar.getFullName());
		body.add(label,gbc);
		
		if(getInputs() != null && !getInputs().isEmpty() && (getInputs().get(0) == Variable.DataType.GENERIC))
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.GENERIC,true));
		
		if(!getParentVar().isStatic){
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.OBJECT));
			defaultActiveNode = 0;
		}
		
		if(getInputs() != null){
			for(Variable.DataType dt : getInputs()){
				if(dt != Variable.DataType.GENERIC)
					addInputNode(new Node(Node.NodeType.RECIEVING,this,dt));
			}
		}
		if(getOutputs() != null){
			for(Variable.DataType dt : getOutputs()){
				boolean b = (dt != Variable.DataType.GENERIC);
				addOutputNode(new Node(Node.NodeType.SENDING,this,dt,b));
			}
		}
		
		setBounds(new Rectangle(pos,getSize()));
	       
	}
	
	public void setText(String s) {
		this.text = (getParentVar().isStatic ? "" : "\u0394 ")+crop(getPathName(),15) + crop(s,15)+" : "+crop(getSimpleName(),15);
		label.setText(text);
		this.setSize(this.getSize());
	}
	private String crop(String s, int i){
		if(s.length() <= i){
			return s;
		}
		return s.substring(0, i-2)+"...";
	}
	PrimitiveFunction(Variable parent) {
		super();
		this.parentVar = parent;
	}

	public void removeFromParent(){
		parentVar.removeChild(this);
	}
	
}