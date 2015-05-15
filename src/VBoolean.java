import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

class VBoolean extends Primative{
	static String name = "Boolean";
	static int idCounter = 0;
	boolean value = false;
	VBoolean(){
		super();
		this.dataType = Primative.DataType.BOOLEAN;
		this.headerLabel.setText(name);
		this.id = "Bool"+Integer.toString(idCounter);
		idCounter++;
		this.color = Main.colors.get(this.dataType);
		this.valueField.setText(Boolean.toString(value));
		new BooleanDocumentFilter((AbstractDocument) valueField.getDocument());
		this.width = 12;
		this.height = 5;
		this.position = getFreePosition();
		this.borderWidth = 10;
		this.setBounds(this.position.x*Main.gridWidth, this.position.y*Main.gridWidth, this.width*Main.gridWidth, this.height*Main.gridWidth);
		Main.panel.add(this);
		Main.panel.repaint();
		Main.panel.revalidate();
		this.functions.add(new Get());
		this.functions.add(new Set());
		this.functions.add(new Toggle());
	}
	public VBoolean(Point p) {
		this();
		//this.position = p;
		this.setBounds(p.x, p.y, this.width*Main.gridWidth, this.height*Main.gridWidth);
	}
	@Override
	protected void setValue(String s){
		value = (s.equals("true") || s.equals("True") || s.equals("TRUE"));
		valueField.getDocument().removeDocumentListener(this);
	}
	
	static class Get extends PrimativeFunction{
		public ArrayList<Primative.DataType> input = new ArrayList<Primative.DataType>();
		Get(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.BOOLEAN, parentNode, parent, null, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.BOOLEAN)));
		}
		Get(){
			super();
		}
		
	}
	static class Set extends PrimativeFunction{
		public ArrayList<Primative.DataType> input = new ArrayList<Primative.DataType>();
		Set(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.BOOLEAN, parentNode, parent, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.BOOLEAN)),null);
		}
		Set(){
			super();
		}
		
	}
	static class Toggle extends PrimativeFunction{
		Toggle(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.BOOLEAN, parentNode, parent, new ArrayList<Primative.DataType>(),null);
		}
		Toggle(){
			super();//TODO fix toggle input
		}
		
	}
	
	static class BooleanDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		
		public BooleanDocumentFilter(AbstractDocument doc) {
			this.doc = doc;
			this.doc.setDocumentFilter(this);
		}
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {
		    super.insertString(fb, offset, string, attr);
		}
		
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {		    
		    super.remove(fb, 0, doc.getLength());
		}
		  
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
			//String curentText = doc.getText(0, Math.min(4, doc.getLength()));
			/*if(doc.getLength() >= 4 && curentText.equals("true")){
				text = "true";
				offset = 0;
				length = doc.getLength();
			}else if(doc.getLength() >= 5){
				text = "false";
				offset = 0;
				length = doc.getLength();
			}*/
			if(doc.getText(0,Math.min(1, doc.getLength())).equals("t") || doc.getText(0,Math.min(1, doc.getLength())).equals("T") || (text.equals("t")) || (text.equals("T"))){
				text = "true";
				offset = 0;
				length = doc.getLength();
			}else{
				text = "false";
				offset = 0;
				length = doc.getLength();
			}
			super.replace(fb, offset, length, text, attrs);
		}
	}
}