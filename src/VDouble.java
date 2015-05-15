import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VDouble extends Primative{
	static final String name = "Double";
	static int idCounter = 0;
	double value = 0;
	VDouble(){
		super();
		this.dataType = Primative.DataType.DOUBLE;
		this.headerLabel.setText(name);
		this.id = "d"+Integer.toString(idCounter);
		idCounter++;
		this.color = Main.colors.get(this.dataType);
		this.valueField.setText(Double.toString(value));
		new IntDocumentFilter((AbstractDocument) valueField.getDocument());
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
		this.functions.add(new Subtract_From());
		this.functions.add(new Multiply_By());
	}
	public VDouble(Point p) {
		this();
		//this.position = p;
		this.setBounds(p.x, p.y, this.width*Main.gridWidth, this.height*Main.gridWidth);
	}
	@Override
	protected void setValue(String s){
		value = Double.parseDouble(s);
		valueField.getDocument().removeDocumentListener(this);
		//this.valueField.setText(Boolean.toString(value));
	}
	static class Get extends PrimativeFunction{
		Get(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.DOUBLE, parentNode, parent, null, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.DOUBLE)));
		}
		Get(){
			super();
		}
		
	}
	static class Set extends PrimativeFunction{
		Set(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.DOUBLE, parentNode, parent, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.DOUBLE)),null);
		}
		Set(){
			super();
		}
		
	}
	static class Subtract_From extends PrimativeFunction{
		Subtract_From(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.DOUBLE, parentNode, parent, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.DOUBLE)),null);
			this.setSize(120,40);
		}
		Subtract_From(){
			super();
		}
		
	}
	static class Multiply_By extends PrimativeFunction{
		Multiply_By(Point pos, Node parentNode, Primative parent) {
			super(pos, Primative.DataType.DOUBLE, parentNode, parent, new ArrayList<Primative.DataType>(Arrays.asList(Primative.DataType.DOUBLE)),null);
			this.setSize(120,40);
		}
		Multiply_By(){
			super();
		}
		
	}
	static class IntDocumentFilter extends DocumentFilter {
		
		AbstractDocument doc;
		
		public IntDocumentFilter(AbstractDocument doc) {
			this.doc = doc;
			this.doc.setDocumentFilter(this);
		}
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {
		    System.out.println("insert string"+ string);
		    System.out.println(offset);
		    super.insertString(fb, offset, string, attr);
		}
		
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
			
		    super.remove(fb, offset, length);
		}
		  
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				if(c == '0'|| c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '.'){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
}