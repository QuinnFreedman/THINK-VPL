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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

class UserFunc extends Executable{
	private static final long serialVersionUID = 1L;
	Variable.DataType type;
	private String text = "";
	protected FunctionOverseer parentFunc;
	
	 FunctionOverseer getParentVar(){
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
	 void setIO(){
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
	
	 void setText() {
		this.text = Main.crop(this.getPathName(),20);
		headerLabel.setText(text);
		this.setSize(this.getSize());
	}
	

	 void removeFromParent(){
		parentFunc.removeChild(this);
	}

	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
}