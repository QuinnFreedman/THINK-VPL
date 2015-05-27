import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import javax.swing.JLabel;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class Main implements ActionListener, MouseInputListener, KeyListener, GraphEditor{
	static ArrayList<VObject> objects = new ArrayList<VObject>();
	static ArrayList<Curve> curves = new ArrayList<Curve>();
	static ArrayList<Node> nodes = new ArrayList<Node>();
	static final int gridWidth = 10;
	public static ArrayList<Variable> variables = new ArrayList<Variable>();
	public static ArrayList<VFunction> functions = new ArrayList<VFunction>();
	static Point mousePos = new Point();
	static HashMap<Variable.DataType,Color> colors = new HashMap<Variable.DataType,Color>();
	public static boolean altPressed = false;
	/**
	 * @wbp.parser.entryPoint
	 */
	static ComponentMover componentMover;
	public static JFrame window;
	static DisplayPanel panel;
	static JButton addVar;
	static JButton addFunc;
	private static JPanel vars;
	private static JPanel funcs;
	private JScrollPane scrollVars;
	private JScrollPane scrollFuncs;
	private static JPopupMenu panelPopup;
	private static Point clickLocation;
	public static EntryPoint entryPoint;
	public static Main THIS;
	@Override
	public ArrayList<Variable> getVariables(){
		return variables;
	}
	@Override
	public ArrayList<VObject> getObjects(){
		return objects;
	}
	@Override
	public ArrayList<Curve> getCurves(){
		return curves;
	}
	@Override
	public Main.DisplayPanel getPanel(){
		return panel;
	}
	public static void main(String[] args){
		THIS = new Main();
		colors.put(Variable.DataType.BOOLEAN, new Color(20,210,20));
		colors.put(Variable.DataType.INTEGER, Color.red);
		colors.put(Variable.DataType.DOUBLE, new Color(196,0,167));
		colors.put(Variable.DataType.FLOAT, new Color(207,0,91));
		colors.put(Variable.DataType.STRING, new Color(0,132,0));
		colors.put(Variable.DataType.GENERIC, Color.WHITE);
		colors.put(Variable.DataType.NUMBER, Color.GRAY);
		colors.put(Variable.DataType.FLEX, Color.GRAY);
		
		try {
			SidebarItem.bufferedImage = ImageIO.read(Main.class.getResource("/drag.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (Main.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                            altPressed = true;
                        }else if (ke.getKeyCode() == KeyEvent.VK_F1){
                        	Debug.f1();
                        }else if (ke.getKeyCode() == KeyEvent.VK_F2){
                        	Debug.f2();
                        }else if (ke.getKeyCode() == KeyEvent.VK_F3){
                        	Debug.f3();
                        }else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE){
                        	if(Debug.isStepping()){
                        		Debug.exit();
                        	}
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                        	altPressed = false;
                        }
                        break;
                    }
                    return false;
                }
            }
        });
	}
	
	Main(){
		THIS = this;
		SwingUtilities.invokeLater(new Runnable() {

	        @Override
	        public void run() {
				window = new JFrame();
				window.setTitle("VisualIDE");
				window.setSize(800,500);
				window.setMinimumSize(new Dimension(555,325));
				//window.setLocationByPlatform(true);
				window.setLocationRelativeTo(null);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
				try {
					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | UnsupportedLookAndFeelException e1) {
					// Auto-generated catch block
					e1.printStackTrace();
				}
			
			//Menu Bar
				JMenuBar menuBar = new JMenuBar();
				
				// File Menu, F - Mnemonic
			    JMenu fileMenu = new JMenu("File");
			    menuBar.add(fileMenu);
			    
			    JMenuItem mntmOpen = new JMenuItem("Open");
			    mntmOpen.setEnabled(false);
			    fileMenu.add(mntmOpen);
			    
			    JMenuItem mntmSave = new JMenuItem("Save");
			    mntmSave.setEnabled(false);
			    fileMenu.add(mntmSave);
			    
			    JMenuItem mntmSaveAs = new JMenuItem("Save As...");
			    mntmSaveAs.setEnabled(false);
			    fileMenu.add(mntmSaveAs);
			    
				JMenu mnEdit = new JMenu("Edit");
				menuBar.add(mnEdit);
				
				JMenu mnVariable = new JMenu("Variable");
				menuBar.add(mnVariable);
				
				JMenuItem mntmNewInteger = new JMenuItem("New Integer");
				mntmNewInteger.addActionListener(THIS);
				mnVariable.add(mntmNewInteger);
				
				JMenuItem mntmNewFloat = new JMenuItem("New Float");
				mntmNewFloat.addActionListener(THIS);
				mnVariable.add(mntmNewFloat);
				
				JMenuItem mntmNewDouble = new JMenuItem("New Double");
				mntmNewDouble.addActionListener(THIS);
				mnVariable.add(mntmNewDouble);
				
				JMenuItem mntmNewLong = new JMenuItem("New Long");
				mntmNewLong.addActionListener(THIS);
				mntmNewLong.setEnabled(false);
				mnVariable.add(mntmNewLong);
				
				JMenuItem mntmNewShort = new JMenuItem("New Short");
				mntmNewShort.addActionListener(THIS);
				mntmNewShort.setEnabled(false);
				mnVariable.add(mntmNewShort);
				
				JMenuItem mntmNewByte = new JMenuItem("New Byte");
				mntmNewByte.addActionListener(THIS);
				mntmNewByte.setEnabled(false);
				mnVariable.add(mntmNewByte);
				
				mnVariable.addSeparator();
				
				JMenuItem mntmNewBoolean = new JMenuItem("New Boolean");
				mntmNewBoolean.addActionListener(THIS);
				mnVariable.add(mntmNewBoolean);
				
				mnVariable.addSeparator();
				
				JMenuItem mntmNewString = new JMenuItem("New String");
				mntmNewString.addActionListener(THIS);
				mnVariable.add(mntmNewString);
				
				JMenuItem mntmNewCharacter = new JMenuItem("new Character");
				mntmNewCharacter.addActionListener(THIS);
				mntmNewCharacter.setEnabled(false);
				mnVariable.add(mntmNewCharacter);
				
				JMenu mnRun = new JMenu("Run");
				menuBar.add(mnRun);
				
				JMenuItem mntmRun = new JMenuItem("Run (f1)");
				mntmRun.addActionListener(THIS);
				mnRun.add(mntmRun);
				
				mntmRun = new JMenuItem("Fast Debug (f2)");
				mntmRun.addActionListener(THIS);
				mnRun.add(mntmRun);
				
				JMenuItem mntmDebug = new JMenuItem("Debug (f3)");
				mntmDebug.addActionListener(THIS);
				mnRun.add(mntmDebug);
				
				JMenu mnHelp = new JMenu("Help");
				menuBar.add(mnHelp);
				
				panel = new DisplayPanel(THIS);
				
				JScrollPane scrollPane = new JScrollPane(panel);
				scrollPane.getVerticalScrollBar().setUnitIncrement(10);
				scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
					
			//SIDEBAR
				
				Dimension minimumSize = new Dimension(100,100);
				
				JPanel varsContainer = new JPanel();
				
				varsContainer.setLayout(new BorderLayout(0, 0));
				
				JPanel varsHeader = new JPanel();
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
						updateFuncs();
						functions.get(0).fields.get(1).requestFocusInWindow();
						scrollFuncs.getViewport().setViewPosition(new Point(0,0));
					}
				});
				
				scrollFuncs.setMinimumSize(minimumSize);
				funcsContainer.add(scrollFuncs);
				
				JSplitPane sidebar = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						varsContainer, funcsContainer);
					sidebar.setDividerLocation(300);
				
				JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                sidebar, scrollPane);
						splitPane.setDividerLocation(253);
				
				
				window.getContentPane().add(splitPane, BorderLayout.CENTER);
				
				window.setJMenuBar(menuBar);
				
				window.setVisible(true);
				
				window.setFocusTraversalKeysEnabled(false);
				vars.setFocusTraversalKeysEnabled(false);
				
				componentMover = new ComponentMover();
				componentMover.setEdgeInsets(new Insets(10, 10, 10, 10));
				
				panel.addKeyListener(THIS);
				
				panelPopup = new JPopupMenu();
				panel.addMouseListener(THIS);
				
				JMenuItem popupAdd = new JMenuItem("Add");
				popupAdd.addActionListener(THIS);
				panelPopup.add(popupAdd);
				
				JMenuItem popupSubract = new JMenuItem("Subtract");
				popupSubract.addActionListener(THIS);
				panelPopup.add(popupSubract);
				
				JMenuItem popupMultiply = new JMenuItem("Multiply");
				popupMultiply.addActionListener(THIS);
				panelPopup.add(popupMultiply);
				
				JMenuItem popupDivide = new JMenuItem("Divide");
				popupDivide.addActionListener(THIS);
				panelPopup.add(popupDivide);
				
				JMenuItem popupRand = new JMenuItem("Random");
				popupRand.addActionListener(THIS);
				panelPopup.add(popupRand);
				
				JMenuItem popupRound = new JMenuItem("Round");
				popupRound.addActionListener(THIS);
				panelPopup.add(popupRound);
				
				JMenuItem popupConcat = new JMenuItem("Concatenate");
				popupConcat.addActionListener(THIS);
				panelPopup.add(popupConcat);
				
				panelPopup.addSeparator();
				
				JMenuItem popupLogic = new JMenuItem("Branch");
				popupLogic.addActionListener(THIS);
				panelPopup.add(popupLogic);
				
				JMenuItem popupEquals = new JMenuItem("Equals");
				popupEquals.addActionListener(THIS);
				panelPopup.add(popupEquals);
				
				JMenuItem popupLessThan = new JMenuItem("Is Less Than");
				popupLessThan.addActionListener(THIS);
				panelPopup.add(popupLessThan);
				
				JMenuItem popupGreaterThan = new JMenuItem("Is Greater Than");
				popupGreaterThan.addActionListener(THIS);
				panelPopup.add(popupGreaterThan);
				
				JMenuItem popupLessEqual = new JMenuItem("Is Less Than Or Equal To");
				popupLessEqual.addActionListener(THIS);
				panelPopup.add(popupLessEqual);
				
				JMenuItem popupGreaterEqual = new JMenuItem("Is Greater Than Or Equal To");
				popupGreaterEqual.addActionListener(THIS);
				panelPopup.add(popupGreaterEqual);
				
				JMenuItem popupItem = new JMenuItem("And");
				popupItem.addActionListener(THIS);
				panelPopup.add(popupItem);
				
				popupItem = new JMenuItem("Or");
				popupItem.addActionListener(THIS);
				panelPopup.add(popupItem);
				
				popupItem = new JMenuItem("Not");
				popupItem.addActionListener(THIS);
				panelPopup.add(popupItem);

				panelPopup.addSeparator();
				
				popupItem = new JMenuItem("While");
				popupItem.addActionListener(THIS);
				panelPopup.add(popupItem);
				
				popupItem = new JMenuItem("Sequence");
				popupItem.addActionListener(THIS);
				panelPopup.add(popupItem);
				
				panelPopup.addSeparator();
				
				JMenuItem popupLog = new JMenuItem("Log To Console");
				popupLog.addActionListener(THIS);
				panelPopup.add(popupLog);
				
				JMenuItem popupGet = new JMenuItem("Get String From Console");
				popupGet.addActionListener(THIS);
				panelPopup.add(popupGet);
				
				JMenuItem popupGetNum = new JMenuItem("Get Number From Console");
				popupGetNum.addActionListener(THIS);
				panelPopup.add(popupGetNum);
				
				/*JMenuItem popupMath = new JMenuItem("Math");
				popupMath.setEnabled(false);
				popupMath.addActionListener(THIS);
				panelPopup.add(popupMath);
				
				JMenuItem popupTimeline = new JMenuItem("Timeline");
				popupTimeline.addActionListener(THIS);
				popupTimeline.setEnabled(false);
				panelPopup.add(popupTimeline);
				
				JMenuItem popupBlueprint = new JMenuItem("Blueprint");
				popupBlueprint.addActionListener(THIS);
				popupBlueprint.setEnabled(false);
				panelPopup.add(popupBlueprint);*/
				
				entryPoint = new EntryPoint(THIS);
				panel.add(entryPoint);
		
				Main.panel.requestFocusInWindow();
	        }
		});
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
	
	public static void updateFuncs(){
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
		Point p;
		if(((JComponent) e.getSource()).getParent() == panelPopup){
			p = clickLocation;
			if(c == "Add"){
				objects.add(new Arithmetic.Add(p,THIS));
			}else if(c == "Subtract"){
				objects.add(new Arithmetic.Subtract(p,THIS));
			}else if(c == "Multiply"){
				objects.add(new Arithmetic.Multiply(p, THIS));
			}else if(c == "Divide"){
				objects.add(new Arithmetic.Divide(p, THIS));
			}else if(c == "Random"){
				objects.add(new Arithmetic.Random(p, THIS));
			}else if(c == "Round"){
				objects.add(new Arithmetic.Round(p, THIS));
			}else if(c == "Concatenate"){
				objects.add(new Arithmetic.Concat(p, THIS));
			}else if(c == "Branch"){
				objects.add(new Logic.Branch(p, THIS));
			}else if(c == "While"){
				objects.add(new Logic.While(p, THIS));
			}else if(c == "Sequence"){
				objects.add(new Logic.Sequence(p, THIS));
			}else if(c == "Equals"){
				objects.add(new Logic.Equals(p, THIS));
			}else if(c == "Is Less Than"){
				objects.add(new Logic.LessThan(p, THIS));
			}else if(c == "Is Greater Than"){
				objects.add(new Logic.GreaterThan(p, THIS));
			}else if(c == "Is Less Than Or Equal To"){
				objects.add(new Logic.LessOrEqual(p, THIS));
			}else if(c == "Is Greater Than Or Equal To"){
				objects.add(new Logic.GreaterOrEqual(p, THIS));
			}else if(c == "And"){
				objects.add(new Logic.And(p, THIS));
			}else if(c == "Or"){
				objects.add(new Logic.Or(p, THIS));
			}else if(c == "Not"){
				objects.add(new Logic.Not(p, THIS));
			}else if(c == "Log To Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new Log(p, (GraphEditor) THIS));
			}else if(c == "Get String From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new getStr(p, Variable.DataType.STRING, (GraphEditor) THIS));
			}else if(c == "Get Number From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new getStr(p, Variable.DataType.DOUBLE, (GraphEditor) THIS));
			}else{
				System.out.println("null Action:"+c);
			}
		}else{
			p = VObject.getFreePosition();
			if(c == "New Integer"){
				Main.variables.add(0, new VInt(this));
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Double"){
				Main.variables.add(0, new VDouble(this));
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Float"){
				Main.variables.add(0, new VFloat(this));
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Boolean"){
				Main.variables.add(0, new VBoolean(this));
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New String"){
				Main.variables.add(0, new VString(this));
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "Run (f1)"){
				Debug.f1();
			}else if(c == "Fast Debug (f2)"){
				Debug.f2();
			}else if(c == "Debug (f3)"){
				Debug.f3();
			}else{
				System.out.println("null Action:"+c);
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
	public void mouseDragged(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_TAB){
			Debug.tab();
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
	static class DisplayPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		private GraphEditor owner;
		
		DisplayPanel(GraphEditor owner){
			this.owner = owner;
			this.setFocusTraversalKeysEnabled(false);
			this.setLayout(null);
			this.setBackground(new Color(0x5D5D5D));
		}
		
		@Override
		public Dimension getPreferredSize(){
			Dimension dimension = new Dimension(1000, 1000);
			for(VObject o : Main.objects){
				if(o.getX() + o.getWidth() > dimension.width){
					dimension.width = o.getX() + o.getWidth() + 10;
				}
				if(o.getY() + o.getHeight() > dimension.height){
					dimension.height = o.getY() + o.getHeight() + 10;
				}
			}
			return dimension;
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.setColor(new Color(0x4A4A4A));
			for(int x = 0; x < this.getSize().width/gridWidth; x++){
				g.drawLine(x*gridWidth, 0, x*gridWidth, this.getSize().height);
			}
			for(int y = 0; y < this.getSize().height/gridWidth; y++){
				g.drawLine(0, y*gridWidth, this.getSize().width,y*gridWidth);
			}
			GradientPaint gradient = new GradientPaint(0, 0, new Color(20,20,20,200), 0, 20, new Color(0,0,0,0));
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(gradient);
			g2.fill(new Rectangle.Double(0, 0, this.getSize().width, this.getSize().height));
			
			gradient = new GradientPaint(0, this.getSize().height-20, new Color(0,0,0,0), 0, this.getSize().height, new Color(20,20,20,200));
			g2.setPaint(gradient);
			g2.fill(new Rectangle.Double(0, 0, this.getSize().width, this.getSize().height));
			
			gradient = new GradientPaint(0, 0, new Color(20,20,20,200), 20, 0, new Color(0,0,0,0));
			g2.setPaint(gradient);
			g2.fill(new Rectangle.Double(0, 0, this.getSize().width, this.getSize().height));
			
			gradient = new GradientPaint(this.getSize().width-20, 0, new Color(0,0,0,0), this.getSize().width, 0, new Color(20,20,20,200));
			g2.setPaint(gradient);
			g2.fill(new Rectangle.Double(0, 0, this.getSize().width, this.getSize().height));
			
			if(mousePos != null && Node.currentlyDragging != null)
				(new Curve(Node.currentlyDragging,mousePos)).draw(g);
			for(Curve curve : owner.getCurves()){
				curve.draw(g);
			}
		}
	}

}