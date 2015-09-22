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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main{
	
	static Point mousePos = new Point();
	static HashMap<Variable.DataType,Color> colors = new HashMap<Variable.DataType,Color>();
	static final Cursor DRAG_CURSOR = System.getProperty("os.name").startsWith("Mac OS X") ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	static boolean altPressed = false;
	static boolean ctrlPressed = false;
	
	static ComponentMover componentMover;
	static JFrame window;
	static EntryPoint entryPoint;
	static Blueprint mainBP;
	protected static JTabbedPane tabbedPane;
	static JPanel container;
	
	static ArrayList<Blueprint> blueprints;
	static ArrayList<Module> modules;
	static ArrayList<Executable> defaultLibrairy;
	
	static Image icon;
	
	private static final String MODULE_DIR = System.getProperty("user.home")+"/Documents/THINK VPL/Modules";
	static final String SAVE_DIR = System.getProperty("user.home")+"/Documents/THINK VPL/My Projects";
	static final String VERSION_ID = "0.1.4";
	
	private static String lastSave;
	private static JMenuItem mntmSave;
	
	private static boolean canChangeTitle = true;
	
	public static void main(String args[]){	
		colors.put(Variable.DataType.BOOLEAN, new Color(20,210,20));
		colors.put(Variable.DataType.INTEGER, Color.red);
		colors.put(Variable.DataType.DOUBLE, new Color(196,0,167));
		colors.put(Variable.DataType.FLOAT, new Color(207,0,91));
		colors.put(Variable.DataType.STRING, new Color(0,132,0));
		colors.put(Variable.DataType.OBJECT, new Color(69,168,230));
		colors.put(Variable.DataType.ARRAY, new Color(69,168,230));
		colors.put(Variable.DataType.GENERIC, Color.WHITE);
		colors.put(Variable.DataType.NUMBER, Color.GRAY);
		colors.put(Variable.DataType.FLEX, Color.GRAY);
		
		try {
			SidebarItem.bufferedImage = ImageIO.read(Main.class.getResource("/images/drag.png"));
		} catch (IOException e1) {
			Out.printStackTrace(e1);
		}
		
		defaultLibrairy = new ArrayList<Executable>();
		try {
			SwingUtilities.invokeAndWait(() -> {
				defaultLibrairy.add(new Arithmetic.Add());
				defaultLibrairy.add(new Arithmetic.Subtract());
				defaultLibrairy.add(new Arithmetic.Multiply());
				defaultLibrairy.add(new Arithmetic.Divide());
				defaultLibrairy.add(new Arithmetic.Mod());
				defaultLibrairy.add(new Arithmetic.Round());
				defaultLibrairy.add(new Arithmetic.Random());
				defaultLibrairy.add(new Arithmetic.Concatinate());
				defaultLibrairy.add(new Logic.Equals());
				defaultLibrairy.add(new Logic.Less_Than());
				defaultLibrairy.add(new Logic.Greater_Than());
				defaultLibrairy.add(new Logic.Less_Than_Or_Equal_To());
				defaultLibrairy.add(new Logic.Greater_Than_Or_Equal_To());
				defaultLibrairy.add(new Logic.And());
				defaultLibrairy.add(new Logic.Not());
				defaultLibrairy.add(new Logic.Or());
				defaultLibrairy.add(new FlowControl.Branch());
				defaultLibrairy.add(new FlowControl.While());
				defaultLibrairy.add(new FlowControl.Sequence());
				defaultLibrairy.add(new FlowControl.Wait());
				defaultLibrairy.add(new FlowControl.For());
				defaultLibrairy.add(new FlowControl.AdvancedFor());
				defaultLibrairy.add(new StringFuncs.Parse_Int());
				defaultLibrairy.add(new StringFuncs.Parse_Float());
				defaultLibrairy.add(new StringFuncs.Parse_Double());
				defaultLibrairy.add(new StringFuncs.Split());
				defaultLibrairy.add(new JavaScript_Math());
			
				for(Class c : SystemLib.class.getDeclaredClasses()){
					if(Executable.class.isAssignableFrom(c)){
						try {
							defaultLibrairy.add((Executable) c.newInstance());
						} catch (Exception e){
							e.printStackTrace();
						}
					}
				}
				
				modules = new ArrayList<Module>();
				
				ArrayList<Module> loadedModules = getAllJars(MODULE_DIR);
				if(loadedModules != null){
					loadedModules.removeAll(Collections.singleton(null));
				
					modules.addAll(loadedModules);
				}
				//modules.add(new modules.FileIO());
				//modules.add(new modules.CanvasModule());
				

				for(Module m : modules){
					m.setup();
				}
				
			});
		} catch (InvocationTargetException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> {
	        	setupWindow();
	        	setupGUI();
        });
		new File(SAVE_DIR).mkdirs();
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
           	//Thread t = new Thread() {
     	        // void run() {
                synchronized (Main.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                            altPressed = true;
                        }else if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                            ctrlPressed = true;
                        }else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE){
                        	if(Debug.isStepping()){
                        		Debug.exit();
                        	}
                        }else if (ke.getKeyCode() == KeyEvent.VK_TAB){
                        	Debug.tab();
                        }else if (ke.getKeyCode() == KeyEvent.VK_BACK_QUOTE){
                        	for(Variable v : mainBP.getVariables()){
                        		if(!(v instanceof VInstance)){
                        			Out.pln("\""+v.getID()+"\" : "+v.varData.getValueAsString());
                        		}else{
                        			Out.pln("\""+v.getID()+"\" : ");
                        			for(Variable v2 : ((VInstance) v).childVariables){
                        				Out.pln("	\""+v2.getID()+"\" : "+v2.varData.getValueAsString());
                        			}
                        		}
                        	}
                        	for(Variable v : mainBP.getVariables()){
                        		for(PrimitiveFunction pf : v.getChildren()){
                        			Out.pln(v.varData.getValueAsString()+" "+pf.getParentVarData().getValueAsString()+" "+(v.varData == pf.getParentVarData()));
                        		}
                        	}
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                        	altPressed = false;
                        } else if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                        	ctrlPressed = false;
                        }
                        break;
                    }
         	        return false;
                }
            }
        });
	}
	private static ArrayList<Module> getAllJars(String path){
		new File(MODULE_DIR).mkdirs();
		
		File folder = new File(path);
		Out.pln("Looking for files in "+path);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<Module> modules = new ArrayList<Module>();
		if(listOfFiles == null)
			return null;
		
		for(int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()){
				Out.pln("opening file " + listOfFiles[i].getName());
				modules.add(loadJar(path,listOfFiles[i].getName()));
			}else if(listOfFiles[i].isDirectory()) {
				ArrayList<Module> jars = getAllJars(path+"/"+listOfFiles[i].getName());
				if(jars != null)
					modules.addAll(jars);
			}
		}
		return modules;
	}
	private static Module loadJar(String path, String jar){
		try{
			ZipInputStream zip = new ZipInputStream(new FileInputStream(path+"/"+jar));
			//URL[] myJarFile = new URL[]{new URL("jar","","file:/"+path)};
			//URLClassLoader child = new URLClassLoader (myJarFile , Main.class.getClassLoader());
			for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
			    if(!entry.isDirectory() && entry.getName().endsWith(".class")){
			        
			    	String className = entry.getName().replace('/', '.');
			        className = className.substring(0, className.length() - ".class".length());
			        String[] classNameParts = className.replace('$', ':').split(":");
			        if(classNameParts.length == 1){
			        	Out.pln("trying to load classes from "+jar);
			        	addLibrary(path+"/"+jar);
			        	Class classToLoad = Class.forName(className);
			        	Out.pln("loaded "+classToLoad);
			        	if(classToLoad.getSuperclass() == Module.class){
			    			zip.close();
			        		return (Module) classToLoad.newInstance();
			        	}
			        }
			    }
			}
			zip.close();
		}catch (Exception e){
			Out.printStackTrace(e);
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
	
	private static void setupWindow(){
		MenuListener listener = new MenuListener();
		
		window = new JFrame();
		window.setTitle("Think - Main");//\u2148
		window.setSize(800,500);
		window.setMinimumSize(new Dimension(555,325));
		//window.setLocationByPlatform(true);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		icon = Toolkit.getDefaultToolkit().getImage(window.getClass().getResource("/images/icon.png"));
		window.setIconImage(icon);
		
		
		try {
			UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			Out.printStackTrace(e1);
		}
		
	//Menu Bar
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
	    menuBar.add(fileMenu);
	    
	    JMenuItem mntmNew = new JMenuItem("New");
	    mntmNew.addActionListener(listener);
	    mntmNew.setAccelerator(KeyStroke.getKeyStroke(
	            KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	    fileMenu.add(mntmNew);
	    
	    JMenuItem mntmOpen = new JMenuItem("Open");
	    mntmOpen.addActionListener(listener);
	    mntmOpen.setAccelerator(KeyStroke.getKeyStroke(
	            KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	    fileMenu.add(mntmOpen);
	    
	    mntmSave = new JMenuItem("Save");
	    mntmSave.setAccelerator(KeyStroke.getKeyStroke(
	            KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	    mntmSave.addActionListener(listener);
	    //mntmSave.setEnabled(lastSave != null);
	    fileMenu.add(mntmSave);
	    
	    JMenuItem mntmSaveAs = new JMenuItem("Save As...");
	    mntmSaveAs.addActionListener(listener);
	    mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
	    fileMenu.add(mntmSaveAs);
	    
	    fileMenu.addSeparator();
	    
	    JMenuItem mntmCompile = new JMenuItem("Compile...");
	    mntmCompile.addActionListener(listener);
	    mntmCompile.setAccelerator(KeyStroke.getKeyStroke("control shift E"));
	    fileMenu.add(mntmCompile);
	    
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmEdit = new JMenuItem("Cut");
		mntmEdit.addActionListener(listener);
		mntmEdit.setEnabled(false);
		mnEdit.add(mntmEdit);
		
		mntmEdit = new JMenuItem("Copy");
		mntmEdit.addActionListener(listener);
		mntmEdit.setEnabled(false);
		mnEdit.add(mntmEdit);
		
		mntmEdit = new JMenuItem("Paste");
		mntmEdit.addActionListener(listener);
		mntmEdit.setEnabled(false);
		mnEdit.add(mntmEdit);
		
		JMenu mnVariable = new JMenu("Variables");
		menuBar.add(mnVariable);
		
		JMenuItem mntmNewInteger = new JMenuItem("New Integer");
		mntmNewInteger.addActionListener(listener);
		mnVariable.add(mntmNewInteger);
		
		JMenuItem mntmNewFloat = new JMenuItem("New Float");
		mntmNewFloat.addActionListener(listener);
		mnVariable.add(mntmNewFloat);
		
		JMenuItem mntmNewDouble = new JMenuItem("New Double");
		mntmNewDouble.addActionListener(listener);
		mnVariable.add(mntmNewDouble);
		
		JMenuItem mntmNewLong = new JMenuItem("New Long");
		mntmNewLong.addActionListener(listener);
		mntmNewLong.setEnabled(false);
		mnVariable.add(mntmNewLong);
		
		JMenuItem mntmNewShort = new JMenuItem("New Short");
		mntmNewShort.addActionListener(listener);
		mntmNewShort.setEnabled(false);
		mnVariable.add(mntmNewShort);
		
		JMenuItem mntmNewByte = new JMenuItem("New Byte");
		mntmNewByte.addActionListener(listener);
		mntmNewByte.setEnabled(false);
		mnVariable.add(mntmNewByte);
		
		mnVariable.addSeparator();
		
		JMenuItem mntmNewBoolean = new JMenuItem("New Boolean");
		mntmNewBoolean.addActionListener(listener);
		mnVariable.add(mntmNewBoolean);
		
		mnVariable.addSeparator();
		
		JMenuItem mntmNewString = new JMenuItem("New String");
		mntmNewString.addActionListener(listener);
		mnVariable.add(mntmNewString);
		
		JMenuItem mntmNewCharacter = new JMenuItem("new Character");
		mntmNewCharacter.addActionListener(listener);
		mntmNewCharacter.setEnabled(false);
		mnVariable.add(mntmNewCharacter);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		JMenuItem mntmRun = new JMenuItem("Run");
		mntmRun.addActionListener(listener);
		mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnRun.add(mntmRun);
		
		mntmRun = new JMenuItem("Fast Debug");
		mntmRun.addActionListener(listener);
		mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		mnRun.add(mntmRun);
		
		mntmRun = new JMenuItem("Debug");
		mntmRun.addActionListener(listener);
		mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		mnRun.add(mntmRun);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Go to website");
		mntmHelp.addActionListener(listener);
		mnHelp.add(mntmHelp);
		
		window.setJMenuBar(menuBar);

		window.setFocusTraversalKeysEnabled(false);
		
		componentMover = new ComponentMover();
		componentMover.setEdgeInsets(new Insets(10, 10, 10, 10));
		
	}
	
	private static void setupGUI(){
		
		blueprints = new ArrayList<Blueprint>();
		
		mainBP = new Blueprint();
		mainBP.setName("Main");
		blueprints.add(mainBP);
		
		tabbedPane = new JTabbedPane();
		
		container = new JPanel();
		container.setLayout(new BorderLayout());
		container.add(tabbedPane, BorderLayout.CENTER);
		window.getContentPane().add(container);
		
		for(Blueprint bp : blueprints){
			tabbedPane.addTab(bp.getName(), bp.splitPane);
		}

		final JPanel plus = null;
		tabbedPane.addTab("+", plus);
		
		tabbedPane.addChangeListener(e -> {

			SwingUtilities.invokeLater(() -> {
	            if(tabbedPane.getSelectedComponent() == plus){
	            	InstantiableBlueprint ibp = new InstantiableBlueprint();
	            	blueprints.add(ibp);
            		canChangeTitle = true;
	            	tabbedPane.removeTabAt(tabbedPane.getTabCount()-1);
	            	tabbedPane.addTab("", ibp.splitPane);
	            	tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
	            	ibp.className.requestFocusInWindow();
	            	tabbedPane.addTab("+", plus);
	            }else{
	            	setWindowTitle(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
	            	if(tabbedPane.getSelectedComponent() == Main.mainBP.splitPane){
	            		canChangeTitle = false;
	            	}else{
	            		canChangeTitle = true;
	            	}
	            }
	        });
				
	    });
		window.setVisible(true);
		
		
		entryPoint = new EntryPoint(mainBP);
		mainBP.getPanel().add(entryPoint);
		
		
		if(mainBP != null)
			mainBP.getPanel().requestFocusInWindow();

	}
	
	static void setWindowTitle(String s){
		if(canChangeTitle && s != null && !s.isEmpty())
			window.setTitle("Think - "+s);
	}
	
	static Blueprint getOpenClass(){
		return blueprints.get(tabbedPane.getSelectedIndex());
	}
	
	private static class MenuListener implements ActionListener{
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
			}else if(c == "Run"){
				Debug.f1();
			}else if(c == "Fast Debug"){
				Debug.f2();
			}else if(c == "Debug"){
				Debug.f3();
			}else if(c == "Go to website"){
				String url = "http://thinkvpl.org#about";
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
				} catch (IOException e1) {
					Out.printStackTrace(e1);
				}
			}else if(c == "Save As..."){
				saveAs();
			}else if(c == "New"){
				window.getContentPane().removeAll();
				setupGUI();
			}else if(c == "Open"){
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(SAVE_DIR));
				FileFilter filter = new FileNameExtensionFilter("THINK Graph","graph");
			    jfc.setFileFilter(filter);
				int result = jfc.showOpenDialog(window);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = jfc.getSelectedFile();
				    Out.pln("Selected file: " + selectedFile.getAbsolutePath());
					if(!selectedFile.exists() || !selectedFile.isFile()){
				    	Out.pln("no such file");
				    	return;
				    }
				    try {
						/*ObjectInputStream is = new ObjectInputStream(new FileInputStream(selectedFile.getAbsolutePath()));
						final SaveBundle save = (SaveBundle) is.readObject();
						is.close();
						Out.pln("loaded file");
						window.dispose();
						
						SwingUtilities.invokeLater(new Runnable() {
					        @Override
					        public void run() {
					        	window.getContentPane().removeAll();
					        	setupGUI(save.blueprints);
				        }});
						
						Out.pln("restored save");*/
				    	
				    	window.getContentPane().removeAll();
			        	setupGUI();
			        	
				    	SaveFileIO.read(selectedFile);
				    	
						lastSave = selectedFile.getAbsolutePath();
						//mntmSave.setEnabled(true);
					} catch (Exception e1){
						e1.printStackTrace();
						String message = "Error loading file "+selectedFile.getAbsolutePath();
						error(message);
					}
				}
			}else if(c == "Save"){
				if(lastSave == null)
					saveAs();
				else
					saveFile(lastSave);
			}else if(c == "Compile..."){
				try{
					Compiler.compile();
				}catch(Exception e1){
					e1.printStackTrace();
					String s = "Error compiling file:\n";
					s += ((e1.getMessage() == null || e1.getMessage().isEmpty()) ? e1.getClass().getSimpleName() : e1.getMessage());
					error(e1.getMessage());
				}
			}else{
				Out.pln("null Action: "+c);
			}
			
	    }
		private static void saveAs() {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File(SAVE_DIR));
			FileFilter filter = new FileNameExtensionFilter("THINK Graph","graph");
		    jfc.setFileFilter(filter);
			int result = jfc.showSaveDialog(window);
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = jfc.getSelectedFile();
			    Out.pln("Selected file: " + selectedFile.getAbsolutePath());
			    if(!selectedFile.getName().endsWith(".graph")){
			    	selectedFile = new File(selectedFile.getAbsolutePath()+".graph");
			    }
			    lastSave = selectedFile.getAbsolutePath();
				//mntmSave.setEnabled(true);
				saveFile(selectedFile.getAbsolutePath());
			}
		}
		
		private static void saveFile(String path){
			try {
				//ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
				//os.writeObject(new SaveBundle());
				//os.close();
				SaveFileIO.write(new File(path));
				Out.pln("created file");
			} catch (Exception e1){
				e1.printStackTrace();
				String message = "Error saving file "+path;
				error(message);
			}
		}
	}
	static String crop(String s, int i){
		if(s.length() <= i){
			return s;
		}
		return s.substring(0, i-2)+"...";
	}
	public static void warn(String string) {
		JOptionPane.showMessageDialog(window,
			    string,
			    "Warning",
			    JOptionPane.WARNING_MESSAGE);
	}
	public static void error(String string) {
		JOptionPane.showMessageDialog(window,
				string,
				"Error",
		        JOptionPane.ERROR_MESSAGE);
	}

}