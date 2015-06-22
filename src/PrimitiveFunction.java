import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class PrimitiveFunction extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private JLabel label;
	private String text = "";
	protected VariableData parentVarData;
	protected Variable parentVariable;
	public ArrayList<Variable.DataType> getInputs(){return null;};
	public ArrayList<Variable.DataType> getOutputs(){return null;};
	
	private boolean isStatic = true;
	
	public VariableData getParentVarData(){
		return parentVarData;
	}
	public void setParentVarData(VariableData v) {
		parentVarData = v;
		
	}
	public boolean isStatic(){
		return isStatic;
	}
	PrimitiveFunction(Point pos, Variable parent){
		this(pos,parent,parent.getOwner());
	}
	PrimitiveFunction(Point pos, Variable parent, GraphEditor owner){
		super(null, owner);
		this.type = parent.dataType;
		this.parentVarData = parent.varData;
		this.parentVariable = parent;
		parent.addChild(this);
		this.color = Main.colors.get(parent.dataType);
		
		body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		label = new JLabel();
		setText(parent.getFullName());
		body.add(label,gbc);
		
		if(getInputs() != null && !getInputs().isEmpty() && (getInputs().get(0) == Variable.DataType.GENERIC))
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.GENERIC,true));
		
		if(!parent.isStatic){
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.OBJECT));
			defaultActiveNode = 0;
			isStatic = false;
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
		this.text = (isStatic ? "" : "\u0394 ")+crop(getPathName(),15) + crop(s,15)+" : "+crop(getSimpleName(),15);
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
		this.parentVarData = parent.varData;
		this.parentVariable = parent;
	}

	public void removeFromParent(){
		if(parentVariable != null)
			parentVariable.removeChild(this);
	}
	public Variable getParentVariable() {
		return parentVariable;
	}
	
}