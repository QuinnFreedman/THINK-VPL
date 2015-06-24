/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package think;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class DisplayPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		static final int gridWidth = 10;
		private GraphEditor owner;
		
		DisplayPanel(GraphEditor owner){
			this.owner = owner;
			this.setFocusTraversalKeysEnabled(false);
			this.setLayout(null);
			this.setBackground(new Color(0x5D5D5D));
		}
		
		@Override
		public Dimension getPreferredSize(){
			Dimension dimension = new Dimension(100, 100);
			for(VObject o : owner.getObjects()){
				if(o instanceof FunctionEditor.FunctionIO)
					continue;
				if(o.getX() + o.getWidth() > dimension.width){
					dimension.width = o.getX() + o.getWidth() + 15;
				}
				if(o.getY() + o.getHeight() > dimension.height){
					dimension.height = o.getY() + o.getHeight() + 15;
				}
			}
			return dimension;
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
			
			if(Main.mousePos != null && Node.currentlyDragging != null)
				(new Curve(Node.currentlyDragging,Main.mousePos)).draw(g);
			for(Curve curve : owner.getCurves()){
				curve.draw(g);
			}
		}
	}