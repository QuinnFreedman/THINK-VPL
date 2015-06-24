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

package think;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class InstantiableBlueprint extends Blueprint implements FunctionOverseer, ComponentListener{
	
	private FunctionEditor.FunctionIO inputObject;
	private FunctionEditor.FunctionIO outputObject;
	private ArrayList<UserFunc> children = new ArrayList<UserFunc>();
	private UserFunc currentlyExecuting = null;
	private ArrayList<Variable.DataType> input = new ArrayList<Variable.DataType>();
	private ArrayList<Variable.DataType> output = new ArrayList<Variable.DataType>();
	private VariableData.Instance workingInstance;
	
	private NodeAdder adder = new NodeAdder(this);
	
	JTextField className;
	
	public void setWorkingInstance(VariableData.Instance instance) {
		this.workingInstance = instance;
		
	}
	
	public VariableData.Instance getWorkingInstance() {
		return workingInstance;
		
	}
	
	InstantiableBlueprint(){
		JPanel holder = new JPanel(new BorderLayout(0,0));
		className = new JTextField();
		holder.add(className,BorderLayout.PAGE_START);
		holder.add(varsHeader,BorderLayout.CENTER);
		varsContainer.add(holder,BorderLayout.PAGE_START);
		
		className.addFocusListener(new SidebarItem.NoDuplicateNames(this,className));
		
		InstantiableBlueprint THIS = this;
		className.getDocument().addDocumentListener(new DocumentListener(){{}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
	
			@Override
			public void insertUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
	
			@Override
			public void removeUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
			private void update(String s){
				setName(s);
				Main.tabbedPane.setTitleAt(Main.blueprints.indexOf(THIS),Main.crop(s,20));
			}
		});
		
		JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		buttonHolder.add(new JLabel("Add input node:"));
		
		JButton button = new JButton("Integer");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		button = new JButton("Double");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		button = new JButton("Float");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		button = new JButton("Boolean");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		button = new JButton("String");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		button = new JButton("Object");
		button.addActionListener(adder);
		buttonHolder.add(button);
		
		panelHolder.add(buttonHolder, BorderLayout.PAGE_START);
		
		inputObject = new FunctionEditor.FunctionIO(this, FunctionEditor.FunctionIO.Mode.INPUT);
		inputObject.addOutputNode(new Node(Node.NodeType.SENDING, inputObject, Variable.DataType.GENERIC));
		outputObject = new FunctionEditor.FunctionIO(this, FunctionEditor.FunctionIO.Mode.OUTPUT);
		outputObject.addInputNode(new Node(Node.NodeType.RECIEVING, outputObject, Variable.DataType.GENERIC));
		
		panel.addComponentListener(this);
		
		this.addInput(Variable.DataType.GENERIC);
		this.addOutput(Variable.DataType.GENERIC);
		this.addOutput(Variable.DataType.OBJECT);
	}
	
	
	@Override
	public FunctionEditor.FunctionIO getInputObject() {
		return inputObject;
	}

	@Override
	public FunctionEditor.FunctionIO getOutputObject() {
		return outputObject;
	}

	@Override
	public void addInput(Variable.DataType dataType){
		input.add(dataType);
		for(UserFunc f : children){
			f.addInputNode(new Node(Node.NodeType.RECIEVING,f,dataType, (dataType == Variable.DataType.GENERIC) ? true : false));
			f.inputNodeHolder.revalidate();
			f.inputNodeHolder.repaint();
		}
	}
	@Override
	public void addOutput(Variable.DataType dataType) {
		output.add(dataType);
		/*for(UserFunc f : children){
			f.addOutputNode(new Node(Node.NodeType.SENDING,f,dataType, (dataType == Variable.DataType.GENERIC) ? false : true));
			f.outputNodeHolder.revalidate();
			f.outputNodeHolder.repaint();
			f.setBounds(new Rectangle(f.getLocation(),f.getSize()));
			f.owner.getPanel().revalidate();
			f.owner.getPanel().repaint();
		}*/
	}
	
	@Override
	public void removeInput(int i){
		input.remove(i+1);
		for(UserFunc f : children){
			f.removeInputNode(f.getInputNodes().get(i));
		}
	}
	@Override
	public void removeOutput(int i){
		//output.remove(i);
		//for(UserFunc f : children){
		//	f.removeOutputNode(f.getOutputNodes().get(i));
		//}
	}
	@Override
	public ArrayList<Variable.DataType> getInput() {
		return input;
	}
	@Override
	public ArrayList<Variable.DataType> getOutput() {
		return output;
	}
	@Override
	public void setCurrentlyExecuting(UserFunc f) {
		currentlyExecuting = f;
	}
	@Override
	public UserFunc getCurrentlyExecuting() {
		return currentlyExecuting;
	}

	@Override
	public void removeChild(UserFunc f){
		children.remove(f);
	}
	@Override
	public void addChild(UserFunc f){
		children.add(f);
	}
	@Override
	public void clearChildren(){
		while(children.size() > 0) {
			children.get(0).delete();
		}
	}
	@Override
	public boolean isStatic() {
		return true;
	}
	@Override
	public void componentResized(ComponentEvent e) {
		inputObject.setLocation((panel.getWidth()-inputObject.getWidth())/2,10);
		outputObject.setLocation((panel.getWidth()-outputObject.getWidth())/2,panel.getHeight()-48);
		
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		//Auto-generated method stub
	}
	
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// Auto-generated method stub
	}
	
	@Override
	public void componentShown(ComponentEvent arg0) {
		// Auto-generated method stub
	}

	@Override
	public void setInput(ArrayList<Variable.DataType> dt) {
		input = dt;
	}

	@Override
	public void setOutput(ArrayList<Variable.DataType> dt) {
		output = dt;
	}


	@Override
	public boolean isEcexuteOnce() {
		//System.err.println("Error: incomplete 207");
		return true;
	}

	@Override
	public GraphEditor getEditor() {
		return this;
	}
	
	private class NodeAdder implements ActionListener{
		private InstantiableBlueprint overseer;

		@Override
		public void actionPerformed(ActionEvent e) {
			String s = ((JButton) e.getSource()).getText();
			Variable.DataType dataType;
			if(s.equals("Integer")){
				dataType = Variable.DataType.INTEGER;
			}else if(s.equals("Float")){
				dataType = Variable.DataType.FLOAT;
			}else if(s.equals("Double")){
				dataType = Variable.DataType.DOUBLE;
			}else if(s.equals("Boolean")){
				dataType = Variable.DataType.BOOLEAN;
			}else if(s.equals("String")){
				dataType = Variable.DataType.STRING;
			}else if(s.equals("Object")){
				dataType = Variable.DataType.OBJECT;
			}else{
				dataType = null;
			}
			inputObject.addOutputNode(new removeableNode(Node.NodeType.SENDING, inputObject, dataType, overseer));
			inputObject.revalidate();
			inputObject.repaint();
		}
		
		NodeAdder(InstantiableBlueprint overseer){
			this.overseer = overseer;
		}
			
	}
	class removeableNode extends Node{
		Node THIS = this;
		private InstantiableBlueprint overseer;
		
		public removeableNode(NodeType type, VObject parentObj, Variable.DataType dt, InstantiableBlueprint overseer) {
			super(type, parentObj, dt, true);
			this.overseer = overseer;
			overseer.addInput(dataType);
			this.addMouseListener(new Deleter());
			
		}
		
		private class Deleter implements MouseListener{
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					overseer.removeInput(((Executable) THIS.parentObject).getOutputNodes().indexOf(THIS)-1);
					((Executable) THIS.parentObject).removeOutputNode(THIS);
					
				}
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
		}
		
	}
}