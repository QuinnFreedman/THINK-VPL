import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SidebarItem extends JPanel{
	private String name;
	private Type type;
	protected SpecialEditorPane nameField;
	protected SpecialEditorPane typeField;
	public enum Type{
    	BOOLEAN,BYTE,SHORT,INTEGER,FLOAT,DOUBLE,LONG,CHARACTER,STRING,FUNCTION
    }
	public void setName(String s){
		this.name = s;
		this.nameField.setText(s);
	}
	public String getSymbol(){
		String symbol = "";
				
		switch (this.type) {
	        case BOOLEAN: symbol = "B";
	                 break;
	
	        case BYTE: symbol = "by";
	        break;
	        
	        case SHORT: symbol = "sh";
	        break;
	        
	        case INTEGER: symbol = "I";
	        break;
	        
	        case FLOAT: symbol = "F";
	        break;
	        
	        case DOUBLE: symbol = "D";
	        break;
	        
	        case LONG: symbol = "L";
	        break;
	        
	        case CHARACTER: symbol = "C";
	        break;
	        
	        case STRING: symbol = "S";
	        break;
	        
	        case FUNCTION: symbol = "\u0192";
	        break;
	    }
		
		return symbol;
	}
	SidebarItem(){
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setOpaque(false);
	}
	SidebarItem(Type t, String s){
		this();
		this.type = t;
		this.setName(s);
	}
	@Override
	public Dimension getMaximumSize(){
		return new Dimension(32767,this.getPreferredSize().height);
	}
}