import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VDouble extends Variable{
	private static final long serialVersionUID = 1L;
	
	static final String name = "Int";
	static int idCounter = 0;
	int value = 0;
	VDouble(){
		super();
		this.dataType = DataType.DOUBLE;
		this.typeField.setText(getSymbol());
		this.typeField.setBackground(Main.colors.get(this.dataType));
		this.typeField.setEditable(false);
		this.typeField.setFocusable(false);
		
		new IntDocumentFilter((AbstractDocument) valueField.getDocument());
		
		//this.functions = new ArrayList<Class<? extends PrimitiveFunction>>();
		//this.functions.add(set.class);
		//this.functions.add(subtractFrom.class);
		this.functions.add(new Get());
		this.functions.add(new Set());
		this.functions.add(new Add_To());
		this.functions.add(new Multiply_By());
		this.functions.add(new Incrament());
	}
	
	@Override
	protected void setValue(String s){
		value = Integer.parseInt(s);
		valueField.getDocument().removeDocumentListener(this);
	}
	
	static class Get extends PrimitiveFunction{
		Get(Point pos, Variable parent) {
			super(pos, Variable.DataType.DOUBLE, parent, null, new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE)));
		}
		Get(){
			super();
		}
		
	}
	static class Set extends PrimitiveFunction{
		Set(Point pos, Variable parent) {
			super(pos, Variable.DataType.DOUBLE, parent, new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE)),null);
		}
		Set(){
			super();
		}
		
	}
	static class Add_To extends PrimitiveFunction{
		Add_To(Point pos, Variable parent) {
			super(pos, Variable.DataType.DOUBLE, parent, new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE)),null);
		}
		Add_To(){
			super();
		}
		
	}
	static class Multiply_By extends PrimitiveFunction{
		Multiply_By(Point pos, Variable parent) {
			super(pos, Variable.DataType.INTEGER, parent, new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE)),null);
		}
		Multiply_By(){
			super();
		}
		
	}
	static class Incrament extends PrimitiveFunction{
		Incrament(Point pos, Variable parent) {
			super(pos, Variable.DataType.INTEGER, parent, new ArrayList<Variable.DataType>(),null);
		}
		Incrament(){
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
				if(Character.isDigit(c) || c == '.'){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
}