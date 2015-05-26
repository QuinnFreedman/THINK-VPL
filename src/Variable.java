import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class Variable extends SidebarItem implements DocumentListener, ContainsChildFunctions{
	private static final long serialVersionUID = 1L;
	
	InputPane valueField;
	DataType dataType;
	VariableData varData;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	protected ArrayList<PrimitiveFunction> functions = new ArrayList<PrimitiveFunction>();
	private ArrayList<PrimitiveFunction> children = new ArrayList<PrimitiveFunction>();
	protected PrimitiveChildPicker childPicker;
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
	private Variable getThis(){
		return this;
	}
	Variable(GraphEditor owner){
		super(owner);
		
		this.type = Type.VARIABLE;
		
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
				if(getThis().dataType != null){
					drag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					Point p = Node.getLocationOnPanel(e,owner.getPanel());
					if(getThis().childPicker != null){
						childPicker.delete();
					}
					if(p.x > 0 && p.y > 0 && p.x < owner.getPanel().getWidth() && p.y < owner.getPanel().getHeight()){
						PrimitiveChildPicker childPicker = new PrimitiveChildPicker(getThis(), p, owner);
						Main.objects.add(childPicker);
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
		for(PrimitiveFunction child : children){
			child.setText(s);
		}
	}
	
    @Override
    public void removeUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    	try {
			setValue(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
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
				var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
	    }

	    @Override
	    public void insertUpdate(DocumentEvent e) {
	    	try {
		    	var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
	    }

	    @Override
	    public void changedUpdate(DocumentEvent e) {
	    	try {
	    		var.setChildTexts(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
	    }
    	
    }
    public enum DataType{
    	BOOLEAN,BYTE,SHORT,INTEGER,FLOAT,DOUBLE,LONG,CHARACTER,STRING,GENERIC,NUMBER,FLEX;

		public boolean isNumber() {
			if(this == BYTE || this == SHORT || this == INTEGER || this == FLOAT || this == DOUBLE || this == LONG)
				return true;
			else
				return false;
		}
    	
    }
	@Override
	public ArrayList<PrimitiveFunction> getFunctions() {
		return functions;
	}
}