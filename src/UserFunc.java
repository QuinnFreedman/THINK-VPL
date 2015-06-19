import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

class UserFunc extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private String text = "";
	protected FunctionOverseer parentFunc;
	
	public FunctionOverseer getParentVar(){
		return parentFunc;
	}
	protected UserFunc getThis(){
		return this;
	}
	UserFunc(Point pos, FunctionOverseer parent, GraphEditor owner){
		super(null,owner);
		this.color = Color.BLACK;
		this.parentFunc = parent;
		this.parentFunc.addChild(this);
		
		this.executeOnce = parent.isEcexuteOnce();
		
		setText();
		
		setIO();
		
		setBounds(new Rectangle(pos,getSize()));
	}
	public void setIO(){
		if(parentFunc.getInput() != null){
			for(Variable.DataType dt : parentFunc.getInput()){
				if(dt == Variable.DataType.GENERIC)
					addInputNode(new Node(Node.NodeType.RECIEVING,getThis(),dt,true));
				else
					addInputNode(new Node(Node.NodeType.RECIEVING,getThis(),dt,false));
			}
		}
		if(parentFunc.getOutput() != null){
			for(Variable.DataType dt : parentFunc.getOutput()){
				boolean b = (dt != Variable.DataType.GENERIC);
				addOutputNode(new Node(Node.NodeType.SENDING,getThis(),dt,b));
			}
		}
	}
	
	public void setText() {
		this.text = Main.crop(this.getPathName(),20);
		headerLabel.setText(text);
		this.setSize(this.getSize());
	}
	

	public void removeFromParent(){
		parentFunc.removeChild(this);
	}

	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
}