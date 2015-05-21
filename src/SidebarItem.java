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
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class SidebarItem extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	protected Type type;
	protected JPanel header;
	protected InputPane nameField;
	protected InputPane typeField;
	protected ArrayList<InputPane> fields = new ArrayList<InputPane>();
	private SidebarItem getThis(){
		return this;
	}
	public enum Type{
    	VARIABLE,FUNCTION,CLASS
    }
	
	public String getSymbol(){
		String symbol = "";

		switch (this.type) {
		case VARIABLE:
			switch(((Variable) this).dataType){
	        case BOOLEAN: symbol = "bool";
	        	break;
	
	        case BYTE: symbol = "byt";
		        break;
		        
	        case SHORT: symbol = "sh";
		        break;
		        
	        case INTEGER: symbol = "int";
		        break;
		        
	        case FLOAT: symbol = "fl";
		        break;
		        
	        case DOUBLE: symbol = "db";
		        break;
		        
	        case LONG: symbol = "long";
		        break;
		        
	        case CHARACTER: symbol = "char";
		        break;
		        
		    case STRING: symbol = "str";
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
		
		return symbol;
	}
	SidebarItem(){
	  	setBorder(BorderFactory.createLineBorder(new Color(0x414141)));
    	setOpaque(false);
    	setLayout(new FlowLayout(FlowLayout.LEFT));

		header = new JPanel(new FlowLayout());
		
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
								Main.variables.remove(getThis());
								((Variable) getThis()).clearChildren();
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
		
		typeField = new TypeField(this);
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
	SidebarItem(Type t){
		this();
		this.type = t;
	}
	
	public void setEditable(boolean b){
		for(InputPane ip : fields){
			ip.setEditable(b);
		}
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		TypeField(SidebarItem si){
			super(si);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
				if(this.getText().toLowerCase().equals("i") || this.getText().toLowerCase().equals("in") || this.getText().toLowerCase().equals("int") || this.getText().toLowerCase().equals("integer")){
					VInt newint = new VInt();
					Main.variables.set(
							Main.variables.indexOf(sidebarItemParent),
							newint);
					Main.updateVars();
					newint.nameField.requestFocusInWindow();
				}else if(this.getText().toLowerCase().equals("d") || this.getText().toLowerCase().equals("db") || this.getText().toLowerCase().equals("do") || this.getText().toLowerCase().equals("double")){
					VDouble newdouble = new VDouble();
					Main.variables.set(
							Main.variables.indexOf(sidebarItemParent),
							newdouble);
					Main.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(this.getText().toLowerCase().equals("f") || this.getText().toLowerCase().equals("fl") || this.getText().toLowerCase().equals("flo") || this.getText().toLowerCase().equals("float")){
					VFloat newdouble = new VFloat();
					Main.variables.set(
							Main.variables.indexOf(sidebarItemParent),
							newdouble);
					Main.updateVars();
					newdouble.nameField.requestFocusInWindow();
				}else if(this.getText().toLowerCase().equals("b") || this.getText().toLowerCase().equals("bo") || this.getText().toLowerCase().equals("bool") || this.getText().toLowerCase().equals("boolean")){
					VBoolean newBool = new VBoolean();
					Main.variables.set(
							Main.variables.indexOf(sidebarItemParent),
							newBool);
					Main.updateVars();
					newBool.nameField.requestFocusInWindow();
				}else if(this.getText().toLowerCase().equals("s") || this.getText().toLowerCase().equals("st") || this.getText().toLowerCase().equals("str") || this.getText().toLowerCase().equals("string")){
					VString newStr = new VString();
					Main.variables.set(
							Main.variables.indexOf(sidebarItemParent),
							newStr);
					Main.updateVars();
					newStr.nameField.requestFocusInWindow();
				}else{
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
			if(si.getClass() != Variable.class && si instanceof Variable){//if datatype has been set
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
				switch(si.type){
				case VARIABLE:
					s = ((Variable) si).getSymbol();
					i = 1;
					break;
				case FUNCTION:
					s = "func";
					i = 1;
					break;
				case CLASS:
					s = "class";
					i = 1;
					break;
				}
			}
			ArrayList<? extends SidebarItem> list = null;
			switch(si.type){
			case VARIABLE:
				list = Main.variables;
				break;
			case FUNCTION:
				list = null;//TODO
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
			ip.setText(s+((i == -1) ? "" : i));
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