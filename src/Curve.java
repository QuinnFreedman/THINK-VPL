import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;

public class Curve{
	CubicCurve2D c = new CubicCurve2D.Double();
	Point[] points = new Point[2];
	Node[] nodes = new Node[2];
	boolean[] isNode = new boolean[2];
	public void draw(Graphics g){
		Point[][] curvePoints = new Point[2][2];
		if(isNode[0]){
			curvePoints[0] = getPointsFromNode(nodes[0]);
		}else{
			curvePoints[0][0] = points[0];
			curvePoints[0][1] = points[0];
		}
		if(isNode[1]){
			curvePoints[1] = getPointsFromNode(nodes[1]);
		}else{
			curvePoints[1][0] = points[1];
			curvePoints[1][1] = points[1];
		}
		c.setCurve(curvePoints[0][0],curvePoints[0][1],curvePoints[1][1],curvePoints[1][0]);
		g.setColor(Color.white);
		((Graphics2D) g).draw(c);
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
		
		//c.setCurve(pointsFromNode[0], pointsFromNode[1], b, b);
	}
	private static Point[] getPointsFromNode(Node a){
		Point p1;
		Point p2 = null;
		p1 = new Point(a.getLocationOnScreen().x-Main.panel.getLocationOnScreen().x, a.getLocationOnScreen().y-Main.panel.getLocationOnScreen().y);
		switch(a.facing){
			case NORTH:
				p1.x += (a.getPreferredSize().width/2);
				p2 = new Point(p1.x,p1.y);
				p2.y -= 40;
				break;
			case SOUTH:
				p1.y += a.getPreferredSize().height;
				p1.x += (a.getPreferredSize().width/2);
				p2 = new Point(p1.x,p1.y);
				p2.y += 40;
				break;
			case EAST:
				p1.x += a.getPreferredSize().width;
				p1.y += (a.getPreferredSize().height/2);
				p2 = new Point(p1.x,p1.y);
				p2.x += 40;
				break;
			case WEST:
				p1.y += (a.getPreferredSize().height/2);
				p2 = new Point(p1.x,p1.y);
				p2.x -= 40;
				break;
		}
		return new Point[]{p1,p2};
	}
}