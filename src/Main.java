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
import java.awt.Toolkit;
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
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.JLabel;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class Main implements ActionListener{
	static Point mousePos = new Point();
	static HashMap<Variable.DataType,Color> colors = new HashMap<Variable.DataType,Color>();
	public static boolean altPressed = false;
	/**
	 * @wbp.parser.entryPoint
	 */
	static ComponentMover componentMover;
	public static JFrame window;
	static Point clickLocation;
	public static EntryPoint entryPoint;
	public static Main THIS;
	public static Blueprint mainBP;
	protected static JTabbedPane tabbedPane;
	
	static ArrayList<Blueprint> blueprints;
	static ArrayList<Module> modules;
	
	public static void main(String[] args){
		THIS = new Main();
		colors.put(Variable.DataType.BOOLEAN, new Color(20,210,20));
		colors.put(Variable.DataType.INTEGER, Color.red);
		colors.put(Variable.DataType.DOUBLE, new Color(196,0,167));
		colors.put(Variable.DataType.FLOAT, new Color(207,0,91));
		colors.put(Variable.DataType.STRING, new Color(0,132,0));
		colors.put(Variable.DataType.OBJECT, new Color(69,168,230));
		colors.put(Variable.DataType.GENERIC, Color.WHITE);
		colors.put(Variable.DataType.NUMBER, Color.GRAY);
		colors.put(Variable.DataType.FLEX, Color.GRAY);
		
		try {
			SidebarItem.bufferedImage = ImageIO.read(Main.class.getResource("/images/drag.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
           	//Thread t = new Thread() {
     	        //public void run() {
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
                        }else if (ke.getKeyCode() == KeyEvent.VK_TAB){
                        	Debug.tab();
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

     			//}};
     	        //t.start();
     	        }
        });
	}
	
	Main(){
		THIS = this;
		SwingUtilities.invokeLater(new Runnable() {

	        @Override
	        public void run() {
				window = new JFrame();
				window.setTitle("Think - Main");//\u2148
				window.setSize(800,500);
				window.setMinimumSize(new Dimension(555,325));
				//window.setLocationByPlatform(true);
				window.setLocationRelativeTo(null);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				window.setIconImage(Toolkit.getDefaultToolkit().getImage(window.getClass().getResource("/images/icon.png")));
				
				
				try {
					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				
				blueprints = new ArrayList<Blueprint>();
				modules = new ArrayList<Module>();
				
				mainBP = new Blueprint();
				mainBP.setName("Main");
				blueprints.add(mainBP);
				
				modules.add(new CanvasModule());
				
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
				
				tabbedPane = new JTabbedPane();
				
				for(Blueprint bp : blueprints){
					tabbedPane.addTab(bp.getName(), bp.splitPane);
				}
				
				for(Module m : modules){
					m.setup();
				}
				
				JPanel plus = null;
				
				tabbedPane.addTab("+", plus);
				
				tabbedPane.addChangeListener(new ChangeListener() {
			        public void stateChanged(ChangeEvent e) {
			            if(tabbedPane.getSelectedComponent() == plus){
			            	InstantiableBlueprint ibp = new InstantiableBlueprint();
			            	blueprints.add(ibp);
			            	tabbedPane.removeTabAt(tabbedPane.getTabCount()-1);
			            	tabbedPane.addTab("new_Blueprint", ibp.splitPane);
			            	tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
			            	ibp.className.requestFocusInWindow();
			            	tabbedPane.addTab("+", plus);
			            }
			            //window.setTitle(Main.blueprints.get(tabbedPane.getSelectedIndex()).getName());
			        }
			    });
				
				JPanel container = new JPanel();
				container.setLayout(new BorderLayout());
				container.add(tabbedPane, BorderLayout.CENTER);
				
				window.getContentPane().add(container);
				
				window.setJMenuBar(menuBar);
				
				window.setVisible(true);
				
				window.setFocusTraversalKeysEnabled(false);

				componentMover = new ComponentMover();
				componentMover.setEdgeInsets(new Insets(10, 10, 10, 10));
				
				entryPoint = new EntryPoint(getOpenClass());
				getOpenClass().getPanel().add(entryPoint);
				
				getOpenClass().getPanel().requestFocusInWindow();
	        }
		});
	}
	
	public static Blueprint getOpenClass(){
		return blueprints.get(tabbedPane.getSelectedIndex());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		if(c == "New Integer"){
			getOpenClass().getVariables().add(0, new VInt(getOpenClass()));
			getOpenClass().updateVars();
			getOpenClass().scrollVars.getViewport().setViewPosition(new Point(0,0));
			getOpenClass().getVariables().get(0).nameField.requestFocusInWindow();
		}else if(c == "New Double"){
			getOpenClass().getVariables().add(0, new VDouble(getOpenClass()));
			getOpenClass().updateVars();
			getOpenClass().scrollVars.getViewport().setViewPosition(new Point(0,0));
			getOpenClass().getVariables().get(0).nameField.requestFocusInWindow();
		}else if(c == "New Float"){
			getOpenClass().getVariables().add(0, new VFloat(getOpenClass()));
			getOpenClass().updateVars();
			getOpenClass().scrollVars.getViewport().setViewPosition(new Point(0,0));
			getOpenClass().getVariables().get(0).nameField.requestFocusInWindow();
		}else if(c == "New Boolean"){
			getOpenClass().getVariables().add(0, new VBoolean(getOpenClass()));
			getOpenClass().updateVars();
			getOpenClass().scrollVars.getViewport().setViewPosition(new Point(0,0));
			getOpenClass().getVariables().get(0).nameField.requestFocusInWindow();
		}else if(c == "New String"){
			getOpenClass().getVariables().add(0, new VString(getOpenClass()));
			getOpenClass().updateVars();
			getOpenClass().scrollVars.getViewport().setViewPosition(new Point(0,0));
			getOpenClass().getVariables().get(0).nameField.requestFocusInWindow();
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
	
	public static String crop(String s, int i){
		if(s.length() <= i){
			return s;
		}
		return s.substring(0, i-2)+"...";
	}

}