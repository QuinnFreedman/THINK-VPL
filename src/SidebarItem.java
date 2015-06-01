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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SidebarItem extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private GraphEditor owner;
	protected String name;
	protected Type type;
	public boolean isStatic = true;
	public VInstance parentInstance = null;
	protected JPanel header;
	protected InputPane nameField;
	protected InputPane typeField;
	protected ArrayList<InputPane> fields = new ArrayList<InputPane>();
	static protected BufferedImage bufferedImage = null;
	
	public void setID(String name){
		this.name = name;
	}
	public String getID(){
		return name;
	}
	public String getFullName(){
		return ((!isStatic) ? parentInstance.getID()+" > " : "")+name;
	}
	public GraphEditor getOwner(){
		return owner;
	}
	private SidebarItem getThis(){
		return this;
	}
	public enum Type{
    	VARIABLE,FUNCTION,CLASS
    }
	
	public String getSymbol(){
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
		    case OBJECT: symbol += ((VInstance) this).parentBlueprint.getName();
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
	SidebarItem(GraphEditor owner){
		this.owner = owner;
	  	setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0x414141)), new EmptyBorder(new Insets(0,0,0,-5))));
    	setOpaque(false);
    	setLayout(new FlowLayout(FlowLayout.LEFT));
		
    	
		header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel close = new JLabel("\u00D7");
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
							if(getThis().type == Type.VARIABLE){
								getThis().owner.getVariables().remove(getThis());
								((Variable) getThis()).clearChildren();
							}else if(getThis().type == Type.FUNCTION){
								((Blueprint) getThis().owner).getFunctions().remove(getThis());
								((VFunction) getThis()).clearChildren();
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
	
	public void setEditable(boolean b){
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
		
		public SidebarItem sidebarItemParent;
		
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
				if(text.equals("i") || text.equals("in") || text.equals("int") || text.equals("integer")){
					VInt newint = new VInt(owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newint);
					owner.updateVars();
					newint.nameField.requestFocusInWindow();
				}else if(text.equals("d") || text.equals("db") || text.equals("do") || text.equals("double")){
					VDouble newdouble = new VDouble(owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newdouble);
					owner.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(text.equals("f") || text.equals("fl") || text.equals("flo") || text.equals("float")){
					VFloat newdouble = new VFloat(owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newdouble);
					owner.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(text.equals("b") || text.equals("bo") || text.equals("bool") || text.equals("boolean")){
					VBoolean newBool = new VBoolean(owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newBool);
					owner.updateVars();
					newBool.nameField.requestFocusInWindow();
				}else if(text.equals("s") || text.equals("st") || text.equals("str") || text.equals("string")){
					VString newStr = new VString(owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newStr);
					owner.updateVars();
					newStr.nameField.requestFocusInWindow();
				}
				
			//ARRAYS
				else if(text.equals("<i") || text.equals("<in") || text.equals("<int") || text.equals("<int>")){
					VArray newint = new VArray(Variable.DataType.INTEGER,owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newint);
					owner.updateVars();
					newint.nameField.requestFocusInWindow();
				}else if(text.equals("<d") || text.equals("<db") || text.equals("<do") || text.equals("<db>")){
					VArray newdouble = new VArray(Variable.DataType.DOUBLE,owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newdouble);
					owner.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(text.equals("<f") || text.equals("<fl") || text.equals("<fl>") || text.equals("<float>")){
					VArray newdouble = new VArray(Variable.DataType.FLOAT,owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newdouble);
					owner.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(text.equals("<b") || text.equals("<bo") || text.equals("<bool") || text.equals("<bool>")){
					VArray newBool = new VArray(Variable.DataType.BOOLEAN,owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newBool);
					owner.updateVars();
					newBool.nameField.requestFocusInWindow();
				}else if(text.equals("<s") || text.equals("<st") || text.equals("<str") || text.equals("<string")){
					VArray newStr = new VArray(Variable.DataType.STRING,owner);
					owner.getVariables().set(
							owner.getVariables().indexOf(sidebarItemParent),
							newStr);
					owner.updateVars();
					newStr.nameField.requestFocusInWindow();
				}else{
					for(Blueprint bp : Main.blueprints){
						if(text.equalsIgnoreCase(bp.getName())){
							VInstance newObj = new VInstance(owner, bp);
							owner.getVariables().set(
									owner.getVariables().indexOf(sidebarItemParent),
									newObj);
							owner.updateVars();
							newObj.nameField.requestFocusInWindow();
						}
					}
					return;
				}
				
			}
			
		}
	}
	static class NoDuplicateNames implements FocusListener{
		
		SidebarItem si;
		InputPane ip;
		
		NoDuplicateNames(InputPane ip){
			this.ip = ip;
			this.si = ip.sidebarItemParent;
		}
		
		@Override
		public void focusGained(FocusEvent arg0) {
			if((si.getClass() != Variable.class && si instanceof Variable) || si instanceof VFunction){//if datatype has been set
				Boolean b = fixName();
				if(b)
					this.ip.selectAll();
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if(si.getClass() != Variable.class && si instanceof Variable){
				fixName();
			}
		}
		public boolean fixName(){
			String s = ip.getText();
			int i = -1;
			if(ip.getText().length() == 0){
				if(si.getClass() == VArray.class){
					s = "Array";
					i = 1;
				}else{
				switch(si.type){
					case VARIABLE:
						s = ((Variable) si).getSymbol();
						i = 1;
						break;
					case FUNCTION:
						s = "function_";
						i = 1;
						break;
					case CLASS:
						s = "class";
						i = 1;
						break;
					}
				}
			}
			ArrayList<? extends SidebarItem> list = null;
			switch(si.type){
			case VARIABLE:
				list = si.owner.getVariables();
				break;
			case FUNCTION:
				list = ((Blueprint) si.owner).getFunctions();
				break;
			case CLASS:
				list = null;//TODO
				break;
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
		
	}
}