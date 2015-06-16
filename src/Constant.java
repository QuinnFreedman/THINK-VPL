import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
class Constant extends Executable implements DocumentListener{
	private static final long serialVersionUID = 1L;
	
	VariableData value;
	
	Variable.DataType dt;
	
	JTextField editor;
	
	Constant(Point pos, Variable.DataType dt, GraphEditor owner){
		super(owner);
		this.dt = dt;
		color = Main.colors.get(dt);
		
		body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(new EmptyBorder(new Insets(0, 7, 0, 7)));
		
        editor = new JTextField();
		editor.setColumns(4);
		editor.setOpaque(false);
		editor.getDocument().addDocumentListener(this);
		editor.requestFocusInWindow();
		
		if(dt == Variable.DataType.BOOLEAN){
			new VBoolean.BooleanDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("false");
		}else if(dt == Variable.DataType.INTEGER || dt == Variable.DataType.LONG || dt == Variable.DataType.SHORT || dt == Variable.DataType.BYTE){
			new VInt.IntDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("0");
		}else if(dt == Variable.DataType.DOUBLE || dt == Variable.DataType.FLOAT){
			new VDouble.DoubleDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("0");
		}
		
		editor.selectAll();
		
        body.add(editor,gbc);
		
		
		
		addOutputNode(new Node(Node.NodeType.SENDING,this,dt,true));
		
		setBounds(new Rectangle(pos,getSize()));
	}
	
	@Override
	public VariableData execute(VariableData[] inputs){
		return value;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// Auto-generated method stub
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		switch(dt){
		case BOOLEAN:
			this.value = new VariableData.Boolean((editor.getText().isEmpty()) ? false : Boolean.parseBoolean(editor.getText()));
			break;
		case BYTE:
			this.value = new VariableData.Byte((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Byte.parseByte(editor.getText()));
			break;
		case CHARACTER:
			this.value = new VariableData.Character((editor.getText().isEmpty()) ? ' ' : editor.getText().charAt(0));
			break;
		case DOUBLE:
			this.value = new VariableData.Double((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Double.parseDouble(editor.getText()));
			break;
		case FLOAT:
			this.value = new VariableData.Float((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Float.parseFloat(editor.getText()));
			break;
		case INTEGER:
			this.value = new VariableData.Integer((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Integer.parseInt(editor.getText()));
			break;
		case LONG:
			this.value = new VariableData.Long((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Long.parseLong(editor.getText()));
			break;
		case SHORT:
			this.value = new VariableData.Short((editor.getText().isEmpty() || editor.getText().equals("-")) ? 0 : Short.parseShort(editor.getText()));
			break;
		case STRING:
			this.value = new VariableData.String(editor.getText());
			break;
		default:
			break;
		}
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// Auto-generated method stub
		
	}
	
	
}