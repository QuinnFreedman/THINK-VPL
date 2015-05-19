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
import java.awt.event.MouseMotionListener;
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
	static JPanel panel;
	private static JPanel vars;
	private static JPopupMenu panelPopup;
	private static Point clickLocation;
	public static EntryPoint entryPoint;
	
	public static void main(String[] args){
		new Main();
		colors.put(Variable.DataType.BOOLEAN, Color.green);
		colors.put(Variable.DataType.INTEGER, Color.red);
		colors.put(Variable.DataType.DOUBLE, new Color(196,0,167));
		colors.put(Variable.DataType.FLOAT, new Color(207,0,91));
		colors.put(Variable.DataType.GENERIC, Color.WHITE);
		
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
                        		Debug.tab();
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
				JFrame window = new JFrame();
				window.setTitle("VisualIDE");
				window.setSize(800,500);
				window.setMinimumSize(new Dimension(555,325));
				window.setLocationRelativeTo(null);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
			
				try {
					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
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
				
				JMenu mnAdd = new JMenu("Add");
				menuBar.add(mnAdd);
				
				JMenuItem mntmBoolean = new JMenuItem("Boolean");
				mntmBoolean.addActionListener(THIS);
				mnAdd.add(mntmBoolean);
				
				JMenuItem mntmInt = new JMenuItem("Integer");
				mntmInt.addActionListener(THIS);
				mnAdd.add(mntmInt);
				
				JMenuItem mntmDouble = new JMenuItem("Double");
				mntmDouble.addActionListener(THIS);
				mnAdd.add(mntmDouble);
				
				mnAdd.addSeparator();
				
				JMenuItem mntmMath = new JMenuItem("Math");
				mntmMath.addActionListener(THIS);
				mnAdd.add(mntmMath);
				
				JMenuItem mntmFunction = new JMenuItem("Function");
				mntmFunction.addActionListener(THIS);
				mnAdd.add(mntmFunction);
				
				JMenuItem mntmTimeline = new JMenuItem("Timeline");
				mntmTimeline.addActionListener(THIS);
				mnAdd.add(mntmTimeline);
				
				JMenuItem mntmBlueprint = new JMenuItem("Blueprint");
				mntmBlueprint.addActionListener(THIS);
				mnAdd.add(mntmBlueprint);
				
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
				JScrollPane scrollVars = new JScrollPane(vars);
				BoxLayout layout = new BoxLayout(vars, BoxLayout.Y_AXIS);
				vars.setLayout(layout);
				
				JButton addVar = new JButton("+");
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
				
				scrollVars.addKeyListener(THIS);//TODO doesn't work
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
				
				JMenuItem popupBoolean = new JMenuItem("Boolean");
				popupBoolean.addActionListener(THIS);
				panelPopup.add(popupBoolean);
				
				JMenuItem popupInt = new JMenuItem("Integer");
				popupInt.addActionListener(THIS);
				panelPopup.add(popupInt);
				
				JMenuItem popupDouble = new JMenuItem("Double");
				popupDouble.addActionListener(THIS);
				panelPopup.add(popupDouble);
				
				panelPopup.addSeparator();
				
				JMenuItem popupMath = new JMenuItem("Math");
				popupMath.addActionListener(THIS);
				panelPopup.add(popupMath);
				
				JMenuItem popupFunction = new JMenuItem("Function");
				popupFunction.addActionListener(THIS);
				panelPopup.add(popupFunction);
				
				JMenuItem popupTimeline = new JMenuItem("Timeline");
				popupTimeline.addActionListener(THIS);
				panelPopup.add(popupTimeline);
				
				JMenuItem popupBlueprint = new JMenuItem("Blueprint");
				popupBlueprint.addActionListener(THIS);
				panelPopup.add(popupBlueprint);
				
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
		}else{
			p = VObject.getFreePosition();
		}
		/*if(c == "Boolean"){
			objects.add(new VBoolean(p));
		}else if(c == "Integer"){
			objects.add(new VInt(p));
		}else if(c == "Double"){
			objects.add(new VDouble(p));
		}else */if(c == "Math"){
			//objects.add(new VMath(p));
		}else if(c == "Timeline"){
			objects.add(new Timeline(p));
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