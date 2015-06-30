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
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import think.Variable.DataType;

class Logic extends Executable{
	private static final long serialVersionUID = 1L;
	
	
	protected String getID(){
		return null;
	}
	
	@Override
	public ArrayList<Variable.DataType> getOutputs(){
		return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.BOOLEAN));
	}
	
	Logic(Point pos, GraphEditor owner){
		super(pos, owner);
		this.color = Color.DARK_GRAY;
		
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		headerLabel.setText(getID());

	}
	
	Logic() {
		
	}
	
	static class Equals extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "=";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.FLEX,
					Variable.DataType.FLEX));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(VariableData.isNumber(inputs[0]) && VariableData.isNumber(inputs[0]))
				return new VariableData.Boolean((inputs[0].getValueAsDouble() == inputs[1].getValueAsDouble()));
			else
				return new VariableData.Boolean((inputs[0].getValueAsString().equals(inputs[1].getValueAsString())));
		}
		Equals(Point p, GraphEditor owner) {
			super(p, owner);
		}

		Equals() {
			
		}
		
	}
	
	static class Less_Than extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "<";
		}
		@Override
		String getMenuName() {
			return"Less Than (<)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() < inputs[1].getValueAsDouble()));
		}
		Less_Than(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Less_Than(){
			
		}
	}
	
	static class Greater_Than extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return ">";
		}
		@Override
		String getMenuName() {
			return"Greater Than (>)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() > inputs[1].getValueAsDouble()));
		}
		Greater_Than(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Greater_Than(){
			
		}
		
	}
	
	static class Less_Than_Or_Equal_To extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2264";
		}
		@Override
		String getMenuName() {
			return"Greater Than Or Equal To (\u2264)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() <= inputs[1].getValueAsDouble()));
		}
		Less_Than_Or_Equal_To(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Less_Than_Or_Equal_To(){
			
		}
		
	}
	
	static class Greater_Than_Or_Equal_To extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2265";
		}
		@Override
		String getMenuName() {
			return"Less Than Or Equal To (\u2265)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() >= inputs[1].getValueAsDouble()));
		}
		Greater_Than_Or_Equal_To(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Greater_Than_Or_Equal_To(){
			
		}
		
	}
	
	static class And extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "&";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(((VariableData.Boolean) inputs[0]).value && ((VariableData.Boolean) inputs[1]).value);
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN,
					Variable.DataType.BOOLEAN));
		}
		
		And(Point p, GraphEditor owner) {
			super(p, owner);
		}
		public And() {
			
		}
		
	}
	
	static class Or extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "||";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN,
					Variable.DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(((VariableData.Boolean) inputs[0]).value || ((VariableData.Boolean) inputs[1]).value);
		}
		Or(Point p, GraphEditor owner) {
			super(p, owner);
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()-6));
		        }});
		}

		Or() {
			
		}
		
	}
	
	static class Not extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "!";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(!((VariableData.Boolean) inputs[0]).value);
		}
		Not(Point p, GraphEditor owner) {
			super(p, owner);
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setBorder(new EmptyBorder(new Insets(-8,-1,-1,-1)));
		        }});
		}

		public Not() {
			
		}
		
	}
	
	static class Branch extends Executable{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		
		 boolean isTrue(){
			return isTrue;
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.BOOLEAN));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.GENERIC));
		}
		
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList("Condition"));
		}@Override
		public ArrayList<String> getOutputTooltips(){
			return new ArrayList<String>(Arrays.asList("If true...",
					"If false..."));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			isTrue = ((VariableData.Boolean) inputs[0]).value;
			return null;
		}
		Branch(Point pos, GraphEditor owner) {
			super(pos, owner);
			
		}

		public Branch() {
			
		}
		
	}
	static class While extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		
		@Override
		public boolean isContinue(){
			return isTrue;
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.BOOLEAN));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.GENERIC));
		}

		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList("Condition"));
		}
		@Override
		public ArrayList<String> getOutputTooltips(){
			return new ArrayList<String>(Arrays.asList("Repeat while true...",
					"When false..."));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			isTrue = ((VariableData.Boolean) inputs[0]).value;
			return null;
		}
		While(Point pos, GraphEditor owner) {
			super(pos, owner);
			
		}
		While(){
			
		}
		
	}
	
	static class For extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isContinue;
		
		private int max = -1;
		private int index = -1;
		
		@Override
		public boolean isContinue(){
			return isContinue;
		}
		
		@Override
		String getFunctionName() {
			return "For 0 . . . n";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER,
					Variable.DataType.GENERIC));
		}

		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList("Max"));
		}
		@Override
		public ArrayList<String> getOutputTooltips(){
			return new ArrayList<String>(Arrays.asList("Repeat while index < max...",
					"index",
					"When done..."));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(index == -1){
				max = ((VariableData.Integer) inputs[0]).value;
				index = 0;
				isContinue = true;
			}else{
				if(index < max){
					index++;
				}else{
					index = -1;
					isContinue = false;
				}
			}
			
			
			return new VariableData.Integer(index);
		}
		For(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.executeOnce = true;
		}
		For(){
			
		}
		
	}
	static class AdvancedFor extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isContinue;
		
		private VariableData.Array array;
		private int index = -1;
		
		@Override
		public boolean isContinue(){
			return isContinue;
		}
		
		@Override
		String getFunctionName() {
			return "For Element in List";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.ARRAY));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.FLEX,
					Variable.DataType.GENERIC));
		}

		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList("List"));
		}
		@Override
		public ArrayList<String> getOutputTooltips(){
			return new ArrayList<String>(Arrays.asList("Repeat while for each element in list...",
					"element",
					"When done..."));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			VariableData output = null;
			if(index == -1){
				array = ((VariableData.Array) inputs[0]);
				index = 0;
				isContinue = true;
				output = array.value.get(index);
				index++;
			}else{
				if(index < array.value.size()){
					output = array.value.get(index);
					index++;
				}else{
					index = -1;
					isContinue = false;
				}
			}
			
			
			return output;
		}
		AdvancedFor(Point pos, GraphEditor owner) {
			super(pos, owner);
			this.executeOnce = true;
		}
		AdvancedFor(){
			
		}
		
	}
	/*
	static class For extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean cont;
		
		private int index = -1;
		private int max = -1;
		
		@Override
		public boolean isContinue(){
			return cont;
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			Out.println("executing for...");
			if(index == -1){
				index = 0;
				max = ((VariableData.Integer) inputs[0]).value;
				cont = true;
			}else{
				index++;
				if(index == max){
					cont = false;
					index = -1;
				}
			}
			
			return new VariableData.Integer(index);
		}
		For(Point pos, GraphEditor owner) {
			super(pos, owner);
			//this.executeOnce = true;
			
		}
		For(){
			
		}
		
	}*/
	
	static class Sequence extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		 int activeOutNode = 0;
		
		public boolean isContinue(){
			return isTrue;
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return null;
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return null;
		}
		Sequence(Point pos, GraphEditor owner) {
			super(pos, owner);
			
			addOutputNode(new ReplicatingNode(Node.NodeType.RECIEVING, this, Variable.DataType.GENERIC));
		}
		
		public Sequence() {
			
		}

		@Override
		public Dimension getSize(){
			return new Dimension(Math.max(60,this.getPreferredSize().width),
					30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
		}
		
		private class ReplicatingNode extends Node{
			private static final long serialVersionUID = 1L;

			@Override
			 void onConnect(){
				boolean allConnected = true;
				for(Node n : ((Executable) parentObject).getOutputNodes()){
					if(n.children.isEmpty()){
						allConnected = false;
						break;
					}
				}
				if(allConnected){
					Out.println(parentObject.getPreferredSize().width);
					Out.println(((Executable) parentObject).outputNodeHolder.getPreferredSize().width);
					
					((Executable) parentObject).addOutputNode(new ReplicatingNode(Node.NodeType.SENDING, ((Executable) parentObject), Variable.DataType.GENERIC));

					parentObject.setBounds(new Rectangle(parentObject.getLocation(),parentObject.getSize()));
					owner.getPanel().repaint();
					owner.getPanel().revalidate();
					
					
				}
			}
			
			ReplicatingNode(NodeType type, Executable parent, Variable.DataType dt) {
				super(type, parent, dt);
			}
		}
	}
	
	static class Wait extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList("Wait time (miliseconds)"));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(Debug.getRunMode() == Debug.RunMode.RUN){
				try {
					Thread.sleep((long)((VariableData.Integer) inputs[0]).value);
				} catch (InterruptedException e) {
					Out.printStackTrace(e);
				}
			}
			return null;
		}
		Wait(Point pos, GraphEditor owner) {
			super(pos, owner);
		}
		Wait(){
			
		}
		
	}
}