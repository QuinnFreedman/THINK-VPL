import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
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
	private String name;
	protected Type type;
	protected JPanel header;
	protected InputPane nameField;
	protected InputPane typeField;
	protected ArrayList<InputPane> fields = new ArrayList<InputPane>();
	protected SidebarItem getThis(){
		return this;
	}
	public enum Type{
    	VARIABLE,FUNCTION,CLASS
    }
	
	public String getSymbol(){
		String symbol = "";

		switch (this.type) {
		case VARIABLE:
			System.out.println(this.type);
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
							Main.variables.remove(getThis());
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
		
		typeField = new TypeField();
		typeField.setColumns(2);
		header.add(typeField);
		fields.add(typeField);
		
		nameField = new InputPane();
		nameField.setColumns(7);
		nameField.addIllegalChar(' ');
		header.add(nameField);
		fields.add(nameField);
		
		add(header);
	}
	SidebarItem(Type t){
		this();
		this.type = t;
	}
	@Override
	public Dimension getMaximumSize(){
		return new Dimension(32767,this.getPreferredSize().height);
	}

	static class InputPane extends SpecialEditorPane{
		InputPane(){
			super();
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
		protected void gotoNext(SidebarItem sidebarItemParent){
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
				if(this.getParent() != null && this.getParent().getParent() instanceof SidebarItem){
					gotoNext((SidebarItem) this.getParent().getParent());
				}
			}
			
		}
	}
	
	static class TypeField extends InputPane{
		TypeField(){
			super();
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
				if(this.getParent() != null && this.getParent().getParent() instanceof SidebarItem){
					SidebarItem sidebarItemParent = (SidebarItem) this.getParent().getParent();
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
					}else{
						return;
					}
					//gotoNext(sidebarItemParent);
				}
			}
			
		}
	}
}