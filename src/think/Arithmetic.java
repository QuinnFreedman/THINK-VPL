/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015 Quinn Freedman
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.border.EmptyBorder;

abstract class Arithmetic extends Executable implements Binop{
	private static final long serialVersionUID = 1L;
	
	protected String getID(){
		return null;
		
	}
	
	@Override
	public ArrayList<Variable.DataType> getInputs(){
		return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.NUMBER,
				Variable.DataType.NUMBER));
	}
	
	Arithmetic(Point pos, GraphEditor owner){
		super(pos,owner);
		this.color = Color.GRAY;

		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		headerLabel.setText(getID());
		
		addInputNode(new NumberNode(Node.NodeType.RECIEVING, this));
		addInputNode(new NumberNode(Node.NodeType.RECIEVING, this));
		 
		setBounds(new Rectangle(pos,getSize()));
	}
	
	Arithmetic(){};
	

	@Override
	public String getJavaBinop() {
		return getID();
	}
	
	static class NumberNode extends Node{
		private static final long serialVersionUID = 1L;

		@Override
		void onConnect(){
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
		void onDisconnect(){
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

		NumberNode(NodeType type, VObject parentObj, boolean b) {
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
		public String getMenuName() {
			return "Add (+)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.NUMBER));
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
			addOutputNode(new NumberNode(Node.NodeType.SENDING, this, true));
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
		public String getMenuName() {
			return "Subtract (-)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.NUMBER));
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
			addOutputNode(new NumberNode(Node.NodeType.SENDING, this, true));
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
		public String getJavaBinop() {
			return "*";
		}
		@Override
		public String getMenuName() {
			return "Multiply (\u00D7)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.NUMBER));
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
			addOutputNode(new NumberNode(Node.NodeType.SENDING, this, true));
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
		public String getJavaBinop() {
			return "/";
		}
		@Override
		public String getMenuName() {
			return "Divide (\u00F7)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
		}
		@Override
		public VariableData execute(VariableData[] inputs) throws Exception{
			if(inputs[1].getValueAsDouble() == 0){
				throw new Exception("Can't divide by zero");
			}
			return new VariableData.Double((inputs[0].getValueAsDouble() / inputs[1].getValueAsDouble()));
		}
		Divide(Point p, GraphEditor owner) {
			super(p, owner);
		}
		
		Divide(){};
		
	}
	static class Mod extends Arithmetic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "%";
		}
		
		@Override
		public String getMenuName() {
			return "Modulo (%)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(this.getOutputNodes().get(0).dataType == Variable.DataType.INTEGER)
				return new VariableData.Integer((int) (inputs[0].getValueAsDouble() % inputs[1].getValueAsDouble()));
			else if(this.getOutputNodes().get(0).dataType == Variable.DataType.FLOAT)
				return new VariableData.Float((float) (inputs[0].getValueAsDouble() % inputs[1].getValueAsDouble()));
			else
				return new VariableData.Double((inputs[0].getValueAsDouble() % inputs[1].getValueAsDouble()));
		}
		Mod(Point p, GraphEditor owner) {
			super(p, owner);
			addOutputNode(new NumberNode(Node.NodeType.SENDING, this, true));
		}
		
		Mod(){};
		
	}
	static class Concatinate extends Executable implements Binop{
		private static final long serialVersionUID = 1L;
		@Override
		public String getMenuName() {
			return "Concatinate (+)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.STRING,
					Variable.DataType.STRING));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.String((inputs[0].getValueAsString().concat(inputs[1].getValueAsString())));
		}
		
		Concatinate(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.color = Color.GRAY;
			
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("+");
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
		Concatinate(){}

		@Override
		public String getJavaBinop() {
			return "+";
		};
		
	}
	static class Random extends Executable implements JavaKeyword{
		private static final long serialVersionUID = 1L;
		
		@Override
		public String getMenuName() {
			return "Random (?)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.Double(Math.random());
		}
		Random(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.color = Color.GRAY;

			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("?");
			 
			setBounds(new Rectangle(pos,getSize()));
		        
		}
		
		Random(){}

		@Override
		public String getJavaKeyword() {
			return "Math.random";
		}
		
	}
	static class Round extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public String getMenuName() {
			return "Round (\u2248)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			
			return new VariableData.Double(Math.round(inputs[0].getValueAsDouble()));
		}
		Round(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.color = Color.GRAY;
			
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("\u2248");
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
		Round(){};
		
	}
}