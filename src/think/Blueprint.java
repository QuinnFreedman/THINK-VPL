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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

 class Blueprint implements GraphEditor,MouseListener,KeyListener{
	
	private ArrayList<VObject> objects = new ArrayList<VObject>();
	private ArrayList<Curve> curves = new ArrayList<Curve>();
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Variable> variables = new ArrayList<Variable>();
	private ArrayList<VFunction> functions = new ArrayList<VFunction>();
	
	
	DisplayPanel panel;
	JButton addVar;
	JButton addFunc;
	JPanel varsContainer;
	JPanel varsHeader;
	
	private JPanel vars;
	private JPanel funcs;
	JScrollPane scrollVars;
	JScrollPane scrollFuncs;
	JSplitPane splitPane;
	private JPopupMenu panelPopup;
	private String name = "";
	protected JPanel panelHolder;
	private Point clickLocation;
	
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	ArrayList<VFunction> getFunctions() {
		return functions;
	}
	@Override
	public ArrayList<Variable> getVariables() {
		return variables;
	}
	
	@Override
	public DisplayPanel getPanel() {
		return panel;
	}

	@Override
	public ArrayList<VObject> getObjects() {
		return objects;
	}

	@Override
	public ArrayList<Curve> getCurves() {
		return curves;
	}

	@Override
	public void addNode(Node n) {
		nodes.add(n);
		
	}

	@Override
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	Blueprint(){
		//SIDEBAR
		
		Dimension minimumSize = new Dimension(100,100);
		
		varsContainer = new JPanel();
		
		varsContainer.setLayout(new BorderLayout(0, 0));
		
		varsHeader = new JPanel();
		varsContainer.add(varsHeader, BorderLayout.NORTH);
		varsHeader.setLayout(new BorderLayout(0, 0));
		
		JLabel lblVars = new JLabel("Variables");
		lblVars.setHorizontalAlignment(SwingConstants.CENTER);
		varsHeader.add(lblVars);
		
		JPanel varButtonHolder = new JPanel();
		varsHeader.add(varButtonHolder, BorderLayout.EAST);
		
		vars = new JPanel();
		scrollVars = new JScrollPane(vars);
		BoxLayout layout = new BoxLayout(vars, BoxLayout.Y_AXIS);
		vars.setLayout(layout);
		
		addVar = new JButton("+");
		addVar.setPreferredSize(new Dimension(30,addVar.getPreferredSize().height));
		//addVar.setFocusable(false);
		varButtonHolder.add(addVar);
		final Blueprint THIS = this;
		addVar.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {		
				Variable v = new Variable(THIS);		
				variables.add(0,v);		
				updateVars();		
				variables.get(0).fields.get(0).requestFocusInWindow();		
				scrollVars.getViewport().setViewPosition(new Point(0,0));		
			}		
		});
		
		scrollVars.setMinimumSize(minimumSize);
		varsContainer.add(scrollVars);
		
		
		JPanel funcsContainer = new JPanel();
		
		funcsContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel funcsHeader = new JPanel();
		funcsContainer.add(funcsHeader, BorderLayout.NORTH);
		funcsHeader.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFuncs = new JLabel("Functions");
		lblFuncs.setHorizontalAlignment(SwingConstants.CENTER);
		funcsHeader.add(lblFuncs);
		
		JPanel funcsButtonHolder = new JPanel();
		funcsHeader.add(funcsButtonHolder, BorderLayout.EAST);
		
		funcs = new JPanel();
		scrollFuncs = new JScrollPane(funcs);
		BoxLayout layout2 = new BoxLayout(funcs, BoxLayout.Y_AXIS);
		funcs.setLayout(layout2);
		
		addFunc = new JButton("+");
		addFunc.setPreferredSize(new Dimension(30,addFunc.getPreferredSize().height));
		funcsButtonHolder.add(addFunc);
		addFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VFunction f = new VFunction(THIS);
				functions.add(0,f);
				THIS.updateFuncs();
				functions.get(0).fields.get(1).requestFocusInWindow();
				scrollFuncs.getViewport().setViewPosition(new Point(0,0));
				if(THIS instanceof InstantiableBlueprint){
					for(Variable v : Main.mainBP.getVariables()){
						if(v instanceof VInstance && ((VInstance) v).parentBlueprint == THIS){
							((VInstance) v).addChildFunction(f);
						}
					}
				}
			}
		});
		
		scrollFuncs.setMinimumSize(minimumSize);
		funcsContainer.add(scrollFuncs);
		
		vars.setFocusTraversalKeysEnabled(false);
		panel = new DisplayPanel(this);
		
		panelHolder = new JPanel(new BorderLayout());
		panelHolder.add(panel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(panelHolder);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.setMinimumSize(minimumSize);
		
		JSplitPane sidebar = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				varsContainer, funcsContainer);
				sidebar.setDividerLocation(300);
				//TODO sidebar.setMaximumSize(new Dimension(300,Integer.MAX_VALUE));
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                sidebar, scrollPane);
				splitPane.setDividerLocation(253);
		
		panel.addKeyListener(this);
		
		panelPopup = new ContextualMenu(this);
		panel.addMouseListener(this);
		
		
	}
	
	@Override
	public void updateVars(){
		vars.removeAll();
		for(Variable var : variables){
			vars.add(var);
		}
		vars.repaint();
		vars.revalidate();
	}
	
	 void updateFuncs(){
		funcs.removeAll();
		for(VFunction f : functions){
			funcs.add(f);
		}
		funcs.repaint();
		funcs.revalidate();
	}
	static class ContextualMenu extends JPopupMenu{
		private static final long serialVersionUID = 1L;

		ContextualMenu(GraphEditor ge){
			ActionListener al =  new ContextualMenuListener(ge);
			
			JMenuItem popupAdd = new JMenuItem("Add");
			popupAdd.addActionListener(al);
			this.add(popupAdd);
			
			JMenuItem popupSubract = new JMenuItem("Subtract");
			popupSubract.addActionListener(al);
			this.add(popupSubract);
			
			JMenuItem popupMultiply = new JMenuItem("Multiply");
			popupMultiply.addActionListener(al);
			this.add(popupMultiply);
			
			JMenuItem popupDivide = new JMenuItem("Divide");
			popupDivide.addActionListener(al);
			this.add(popupDivide);
			
			JMenuItem popupRand = new JMenuItem("Random");
			popupRand.addActionListener(al);
			this.add(popupRand);
			
			JMenuItem popupRound = new JMenuItem("Round");
			popupRound.addActionListener(al);
			this.add(popupRound);
			
			JMenuItem popupConcat = new JMenuItem("Concatenate");
			popupConcat.addActionListener(al);
			this.add(popupConcat);
			
			this.addSeparator();
			
			JMenuItem popupLogic = new JMenuItem("Branch");
			popupLogic.addActionListener(al);
			this.add(popupLogic);
			
			JMenuItem popupEquals = new JMenuItem("Equals");
			popupEquals.addActionListener(al);
			this.add(popupEquals);
			
			JMenuItem popupLessThan = new JMenuItem("Is Less Than");
			popupLessThan.addActionListener(al);
			this.add(popupLessThan);
			
			JMenuItem popupGreaterThan = new JMenuItem("Is Greater Than");
			popupGreaterThan.addActionListener(al);
			this.add(popupGreaterThan);
			
			JMenuItem popupLessEqual = new JMenuItem("Is Less Than Or Equal To");
			popupLessEqual.addActionListener(al);
			this.add(popupLessEqual);
			
			JMenuItem popupGreaterEqual = new JMenuItem("Is Greater Than Or Equal To");
			popupGreaterEqual.addActionListener(al);
			this.add(popupGreaterEqual);
			
			JMenuItem popupItem = new JMenuItem("And");
			popupItem.addActionListener(al);
			this.add(popupItem);
			
			popupItem = new JMenuItem("Or");
			popupItem.addActionListener(al);
			this.add(popupItem);
			
			popupItem = new JMenuItem("Not");
			popupItem.addActionListener(al);
			this.add(popupItem);

			this.addSeparator();
			
			popupItem = new JMenuItem("While");
			popupItem.addActionListener(al);
			this.add(popupItem);
			
			popupItem = new JMenuItem("Sequence");
			popupItem.addActionListener(al);
			this.add(popupItem);
			
			this.addSeparator();
			
			JMenuItem popupLog = new JMenuItem("Log To Console");
			popupLog.addActionListener(al);
			this.add(popupLog);
			
			JMenuItem popupGet = new JMenuItem("Get String From Console");
			popupGet.addActionListener(al);
			this.add(popupGet);
			
			JMenuItem popupGetNum = new JMenuItem("Get Number From Console");
			popupGetNum.addActionListener(al);
			this.add(popupGetNum);
			
		}
	}
	static class ContextualMenuListener implements ActionListener{
		
		private GraphEditor editor;

		ContextualMenuListener(GraphEditor ge){
			this.editor = ge;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String c = e.getActionCommand();
			Point p = editor.getClickLocation();
			
			if(c == "Add"){
				new Arithmetic.Add(p, editor);
			}else if(c == "Subtract"){
				new Arithmetic.Subtract(p, editor);
			}else if(c == "Multiply"){
				new Arithmetic.Multiply(p, editor);
			}else if(c == "Divide"){
				new Arithmetic.Divide(p, editor);
			}else if(c == "Random"){
				new Arithmetic.Random(p, editor);
			}else if(c == "Round"){
				new Arithmetic.Round(p, editor);
			}else if(c == "Concatenate"){
				new Arithmetic.Concatinate(p, editor);
			}else if(c == "Branch"){
				new FlowControl.Branch(p, editor);
			}else if(c == "While"){
				new FlowControl.While(p, editor);
			}else if(c == "Sequence"){
				new FlowControl.Sequence(p, editor);
			}else if(c == "Equals"){
				new Logic.Equals(p, editor);
			}else if(c == "Is Less Than"){
				new Logic.Less_Than(p, editor);
			}else if(c == "Is Greater Than"){
				new Logic.Greater_Than(p, editor);
			}else if(c == "Is Less Than Or Equal To"){
				new Logic.Less_Than_Or_Equal_To(p, editor);
			}else if(c == "Is Greater Than Or Equal To"){
				new Logic.Greater_Than_Or_Equal_To(p, editor);
			}else if(c == "And"){
				new Logic.And(p, editor);
			}else if(c == "Or"){
				new Logic.Or(p, editor);
			}else if(c == "Not"){
				new Logic.Not(p, editor);
			}else if(c == "Log To Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.Log_To_Console(p, editor);
			}else if(c == "Get String From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.getStr(p, Variable.DataType.STRING, editor);
			}else if(c == "Get Number From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.getStr(p, Variable.DataType.DOUBLE, editor);
			}else{
				Out.pln("null Action: "+c);
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		panel.requestFocusInWindow();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3){
			clickLocation = new Point(e.getX(), e.getY());
			panelPopup.show(panel, e.getX(), e.getY());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_TAB){
			//Debug.tab();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Auto-generated method stub
		
	}
	@Override
	public Point getClickLocation() {
		return clickLocation;
	}
}