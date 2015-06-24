/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class Cast extends Executable{
	private static final long serialVersionUID = 1L;
	private Node sendingNode;
	private Node recievingNode;
	private Variable.DataType input;
	private Variable.DataType output;
	protected Cast getThis(){
		return this;
	}
	Cast(Node sendingNode, Node recievingNode){
		super(null, sendingNode.parentObject.owner);
		this.color = Color.BLACK;
		
		System.out.println("CAST "+sendingNode.dataType+" > "+recievingNode.dataType);
		
    	getThis().sendingNode = sendingNode;
    	getThis().recievingNode = recievingNode;
    	owner.getPanel().add(getThis());
		Node inputNode = new Node(Node.NodeType.RECIEVING, getThis(), sendingNode.dataType);
		Node outputNode = new Node(Node.NodeType.SENDING, getThis(), recievingNode.dataType,true);
		addInputNode(inputNode);
		addOutputNode(outputNode);
		input = inputNode.dataType;
		output = outputNode.dataType;
		setBounds(new Rectangle(
				new Point(
						((Node.getLocationOnPanel(recievingNode,owner.getPanel()).x+(recievingNode.getPreferredSize().width/2))+(Node.getLocationOnPanel(sendingNode,owner.getPanel()).x+(sendingNode.getPreferredSize().width/2)))/2 - getThis().getSize().width/2, 
						((Node.getLocationOnPanel(recievingNode,owner.getPanel()).y+(recievingNode.getPreferredSize().height/2))+(Node.getLocationOnPanel(sendingNode,owner.getPanel()).y+(sendingNode.getPreferredSize().height/2)))/2 - getThis().getSize().height/2
				),
				getSize()));

		Node.connect(sendingNode, inputNode);
		Node.connect(outputNode, recievingNode);
	}
	
	private static boolean isNumber(Variable.DataType dt){
		if(dt == Variable.DataType.DOUBLE || dt == Variable.DataType.INTEGER || dt == Variable.DataType.FLOAT || dt == Variable.DataType.SHORT || dt == Variable.DataType.LONG){
			return true;
		}
		return false;
	}
	public static boolean isCastable(Variable.DataType dataType, Variable.DataType dataType2) {
		
		if((isNumber(dataType) && isNumber(dataType2)) ||
				(dataType == Variable.DataType.CHARACTER && dataType2 == Variable.DataType.STRING) ||
				(dataType == Variable.DataType.BOOLEAN && dataType2 == Variable.DataType.INTEGER) ||
				(dataType2 == Variable.DataType.STRING && dataType != Variable.DataType.GENERIC)
		){
			return true;
		}
		
		return false;
	}
	@Override
	public VariableData execute(VariableData[] inputs){
		if(this.output == Variable.DataType.STRING){
			return new VariableData.String(inputs[0].getValueAsString());
		}
		System.out.println("CASTING "+sendingNode.dataType+" > "+recievingNode.dataType);
		
		switch(this.sendingNode.dataType){
		case BOOLEAN:
			System.out.println("sendingNode.dataType = BOOLEAN");
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
			System.out.println("sendingNode.dataType = DOUBLE");
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
			System.out.println("sendingNode.dataType = FLOAT");
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
			System.out.println("sendingNode.dataType = INT");
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