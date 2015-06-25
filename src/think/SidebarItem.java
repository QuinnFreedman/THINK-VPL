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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

 class SidebarItem extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private GraphEditor owner;
	private String name;
	protected Type type;
	 boolean isStatic = true;
	 VInstance parentInstance = null;
	protected JPanel header;
	protected InputPane nameField;
	protected InputPane typeField;
	private JLabel close;
	protected ArrayList<InputPane> fields = new ArrayList<InputPane>();
	static protected BufferedImage bufferedImage = null;
	
	 void setID(String name){
		this.name = name;
	}
	 String getID(){
		return name;
	}
	 String getFullName(){
		return ((!isStatic && parentInstance != null) ? parentInstance.parentBlueprint.getName()+" > " : "")+name;
	}
	 GraphEditor getOwner(){
		return owner;
	}
	private SidebarItem getThis(){
		return this;
	}
	 enum Type{
    	VARIABLE,FUNCTION,CLASS
    }
	
	 String getSymbol(){
		String symbol = (this.getClass() == VArray.class) ? "<" : "";

		switch (this.type) {
		case VARIABLE:
			switch(((Variable) this).dataType){
	        case BOOLEAN: symbol += "bool";
	        	break;
	
	        case BYTE: symbol += "byt";
		        break;
		        
	        case SHORT: symbol += "sh";
		        break;
		        
	        case INTEGER: symbol += "int";
		        break;
		        
	        case FLOAT: symbol += "fl";
		        break;
		        
	        case DOUBLE: symbol += "db";
		        break;
		        
	        case LONG: symbol += "long";
		        break;
		        
	        case CHARACTER: symbol += "char";
		        break;
		        
		    case STRING: symbol += "str";
		        break;
		    case OBJECT:
		    	if(this instanceof VInstance){
	    			symbol += ((VInstance) this).parentBlueprint.getName();
		    	}else{
		    		if(((VArray) this).objectType != null)
		    			symbol += ((VArray) this).objectType.getName();
		    		else
		    			symbol += "obj";
		    	}
		    	break;
			default:
				break;
			}
			break;
        
        case FUNCTION: symbol = "\u0192";
        	break;
        
        case CLASS: symbol = "C";
        	break;
	    }
		
		symbol += (this.getClass() == VArray.class) ? ">" : "";
		
		return symbol;
	}
	
	 void setParentInstance(VInstance parentInstance){
		this.parentInstance = parentInstance;
		this.close.setText((parentInstance == null) ? "\u00D7" : ">");
		close.setEnabled(parentInstance == null);
		if(parentInstance != null){
			this.setBorder(new EmptyBorder(new Insets(0,0,0,-5)));
		}
	}
	
	SidebarItem(GraphEditor owner){
		if(owner == null)
			return;
		
		this.owner = owner;
	  	setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0x414141)), new EmptyBorder(new Insets(0,0,0,-5))));
    	setOpaque(false);
    	setLayout(new FlowLayout(FlowLayout.LEFT));
		
    	
		header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		close = new JLabel("\u00D7");
		close.setFont(close.getFont().deriveFont(Font.PLAIN, close.getFont().getSize()+8));
		close.setBorder(new EmptyBorder(new Insets(-7,-1,-1,-1)));
		close.addMouseListener(
					new MouseListener(){
						Color defaultColor = close.getForeground();
						{
							close.setCursor(new Cursor(Cursor.HAND_CURSOR));
						}

						@Override
						public void mouseClicked(MouseEvent e) {
							if(parentInstance != null){
								return;
							}
							if(getThis().type == Type.VARIABLE){
								getThis().owner.getVariables().remove(getThis());
								((Variable) getThis()).clearChildren();
								if(!getThis().isStatic && getThis().parentInstance == null){
									for(Variable v : Main.mainBP.getVariables()){
										if(v instanceof VInstance){
											((VInstance) v).removeChildVariable((Variable) getThis());
										}
									}
								}
							}else if(getThis().type == Type.FUNCTION){
								((Blueprint) getThis().owner).getFunctions().remove(getThis());
								((VFunction) getThis()).clearChildren();
								if(!getThis().isStatic && getThis().parentInstance == null){
									for(Variable v : Main.mainBP.getVariables()){
										if(v instanceof VInstance){
											((VInstance) v).removeChildFunction((VFunction) getThis());
										}
									}
								}
							}
							Container parent = getThis().getParent();
							parent.remove(getThis());
							parent.revalidate();
							parent.repaint();
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							// Auto-generated method stub
						}

						@Override
						public void mouseExited(MouseEvent e) {
							// Auto-generated method stub
						}

						@Override
						public void mousePressed(MouseEvent e) {
							close.setForeground(new Color(0x414141));
							
						}

						@Override
						public void mouseReleased(MouseEvent e) {
							close.setForeground(defaultColor);
							
					}});
		
		header.add(close);
		
		typeField = new TypeField(this,owner);
		typeField.setColumns(2);
		header.add(typeField);
		fields.add(typeField);
		
		nameField = new InputPane(this);
		nameField.setColumns(7);
		nameField.addIllegalChar(' ');
		nameField.addFocusListener(new NoDuplicateNames(nameField));
		header.add(nameField);
		fields.add(nameField);
		
		add(header);
	}
	SidebarItem(Type t,GraphEditor owner){
		this(owner);
		this.type = t;
	}
	
	 void setEditable(boolean b){
		for(InputPane ip : fields){
			ip.setEditable(b);
		}
	}
	
	protected void setChildTexts(String s){
		
	}
	
	@Override
	public Dimension getMaximumSize(){
		return new Dimension(32767,this.getPreferredSize().height);
	}

	static class InputPane extends SpecialEditorPane{
		private static final long serialVersionUID = 1L;
		
		 SidebarItem sidebarItemParent;
		
		InputPane(SidebarItem si){
			super();
			this.sidebarItemParent = si;
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
					//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
					setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(new Color(0x414141)),
							BorderFactory.createEmptyBorder(0, 3, 0, 3)
							));
		        }});
		}
		protected void gotoNext(){
			ArrayList<InputPane> fields = sidebarItemParent.fields;
			int index = fields.indexOf(this);
			if(index + 1 < fields.size()){
				fields.get(index+1).requestFocusInWindow();
			}else{
				if(sidebarItemParent.getParent() != null){
					sidebarItemParent.getParent().requestFocusInWindow();
				}
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
				gotoNext();
			}
			
		}
	}
	
	static class TypeField extends InputPane{
		private static final long serialVersionUID = 1L;
		
		private GraphEditor owner;
		
		TypeField(SidebarItem si, GraphEditor owner){
			super(si);
			this.owner = owner;
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
				String text = this.getText().toLowerCase();
				boolean isArray = false;
				if(text.startsWith("<")){
					isArray = true;
					text = text.substring(1);
				}
				
				for(Blueprint bp : Main.blueprints){
					if(text.equalsIgnoreCase(bp.getName()) && bp instanceof InstantiableBlueprint){
						if(isArray){
							VArray newObj = new VArray(Variable.DataType.OBJECT, (InstantiableBlueprint) bp, owner);
							owner.getVariables().set(
									owner.getVariables().indexOf(sidebarItemParent),
									newObj);
							owner.updateVars();
							newObj.nameField.requestFocusInWindow();
							return;
						}
						VInstance newObj = new VInstance(owner, (InstantiableBlueprint) bp);//TODO
						owner.getVariables().set(
								owner.getVariables().indexOf(sidebarItemParent),
								newObj);
						owner.updateVars();
						newObj.nameField.requestFocusInWindow();
						return;
					}
				}
				
				Variable.DataType varType = null;
				
				if(("integer").startsWith(text)){
					varType = Variable.DataType.INTEGER;
					
				}else if(("double").startsWith(text) || text.equals("db")){
					varType = Variable.DataType.DOUBLE;
					
				}else if(("float").startsWith(text)){
					varType = Variable.DataType.FLOAT;
					
				}else if(("boolean").startsWith(text)){
					varType = Variable.DataType.BOOLEAN;
					
				}else if(("string").startsWith(text)){
					varType = Variable.DataType.STRING;
					
				}else if(("object").startsWith(text)){
					varType = Variable.DataType.OBJECT;
					
				}
				
				if(varType != null){
					Variable newVar;
					if(!isArray){
						newVar = Variable.create(varType, owner);
					}else{
						newVar = new VArray(varType, owner);
					}
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newVar);
					owner.updateVars();
					newVar.nameField.requestFocusInWindow();
					
					if(owner instanceof InstantiableBlueprint){
						for(Variable v2 : Main.mainBP.getVariables()){
							if(v2 instanceof VInstance && ((VInstance) v2).parentBlueprint == owner){
								((VInstance) v2).addChildVariable(newVar);
							}
						}
					}
					
				}
			}
			
		}
	}
	static class NoDuplicateNames implements FocusListener{
		
		SidebarItem si;
		InputPane ip;
		JTextComponent tc;
		Blueprint blueprint;
		
		NoDuplicateNames(InputPane ip){
			this.ip = ip;
			this.tc = ip;
			this.si = ip.sidebarItemParent;
		}
		
		NoDuplicateNames(Blueprint bp, JTextField tf){
			this.tc = tf;
			this.blueprint = bp;
		}
		private Type getType(){
			if(si != null){
				return si.type;
			}else if(blueprint != null){
				return Type.CLASS;
			}
			return null;
		}
		
		@Override
		public void focusGained(FocusEvent arg0) {
			if(getType() == Type.CLASS){
				Boolean b = fixName();
				if(b)
					this.tc.selectAll();
			}else{
				if((si.getClass() != Variable.class && si instanceof Variable) || si instanceof VFunction){//if datatype has been set
					Boolean b = fixName();
					if(b)
						this.ip.selectAll();
				}
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			fixName();
			
		}
		 boolean fixName(){
			String s = tc.getText();
			int i = -1;
			if(tc.getText().length() == 0){
				if(si != null && si.getClass() == VArray.class){
					s = "Array";
					i = 1;
				}else{
					switch(getType()){
					case VARIABLE:
						s = ((Variable) si).getSymbol();
						i = 1;
						break;
					case FUNCTION:
						s = "function_";
						i = 1;
						break;
					case CLASS:
						s = "Blueprint_";
						i = 1;
						break;
					}
				}
			}
			ArrayList<? extends SidebarItem> list = null;
			switch(getType()){
			case VARIABLE:
				list = si.owner.getVariables();
				break;
			case FUNCTION:
				list = ((Blueprint) si.owner).getFunctions();
				break;
			case CLASS:
				ArrayList<Blueprint> list2 = Main.blueprints;
				while((isConflictBP(s, list2) && i == -1) ||
						isConflictBP(s+i, list2)
						){
					i++;
				}
				s += ((i == -1) ? "" : i);
				tc.setText(s);
				if(i == -1)
					return false;
				else
					return true;
			}
			while((isConflict(s, list) && i == -1) ||
					isConflict(s+i, list)
					){
				i++;
			}
			s += ((i == -1) ? "" : i);
			ip.setText(s);
			ip.sidebarItemParent.setID(s);
			if(i == -1)
				return false;
			else
				return true;
		}
		private boolean isConflict(String s, ArrayList<? extends SidebarItem> list){
			for(SidebarItem listItem : list){
				if(listItem != si && listItem.nameField.getText().equals(s)){
					return true;
				}
			}
			return false;
		}
		private boolean isConflictBP(String s, ArrayList<Blueprint> list){
			for(Blueprint bp : list){
				if(bp != blueprint && bp.getName().equals(s)){
					return true;
				}
			}
			return false;
		}
		
	}
}