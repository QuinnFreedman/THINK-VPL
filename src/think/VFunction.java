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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class VFunction extends SidebarItem implements FunctionOverseer{
	private static final long serialVersionUID = 1L;
	FunctionEditor editor;
	InputPane valueField;
	Variable.DataType dataType;
	VariableData varData;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	private ArrayList<UserFunc> children = new ArrayList<UserFunc>();
	private UserFunc currentlyExecuting = null;
	private ArrayList<Variable.DataType> input = new ArrayList<Variable.DataType>();
	private ArrayList<Variable.DataType> output = new ArrayList<Variable.DataType>();
	protected PrimitiveFunctionSelector childPicker;
	private boolean executeOnce = false;
	private VFunction originalFunc = null;
	private VariableData.Instance workingInstance = null;
	
	public VFunction getOriginal(){
		if(parentInstance != null){
			return originalFunc;
		}
		return this;
	}
	public void setOriginalFunc(VFunction f) {
		originalFunc = f;
		
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
	public void resetVariableData(){
		this.varData = null;
	}
	@Override
	public FunctionEditor.FunctionIO getInputObject(){
		return editor.getInputObject();
	}
	@Override
	public FunctionEditor.FunctionIO getOutputObject(){
		return editor.getOutputObject();
	}
	@Override
	public VariableData.Instance getWorkingInstance() {
		return workingInstance;
	}
	private VFunction getThis(){
		return this;
	}
	VFunction(GraphEditor owner){
		super(owner);
		
		this.type = Type.FUNCTION;
		
		if(owner instanceof InstantiableBlueprint){
			isStatic = false;
		}else{
			isStatic = true;
		}
		
		this.typeField.setText(getSymbol());
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		nameField.getDocument().addDocumentListener(new Variable.NameDocListener(this));
		
		JButton edit = new JButton("edit");
		edit.setPreferredSize(new Dimension(65,edit.getPreferredSize().height));
		header.add(edit);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(editor == null){
					editor = new FunctionEditor(getThis());
				}else if(!editor.isVisible()){
					editor.setVisible(true);
				}else{
					editor.requestFocus();
				}
			}
		});
		
		JLabel drag = new JLabel();
		ImageIcon image = new ImageIcon(bufferedImage);
		drag.setIcon(image);
		drag.setFocusable(false);
		drag.addMouseListener(new MouseListener(){{
			
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Auto-generated method stub
				
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
				drag.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				drag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				Point p = Node.getLocationOnPanel(e,owner.getPanel());
				if(p.x > 0 && p.y > 0 && p.x < owner.getPanel().getWidth() && p.y < owner.getPanel().getHeight()){
					new UserFunc(p,getOriginal(),getThis().getOwner());
				}
			}
			
		});
		header.add(drag);
		
	}
	
	@Override
	public void setEditable(boolean b){
		for(int i = 1; i < fields.size(); i++){
			fields.get(i).setEditable(b);
		}
	}
	
	@Override
	protected void setChildTexts(String s){
		if(!this.isStatic){
			for(Variable v : Main.mainBP.getVariables()){
				if(v instanceof VInstance){
					for(VFunction f : ((VInstance) v).childFunctions){
						if(f.getOriginal() == this){
							f.setID(this.getID());
							f.nameField.setText(this.getID());
						}
					}
				}
			}
		}
		for(UserFunc child : children){
			child.setText();
		}
		if(editor != null)
			this.editor.setTitle(this.getID());
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
		for(UserFunc f : children){
			f.addOutputNode(new Node(Node.NodeType.SENDING,f,dataType, (dataType == Variable.DataType.GENERIC) ? false : true));
			f.outputNodeHolder.revalidate();
			f.outputNodeHolder.repaint();
			f.setBounds(new Rectangle(f.getLocation(),f.getSize()));
			f.owner.getPanel().revalidate();
			f.owner.getPanel().repaint();
		}
	}
	
	@Override
	public void removeInput(int i){
		input.remove(i);
		for(UserFunc f : children){
			f.removeInputNode(f.getInputNodes().get(i));
		}
	}
	@Override
	public void removeOutput(int i){
		output.remove(i);
		for(UserFunc f : children){
			f.removeOutputNode(f.getOutputNodes().get(i));
		}
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
	public void setInput(ArrayList<Variable.DataType> dt) {
		input = dt;
	}
	@Override
	public void setOutput(ArrayList<Variable.DataType> dt) {
		output = dt;
	}
	@Override
	public void setCurrentlyExecuting(UserFunc f) {
		currentlyExecuting = f;
	}
	@Override
	public UserFunc getCurrentlyExecuting() {
		return currentlyExecuting;
	}
	public void setExecuteOnce(boolean b) {
		this.executeOnce = b;
		for(UserFunc f : children){
			f.executeOnce = b;
		}
	}
	@Override
	public boolean isStatic() {
		return isStatic;
	}
	@Override
	public boolean isEcexuteOnce() {
		return executeOnce;
	}
	
	public static Variable getVariable(ArrayList<Variable> variables, String s){
		Out.println("	looking for\""+s+"\" in "+variables);
		for(Variable v : variables){
			Out.println("		"+v);
			if(v.getID().equals(s))
				return v;
		}
		return null;
	}
	@Override
	public GraphEditor getEditor() {
		return editor;
	}
	
}