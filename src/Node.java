import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JPanel;

public class Node extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	static Node currentlyDragging;
	ArrayList<Node> parents = new ArrayList<Node>();
	ArrayList<Node> children = new ArrayList<Node>();
	VObject parentObject;
	Direction facing;
	NodeType type;
	boolean canHaveMultipleInputs = true;
	public Variable.DataType dataType;
	protected Dimension size = new Dimension(15,15);
	Node(NodeType type,VObject parentObj,boolean mi){
		this.canHaveMultipleInputs = mi;
		this.setOpaque(false);
		this.addMouseListener(this);
		this.facing = (type == NodeType.SENDING || type == NodeType.INHERITANCE_SENDING) ? Direction.SOUTH : Direction.NORTH;
		this.type = type;
		this.parentObject = parentObj;
	}
	Node(NodeType type,VObject parentObj){
		this(type,parentObj,false);
	}
	Node(NodeType type, VObject parentObj, Variable.DataType dt){
		this(type,parentObj,false);
		this.dataType = dt;
	}
	public Node(NodeType type, VObject parentObj, Variable.DataType dt,
			boolean b) {
		this(type,parentObj,b);
		this.dataType = dt;
	}
	public void onConnect(){
		//override in subclass
	}
	public void onDisconnect(){
		//override in subclass
	}
	private static void clearChildren(Node nodeToClear){
		if(nodeToClear.canHaveMultipleInputs == false){
			nodeToClear.onDisconnect(); //TODO be more specific? ie call later in the method only if connection is actually removed
			Iterator<Curve> itr = Main.curves.iterator();
			while(itr.hasNext()){
				Curve c = itr.next();
				Node node;
				if(c.nodes[0] == nodeToClear){
					node = c.nodes[1];
				}else if(c.nodes[1] == nodeToClear){
					node = c.nodes[0];
				}else{
					continue;
				}
				
				if(node.type == Node.NodeType.RECIEVING){
					node.parents.remove(nodeToClear);
					nodeToClear.children.remove(node);
				}else{
					nodeToClear.parents.remove(node);
					node.children.remove(nodeToClear);
				}
				itr.remove();
				
			}
		}
	}
	
	public static ArrayList<ArrayList<Variable.DataType>> complement(ArrayList<Variable.DataType> A, ArrayList<Variable.DataType> B){
		ArrayList<Variable.DataType> sourceList = new ArrayList<Variable.DataType>(A);
		ArrayList<Variable.DataType> destinationList = new ArrayList<Variable.DataType>(B);
		
		System.out.println("A : "+A);
		System.out.println("B : "+B);
		System.out.println("sourceList : "+sourceList);
		System.out.println("destinationList : "+destinationList);
		
		sourceList.removeAll(B);
		destinationList.removeAll(A);
		
		return new ArrayList<ArrayList<Variable.DataType>>(Arrays.asList(sourceList,destinationList));
	}
	
	public static void connect(Node A, Node B){
		if(A.type == NodeType.RECIEVING && B.type == NodeType.SENDING){
			
			A.parents.add(B);
			B.children.add(A);
		
		}else if(B.type == NodeType.RECIEVING && A.type == NodeType.SENDING){
		
			B.parents.add(A);
			A.children.add(B);
		
		}else{
			System.out.println("connect Failed");
			return;
		}
		Main.curves.add(new Curve(A,B));
		A.onConnect();
		B.onConnect();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.fillArc(0, 0, 14, 14, 0, 360);
		if(this.dataType != Variable.DataType.GENERIC){
			g.setColor(Main.colors.get(this.dataType));
			g.fillArc(this.size.width/2 - 4, this.size.height/2 - 4, 8, 8, 0, 360);
		}
			
	}
	/*@Override
	public Dimension getSize(){
		return new Dimension(300,300);
	}*/
	@Override
	public Dimension getPreferredSize(){
		return size;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(Main.altPressed){
			clearChildren(this);
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		currentlyDragging = this;
		this.addMouseMotionListener(this);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		currentlyDragging = null;
		this.removeMouseMotionListener(this);
		Point mouse = getLocationOnPanel(e);
		Rectangle rect = new Rectangle();
		for(Node node : Main.nodes){
			rect = new Rectangle(getLocationOnPanel(node),new Dimension(node.getWidth(),node.getHeight()));
			if(rect.contains(mouse)){
				if(this.parentObject == node.parentObject){
					continue;
				}/*else if(this.parentObject instanceof PrimitiveFunction && node.parentObject instanceof PrimitiveFunction &&
						((PrimitiveFunction) this.parentObject).getParentVar() == ((PrimitiveFunction) node.parentObject).getParentVar()
					){
					continue;
				}*/
				System.out.println("this : "+this.type+" "+this);
				System.out.println("node : "+node.type+" "+node);
				if((this.type == NodeType.SENDING && node.type == NodeType.RECIEVING) ||
					(this.type == NodeType.RECIEVING && node.type == NodeType.SENDING))
				{
					if(this.type == NodeType.RECIEVING){
						if(this.parents.contains(node) || node.children.contains(this)){
							continue;
						}
					}else{
						if(node.parents.contains(this) || this.children.contains(node)){
							continue;
						}
					}
					clearChildren(this);
					clearChildren(node);
					if(this.dataType == node.dataType){
						connect(this,node);
					}
					break;
				}
			}
		}
		
		Main.panel.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Main.mousePos = getLocationOnPanel(e);
		Main.panel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*Main.mousePos.x = e.getLocationOnScreen().x-this.getLocationOnScreen().x;
		Main.mousePos.x = e.getLocationOnScreen().y-this.getLocationOnScreen().y;
		Main.panel.repaint();*/
	}
	public static Point getLocationOnPanel(Component c){
		return new Point(c.getLocationOnScreen().x-Main.panel.getLocationOnScreen().x,c.getLocationOnScreen().y-Main.panel.getLocationOnScreen().y);
	}
	public static Point getLocationOnPanel(MouseEvent e){
		return new Point(e.getLocationOnScreen().x-Main.panel.getLocationOnScreen().x,e.getLocationOnScreen().y-Main.panel.getLocationOnScreen().y);
	}
	public enum Direction{
		NORTH,SOUTH,EAST,WEST
	}
	public enum NodeType{
		SENDING,RECIEVING,INHERITANCE_SENDING,INHERITANCE_RECIEVING
	}
	public enum NodeStyle{
		INVISIBLE,VISIBLE
	}
}