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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

 class VObject extends JPanel implements MouseInputListener{
	private static final long serialVersionUID = 1L;

	private String UNIQUE_ID;
	private static final SimpleDateFormat ID_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
	
	Boolean isDragged;
	int width;
	int height;
	int borderWidth;
	JPanel body;
	JLabel headerLabel;
	 GraphEditor owner;
	
	protected static Point getFreePosition(){
		return new Point(10,10);
	}
	VObject(GraphEditor owner){
		this.UNIQUE_ID = ID_FORMAT.format(new Date());
		this.owner = owner;
		this.addMouseListener(this);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		Main.componentMover.registerComponent(this);
		body = new JPanel();
		body.setOpaque(false);
		this.add(body,BorderLayout.CENTER);
		
		owner.getObjects().add(this);
		owner.getPanel().add(this);
		owner.getPanel().repaint();
		owner.getPanel().revalidate();
	}
	
	VObject() {
		
	}
	public void delete(){
		if(Debug.isStepping() && this.getClass() != PrimitiveFunctionSelector.class)
			return;
		
		if(this instanceof PrimitiveFunction){
			((PrimitiveFunction) this).removeFromParent();
		}else if(this instanceof UserFunc){
			((UserFunc) this).getParentVar().removeChild(((UserFunc) this));
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
	String getUniqueID() {
		return UNIQUE_ID;
	}
	void setUniqueID(String id) {
		UNIQUE_ID = id;
		
	}
}