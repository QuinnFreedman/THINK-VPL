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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
	
	Color defaultColor;
	
	Constant(Point pos, Variable.DataType dt, GraphEditor owner){
		super(null, owner);
		this.dt = dt;
		color = Main.colors.get(dt);
		
		GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
		
        editor = new JTextField();
		editor.setColumns(4);
		editor.setOpaque(false);
		editor.getDocument().addDocumentListener(this);
		editor.requestFocusInWindow();
		
		defaultColor = editor.getBackground();
		
		if(dt == Variable.DataType.BOOLEAN){
			new VBoolean.BooleanDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("false");
		}else if(dt == Variable.DataType.INTEGER || dt == Variable.DataType.LONG || dt == Variable.DataType.SHORT || dt == Variable.DataType.BYTE){
			new VInt.IntDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("0");
		}else if(dt == Variable.DataType.DOUBLE || dt == Variable.DataType.FLOAT){
			new VDouble.DoubleDocumentFilter((AbstractDocument) editor.getDocument());
			editor.setText("0");
		}else{
			update();
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
		update();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		update();
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		update();
		
	}
	
	private void update(){
		try{
			switch(dt){
			case BOOLEAN:
				this.value = new VariableData.Boolean(Boolean.parseBoolean(editor.getText()));
				break;
			case BYTE:
				this.value = new VariableData.Byte(Byte.parseByte(editor.getText()));
				break;
			case CHARACTER:
				this.value = new VariableData.Character(editor.getText().charAt(0));
				break;
			case DOUBLE:
				this.value = new VariableData.Double(Double.parseDouble(editor.getText()));
				break;
			case FLOAT:
				this.value = new VariableData.Float(Float.parseFloat(editor.getText()));
				break;
			case INTEGER:
				this.value = new VariableData.Integer(Integer.parseInt(editor.getText()));
				break;
			case LONG:
				this.value = new VariableData.Long(Long.parseLong(editor.getText()));
				break;
			case SHORT:
				this.value = new VariableData.Short(Short.parseShort(editor.getText()));
				break;
			case STRING:
				this.value = new VariableData.String(editor.getText());
				break;
			default:
				break;
			}
			editor.setBackground(defaultColor);
		}catch(Exception e){
			switch(dt){
			case BOOLEAN:
				this.value = new VariableData.Boolean(false);
				break;
			case BYTE:
				this.value = new VariableData.Byte((byte) 0);
				break;
			case CHARACTER:
				this.value = new VariableData.Character(' ');
				break;
			case DOUBLE:
				this.value = new VariableData.Double(0d);
				break;
			case FLOAT:
				this.value = new VariableData.Float(0f);
				break;
			case INTEGER:
				this.value = new VariableData.Integer(0);
				break;
			case LONG:
				this.value = new VariableData.Long(0);
				break;
			case SHORT:
				this.value = new VariableData.Short((short) 0);
				break;
			default:
				break;
			}
			editor.setBackground(Color.YELLOW);
		}
	}
	
}