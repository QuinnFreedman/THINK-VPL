/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General  License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General  License for more details.
 *
 *  You should have received a copy of the GNU General  License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package think;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

 class PrimitiveFunction extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private JLabel label;
	private String text = "";
	protected VariableData parentVarData;
	protected Variable parentVariable;
	// static ArrayList<Variable.DataType> getInputs(){return null;};
	// static ArrayList<Variable.DataType> getOutputs(){return null;};
	
	private boolean isStatic = true;
	
	VariableData getParentVarData(){
		return parentVarData;
	}
	void setParentVarData(VariableData v) {
		parentVarData = v;
		
	}
	boolean isStatic(){
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
			//defaultActiveNode = 0;
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
	
	 void setText(String s) {
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

	 void removeFromParent(){
		if(parentVariable != null)
			parentVariable.removeChild(this);
	}
	 Variable getParentVariable() {
		return parentVariable;
	}
	
}