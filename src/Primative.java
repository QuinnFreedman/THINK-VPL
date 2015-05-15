import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class Primative extends VObject implements DocumentListener, ContainsChildFunctions{
	SpecialEditorPane valueField;
	Node childNode;
	SpecialEditorPane headerTextField;
	DataType dataType;
	static final Border bodyPadding = new EmptyBorder(5,10,5,10);
	protected ArrayList<PrimativeFunction> functions = new ArrayList<PrimativeFunction>();
	Primative(){
		super();
		headerTextField = new SpecialEditorPane();
		//headerTextField.setBackground(color);
		headerTextField.setPreferredSize(new Dimension(50,20));
		headerTextField.setOpaque(false);
		header.add(headerTextField);
		body.setLayout(new BorderLayout());
		valueField = new SpecialEditorPane();
		valueField.setPreferredSize(new Dimension(90,18));
		valueField.setOpaque(false);
		valueField.getDocument().addDocumentListener(this);
		valueField.setBorder(bodyPadding);
		this.body.add(valueField,BorderLayout.CENTER);
		childNode = new Node(Node.NodeType.INHERITANCE_SENDING,this);
		childNode.setBorder(bodyPadding);
		Main.nodes.add(childNode);
		this.body.add(childNode,BorderLayout.LINE_END);
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
	public ArrayList<PrimativeFunction> getFunctions() {
		return functions;
	}
}