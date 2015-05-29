import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class VObject extends JPanel implements MouseInputListener{
	Point position;
	Boolean isDragged;
	int width;
	int height;
	int borderWidth;
	JPanel body;
	JLabel headerLabel;
	public GraphEditor owner;
	
	protected static Point getFreePosition(){
		return new Point(10,10);
	}
	VObject(GraphEditor owner){
		this.owner = owner;
		this.addMouseListener(this);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		System.out.println(Main.componentMover);
		Main.componentMover.registerComponent(this);
		body = new JPanel();
		body.setOpaque(false);
		this.add(body,BorderLayout.CENTER);
		
		owner.getObjects().add(this);
		owner.getPanel().add(this);
		owner.getPanel().repaint();
		owner.getPanel().revalidate();
	}
	
	public VObject() {
		//Auto-generated constructor stub
	}
	public void delete(){
		if(Debug.isStepping() && this.getClass() != PrimitiveChildPicker.class)
			return;
		
		if(this instanceof PrimitiveFunction){
			((PrimitiveFunction) this).removeFromParent();
		}
		owner.getPanel().remove(this);
		Iterator<Curve> itr = owner.getCurves().iterator();
		Curve c = null;
		while(itr.hasNext()){
			c = itr.next();
			Node nodeCut = null;
			Node connectedNode = null;
			if(c.isNode[0] && c.nodes[0].parentObject == this){
				itr.remove();
				nodeCut = c.nodes[0];
				connectedNode = c.nodes[1];
			}else if(c.isNode[1] && c.nodes[1].parentObject == this){
				itr.remove();
				nodeCut = c.nodes[1];
				connectedNode = c.nodes[0];
			}
			
			if(nodeCut != null){
				for(Node node : nodeCut.children){
					node.parents.remove(nodeCut);
				}
				for(Node node : nodeCut.parents){
					node.children.remove(nodeCut);
				}
				connectedNode.onDisconnect();
			}
		}
		
		
		Iterator<Node> itrN = owner.getNodes().iterator();
		Node n = null;
		while(itrN.hasNext()){
			n = itrN.next();
			if(n.parentObject == this){
				itrN.remove();
			}
		}
		owner.getObjects().remove(this);
		owner.getPanel().repaint();
		owner.getPanel().revalidate();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint gradient = new GradientPaint(0, 0, Color.BLACK, 0, headerLabel.getPreferredSize().height,
				new Color(20,20,20,127));
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.body.getHeight()+20, 20, 20));
	    g2.setPaint(Color.black);
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
	public void mousePressed(MouseEvent e) {

		if(e.getButton() == MouseEvent.BUTTON3){
			if(this.getClass() != EntryPoint.class){
				this.delete();
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// Auto-generated method stub
		
	}
	
	
	/*static class inlineEditor extends JEditorPane{
		
		
		@Override
		public boolean getScrollableTracksViewportWidth() {
		    Component parent = getParent();
		    ComponentUI ui = getUI();

		    return parent != null ? (ui.getPreferredSize(this).width <= parent
		        .getSize().width) : true;
		}
	}*/
}