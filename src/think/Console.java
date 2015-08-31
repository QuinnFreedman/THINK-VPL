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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


class Console implements KeyListener{
	
	private JFrame consoleWindow;
	private JPanel container;
	
	DisplayMode display = DisplayMode.WINDOW;
	JSplitPane splitPane;
	
	private static enum DisplayMode{
		WINDOW,RIGHT,BOTTOM
	}
	
	private JTextPane output;
	JTextField input;
	
	protected Console getThis(){
		return this;
		
	}
	
	private static Console newestConsole;
	
	Console() {
		super();
		
		newestConsole = this;
		
		consoleWindow = new JFrame();
		
		consoleWindow.setSize(500,300);
		consoleWindow.setMinimumSize(new Dimension(200,150));
		consoleWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		consoleWindow.setIconImage(Main.icon);
		
		consoleWindow.getContentPane().setLayout(new BorderLayout(0, 0));
		
		container = new JPanel();
		consoleWindow.getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new BorderLayout(0, 0));

		output = new JTextPane();//JTextArea();
		output.setOpaque(false);
		output.setEditable(false);
		output.setFocusable(false);
		//output.setWrapStyleWord(true);
		//output.setLineWrap(true);
		output.setEditable(false);
		//output.set
		
		JScrollPane scrollPane = new JScrollPane(output);
		
		JPanel layerdPanel = new JPanel(new BorderLayout());
		
		JPanel buttonHolder = new JPanel();
		buttonHolder.setLayout(new FlowLayout(FlowLayout.RIGHT,1,1));
		layerdPanel.add(buttonHolder, BorderLayout.BEFORE_FIRST_LINE);
		layerdPanel.add(scrollPane, BorderLayout.CENTER);
		
		layerdPanel.setBorder(BorderFactory.createLineBorder(new Color(59,59,59)));
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		
		JButton popout = new JButton("[ ]");
		JButton bottom = new JButton("V");
		JButton side = new JButton(">");
		
		popout.setFocusable(false);
		popout.setPreferredSize(new Dimension(popout.getPreferredSize().height+8,popout.getPreferredSize().height));
		popout.setEnabled(false);
		popout.addActionListener(e -> {
			if(display != DisplayMode.WINDOW){
				consoleWindow.setVisible(true);
				consoleWindow.add(container);
				consoleWindow.repaint();
				consoleWindow.revalidate();
				
				Main.container.remove(splitPane);
				splitPane = null;
				Main.container.add(Main.tabbedPane);
				Main.container.repaint();
				Main.container.revalidate();
			}
			display = DisplayMode.WINDOW;
			popout.setEnabled(false);
			bottom.setEnabled(true);
			side.setEnabled(true);
		});
		popout.setToolTipText("Popout");
		buttonHolder.add(popout);
		
		bottom.setFocusable(false);
		bottom.setPreferredSize(new Dimension(bottom.getPreferredSize().height+8,bottom.getPreferredSize().height));
		bottom.addActionListener(e -> {
			if(display == DisplayMode.WINDOW){
				consoleWindow.setVisible(false);
				consoleWindow.remove(container);
				
				Main.container.remove(Main.tabbedPane);
				splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Main.tabbedPane, container);
				splitPane.setResizeWeight(1);
				
				Main.container.add(splitPane);
				Main.container.repaint();
				Main.container.revalidate();
			}else{
				splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			}
			splitPane.setDividerLocation(Main.container.getHeight()-150);
			display = DisplayMode.BOTTOM;
			popout.setEnabled(true);
			bottom.setEnabled(false);
			side.setEnabled(true);
		});
		bottom.setToolTipText("Dock bottom");
		buttonHolder.add(bottom);
		
		side.setFocusable(false);
		side.setPreferredSize(new Dimension(side.getPreferredSize().height+8,side.getPreferredSize().height));
		side.addActionListener(e -> {
			if(display == DisplayMode.WINDOW){
				consoleWindow.setVisible(false);
				consoleWindow.remove(container);
				
				Main.container.remove(Main.tabbedPane);
				splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, Main.tabbedPane, container);
				splitPane.setResizeWeight(1);
				
				Main.container.add(splitPane);
				Main.container.repaint();
				Main.container.revalidate();
			}else{
				splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			}
			splitPane.setDividerLocation(Main.container.getWidth()-220);
			display = DisplayMode.RIGHT;
			popout.setEnabled(true);
			bottom.setEnabled(true);
			side.setEnabled(false);
		});
		side.setToolTipText("Dock right");
		buttonHolder.add(side);
		
		container.add(layerdPanel,BorderLayout.CENTER);
		
		JPanel bottomBar = new JPanel(new BorderLayout());
		container.add(bottomBar, BorderLayout.SOUTH);
		
		input = new JTextField();
		input.addKeyListener(getThis());
		bottomBar.add(input, BorderLayout.CENTER);
		JButton submit = new JButton(">");
		submit.setFocusable(false);
		submit.setPreferredSize(new Dimension(submit.getPreferredSize().height+5,submit.getPreferredSize().height));
		submit.addActionListener(e -> submit());
		bottomBar.add(submit, BorderLayout.LINE_END);
		
	}
	
	public void post(final String s){
		SwingUtilities.invokeLater(() -> {
        	Out.pln("POST: "+s);
        	try {
        		Document doc = output.getDocument();
        		doc.insertString(doc.getLength(), s+"\n", null);
        	} catch(BadLocationException exc) {
        		exc.printStackTrace();
        	}
        	
        });
	}
	public void postError(final String s){
		SwingUtilities.invokeLater(() -> {
        	Out.pln("POST ERROR: "+s);
        	StyledDocument doc = output.getStyledDocument();
        	
        	SimpleAttributeSet keyWord = new SimpleAttributeSet();
        	StyleConstants.setForeground(keyWord, new Color(193,43,43));
        	StyleConstants.setBold(keyWord, true);
        	
        	try {
        		doc.insertString(doc.getLength(), s+"\n", keyWord );
        	} catch(Exception e) { System.out.println(e); }
        	
        });
	}
	public void clear(){
		if(output != null)
			output.setText("");
	}
	static class Log_To_Console extends Executable implements JavaKeyword{
		private static final long serialVersionUID = 1L;
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			String s;
			
			if(input.length == 0){
				s = "undefined";
			}else if(input[0] == null){
				s = "undefined";
			}else{
				s = input[0].getValueAsString();
			}
			
			newestConsole.post(s);
			
			return null;
			
		}
		Log_To_Console(Point pos, GraphEditor owner) {
			super(pos, owner);
			
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.GENERIC,true));
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.STRING));
			
			addOutputNode(new Node(Node.NodeType.SENDING,this,Variable.DataType.GENERIC));
			
			setBounds(new Rectangle(pos,getSize()));
		}

		@Override
		public String getJavaKeyword() {
			return "System.out.println";
		}
		
		
	}
	
	static class getStr extends Executable{
		private static final long serialVersionUID = 1L;
		
		private Variable.DataType dt;
		
		Variable.DataType getDataType() {
			return dt;
		}
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		getStr(Point pos, Variable.DataType dt, GraphEditor owner) {
			super(pos, owner);
			this.dt = dt;
			headerLabel.setText(getSimpleName());
			this.setBounds(new Rectangle(pos,getSize()));
			addOutputNode(new Node(Node.NodeType.SENDING,this,dt));
		}
		
		@Override
		 String getSimpleName(){
			if(dt.isNumber()){
				return "Get Number From Console";
			}else if(dt == Variable.DataType.STRING){
				return "Get String From Console";
			}
			return null;
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			submit();
		}
	}
	
	private void submit(){

		if(Debug.isStepping() && Debug.waitingForInput() != null){
			
			String s = input.getText();
			input.setText("");
			
			if(Debug.waitingForInput() == Variable.DataType.STRING){

				Main.window.requestFocus();
				Main.getOpenClass().getPanel().requestFocusInWindow();
				
				post("> "+s);
				
				Debug.running.unpause();
				Debug.moveDownStack2(new VariableData.String(s));
				Debug.stepForever();
				if(!consoleWindow.isShowing())
					Debug.tab();
				
			}else if(Debug.waitingForInput() == Variable.DataType.DOUBLE){
				if(isNumeric(s)){
					post("> "+s);
					
					Main.window.requestFocus();
					Main.getOpenClass().getPanel().requestFocusInWindow();
					
					if(Debug.running != null)
					Debug.running.unpause();
					Debug.moveDownStack2(new VariableData.Double(Double.parseDouble(s)));
					Debug.stepForever();
					if(!consoleWindow.isShowing())
						Debug.tab();
				}else{
					
					post("> INVALID INPUT \""+s+"\" please input a number");
				}
			}
			
		}
	}
	
	static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Auto-generated method stub
		
	}

	public void requestFocus() {
		if(consoleWindow.isShowing())
			consoleWindow.requestFocus();
		
	}

	public void setAlwaysOnTop(boolean b) {
		if(consoleWindow.isShowing())
			consoleWindow.setAlwaysOnTop(b);
		
	}

	public void setVisible(boolean b) {
		if(display == DisplayMode.WINDOW)
			consoleWindow.setVisible(b);
	}
}