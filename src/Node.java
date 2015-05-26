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

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Node extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	
	static Node currentlyDragging;
	ArrayList<Node> parents = new ArrayList<Node>();
	ArrayList<Node> children = new ArrayList<Node>();
	VObject parentObject;
	Direction facing;
	NodeType type;
	private static JPopupMenu nodePopup;
	boolean canHaveMultipleInputs = true;
	boolean isHover = false;
	public Variable.DataType dataType;
	protected Dimension size = new Dimension(15,15);
	private ChildPicker childPicker;
	
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
	protected static void clearChildren(Node nodeToClear){
			System.out.println("clear children "+nodeToClear.type.toString()+" "+nodeToClear);
			Iterator<Curve> itr = nodeToClear.parentObject.owner.getCurves().iterator();
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
			nodeToClear.onDisconnect();
			
		
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
		
		if(!A.canHaveMultipleInputs)
			clearChildren(A);
		if(!B.canHaveMultipleInputs)
			clearChildren(B);
		
		B.parents.add(A);
		A.children.add(B);
		A.parentObject.owner.getCurves().add(new Curve(A,B));
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
			if(this.dataType != Variable.DataType.NUMBER && this.dataType != Variable.DataType.FLEX){
				g.setColor(Main.colors.get(this.dataType));
			}else{
				if(!this.parents.isEmpty()){
					g.setColor(Main.colors.get(this.parents.get(0).dataType));
				}else if(!this.children.isEmpty()){
					g.setColor(Main.colors.get(this.children.get(0).dataType));
				}else{
					g.setColor(Main.colors.get(this.dataType));
				}
			}
			g.fillArc(this.size.width/2 - 4, this.size.height/2 - 4, 8, 8, 0, 360);
		}
		if(isHover){
			g2.setColor(new Color(1f,1f,1f,0.5f));
			g.fillArc(0, 0, 14, 14, 0, 360);
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
	public void mouseClicked(MouseEvent e) {
		if(Debug.isStepping()){
			
			String s = "";
			
			ArrayList<VariableData> dataList = null;
			
			int index = 0;
			
			if(this.dataType == Variable.DataType.GENERIC){
				return;
			}
			if(this.type == NodeType.RECIEVING){
				if(((Executable) this.parentObject).workingData != null && !((Executable) this.parentObject).workingData.isEmpty()){
					dataList = ((Executable) this.parentObject).workingData;
					index = ((Executable) this.parentObject).getInputNodes().indexOf(this)
							-((((Executable) this.parentObject).getInputNodes().get(0).dataType == Variable.DataType.GENERIC) ? 1 : 0);
				}else{
					s = "undefined";
				}
			}else{
				if(((Executable) this.parentObject).outputData != null && !((Executable) this.parentObject).outputData.isEmpty()){
					dataList = ((Executable) this.parentObject).outputData;
					index = ((Executable) this.parentObject).getOutputNodes().indexOf(this)
							-((((Executable) this.parentObject).getOutputNodes().get(0).dataType == Variable.DataType.GENERIC) ? 1 : 0);
				}else{
					s = "undefined";
				}
			}
			
				
			if(s.equals("")){
				
				if(index < dataList.size()){
					VariableData data = dataList.get(index);
					s = data.getValueAsString();
					if(data.getClass() == VariableData.String.class){
						s = "\""+s+"\"";
					}else if(data.getClass() == VariableData.Charecter.class){
						s = "\'"+s+"\'";
					}
				}else{
					s = "undefined";
				}
			}
			
			nodePopup = new JPopupMenu();
			
			JMenuItem popupAdd = new JMenuItem(s);
			nodePopup.add(popupAdd);
			
			nodePopup.show(this, e.getX(), e.getY());
			
		}else if(Main.altPressed){
			clearChildren(this);
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(currentlyDragging != null && canConnect(currentlyDragging,this)){
			this.isHover = true;
		}
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		this.isHover = false;
		this.repaint();
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		if(Debug.isStepping())
			return;
		currentlyDragging = this;
		this.addMouseMotionListener(this);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(Main.altPressed){
			return;
		}
		currentlyDragging = null;
		this.removeMouseMotionListener(this);
		if(Debug.isStepping())
			return;
		Point mouse = getLocationOnPanel(e, this.parentObject.owner.getPanel());
		Rectangle rect = new Rectangle();
		for(Node node : Main.nodes){
			rect = new Rectangle(getLocationOnPanel(node,node.parentObject.owner.getPanel()),new Dimension(node.getWidth(),node.getHeight()));
			if(rect.contains(mouse) && canConnect(this,node)){
				
				castOrConnect(node,this);
				
				return;
				
			}
		}
		Point p = getLocationOnPanel(this,this.parentObject.owner.getPanel());
		rect = new Rectangle(
				new Point(p.x-this.getWidth()/2,p.y-this.getHeight()/2),new Dimension(2*this.getWidth(),2*this.getHeight()));
		if(rect.contains(mouse)){
			parentObject.owner.getPanel().repaint();
			return;
		}
		
		if(this.childPicker != null){
			childPicker.delete();
		}
		if(mouse.x > 0 && mouse.y > 0 && mouse.x < parentObject.owner.getPanel().getWidth() && mouse.y < parentObject.owner.getPanel().getHeight()){
			ChildPicker childPicker = new ChildPicker(this, mouse, parentObject.owner);
			Main.objects.add(childPicker);
			this.childPicker = childPicker;
		}
	}
	
	public static void castOrConnect(Node node1, Node node2){
		Node A;
		Node B;
		if(node1.type == NodeType.RECIEVING && node2.type == NodeType.SENDING){
			A = node2;
			B = node1;
		}else{
			A = node1;
			B = node2;
		}
		if(A.dataType == B.dataType || 
				((A.dataType == Variable.DataType.NUMBER && B.dataType.isNumber()) || 
				(B.dataType == Variable.DataType.NUMBER && A.dataType.isNumber()) ||
				(A.dataType == Variable.DataType.FLEX && B.dataType != Variable.DataType.NUMBER) ||
				(B.dataType == Variable.DataType.FLEX && A.dataType != Variable.DataType.NUMBER)
				)){
			connect(A,B);
		}else if(Cast.isCastable(A.dataType,B.dataType)){
			Main.objects.add(new Cast(A,B));
		}
		node2.parentObject.owner.getPanel().repaint();
	}
	
	public static boolean canConnect(Node node1, Node node2){
		if(node1.parentObject == node2.parentObject){
			return false;
		}
		if((node1.dataType == Variable.DataType.FLEX && node2.dataType != Variable.DataType.NUMBER) || 
				(node2.dataType == Variable.DataType.FLEX  && node1.dataType != Variable.DataType.NUMBER)){
			return true;
		}
		Node A;
		Node B;
		if(node1.type == NodeType.SENDING && node2.type == NodeType.RECIEVING){
			A = node1;
			B = node2;
		}else if(node1.type == NodeType.RECIEVING && node2.type == NodeType.SENDING){
			A = node2;
			B = node1;
		}else{
			return false;
		}
		
		
		if(B.parents.contains(A) || A.children.contains(B)){
			return false;
		}
		
		if(A.dataType == B.dataType){
			return true;
		}else if(Cast.isCastable(A.dataType,B.dataType)){
			if(Cast.isCastable(A.dataType, B.dataType)){
				return true;
			}else{
				return false;
			}
		}else if((A.dataType == Variable.DataType.NUMBER && B.dataType.isNumber()) || 
				(B.dataType == Variable.DataType.NUMBER && A.dataType.isNumber())
				){
			return true;
		}
		
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Main.mousePos = getLocationOnPanel(e, this.parentObject.owner.getPanel());
		this.parentObject.owner.getPanel().repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*Main.mousePos.x = e.getLocationOnScreen().x-this.getLocationOnScreen().x;
		Main.mousePos.x = e.getLocationOnScreen().y-this.getLocationOnScreen().y;
		Main.panel.repaint();*/
	}
	public static Point getLocationOnPanel(Component c, JPanel p){
		return new Point(c.getLocationOnScreen().x-p.getLocationOnScreen().x,c.getLocationOnScreen().y-p.getLocationOnScreen().y);
	}
	public static Point getLocationOnPanel(MouseEvent e, JPanel p){
		return new Point(e.getLocationOnScreen().x-p.getLocationOnScreen().x,e.getLocationOnScreen().y-p.getLocationOnScreen().y);
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