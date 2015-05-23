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

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class Main implements ActionListener, MouseInputListener, KeyListener{
	static ArrayList<VObject> objects = new ArrayList<VObject>();
	static ArrayList<Curve> curves = new ArrayList<Curve>();
	static ArrayList<Node> nodes = new ArrayList<Node>();
	static final int gridWidth = 10;
	public static ArrayList<Variable> variables = new ArrayList<Variable>();
	static Point mousePos = new Point();
	static HashMap<Variable.DataType,Color> colors = new HashMap<Variable.DataType,Color>();
	public static boolean altPressed = false;
	/**
	 * @wbp.parser.entryPoint
	 */
	static ComponentMover componentMover;
	public static JFrame window;
	static JPanel panel;
	static JButton addVar;
	private static JPanel vars;
	private JScrollPane scrollVars;
	private static JPopupMenu panelPopup;
	private static Point clickLocation;
	public static EntryPoint entryPoint;
	
	
	public static void main(String[] args){
		new Main();
		colors.put(Variable.DataType.BOOLEAN, new Color(20,210,20));
		colors.put(Variable.DataType.INTEGER, Color.red);
		colors.put(Variable.DataType.DOUBLE, new Color(196,0,167));
		colors.put(Variable.DataType.FLOAT, new Color(207,0,91));
		colors.put(Variable.DataType.STRING, new Color(0,132,0));
		colors.put(Variable.DataType.GENERIC, Color.WHITE);
		colors.put(Variable.DataType.NUMBER, Color.GRAY);
		colors.put(Variable.DataType.FLEX, Color.GRAY);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (Main.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                            altPressed = true;
                        }else if (ke.getKeyCode() == KeyEvent.VK_F1){
                        	if(!Debug.isStepping()){
                        		Main.panel.requestFocusInWindow();
                        		Debug.f1();
                        	}else{
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
		Main THIS = this;
		SwingUtilities.invokeLater(new Runnable() {

	        @Override
	        public void run() {
				window = new JFrame();
				window.setTitle("VisualIDE");
				window.setSize(800,500);
				window.setMinimumSize(new Dimension(555,325));
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
				mnVariable.add(mntmNewCharacter);
				
				JMenu mnHelp = new JMenu("Help");
				menuBar.add(mnHelp);
				
				panel = new DisplayPanel();
				panel.setPreferredSize(new Dimension(1000, 1000));
				
				JScrollPane scrollPane = new JScrollPane(panel);
					
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
						Variable v = new Variable();
						variables.add(0,v);
						updateVars();
						variables.get(0).fields.get(0).requestFocusInWindow();
						scrollVars.getViewport().setViewPosition(new Point(0,0));
					}
				});
				
				scrollVars.setMinimumSize(minimumSize);
				varsContainer.add(scrollVars);
				
				
				JPanel funcsContainer = new JPanel();
				funcsContainer.setLayout(new BoxLayout(funcsContainer, BoxLayout.Y_AXIS));
				
				JLabel lblFuncs = new JLabel("Functions");
				lblFuncs.setAlignmentX(Component.CENTER_ALIGNMENT);
				funcsContainer.add(lblFuncs);
				JPanel funcs = new JPanel();
				//funcs.setLayout(new BoxLayout(vars,BoxLayout.Y_AXIS));
				JScrollPane scrollFuncs = new JScrollPane(funcs);
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
				
				entryPoint = new EntryPoint();
				panel.add(entryPoint);
		
				Main.panel.requestFocusInWindow();
	        }
		});
	}
	public static void updateVars(){
		vars.removeAll();
		for(Variable var : variables){
			vars.add(var);
		}
		vars.repaint();
		vars.revalidate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		Point p;
		if(((JComponent) e.getSource()).getParent() == panelPopup){
			p = clickLocation;
			if(c == "Add"){
				objects.add(new Arithmetic.AddDouble(p));
			}else if(c == "Subtract"){
				objects.add(new Arithmetic.Subtract(p));
			}else if(c == "Multiply"){
				objects.add(new Arithmetic.Multiply(p));
			}else if(c == "Divide"){
				objects.add(new Arithmetic.Divide(p));
			}else if(c == "Concatenate"){
				objects.add(new Arithmetic.Concat(p));
			}else if(c == "Branch"){
				objects.add(new Logic.Branch(p));
			}else if(c == "Equals"){
				objects.add(new Logic.Equals(p));
			}else if(c == "Is Less Than"){
				objects.add(new Logic.LessThan(p));
			}else if(c == "Is Greater Than"){
				objects.add(new Logic.GreaterThan(p));
			}else if(c == "Is Less Than Or Equal To"){
				objects.add(new Logic.LessOrEqual(p));
			}else if(c == "Is Greater Than Or Equal To"){
				objects.add(new Logic.GreaterOrEqual(p));
			}else if(c == "Log To Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new Log(p));
			}else if(c == "Get String From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new getStr(p, Variable.DataType.STRING));
			}else if(c == "Get Number From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				objects.add(Debug.console.new getStr(p, Variable.DataType.DOUBLE));
			}else{
				System.out.println("null Action:"+c);
			}
		}else{
			p = VObject.getFreePosition();
			if(c == "New Integer"){
				Main.variables.add(0, new VInt());updateVars();
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Double"){
				Main.variables.add(0, new VDouble());
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Float"){
				Main.variables.add(0, new VFloat());
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New Boolean"){
				Main.variables.add(0, new VBoolean());
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
			}else if(c == "New String"){
				Main.variables.add(0, new VString());
				updateVars();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
				Main.variables.get(0).nameField.requestFocusInWindow();
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

		DisplayPanel(){
			this.setFocusTraversalKeysEnabled(false);
			this.setLayout(null);
			this.setBackground(new Color(0x5D5D5D));
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
			for(Curve curve : curves){
				curve.draw(g);
			}
		}
	}

}