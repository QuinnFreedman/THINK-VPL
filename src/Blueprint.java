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

public class Blueprint implements GraphEditor,ActionListener,MouseListener,KeyListener{

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
	public JScrollPane scrollVars;
	private JScrollPane scrollFuncs;
	public JSplitPane splitPane;
	private JPopupMenu panelPopup;
	private String name = "";
	protected JPanel panelHolder;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<VFunction> getFunctions() {
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
		Blueprint THIS = this;
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
		
		panelPopup = new JPopupMenu();
		panel.addMouseListener(this);
		
		JMenuItem popupAdd = new JMenuItem("Add");
		popupAdd.addActionListener(this);
		panelPopup.add(popupAdd);
		
		JMenuItem popupSubract = new JMenuItem("Subtract");
		popupSubract.addActionListener(this);
		panelPopup.add(popupSubract);
		
		JMenuItem popupMultiply = new JMenuItem("Multiply");
		popupMultiply.addActionListener(this);
		panelPopup.add(popupMultiply);
		
		JMenuItem popupDivide = new JMenuItem("Divide");
		popupDivide.addActionListener(this);
		panelPopup.add(popupDivide);
		
		JMenuItem popupRand = new JMenuItem("Random");
		popupRand.addActionListener(this);
		panelPopup.add(popupRand);
		
		JMenuItem popupRound = new JMenuItem("Round");
		popupRound.addActionListener(this);
		panelPopup.add(popupRound);
		
		JMenuItem popupConcat = new JMenuItem("Concatenate");
		popupConcat.addActionListener(this);
		panelPopup.add(popupConcat);
		
		panelPopup.addSeparator();
		
		JMenuItem popupLogic = new JMenuItem("Branch");
		popupLogic.addActionListener(this);
		panelPopup.add(popupLogic);
		
		JMenuItem popupEquals = new JMenuItem("Equals");
		popupEquals.addActionListener(this);
		panelPopup.add(popupEquals);
		
		JMenuItem popupLessThan = new JMenuItem("Is Less Than");
		popupLessThan.addActionListener(this);
		panelPopup.add(popupLessThan);
		
		JMenuItem popupGreaterThan = new JMenuItem("Is Greater Than");
		popupGreaterThan.addActionListener(this);
		panelPopup.add(popupGreaterThan);
		
		JMenuItem popupLessEqual = new JMenuItem("Is Less Than Or Equal To");
		popupLessEqual.addActionListener(this);
		panelPopup.add(popupLessEqual);
		
		JMenuItem popupGreaterEqual = new JMenuItem("Is Greater Than Or Equal To");
		popupGreaterEqual.addActionListener(this);
		panelPopup.add(popupGreaterEqual);
		
		JMenuItem popupItem = new JMenuItem("And");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Or");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Not");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);

		panelPopup.addSeparator();
		
		popupItem = new JMenuItem("While");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Sequence");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		panelPopup.addSeparator();
		
		JMenuItem popupLog = new JMenuItem("Log To Console");
		popupLog.addActionListener(this);
		panelPopup.add(popupLog);
		
		JMenuItem popupGet = new JMenuItem("Get String From Console");
		popupGet.addActionListener(this);
		panelPopup.add(popupGet);
		
		JMenuItem popupGetNum = new JMenuItem("Get Number From Console");
		popupGetNum.addActionListener(this);
		panelPopup.add(popupGetNum);
		
		/*JMenuItem popupMath = new JMenuItem("Math");
		popupMath.setEnabled(false);
		popupMath.addActionListener(this);
		panelPopup.add(popupMath);
		
		JMenuItem popupTimeline = new JMenuItem("Timeline");
		popupTimeline.addActionListener(this);
		popupTimeline.setEnabled(false);
		panelPopup.add(popupTimeline);
		
		JMenuItem popupBlueprint = new JMenuItem("Blueprint");
		popupBlueprint.addActionListener(this);
		popupBlueprint.setEnabled(false);
		panelPopup.add(popupBlueprint);*/
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
	
	public void updateFuncs(){
		funcs.removeAll();
		for(VFunction f : functions){
			funcs.add(f);
		}
		funcs.repaint();
		funcs.revalidate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		Point p = Main.clickLocation;
		if(c == "Add"){
			new Arithmetic.Add(p,this);
		}else if(c == "Subtract"){
			new Arithmetic.Subtract(p,this);
		}else if(c == "Multiply"){
			new Arithmetic.Multiply(p, this);
		}else if(c == "Divide"){
			new Arithmetic.Divide(p, this);
		}else if(c == "Random"){
			new Arithmetic.Random(p, this);
		}else if(c == "Round"){
			new Arithmetic.Round(p, this);
		}else if(c == "Concatenate"){
			new Arithmetic.Concat(p, this);
		}else if(c == "Branch"){
			new Logic.Branch(p, this);
		}else if(c == "While"){
			new Logic.While(p, this);
		}else if(c == "Sequence"){
			new Logic.Sequence(p, this);
		}else if(c == "Equals"){
			new Logic.Equals(p, this);
		}else if(c == "Is Less Than"){
			new Logic.LessThan(p, this);
		}else if(c == "Is Greater Than"){
			new Logic.GreaterThan(p, this);
		}else if(c == "Is Less Than Or Equal To"){
			new Logic.LessOrEqual(p, this);
		}else if(c == "Is Greater Than Or Equal To"){
			new Logic.GreaterOrEqual(p, this);
		}else if(c == "And"){
			new Logic.And(p, this);
		}else if(c == "Or"){
			new Logic.Or(p, this);
		}else if(c == "Not"){
			new Logic.Not(p, this);
		}else if(c == "Log To Console"){
			if(Debug.console == null){
				Debug.console = new Console();
			}
			new Console.Log(p, (GraphEditor) this);
		}else if(c == "Get String From Console"){
			if(Debug.console == null){
				Debug.console = new Console();
			}
			new Console.getStr(p, Variable.DataType.STRING, (GraphEditor) this);
		}else if(c == "Get Number From Console"){
			if(Debug.console == null){
				Debug.console = new Console();
			}
			new Console.getStr(p, Variable.DataType.DOUBLE, (GraphEditor) this);
		}else{
			System.out.println("null Action:"+c);
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
			Main.clickLocation = new Point(e.getX(), e.getY());
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
	
}