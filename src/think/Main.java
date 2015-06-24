/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main implements ActionListener{
	static Point mousePos = new Point();
	static HashMap<Variable.DataType,Color> colors = new HashMap<Variable.DataType,Color>();
	public static boolean altPressed = false;
	
	static ComponentMover componentMover;
	public static JFrame window;
	static Point clickLocation;
	public static EntryPoint entryPoint;
	public static Main THIS;
	public static Blueprint mainBP;
	protected static JTabbedPane tabbedPane;
	
	static ArrayList<Blueprint> blueprints;
	static ArrayList<Module> modules;
	
	private static final String MODULE_DIR = System.getProperty("user.home")+"/Documents/THINK VPL/Modules";
	
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
                        }else if (ke.getKeyCode() == KeyEvent.VK_BACK_QUOTE){
                        	for(Variable v : mainBP.getVariables()){
                        		if(!(v instanceof VInstance)){
                        			System.out.println("\""+v.getID()+"\" : "+v.varData.getValueAsString());
                        		}else{
                        			System.out.println("\""+v.getID()+"\" : ");
                        			for(Variable v2 : ((VInstance) v).childVariables){
                        				System.out.println("	\""+v2.getID()+"\" : "+v2.varData.getValueAsString());
                        			}
                        		}
                        	}
                        	for(Variable v : mainBP.getVariables()){
                        		for(PrimitiveFunction pf : v.getChildren()){
                        			System.out.println(v.varData.getValueAsString()+" "+pf.getParentVarData().getValueAsString()+" "+(v.varData == pf.getParentVarData()));
                        		}
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
	private ArrayList<Module> getAllJars(String s){
		File folder = new File(s);
		System.out.println("Looking for files in "+s);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<Module> modules = new ArrayList<Module>();
		
		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()){
				System.out.println("opening file " + listOfFiles[i].getName());
				modules.add(loadJar(listOfFiles[i].getName()));
			}else if(listOfFiles[i].isDirectory()) {
				modules.addAll(getAllJars(listOfFiles[i].getName()));
			}
		}
		return modules;
	}
	private Module loadJar(String jar){
		try{
			ZipInputStream zip = new ZipInputStream(new FileInputStream(MODULE_DIR+"/"+jar));
			URL[] myJarFile = new URL[]{new URL("jar","","file:/"+MODULE_DIR)};
			URLClassLoader child = new URLClassLoader (myJarFile , Main.class.getClassLoader());
			for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
			    if(!entry.isDirectory() && entry.getName().endsWith(".class")){
			        
			    	String className = entry.getName().replace('/', '.');
			        className = className.substring(0, className.length() - ".class".length());
			        String[] classNameParts = className.replace('$', ':').split(":");
			        if(classNameParts.length == 1){
			        	System.out.println("trying to load classes from "+jar);
			        	addLibrary(MODULE_DIR+"/"+jar);
			        	Class classToLoad = Class.forName(className);
			        	System.out.println("loaded "+classToLoad);
			        	if(classToLoad.getSuperclass() == Module.class){
			        		return (Module) classToLoad.newInstance();
			        	}
			        }
			    }
			}
			zip.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		String message = "Error loading moddule from "+MODULE_DIR+"/"+jar;
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		        JOptionPane.ERROR_MESSAGE);
		return null;
	}
	
	private static void addLibrary(String s) throws Exception {
		File file = new File(s);
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
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
				
				ArrayList<Module> loadedModules = getAllJars(MODULE_DIR);
				loadedModules.removeAll(Collections.singleton(null));
				
				modules.addAll(loadedModules);
				
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
				
				JMenuItem mntmEdit = new JMenuItem("Cut");
				mntmEdit.addActionListener(THIS);
				mntmEdit.setEnabled(false);
				mnEdit.add(mntmEdit);
				
				mntmEdit = new JMenuItem("Copy");
				mntmEdit.addActionListener(THIS);
				mntmEdit.setEnabled(false);
				mnEdit.add(mntmEdit);
				
				mntmEdit = new JMenuItem("Paste");
				mntmEdit.addActionListener(THIS);
				mntmEdit.setEnabled(false);
				mnEdit.add(mntmEdit);
				
				JMenu mnVariable = new JMenu("Variables");
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
				
				JMenuItem mntmHelp = new JMenuItem("Go to website");
				mntmHelp.addActionListener(THIS);
				mnHelp.add(mntmHelp);
				
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
		}else if(c == "Go to website"){
			String url = "https://preview.c9.io/quinnfreedman/think/index.html#about";
			try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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