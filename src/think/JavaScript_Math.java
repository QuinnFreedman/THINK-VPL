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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import think.Variable.DataType;

@SuppressWarnings("serial")
class JavaScript_Math extends Executable implements DocumentListener {

	private JTextField editor;
	private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel nullpanel;
	private Color defaultColor;
    
	private static final String alphabetStr = "abcdefghijklmnopqrstuvwxyz";
	private static final char[] alphabet = alphabetStr.toCharArray();
	private static final String[][] dictionairy = {
		{"acos","Math.acos"},
		{"asin","Math.asin"},
		{"atan","Math.atan"},
		{"sin","Math.sin"},
		{"cos","Math.cos"},
		{"tan","Math.tan"},
		{"abs","Math.abs"},
		{"ceil","Math.ceil"},
		{"floor","Math.floor"},
		{"ln","Math.log"},
		{"log","(1 / Math.LN10) * Math.log"},
		{"max","Math.max"},
		{"min","Math.min"},
		{"pow","Math.pow"},
		{"round","Math.round"},
		{"sqrt","Math.sqrt"},
		{"E","Math.E"},
		{"PI","Math.PI"},
	};
	
	private static boolean isValid(String s, int inputs){
		int openParens = 0;
		int closeParens = 0;
		
		char chars[] = s.toCharArray();
		
		for(int i = 0; i < chars.length; i++){
			char c = chars[i];
			if(c == '('){
				openParens++;
			}else if(c == ')'){
				closeParens++;
			}
		}
		if(openParens != closeParens)
			return false;
		
		for(String term[] : dictionairy){
			s = s.replaceAll(term[0]+"[(]", "");
		}
		
		s = s.replaceAll("\\s","");

		chars = s.toCharArray();
		
		for(int i = 0; i < chars.length; i++){
			char c = chars[i];
			
			if(Character.isLetter(c)){
				
				if(alphabetStr.indexOf(c) >= inputs-2){
					return false;
				}
				
				int k = i+1;
				while(k < chars.length){
					if(Character.isLetter(chars[k])/* && chars[k] != chars[i]*/){
						return false;
					}
					else if(!Character.isLetter(chars[k])){
						break;
					}
					k++;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public ArrayList<Variable.DataType> getInputs(){
		return new ArrayList<Variable.DataType>(Arrays.asList(
				Variable.DataType.GENERIC));
	}
	@Override
	public ArrayList<Variable.DataType> getOutputs(){
		return new ArrayList<Variable.DataType>(Arrays.asList(
				Variable.DataType.GENERIC,
				Variable.DataType.DOUBLE));
	}
	
	@Override
	public ArrayList<String> getInputTooltips() {
		ArrayList<String> output = new ArrayList<String>();
		
		for(int i = 0; i < getInputNodes().size() - 1; i++){
			output.add(" "+getNameForIndex(i)+" ");
		}
		
		return output;
	}
	
	@Override
	protected boolean inputsOptional(int i) {
		if(i == getInputNodes().size()-1)
			return true;
		
		return false;
	}
	
	@Override
	public VariableData execute(VariableData[] inputs) throws Exception{
		VariableData output = new VariableData.Double(0);
		
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine engine = manager.getEngineByName("JavaScript");
	    
	    String script = "function execute(){";
	    
		for(int i = 0; i < inputs.length; i++){
			script += ("var "+getNameForIndex(i)+" = "+inputs[i].getValueAsString()+";");
		}
		
		String editorText = editor.getText();
		for(String term[] : dictionairy){
			editorText = editorText.replaceAll(term[0], term[1]);
		}
		
		script += "return ("+editorText+")+0.0;}";
		
		Out.pln(" > SCRIPT = "+script);
		
		try {
			engine.eval(script);
			
		    Invocable inv = (Invocable) engine;
		    Object invoke = inv.invokeFunction("execute");
		    if(!(invoke instanceof Double))
		    	throw new Exception("In JavaScript Math: Invalid output: expected output of type double but recieved output of type "+invoke.getClass().getSimpleName()+" ("+invoke.toString()+")");
		    Double returnValue = (Double) invoke;
		    Out.pln(" > RETURN = "+returnValue);
		    output = new VariableData.Double(returnValue);
		} catch (ScriptException e) {
			throw new Exception("In JavaScript Math: "+e.getMessage().replace(" in <eval> at line number 1", "."));
		} catch(NoSuchMethodException e){
			e.printStackTrace();
			throw new Exception("In JavaScript Math: Internal Error");
		}
		
		Out.pln("test");
		
		return output;
	}
	
	private static String getNameForIndex(int i){
		String name = "";
		
		char charName = alphabet[i%26];
		
		int numberOfChars = Math.floorDiv(i, 26) + 1;
		
		for(int j = 0; j < numberOfChars; j++){
			name += charName;
		}
		
		return name;
	}
	
	private static class RepNode extends FlowControl.Sequence.ReplicatingNode{

		public RepNode(Node.NodeType type, Executable parent, Variable.DataType dt) {
			super(type, parent, dt);
		}
		
		@Override
		protected void additionalOnConnect(){
			checkValid((JavaScript_Math) parentObject);
		}
	}
	
	JavaScript_Math(Point pos, GraphEditor owner) {
		super(pos, owner);
		
		addInputNode(new RepNode(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));

		setupInputTooltips();

		body.removeAll();
		body.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
        
		editor = new JTextField();
		//editor.setColumns(10);
		editor.setOpaque(false);
		editor.setMinimumSize(new Dimension(40, editor.getPreferredSize().height));
		editor.requestFocusInWindow();
		
		body.add(editor, gbc);
		
		nullpanel = new JPanel();
		nullpanel.setPreferredSize(new Dimension(0,0));
		
		body.add(nullpanel, gbc);

		defaultColor = editor.getBackground();
		editor.getDocument().addDocumentListener(this);
		editor.setText("a");
		
		setBounds(new Rectangle(pos,getSize()));
		
	}
	
	JavaScript_Math() {
		
	}

	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width+10),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		readjust();
	}
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		readjust();
		
	}
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		readjust();
		
	}
	private void readjust() {
		body.remove(nullpanel);
		body.add(nullpanel,gbc);
		setBounds(new Rectangle(getLocation(),getSize()));
		this.owner.getPanel().revalidate();
		this.owner.getPanel().repaint();
		
		checkValid(this);
	}
	
	private static void checkValid(JavaScript_Math THIS){
		if(isValid(THIS.editor.getText(), THIS.getInputNodes().size())){
			THIS.editor.setBackground(THIS.defaultColor);
		}else{
			THIS.editor.setBackground(Color.YELLOW);
		}
	}
	
}