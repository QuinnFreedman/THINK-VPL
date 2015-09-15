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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import jdk.nashorn.internal.objects.annotations.Constructor;

abstract class FlowControl{
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
		public String getFunctionName() {
			return "For 0 . . . n";
		}
		

		protected void resetIndex() {
			index = -1;
			max = -1;
			
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
				if(index < max - 1){
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
		public String getFunctionName() {
			return "For Element in List";
		}
		
		protected void resetIndex() {
			index = -1;
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
			Out.pln("executing for...");
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
			
			addOutputNode(new ReplicatingNode(Node.NodeType.SENDING, this, Variable.DataType.GENERIC));
		}
		
		public Sequence() {
			
		}

		@Override
		public Dimension getSize(){
			return new Dimension(Math.max(60,this.getPreferredSize().width),
					30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
		}
		
		static class ReplicatingNode extends Node{
			private static final long serialVersionUID = 1L;
			
			ArrayList<Node> holder;
			
			@Override
			 void onConnect(){
				boolean allConnected = true;
				for(Node n : holder){
					if(((type == NodeType.SENDING) ? n.children : n.parents).isEmpty()){
						allConnected = false;
						break;
					}
				}
				if(allConnected){
					Node newNode = null;
					try {
						java.lang.reflect.Constructor<? extends ReplicatingNode> constructor = this.getClass().getConstructor(
								NodeType.class, 
								Executable.class, 
								Variable.DataType.class);
						newNode = constructor.newInstance(this.type, ((Executable) parentObject), this.dataType);
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
					
					if(type == Node.NodeType.RECIEVING){
						((Executable) parentObject).addInputNode(newNode);
						((Executable) parentObject).setupInputTooltips();
					}else{
						((Executable) parentObject).addOutputNode(newNode);
						((Executable) parentObject).setupOutputTooltips();
					}
					parentObject.setBounds(new Rectangle(parentObject.getLocation(),parentObject.getSize()));
					parentObject.owner.getPanel().repaint();
					parentObject.owner.getPanel().revalidate();
					
					additionalOnConnect();
				}
			}
			
			protected void additionalOnConnect(){
				
			}
			
			public ReplicatingNode(NodeType type, Executable parent, Variable.DataType dt) {
				super(type, parent, dt);
				if(type == Node.NodeType.RECIEVING)
					holder = ((Executable) parentObject).getInputNodes();
				else
					holder = ((Executable) parentObject).getOutputNodes();
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