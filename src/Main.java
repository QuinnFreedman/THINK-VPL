import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class Main implements ActionListener, MouseInputListener, KeyListener{
	static ArrayList<VObject> objects = new ArrayList<VObject>();
	static ArrayList<Curve> curves = new ArrayList<Curve>();
	static ArrayList<Node> nodes = new ArrayList<Node>();
	static final int gridWidth = 10;
	static Point mousePos = new Point();
	static HashMap<Primative.DataType,Color> colors = new HashMap<Primative.DataType,Color>();
	/**
	 * @wbp.parser.entryPoint
	 */
	static ComponentMover componentMover;
	static JPanel panel;
	private static JPopupMenu panelPopup;
	private static Point clickLocation;
	public static EntryPoint entryPoint;

	public static void main(String[] args){
		new Main();
		colors.put(Primative.DataType.BOOLEAN, Color.green);
		colors.put(Primative.DataType.INTEGER, Color.red);
		colors.put(Primative.DataType.DOUBLE, new Color(255,0,255));
		ArrayList<Primative.DataType> A = new ArrayList<Primative.DataType>();
		A.add(Primative.DataType.INTEGER);
		A.add(Primative.DataType.BOOLEAN);
		ArrayList<Primative.DataType> B = new ArrayList<Primative.DataType>();
		B.add(Primative.DataType.INTEGER);
		System.out.println(Node.complement(A, B));
	}
	
	Main(){
		JFrame window = new JFrame();
		window.setTitle("VisualIDE");
		window.setSize(555,325);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
            // Set System L&F
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		
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
		mntmBoolean.addActionListener(this);
		mnAdd.add(mntmBoolean);
		
		JMenuItem mntmInt = new JMenuItem("Integer");
		mntmInt.addActionListener(this);
		mnAdd.add(mntmInt);
		
		JMenuItem mntmDouble = new JMenuItem("Double");
		mntmDouble.addActionListener(this);
		mnAdd.add(mntmDouble);
		
		mnAdd.addSeparator();
		
		JMenuItem mntmMath = new JMenuItem("Math");
		mntmMath.addActionListener(this);
		mnAdd.add(mntmMath);
		
		JMenuItem mntmFunction = new JMenuItem("Function");
		mntmFunction.addActionListener(this);
		mnAdd.add(mntmFunction);
		
		JMenuItem mntmTimeline = new JMenuItem("Timeline");
		mntmTimeline.addActionListener(this);
		mnAdd.add(mntmTimeline);
		
		JMenuItem mntmBlueprint = new JMenuItem("Blueprint");
		mntmBlueprint.addActionListener(this);
		mnAdd.add(mntmBlueprint);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		panel = new DisplayPanel();
		panel.setPreferredSize(new Dimension(1000, 1000));
		
		JScrollPane scrollPane = new JScrollPane(panel);
		
		
		window.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		window.setJMenuBar(menuBar);
		
		window.setVisible(true);
		
		panel.setFocusTraversalKeysEnabled(false);
		
		componentMover = new ComponentMover();
		componentMover.setEdgeInsets(new Insets(10, 10, 10, 10));
		
		panel.addKeyListener(this);
		
		panelPopup = new JPopupMenu();
		panel.addMouseListener(this);
		
		JMenuItem popupBoolean = new JMenuItem("Boolean");
		popupBoolean.addActionListener(this);
		panelPopup.add(popupBoolean);
		
		JMenuItem popupInt = new JMenuItem("Integer");
		popupInt.addActionListener(this);
		panelPopup.add(popupInt);
		
		JMenuItem popupDouble = new JMenuItem("Double");
		popupDouble.addActionListener(this);
		panelPopup.add(popupDouble);
		
		panelPopup.addSeparator();
		
		JMenuItem popupMath = new JMenuItem("Math");
		popupMath.addActionListener(this);
		panelPopup.add(popupMath);
		
		JMenuItem popupFunction = new JMenuItem("Function");
		popupFunction.addActionListener(this);
		panelPopup.add(popupFunction);
		
		JMenuItem popupTimeline = new JMenuItem("Timeline");
		popupTimeline.addActionListener(this);
		panelPopup.add(popupTimeline);
		
		JMenuItem popupBlueprint = new JMenuItem("Blueprint");
		popupBlueprint.addActionListener(this);
		panelPopup.add(popupBlueprint);
		
		entryPoint = new EntryPoint();
		panel.add(entryPoint);
		
		panel.requestFocusInWindow();
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
		if(c == "Boolean"){
			objects.add(new VBoolean(p));
		}else if(c == "Integer"){
			objects.add(new VInt(p));
		}else if(c == "Double"){
			objects.add(new VDouble(p));
		}else if(c == "Math"){
			objects.add(new VMath(p));
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
		// Auto-generated method stub
		
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
		if(e.getKeyCode() == 9){
			Debug.tab();
		}else if(e.getKeyCode() == 88){
			//Point p = getLocationOnPanel();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Auto-generated method stub
		
	}
	static class DisplayPanel extends JPanel{
		DisplayPanel(){
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