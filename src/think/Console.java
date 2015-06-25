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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class Console extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;
	
	private JTextArea output;
	 JTextField input;
	
	protected Console getThis(){
		return this;
		
	}
	
	private static Console newestConsole;
	
	Console() {
		super();
		
		newestConsole = this;
		
    	setSize(500,300);
    	setMinimumSize(new Dimension(200,150));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel container = new JPanel();
		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new BorderLayout(0, 0));

		output = new JTextArea();
		output.setOpaque(false);
		output.setEditable(false);
		output.setFocusable(false);
		output.setWrapStyleWord(true);
		output.setLineWrap(true);
		
		JScrollPane scrollPane = new JScrollPane(output);
		
		container.add(scrollPane,BorderLayout.CENTER);
		
		input = new JTextField();
		container.add(input, BorderLayout.SOUTH);
		input.addKeyListener(getThis());
		
	}
	
	 void post(String s){
		SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
        	Out.println("POST: "+s);
        	output.append(s+"\n\n");
        	
        }});
	}
	 void clear(){
		if(output != null)
			output.setText("");
	}
	static class Log_To_Console extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public VariableData execute(VariableData[] input){
			
			newestConsole.post(input[0].getValueAsString());
			
			return null;
			
		}
		Log_To_Console(Point pos, GraphEditor owner) {
			super(pos, owner);

			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.GENERIC,true));
			addInputNode(new Node(Node.NodeType.RECIEVING,this,Variable.DataType.STRING));
			
			addOutputNode(new Node(Node.NodeType.SENDING,this,Variable.DataType.GENERIC));
			
			setBounds(new Rectangle(pos,getSize()));
		}
		
		
	}
	
	static class getStr extends Executable{
		private static final long serialVersionUID = 1L;
		
		private Variable.DataType dt;
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		getStr(Point pos, Variable.DataType dt, GraphEditor owner) {
			super(pos, owner);
			this.dt = dt;
			headerLabel.setText(getSimpleName());
			this.setBounds(new Rectangle(pos,getSize()));
			addOutputNode(new Node(Node.NodeType.SENDING,this,dt));
		}

		 Variable.DataType getDataType() {
			return dt;
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
					if(!this.isShowing())
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
						if(!this.isShowing())
							Debug.tab();
					}else{
						
						post("> INVALID INPUT \""+s+"\" please input a number");
					}
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
}