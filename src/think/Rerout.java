/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015 Quinn Freedman
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javafx.geometry.NodeOrientation;

@SuppressWarnings("serial")
class Rerout extends Executable{
	
	static final int diameter = 25;
	
	Node inputNode;
	Node outputNode;
	private boolean flowUp = false;
	
	public Rerout(Point pos, GraphEditor owner, boolean flowUp) {
		this(pos, owner);
		if(flowUp)
			flip();
	}
	
	Rerout(Point pos, GraphEditor owner){
		super(pos, owner);
		
		this.body.removeAll();
		
		inputNode = new SpecialInputNode(this);
		outputNode = new SpecialOutputNode(this);
		
		addInputNode(inputNode);
		addOutputNode(outputNode);
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension(diameter, diameter);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		float fractions[] = {0.0f, 1.0f};
		Color colors[] = {new Color(20,20,20,127), Color.BLACK};
		
		RadialGradientPaint rgp = new RadialGradientPaint(
				getWidth()/2,
				getHeight()/2,
				diameter/2,
				fractions,
				colors
			);
		g2.setPaint(rgp);
		g2.fillOval(0, 0, getWidth(), getHeight());
		if(flowUp){
			g2.setColor(Color.WHITE);
			int f = g2.getFont().getSize();
			g2.setFont(new Font("Monospaced", Font.BOLD, f+6));
			String str = "^";
			FontMetrics fm = g2.getFontMetrics();
	        Rectangle2D r = fm.getStringBounds(str, g2);
	        int x = (this.getWidth() - (int) r.getWidth()) / 2;
	        int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
	        g.drawString(str, x, y);
		}
	}
	
	class SpecialInputNode extends Node{

		SpecialInputNode(VObject parentObj) {
			super(Node.NodeType.RECIEVING, parentObj, Variable.DataType.ALL, true);
		}
		
		@Override
		void onConnect() {
			if(inputNode.dataType == Variable.DataType.ALL){
				inputNode.dataType = this.parents.get(0).dataType;
				outputNode.dataType = this.parents.get(0).dataType;
			}
		}
		@Override
		void onDisconnect() {
			if(outputNode.children.isEmpty() && inputNode.parents.isEmpty()){
				inputNode.dataType = Variable.DataType.ALL;
				outputNode.dataType = Variable.DataType.ALL;
			}
		}
		
		@Override
		public void paintComponent(Graphics g){}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(10,12);
		}
	}
	
	class SpecialOutputNode extends Node{

		SpecialOutputNode(VObject parentObj) {
			super(Node.NodeType.SENDING, parentObj, Variable.DataType.ALL, false);
		}
		
		@Override
		void onConnect() {
			if(inputNode.dataType == Variable.DataType.ALL){
				inputNode.dataType = this.children.get(0).dataType;
				outputNode.dataType = this.children.get(0).dataType;
			}
		}
		@Override
		void onDisconnect() {
			if(outputNode.children.isEmpty() && inputNode.parents.isEmpty()){
				inputNode.dataType = Variable.DataType.ALL;
				outputNode.dataType = Variable.DataType.ALL;
			}
		}
		
		@Override
		public void paintComponent(Graphics g){}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(10,12);
		}
	}
	
	@Override
	public VariableData execute(VariableData[] inputs) throws Exception {
		return inputs[0];
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		flip();
	}
	
	private void flip(){
		flowUp = !flowUp;
		this.remove(inputNodeHolder);
		this.remove(outputNodeHolder);
		if(flowUp){
			this.add(inputNodeHolder,BorderLayout.PAGE_END);
			this.add(outputNodeHolder,BorderLayout.PAGE_START);
			this.inputNode.facing = Node.Direction.SOUTH;
			this.outputNode.facing = Node.Direction.NORTH;
		}else{
			this.add(inputNodeHolder,BorderLayout.PAGE_START);
			this.add(outputNodeHolder,BorderLayout.PAGE_END);
			this.inputNode.facing = Node.Direction.NORTH;
			this.outputNode.facing = Node.Direction.SOUTH;
		}
		this.owner.getPanel().repaint();
		this.owner.getPanel().revalidate();
	}
	
	public boolean getInversed() {
		return flowUp;
	}
}