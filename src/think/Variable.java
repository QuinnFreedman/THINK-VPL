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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import think.Variable.DataType;

public class Variable extends SidebarItem implements DocumentListener{
	private static final long serialVersionUID = 1L;
	
	InputPane valueField;
	DataType dataType;
	VariableData varData;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	protected ArrayList<PrimitiveFunction> functions = new ArrayList<PrimitiveFunction>();
	private ArrayList<PrimitiveFunction> children = new ArrayList<PrimitiveFunction>();
	protected PrimitiveFunctionSelector childPicker;
	private Variable originalVar;
	
	public void removeChild(PrimitiveFunction pf){
		children.remove(pf);
	}
	public void addChild(PrimitiveFunction pf){
		children.add(pf);
	}
	public void clearChildren(){
		while(children.size() > 0) {
			children.get(0).delete();
		}
		if(childPicker != null)
			childPicker.delete();
	}
	public void resetVariableData(){
		this.varData = null;
	}
	public Variable getOriginal(){
		if(parentInstance != null){
			return originalVar;
		}
		return this;
	}
	public void setOriginalVar(Variable v){
		originalVar = v;
	}
	private Variable getThis(){
		return this;
	}
	Variable(GraphEditor owner){
		super(owner);
		
		if(owner == null)
			return;
		
		this.type = Type.VARIABLE;
		
		if(owner instanceof InstantiableBlueprint){
			isStatic = false;
		}else{
			isStatic = true;
		}
		
		nameField.getDocument().addDocumentListener(new NameDocListener(this));
		
		valueField = new InputPane(this);
		valueField.setColumns(5);
		valueField.getDocument().addDocumentListener(this);
		header.add(valueField);
		fields.add(valueField);
		
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
				if(getThis().dataType != null){
					Point p = Node.getLocationOnPanel(e,owner.getPanel());
					if(getThis().childPicker != null){
						childPicker.delete();
					}
					if(p.x > 0 && p.y > 0 && p.x < owner.getPanel().getWidth() && p.y < owner.getPanel().getHeight()){
						PrimitiveFunctionSelector childPicker = new PrimitiveFunctionSelector(getThis(), p, owner);
						getThis().childPicker = childPicker;
					}
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
	
	protected void setValue(String s){
		//Overwritten in subclasses
	}
	@Override
	protected void setChildTexts(String s){
		if(!this.isStatic){
			for(Variable v : Main.mainBP.getVariables()){
				if(v instanceof VInstance){
					for(Variable v2 : ((VInstance) v).childVariables){
						if(v2.getOriginal() == this){
							v2.setID(this.getID());
							v2.nameField.setText(this.getID());
							for(PrimitiveFunction child : v2.children){
								child.setText(s);
							}
						}
					}
				}
			}
		}
		for(PrimitiveFunction child : children){
			child.setText(s);
		}
	}
	
    @Override
    public void removeUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			Out.printStackTrace(e1);
		}
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			Out.printStackTrace(e1);
		}
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			Out.printStackTrace(e1);
		}
    }
    static class NameDocListener implements DocumentListener{

    	SidebarItem var;
    	
    	NameDocListener(SidebarItem var){
    		this.var = var;
    	}
    
		@Override
	    public void removeUpdate(DocumentEvent e) {
	    	try {
	    		var.setID(e.getDocument().getText(0, e.getDocument().getLength()));
				var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				Out.printStackTrace(e1);
			}
	    }

	    @Override
	    public void insertUpdate(DocumentEvent e) {
	    	try {
	    		var.setID(e.getDocument().getText(0, e.getDocument().getLength()));
		    	var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				Out.printStackTrace(e1);
			}
	    }

	    @Override
	    public void changedUpdate(DocumentEvent e) {
	    	try {
	    		var.setID(e.getDocument().getText(0, e.getDocument().getLength()));
	    		var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				Out.printStackTrace(e1);
			}
	    }
    	
    }
    public enum DataType{
    	BOOLEAN,BYTE,SHORT,INTEGER,FLOAT,DOUBLE,LONG,CHARACTER,STRING,GENERIC,NUMBER,FLEX,OBJECT;

		public boolean isNumber() {
			if(this == BYTE || this == SHORT || this == INTEGER || this == FLOAT || this == DOUBLE || this == LONG)
				return true;
			else
				return false;
		}
    	
    }
	public ArrayList<PrimitiveFunction> getFunctions() {
		return functions;
	}
	public ArrayList<PrimitiveFunction> getChildren() {
		return children;
	}
	public static Variable create(DataType varType, GraphEditor owner) {
		switch(varType){
		case BOOLEAN:
			return new VBoolean(owner);
		case BYTE:
			return null;
		case CHARACTER:
			return null;
		case DOUBLE:
			return new VDouble(owner);
		case FLEX:
			return null;
		case FLOAT:
			return new VFloat(owner);
		case GENERIC:
			return null;
		case INTEGER:
			return new VInt(owner);
		case LONG:
			return null;
		case NUMBER:
			return null;
		case OBJECT:
			return null;
		case SHORT:
			return null;
		case STRING:
			return new VString(owner);
		default:
			return null;
		
		}
	}
}