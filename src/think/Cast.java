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
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

 class Cast extends Executable{
	private static final long serialVersionUID = 1L;
	private Node sendingNode;
	private Node recievingNode;
	private Variable.DataType input;
	private Variable.DataType output;
	
	Cast(Node sendingNode, Node recievingNode){
		super(null, sendingNode.parentObject.owner);
		this.color = Color.BLACK;
		
		Out.pln("CAST "+sendingNode.dataType+" > "+recievingNode.dataType);
		
    	this.sendingNode = sendingNode;
    	this.recievingNode = recievingNode;
    	owner.getPanel().add(this);
		Node inputNode = new Node(Node.NodeType.RECIEVING, this, sendingNode.dataType);
		Node outputNode = new Node(Node.NodeType.SENDING, this, recievingNode.dataType,true);
		addInputNode(inputNode);
		addOutputNode(outputNode);
		input = inputNode.dataType;
		output = outputNode.dataType;
		
		//Out.pln(UIManager.getDefaults( ).get("text").toString());
		//headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		//headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		
		setDiamondText();
		
		setBounds(new Rectangle(
				new Point(
						((Node.getLocationOnPanel(recievingNode,owner.getPanel()).x+(recievingNode.getPreferredSize().width/2))+(Node.getLocationOnPanel(sendingNode,owner.getPanel()).x+(sendingNode.getPreferredSize().width/2)))/2 - this.getSize().width/2, 
						((Node.getLocationOnPanel(recievingNode,owner.getPanel()).y+(recievingNode.getPreferredSize().height/2))+(Node.getLocationOnPanel(sendingNode,owner.getPanel()).y+(sendingNode.getPreferredSize().height/2)))/2 - this.getSize().height/2
				),
				getSize()));

		Node.connect(sendingNode, inputNode);
		Node.connect(outputNode, recievingNode);
	}
	private Cast getThis(){
		return this;
	}
	Cast(Point pos, Variable.DataType inputType, Variable.DataType outputType, GraphEditor owner){
		super(null, owner);
		owner.getPanel().add(this);
		Node inputNode = new Node(Node.NodeType.RECIEVING, getThis(), inputType){
			@Override
			void onConnect() {
				getThis().sendingNode = this.parents.get(0);
			}
		};
		Node outputNode = new Node(Node.NodeType.SENDING, this, outputType,true){
			@Override
			void onConnect() {
				getThis().sendingNode = this.children.get(this.children.size()-1);
			}
		};
		addInputNode(inputNode);
		addOutputNode(outputNode);
		input = inputType;
		output = outputType;
		
		setDiamondText();
		
		setBounds(new Rectangle(
				pos,
				getSize()));
	}
	private void setDiamondText(){
		URL url = null;
		try{
			url = Main.class.getResource("/images/white_diamond.png");
		}catch (Exception e){
			Out.printStackTrace(e);
		}		
		headerLabel.setIcon(new ImageIcon(url));
	}
	public Dimension getSize(){
		return new Dimension(45,super.getSize().height);
	}
	
	private static boolean isNumber(Variable.DataType dt){
		if(dt == Variable.DataType.DOUBLE || dt == Variable.DataType.INTEGER || dt == Variable.DataType.FLOAT || dt == Variable.DataType.SHORT || dt == Variable.DataType.LONG){
			return true;
		}
		return false;
	}
	static boolean isCastable(Variable.DataType dataType, Variable.DataType dataType2) {
		
		if((isNumber(dataType) && isNumber(dataType2)) ||
				(dataType == Variable.DataType.CHARACTER && dataType2 == Variable.DataType.STRING) ||
				(dataType == Variable.DataType.BOOLEAN && dataType2 == Variable.DataType.INTEGER) ||
				(dataType2 == Variable.DataType.STRING && dataType != Variable.DataType.GENERIC)
		){
			return true;
		}
		
		return false;
	}
	Variable.DataType getInput(){
		return input;
	}
	Variable.DataType getOutput(){
		return output;
	}
	@Override
	public VariableData execute(VariableData[] inputs){
		if(this.output == Variable.DataType.STRING){
			return new VariableData.String(inputs[0].getValueAsString());
		}
		Out.pln("CASTING "+sendingNode.dataType+" > "+recievingNode.dataType);
		
		switch(this.sendingNode.dataType){
		case BOOLEAN:
			Out.pln("sendingNode.dataType = BOOLEAN");
			switch(this.recievingNode.dataType){
			case INTEGER:
				return new VariableData.Integer((((VariableData.Boolean) inputs[0]).value) ? 1 : 0);
			default:
				return null;
			}
		case BYTE:
			switch(this.recievingNode.dataType){
			case DOUBLE:
				return new VariableData.Double((double) ((VariableData.Byte) inputs[0]).value);
			case FLOAT:
				return new VariableData.Float((float) ((VariableData.Byte) inputs[0]).value);
			case INTEGER:
				return new VariableData.Integer((int) ((VariableData.Byte) inputs[0]).value);
			case LONG:
				return new VariableData.Long((long) ((VariableData.Byte) inputs[0]).value);
			case SHORT:
				return new VariableData.Short((short) ((VariableData.Byte) inputs[0]).value);
			default:
				return null;
			
			}
		case CHARACTER:
			switch(this.recievingNode.dataType){
			case STRING:
				return new VariableData.String(Character.toString(((VariableData.Character) inputs[0]).value));
			default:
				return null;
			}
		case DOUBLE:
			Out.pln("sendingNode.dataType = DOUBLE");
			switch(this.recievingNode.dataType){
			case BYTE:
				return new VariableData.Byte((byte) ((VariableData.Double) inputs[0]).value);
			case FLOAT:
				return new VariableData.Float((float) ((VariableData.Double) inputs[0]).value);
			case INTEGER:
				return new VariableData.Integer((int) ((VariableData.Double) inputs[0]).value);
			case LONG:
				return new VariableData.Long((long) ((VariableData.Double) inputs[0]).value);
			case SHORT:
				return new VariableData.Short((short) ((VariableData.Double) inputs[0]).value);
			default:
				return null;
			
			}
		case FLOAT:
			Out.pln("sendingNode.dataType = FLOAT");
			switch(this.recievingNode.dataType){
			case BYTE:
				return new VariableData.Byte((byte) ((VariableData.Float) inputs[0]).value);
			case DOUBLE:
				return new VariableData.Double((double) ((VariableData.Float) inputs[0]).value);
			case INTEGER:
				return new VariableData.Integer((int) ((VariableData.Float) inputs[0]).value);
			case LONG:
				return new VariableData.Long((long) ((VariableData.Float) inputs[0]).value);
			case SHORT:
				return new VariableData.Short((short) ((VariableData.Float) inputs[0]).value);
			default:
				return null;
			
			}
		case INTEGER:
			Out.pln("sendingNode.dataType = INT");
			switch(this.recievingNode.dataType){
			case BYTE:
				return new VariableData.Byte((byte) ((VariableData.Integer) inputs[0]).value);
			case FLOAT:
				return new VariableData.Float((float) ((VariableData.Integer) inputs[0]).value);
			case DOUBLE:
				return new VariableData.Double((double) ((VariableData.Integer) inputs[0]).value);
			case LONG:
				return new VariableData.Long((long) ((VariableData.Integer) inputs[0]).value);
			case SHORT:
				return new VariableData.Short((short) ((VariableData.Integer) inputs[0]).value);
			default:
				return null;
			
			}
		case LONG:
			switch(this.recievingNode.dataType){
			case BYTE:
				return new VariableData.Byte((byte) ((VariableData.Long) inputs[0]).value);
			case FLOAT:
				return new VariableData.Float((float) ((VariableData.Long) inputs[0]).value);
			case DOUBLE:
				return new VariableData.Double((double) ((VariableData.Long) inputs[0]).value);
			case INTEGER:
				return new VariableData.Integer((int) ((VariableData.Long) inputs[0]).value);
			case SHORT:
				return new VariableData.Short((short) ((VariableData.Long) inputs[0]).value);
			default:
				return null;
			
			}
		case SHORT:
			switch(this.recievingNode.dataType){
			case BYTE:
				return new VariableData.Byte((byte) ((VariableData.Short) inputs[0]).value);	
			case FLOAT:
				return new VariableData.Float((float) ((VariableData.Short) inputs[0]).value);
			case DOUBLE:
				return new VariableData.Double((double) ((VariableData.Short) inputs[0]).value);
			case INTEGER:
				return new VariableData.Integer((int) ((VariableData.Short) inputs[0]).value);
			case LONG:
				return new VariableData.Long((long) ((VariableData.Short) inputs[0]).value);
			default:
				return null;
			
			}
		default:
			return null;
		
		}
		
	}
	
}