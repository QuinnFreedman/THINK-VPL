import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class VObject extends JPanel implements MouseInputListener{
	String id;
	Point position;
	Boolean isDragged;
	int width;
	int height;
	int borderWidth;
	JPanel body;
	JLabel headerLabel;
	
	protected static Point getFreePosition(){
		return new Point(10,10);
	}
	VObject(){
		this.addMouseListener(this);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());

		Main.componentMover.registerComponent(this);
		body = new JPanel();
		body.setOpaque(false);
		this.add(body,BorderLayout.CENTER);
	}
	
	public void delete(){
		if(this instanceof PrimitiveFunction){
			((PrimitiveFunction) this).removeFromParent();
		}
		Main.objects.remove(this);
		Main.panel.remove(this);
		Iterator<Curve> itr = Main.curves.iterator();
		Curve c = null;
		while(itr.hasNext()){
			c = itr.next();
			if(c.isNode[0] && c.nodes[0].parentObject == this){
				itr.remove();
				c.nodes[0].onDisconnect();
			}else if(c.isNode[1] && c.nodes[1].parentObject == this){
				itr.remove();
				c.nodes[1].onDisconnect();
			}
		}
		Iterator<Node> itrN = Main.nodes.iterator();
		Node n = null;
		while(itrN.hasNext()){
			n = itrN.next();
			if(n.parentObject == this){
				itrN.remove();
			}
		}
		Main.panel.repaint();
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