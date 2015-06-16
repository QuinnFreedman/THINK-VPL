import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

class Arithmetic extends Executable{
	private static final long serialVersionUID = 1L;
	
	protected Arithmetic getThis() {
		return this;
	}
	protected String getID(){
		return null;
		
	}
	@Override
	public void resetActiveNode() {
		activeNode = 0;
	};
	Arithmetic(Point pos, GraphEditor owner){
		super(owner);
		this.color = Color.GRAY;

		body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		headerLabel = new JLabel();
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		headerLabel.setText(getID());
		body.add(headerLabel,gbc);
		
		addInputNode(new NumberNode(Node.NodeType.RECIEVING, getThis()));
		addInputNode(new NumberNode(Node.NodeType.RECIEVING, getThis()));
		 
		setBounds(new Rectangle(pos,getSize()));
	}
	
	Arithmetic(){};
	
	static class NumberNode extends Node{
		private static final long serialVersionUID = 1L;

		@Override
		public void onConnect(){
			if(this.dataType == Variable.DataType.NUMBER){
				Node connectingNode = (this.type == NodeType.RECIEVING) ? this.parents.get(this.parents.size()-1) : this.children.get(this.children.size()-1);
				this.dataType = connectingNode.dataType;
				if(this.type == NodeType.RECIEVING){
					if(this.parentObject.getClass() != Divide.class){
						Variable.DataType outputType = Variable.DataType.INTEGER;
						
						for(Node inputNode : ((Executable) this.parentObject).getInputNodes()){
							if(inputNode.dataType == Variable.DataType.FLOAT)
								outputType = Variable.DataType.FLOAT;
						}
						for(Node inputNode : ((Executable) this.parentObject).getInputNodes()){
							if(inputNode.dataType == Variable.DataType.DOUBLE)
								outputType = Variable.DataType.DOUBLE;
						}
						
						if(outputType != ((Executable) this.parentObject).getOutputNodes().get(0).dataType){
							Node.clearChildren(((Executable) this.parentObject).getOutputNodes().get(0));
						}
						((Executable) this.parentObject).getOutputNodes().get(0).dataType = outputType;
					}
				}
			}
		}
		@Override
		public void onDisconnect(){
			ArrayList<Node> nodeList = (this.type == NodeType.RECIEVING) ? this.parents : this.children;
			if(nodeList.isEmpty()){
				if(this.type == NodeType.RECIEVING){
					this.dataType = Variable.DataType.NUMBER;
				
					boolean allNumbers = true;
					
					for(Node inputNode : ((Executable) this.parentObject).getInputNodes()){
						if(inputNode.dataType != Variable.DataType.NUMBER){
							allNumbers = false;
							break;
						}
					}
					if(this.parentObject.getClass() != Divide.class){
						
						Node outputNode = ((Executable) this.parentObject).getOutputNodes().get(0);
						
						if(allNumbers){
							if(outputNode.children.isEmpty()){
								outputNode.dataType = Variable.DataType.NUMBER;
							}else{
								outputNode.dataType = outputNode.children.get(0).dataType;
							}
						}else{
						
							Variable.DataType outputType = Variable.DataType.INTEGER;
							
							for(Node inputNode : ((Executable) this.parentObject).getInputNodes()){
								if(inputNode.dataType == Variable.DataType.FLOAT)
									outputType = Variable.DataType.FLOAT;
							}
							for(Node inputNode : ((Executable) this.parentObject).getInputNodes()){
								if(inputNode.dataType == Variable.DataType.DOUBLE)
									outputType = Variable.DataType.DOUBLE;
							}

							if(outputType != ((Executable) this.parentObject).getOutputNodes().get(0).dataType){
								Node.clearChildren(((Executable) this.parentObject).getOutputNodes().get(0));
							}
							((Executable) this.parentObject).getOutputNodes().get(0).dataType = outputType;
						}
					}
				}
				
			}
		}
		
		NumberNode(NodeType type, VObject parentObj) {
			super(type, parentObj,Variable.DataType.NUMBER);
		}

		public NumberNode(NodeType type, VObject parentObj, boolean b) {
			super(type, parentObj,Variable.DataType.NUMBER,b);
		}
		
	}
	
	static class Add extends Arithmetic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "+";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			if(this.getOutputNodes().get(0).dataType == Variable.DataType.INTEGER)
				return new VariableData.Integer((int) (inputs[0].getValueAsDouble() + inputs[1].getValueAsDouble()));
			else if(this.getOutputNodes().get(0).dataType == Variable.DataType.FLOAT)
				return new VariableData.Float((float) (inputs[0].getValueAsDouble() + inputs[1].getValueAsDouble()));
			else
				return new VariableData.Double((inputs[0].getValueAsDouble() + inputs[1].getValueAsDouble()));
		}
		Add(Point p, GraphEditor owner) {
			super(p, owner);
			addOutputNode(new NumberNode(Node.NodeType.SENDING, getThis(), true));
		}
		
		Add(){};
		
	}
	static class Subtract extends Arithmetic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "-";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			if(this.getOutputNodes().get(0).dataType == Variable.DataType.INTEGER)
				return new VariableData.Integer((int) (inputs[0].getValueAsDouble() - inputs[1].getValueAsDouble()));
			else if(this.getOutputNodes().get(0).dataType == Variable.DataType.FLOAT)
				return new VariableData.Float((float) (inputs[0].getValueAsDouble() - inputs[1].getValueAsDouble()));
			else
				return new VariableData.Double((inputs[0].getValueAsDouble() - inputs[1].getValueAsDouble()));
		}
		Subtract(Point p, GraphEditor owner) {
			super(p, owner);
			addOutputNode(new NumberNode(Node.NodeType.SENDING, getThis(), true));
		}
		
		Subtract(){};
		
	}
	static class Multiply extends Arithmetic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u00D7";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			if(this.getOutputNodes().get(0).dataType == Variable.DataType.INTEGER)
				return new VariableData.Integer((int) (inputs[0].getValueAsDouble() * inputs[1].getValueAsDouble()));
			else if(this.getOutputNodes().get(0).dataType == Variable.DataType.FLOAT)
				return new VariableData.Float((float) (inputs[0].getValueAsDouble() * inputs[1].getValueAsDouble()));
			else
				return new VariableData.Double((inputs[0].getValueAsDouble() * inputs[1].getValueAsDouble()));
		}
		Multiply(Point p, GraphEditor owner) {
			super(p, owner);
			addOutputNode(new NumberNode(Node.NodeType.SENDING, getThis(), true));
		}
		
		Multiply(){};
		
	}
	static class Divide extends Arithmetic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u00F7";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Double((inputs[0].getValueAsDouble() * inputs[1].getValueAsDouble()));
		}
		Divide(Point p, GraphEditor owner) {
			super(p, owner);
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.DOUBLE, true));
		}
		
		Divide(){};
		
	}
	static class Concat extends Executable{
		private static final long serialVersionUID = 1L;
		
		private Concat getThis(){
			return this;
		}
		@Override
		public void resetActiveNode() {
			activeNode = 0;
		};
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.String((inputs[0].getValueAsString().concat(inputs[1].getValueAsString())));
		}
		Concat(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.GRAY;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("+");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.STRING));
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.STRING));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.STRING, true));
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
		Concat(){};
		
	}
	static class Random extends Executable{
		private static final long serialVersionUID = 1L;
		
		private Random getThis(){
			return this;
		}
		@Override
		public void resetActiveNode() {
			activeNode = 0;
		};
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.Double(Math.random());
		}
		Random(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.GRAY;

			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("?");
			body.add(headerLabel,gbc);
			
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.DOUBLE, true));
			 
			setBounds(new Rectangle(pos,getSize()));
		        
		}
		
		Random(){};
		
	}
	static class Round extends Executable{
		private static final long serialVersionUID = 1L;
		
		private Round getThis(){
			return this;
		}
		@Override
		public void resetActiveNode() {
			activeNode = 0;
		};
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.Double(Math.round(inputs[0].getValueAsDouble()));
		}
		Round(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.GRAY;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("\u2248");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.DOUBLE));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.DOUBLE, true));
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
		Round(){};
		
	}
}