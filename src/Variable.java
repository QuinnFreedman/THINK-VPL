import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class Variable extends SidebarItem implements DocumentListener, ContainsChildFunctions{
	InputPane valueField;
	DataType dataType;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	protected ArrayList<PrimitiveFunction> functions = new ArrayList<PrimitiveFunction>();
	protected Variable getThis(){
		return this;
	}
	Variable(){
		super();
		
		this.type = Type.VARIABLE;
		
		//TODO if is object w/ children, add all children to body, set body visible
		
		//JPanel body = new JPanel(new FlowLayout());
		
		valueField = new InputPane();
		valueField.setColumns(5);
		valueField.getDocument().addDocumentListener(this);
		header.add(valueField);
		fields.add(valueField);
		
		JRadioButton drag = new JRadioButton();
		drag.setFocusable(false);
		drag.setSelected(true);
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
					Point p = Node.getLocationOnPanel(e);
					if(p.x > 0 && p.y > 0 && p.x < Main.panel.getWidth() && p.y < Main.panel.getHeight()){
						Main.objects.add(new PrimitiveChildPicker(getThis(), p));
					}
				}
			}
			
		});
		header.add(drag);
		
	}
	
	protected void setValue(String s){
		//Overwritten in subclasses
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
    public enum DataType{
    	BOOLEAN,BYTE,SHORT,INTEGER,FLOAT,DOUBLE,LONG,CHARACTER,STRING,GENERIC
    }
	@Override
	public ArrayList<PrimitiveFunction> getFunctions() {
		return functions;
	}
}