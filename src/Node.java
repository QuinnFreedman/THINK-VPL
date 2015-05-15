import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Node extends JPanel implements MouseListener, MouseMotionListener{
	static Node currentlyDragging;
	ArrayList<Node> parents = new ArrayList<Node>();
	ArrayList<Node> children = new ArrayList<Node>();
	VObject parentObject;
	Direction facing;
	NodeType type;
	NodeStyle style = null;
	boolean canHaveMultipleInputs = true;
	public ArrayList<Primative.DataType> dataType = new ArrayList<Primative.DataType>();
	protected Dimension size = new Dimension(30,20);
	Node(NodeType type,VObject parentObj,NodeStyle style){
		this.style = style;
		this.setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
		this.setOpaque(false);
		this.addMouseListener(this);
		this.facing = Direction.EAST;
		this.type = type;
		this.parentObject = parentObj;
		//if(parentObj.instance == Primative){//TODO use 'extends'/'typeof'?
		//	this.dataType = ((Primative) parentObj).dataType;
		//}else{
			//this.dataType = null;
		//}
	}
	Node(NodeType type,VObject parentObj){
		this(type,parentObj,NodeStyle.VISIBLE);
	}
	Node(Direction dir, NodeType type, VObject parentObj,NodeStyle style){
		this(type,parentObj,style);
		this.facing = dir;
	}
	Node(Direction dir, NodeType type, VObject parentObj){
		this(type,parentObj,NodeStyle.VISIBLE);
		this.facing = dir;
	}
	Node(Direction dir, NodeType type, VObject parentObj, ArrayList<Primative.DataType> dt){
		this(type,parentObj,NodeStyle.VISIBLE);
		this.facing = dir;
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
			VObject toRemove = null;
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
				if(node.parentObject instanceof Args){
					boolean argsHasInputs = false;
					for(Node inpNode : ((Args) node.parentObject).getInputNodes()){
						if(inpNode.parents != null && inpNode.parents.size() != 0){
							argsHasInputs = true;
							break;
						}
					}
					if(!argsHasInputs){
						if(toRemove != null){
							System.out.println("ERROR:clearChildren");
						}
						toRemove = node.parentObject;
					}else{
						if(node.type == Node.NodeType.RECIEVING){
							node.parents.remove(nodeToClear);
							nodeToClear.children.remove(node);
						}else{
							nodeToClear.parents.remove(node);
							node.children.remove(nodeToClear);
						}
						itr.remove();
					}
				}else{
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
			if(toRemove != null){
				toRemove.delete();
			}
		}
	}
	
	public static ArrayList<ArrayList<Primative.DataType>> complement(ArrayList<Primative.DataType> A, ArrayList<Primative.DataType> B){
		ArrayList<Primative.DataType> sourceList = new ArrayList<Primative.DataType>(A);
		ArrayList<Primative.DataType> destinationList = new ArrayList<Primative.DataType>(B);
		
		System.out.println("A : "+A);
		System.out.println("B : "+B);
		System.out.println("sourceList : "+sourceList);
		System.out.println("destinationList : "+destinationList);
		
		sourceList.removeAll(B);
		destinationList.removeAll(A);
		
		return new ArrayList<ArrayList<Primative.DataType>>(Arrays.asList(sourceList,destinationList));
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
		switch(style){
		case VISIBLE:
			g.setColor(Color.BLACK);
			g.fillArc(0, 0, this.size.width, this.size.height, 0, 360);
			if(this.dataType.size() == 1){
				g.setColor(Main.colors.get(this.dataType.get(0)));
				g.fillArc(this.size.width/2 - 5, this.size.height/2 - 5, 10, 10, 0, 360);
			}
		case INVISIBLE:
			
		}
	}
	/*@Override
	public Dimension getSize(){
		return new Dimension(300,300);
	}*/
	@Override
	public Dimension getPreferredSize(){
		switch(style){
		case VISIBLE:
			return size;
		case INVISIBLE:
			return new Dimension(0,0);
		}
		return null;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
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
		Point mouse = getLocationOnPanel(e);
		Rectangle rect = new Rectangle();
		boolean isHoverAnotherNode = false;
		for(Node node : Main.nodes){
			rect = new Rectangle(getLocationOnPanel(node),new Dimension(node.getWidth(),node.getHeight()));
			if(rect.contains(mouse)){
				isHoverAnotherNode = true;
				if(this.parentObject == node.parentObject){
					continue;
				}
				System.out.println("this : "+this);
				System.out.println("node : "+node);
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
					//System.out.println(this.dataType+", "+node.dataType);
					if(this.dataType.size() == 1 && this.dataType.get(0) == Primative.DataType.GENERIC && 
							((node.dataType.size() > 0 && node.dataType.get(0) != Primative.DataType.GENERIC) /*||
									(node.dataType.size() == 0)*/
									)
						)	//if this is generic and node isn't
					{
						System.out.println("this is generic");
						this.dataType = node.dataType;
						connect(this,node);
					}
					else if(node.dataType.size() == 1 && node.dataType.get(0) == Primative.DataType.GENERIC && 
						((this.dataType.size() > 0 && this.dataType.get(0) != Primative.DataType.GENERIC)/* ||
								(this.dataType.size() == 0)*/
								)
							)//if node is generic and this isn't
					{
						System.out.println("node is generic");
						node.dataType = this.dataType;
						connect(this,node);
					}else if((node.dataType.size() == 1 && node.dataType.get(0) == Primative.DataType.GENERIC && this.dataType.size() == 0) ||
							(this.dataType.size() == 1 && this.dataType.get(0) == Primative.DataType.GENERIC && node.dataType.size() == 0)
							){
						//Don't connect null data nodes with generic nodes
					}else{
						ArrayList<ArrayList<Primative.DataType>> compl = complement(this.dataType,node.dataType);
						System.out.println(compl);
						if(compl.get(0).size() == 0 && compl.get(1).size() == 0){
							node.parentObject.repaint();node.parentObject.revalidate();
							System.out.println("this.getLocationOnPanel() : "+Node.getLocationOnPanel(this));
							System.out.println("node.getLocationOnPanel() : "+Node.getLocationOnPanel(node));
							connect(this,node);
						}else if(this.parentObject.getClass() != Args.class && node.parentObject.getClass() != Args.class){
							new Args(this,node);
						}
					}
				}//TODO rules for generic
				break;
			}
		}
		if(!isHoverAnotherNode){
			if(this.parentObject instanceof Primative){
				ChildPicker cp = new ChildPicker(this.parentObject,this,new Point(mouse.x-20,mouse.y-20));
				Main.objects.add(this.parentObject);
				Main.panel.add(cp);
				Main.objects.add(cp);
				Main.panel.repaint();
				Main.panel.revalidate();
				//TODO create child
				//TODO make createChild method for each primitive type, instead of doing it here
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