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

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.io.Serializable;

class Curve implements Serializable{
	private static final long serialVersionUID = -1435605905680673953L;
	
	CubicCurve2D c = new CubicCurve2D.Double();
	Point[] points = new Point[2];
	Node[] nodes = new Node[2];
	boolean[] isNode = new boolean[2];
	public void draw(Graphics g){
		Point[][] curvePoints = new Point[2][2];
		int offset;
		if(isNode[0] && isNode[1]){
			double distance = Math.abs(Point.distance(getPointFromNode(nodes[0]).x,getPointFromNode(nodes[0]).y, getPointFromNode(nodes[1]).x,getPointFromNode(nodes[1]).y));
			offset = (int) Math.min(40, Math.max(distance/2,10));
		}else{
			offset = 40;
		}
		if(isNode[0]){
			curvePoints[0] = getPointsFromNode(nodes[0],offset);
		}else{
			curvePoints[0][0] = points[0];
			curvePoints[0][1] = points[0];
		}
		if(isNode[1]){
			curvePoints[1] = getPointsFromNode(nodes[1],offset);
		}else{
			curvePoints[1][0] = points[1];
			curvePoints[1][1] = points[1];
		}
		
		c.setCurve(curvePoints[0][0],curvePoints[0][1],curvePoints[1][1],curvePoints[1][0]);
		g.setColor(Main.colors.get(nodes[0].dataType));
		Graphics2D g2 = ((Graphics2D) g);
		g2.setStroke(new BasicStroke(2));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.draw(c);
	}
	Curve(Point a, Point b){
		points[0] = a;
		points[1] = b;
		isNode[0] = false;
		isNode[1] = false;
		
	}
	Curve(Node a, Point b){		
		nodes[0] = a;
		points[1] = b;
		isNode[0] = true;
		isNode[1] = false;
		
	}
	Curve(Node a, Node b){
		nodes[0] = a;
		nodes[1] = b;
		isNode[0] = true;
		isNode[1] = true;
		
	}
	private static Point[] getPointsFromNode(Node a, int offset){
		Point p1;
		Point p2 = null;
		p1 = new Point(a.getLocationOnScreen().x-a.parentObject.owner.getPanel().getLocationOnScreen().x, a.getLocationOnScreen().y-a.parentObject.owner.getPanel().getLocationOnScreen().y);
		switch(a.facing){
			case NORTH:
				p1.x += (a.getPreferredSize().width/2);
				p2 = new Point(p1.x,p1.y);
				p2.y -= offset;
				break;
			case SOUTH:
				p1.y += a.getPreferredSize().height;
				p1.x += (a.getPreferredSize().width/2);
				p2 = new Point(p1.x,p1.y);
				p2.y += offset;
				break;
			case EAST:
				p1.x += a.getPreferredSize().width;
				p1.y += (a.getPreferredSize().height/2);
				p2 = new Point(p1.x,p1.y);
				p2.x += offset;
				break;
			case WEST:
				p1.y += (a.getPreferredSize().height/2);
				p2 = new Point(p1.x,p1.y);
				p2.x -= offset;
				break;
		}
		return new Point[]{p1,p2};
	}
	private static Point getPointFromNode(Node a){
		Point p1;
		p1 = new Point(a.getLocationOnScreen().x-a.parentObject.owner.getPanel().getLocationOnScreen().x, a.getLocationOnScreen().y-a.parentObject.owner.getPanel().getLocationOnScreen().y);
		switch(a.facing){
			case NORTH:
				p1.x += (a.getPreferredSize().width/2);
				break;
			case SOUTH:
				p1.y += a.getPreferredSize().height;
				p1.x += (a.getPreferredSize().width/2);
				break;
			case EAST:
				p1.x += a.getPreferredSize().width;
				p1.y += (a.getPreferredSize().height/2);
				break;
			case WEST:
				p1.y += (a.getPreferredSize().height/2);
				break;
		}
		return p1;
	}
}