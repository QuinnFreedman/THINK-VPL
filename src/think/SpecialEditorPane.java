/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015 Quinn Freedman
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

import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

 class SpecialEditorPane extends JTextArea implements KeyListener{
	private static final long serialVersionUID = 1L;
	
	private NoBreakDocumentFilter filter;
	private VObject parent;
	
	 void addIllegalChar(char c){
		this.filter.illegalChars.add(c);
	}
	
	SpecialEditorPane(){
		this.addKeyListener(this);
		filter = new NoBreakDocumentFilter();
		((AbstractDocument) this.getDocument()).setDocumentFilter(filter);
	}
	
	 SpecialEditorPane(VObject parent) {
		this.parent = parent;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
			if(parent == null){
				//Main.panel.requestFocusInWindow();
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
	
	static class NoBreakDocumentFilter extends DocumentFilter{
		
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