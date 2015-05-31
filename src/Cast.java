import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

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
		super(sendingNode.parentObject.owner);
		this.color = Color.BLACK;
		
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
	public void resetActiveNode() {
		activeNode = 0;
	};
	@Override
	public VariableData execute(VariableData[] inputs){
		VariableData output = null;
		if(this.output == Variable.DataType.STRING){
			output = new VariableData.String(inputs[0].getValueAsString());
		}
		switch(this.sendingNode.dataType){
		case BOOLEAN:
			switch(this.recievingNode.dataType){
			case INTEGER:
				output = new VariableData.Integer((((VariableData.Boolean) inputs[0]).value) ? 1 : 0);
			default:
				return null;
			}
		case BYTE:
			switch(this.recievingNode.dataType){
			case DOUBLE:
				output = new VariableData.Double((double) ((VariableData.Byte) inputs[0]).value);
				break;
			case FLOAT:
				output = new VariableData.Float((float) ((VariableData.Byte) inputs[0]).value);
				break;
			case INTEGER:
				output = new VariableData.Integer((int) ((VariableData.Byte) inputs[0]).value);
				break;
			case LONG:
				output = new VariableData.Long((long) ((VariableData.Byte) inputs[0]).value);
				break;
			case SHORT:
				output = new VariableData.Short((short) ((VariableData.Byte) inputs[0]).value);
				break;
			default:
				break;
			
			}
		case CHARACTER:
			switch(this.recievingNode.dataType){
			case STRING:
				output = new VariableData.String(Character.toString(((VariableData.Character) inputs[0]).value));
				break;
			default:
				break;
			}
		case DOUBLE:
			switch(this.recievingNode.dataType){
			case BYTE:
				output = new VariableData.Byte((byte) ((VariableData.Double) inputs[0]).value);
				break;
			case FLOAT:
				output = new VariableData.Float((float) ((VariableData.Double) inputs[0]).value);
				break;
			case INTEGER:
				output = new VariableData.Integer((int) ((VariableData.Double) inputs[0]).value);
				break;
			case LONG:
				output = new VariableData.Long((long) ((VariableData.Double) inputs[0]).value);
				break;
			case SHORT:
				output = new VariableData.Short((short) ((VariableData.Double) inputs[0]).value);
				break;
			default:
				return null;
			
			}
		case FLOAT:
			switch(this.recievingNode.dataType){
			case BYTE:
				output = new VariableData.Byte((byte) ((VariableData.Float) inputs[0]).value);
				break;
			case DOUBLE:
				output = new VariableData.Double((double) ((VariableData.Float) inputs[0]).value);
				break;
			case INTEGER:
				output = new VariableData.Integer((int) ((VariableData.Float) inputs[0]).value);
				break;
			case LONG:
				output = new VariableData.Long((long) ((VariableData.Float) inputs[0]).value);
				break;
			case SHORT:
				output = new VariableData.Short((short) ((VariableData.Float) inputs[0]).value);
				break;
			default:
				break;
			
			}
		case INTEGER:
			switch(this.recievingNode.dataType){
			case BYTE:
				output = new VariableData.Byte((byte) ((VariableData.Integer) inputs[0]).value);
				break;
			case FLOAT:
				output = new VariableData.Float((float) ((VariableData.Integer) inputs[0]).value);
				break;
			case DOUBLE:
				output = new VariableData.Double((double) ((VariableData.Integer) inputs[0]).value);
				break;
			case LONG:
				output = new VariableData.Long((long) ((VariableData.Integer) inputs[0]).value);
				break;
			case SHORT:
				output = new VariableData.Short((short) ((VariableData.Integer) inputs[0]).value);
				break;
			default:
				break;
			
			}
		case LONG:
			switch(this.recievingNode.dataType){
			case BYTE:
				output = new VariableData.Byte((byte) ((VariableData.Long) inputs[0]).value);
				break;
			case FLOAT:
				output = new VariableData.Float((float) ((VariableData.Long) inputs[0]).value);
				break;
			case DOUBLE:
				output = new VariableData.Double((double) ((VariableData.Long) inputs[0]).value);
				break;
			case INTEGER:
				output = new VariableData.Integer((int) ((VariableData.Long) inputs[0]).value);
				break;
			case SHORT:
				output = new VariableData.Short((short) ((VariableData.Long) inputs[0]).value);
				break;
			default:
				break;
			
			}
		case SHORT:
			switch(this.recievingNode.dataType){
			case BYTE:
				output = new VariableData.Byte((byte) ((VariableData.Short) inputs[0]).value);
				break;
			case FLOAT:
				output = new VariableData.Float((float) ((VariableData.Short) inputs[0]).value);
				break;
			case DOUBLE:
				output = new VariableData.Double((double) ((VariableData.Short) inputs[0]).value);
				break;
			case INTEGER:
				output = new VariableData.Integer((int) ((VariableData.Short) inputs[0]).value);
				break;
			case LONG:
				output = new VariableData.Long((long) ((VariableData.Short) inputs[0]).value);
				break;
			default:
				break;
			
			}
		default:
			break;
		
		}
		
		return output;
		
	}
	
}