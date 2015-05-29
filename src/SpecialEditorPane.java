import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyledDocument;

public class SpecialEditorPane extends JTextArea implements KeyListener{
	private static final long serialVersionUID = 1L;

	private NoBreakDocumentFilter filter;
	private VObject parent;
	
	public void addIllegalChar(char c){
		this.filter.illegalChars.add(c);
	}
	
	SpecialEditorPane(){
		this.addKeyListener(this);
		filter = new NoBreakDocumentFilter();
		((AbstractDocument) this.getDocument()).setDocumentFilter(filter);
	}
	
	public SpecialEditorPane(VObject parent) {
		this.parent = parent;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
			if(parent == null){
				Main.panel.requestFocusInWindow();
			}else{
				parent.owner.getPanel().requestFocusInWindow();
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Auto-generated method stub
		
	}
	
	static class NoBreakDocumentFilter extends DocumentFilter {
		private ArrayList<Character> illegalChars = new ArrayList<Character>(Arrays.asList('\n','\f','\r','\t'));
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String text,
			AttributeSet attr) throws BadLocationException {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < text.length(); i++){
				char c = text.charAt(i);
				boolean isLegal = true;
				for(char illegal : illegalChars){
					if(c == illegal){
						isLegal = false;
						break;
					}
				}
				if(isLegal){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.insertString(fb, offset, text, attr);
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
				if(c != '\n' && c != '\r' && c != '\f' && c != '\t'){
					sb.append(c);
				}
			}
			text = sb.toString();
			super.replace(fb, offset, length, text, attrs);
		}
	}
	
	
}