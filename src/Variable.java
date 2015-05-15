import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class Variable extends SidebarItem implements DocumentListener, ContainsChildFunctions{
	SpecialEditorPane valueField;
	DataType dataType;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	protected ArrayList<PrimitiveFunction> functions = new ArrayList<PrimitiveFunction>();
	Variable(){
		super();
		JPanel header = new JPanel(new FlowLayout());
		
		typeField = new SpecialEditorPane();
		typeField.setPreferredSize(new Dimension(50,20));
		typeField.setOpaque(false);
		header.add(typeField);
		
		nameField = new SpecialEditorPane();
		nameField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		nameField.setPreferredSize(new Dimension(50,20));
		//nameField.setOpaque(false);
		header.add(nameField);
		
		JPanel body = new JPanel(new FlowLayout());
		
		body.setLayout(new BorderLayout());
		valueField = new SpecialEditorPane();
		valueField.setPreferredSize(new Dimension(90,18));
		valueField.setOpaque(false);
		valueField.getDocument().addDocumentListener(this);
		valueField.setBorder(bodyPadding);
		body.add(valueField,BorderLayout.CENTER);
		
		this.add(header);
		
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