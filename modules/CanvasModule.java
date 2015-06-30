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

package modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import think.Executable;
import think.GraphEditor;
import think.Module;
import think.Variable.DataType;
import think.VariableData;

public class CanvasModule extends Module{
	static Canvas currentlyOpen;
	private static ArrayList<Object> shapes= new ArrayList<Object>();
	private static ArrayList<Boolean> isFill = new ArrayList<Boolean>();
	
	 static void addShape(Shape a, Boolean solid){
		shapes.add(a);
		isFill.add(solid);
	}
	
	 static void setColor(Color a){
		shapes.add(a);
		isFill.add(false);
	}
	
	 static void setLineWidth(float a){
		shapes.add(new BasicStroke(a));
		isFill.add(false);
	}
	
	 static void clear(){
		shapes.clear();
		isFill.clear();
	}
	
	 static void update(){
		currentlyOpen.canvas.repaint();
	}
	
	@Override
	public void setup(){
		currentlyOpen = new Canvas();
		setCanvasSize(200,200);
		addFunction(new Update());
		addFunction(new Clear());
		addFunction(new Set_Color());
		addFunction(new Set_Line_Width());
		addFunction(new Set_Canvas_Size());
		addFunction(new Hide_Canvas());
		addFunction(new Show_Canvas());
		addFunction(new Get_Canvas_Width());
		addFunction(new Get_Canvas_Height());
		addFunction(new Set_Antialiasing_Enabled());
		addFunction(new Fill_Rectangle());
		addFunction(new Draw_Rectangle());
		addFunction(new Fill_Circle());
		addFunction(new Draw_Circle());
		addFunction(new Draw_Line());
	}
	
	@Override
	public void run(){
		clear();
		update();
		currentlyOpen.setVisible(false);
	}
	
	private static void setCanvasSize(int x, int y){
		currentlyOpen.canvas.setPreferredSize(new Dimension(x,y));
		currentlyOpen.pack();
	}
	
	public static class CanvasExecutable extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		CanvasExecutable(Point pos, GraphEditor owner) {
			super(pos, owner);
			
		}
		
		CanvasExecutable(){
			
		}
	}
	
	public static class Update extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			CanvasModule.update();
			return null;
		}
		
		Update(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Update(){
			
		}
	}
	
	public static class Clear extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			CanvasModule.clear();
			return null;
		}
		
		public Clear(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Clear(){
			
		}
	}
	
	public static class Set_Color extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.INTEGER,
					DataType.INTEGER,
					DataType.INTEGER));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					"Red",
					"Green",
					"Blue"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			int red = Math.min(Math.max(0, ((VariableData.Integer) input[0]).value),255);
			int green = Math.min(Math.max(0, ((VariableData.Integer) input[1]).value),255);
			int blue = Math.min(Math.max(0, ((VariableData.Integer) input[2]).value),255);
			setColor(new Color(red,green,blue));
			return null;
		}
		
		public Set_Color(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Color(){
			
		}
	}
	
	public static class Set_Line_Width extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.FLOAT));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					"Line Width"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			setLineWidth(((VariableData.Float) input[0]).value);
			return null;
		}
		
		public Set_Line_Width(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Line_Width(){
			
		}
	}
	
	public static class Set_Canvas_Size extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.INTEGER,
					DataType.INTEGER));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					"Width",
					"Height"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			setCanvasSize(((VariableData.Integer) input[0]).value,((VariableData.Integer) input[1]).value);
			return null;
		}
		
		public Set_Canvas_Size(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Canvas_Size(){
			
		}
	}
	
	public static class Show_Canvas extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			currentlyOpen.setVisible(true);
			return null;
		}
		
		public Show_Canvas(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Show_Canvas(){
			
		}
	}
	
	public static class Hide_Canvas extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			currentlyOpen.setVisible(false);
			return null;
		}
		
		public Hide_Canvas(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Hide_Canvas(){
			
		}
	}
	
	public static class Set_Antialiasing_Enabled extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			shapes.add(((VariableData.Boolean) input[0]).value ? "ANTIALIASING_ENABLED" : "ANTIALIASING_DISABLED");
			isFill.add(false);
			return null;
		}
		
		public Set_Antialiasing_Enabled(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Antialiasing_Enabled(){
			
		}
	}
	
	public static class Get_Canvas_Width extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.INTEGER));
		}
		@Override
		public ArrayList<DataType> getInputs(){
			return null;
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.Integer(currentlyOpen.canvas.getWidth());
		}
		
		public Get_Canvas_Width(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Canvas_Width(){
			
		}
	}
	
	public static class Get_Canvas_Height extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.INTEGER));
		}
		@Override
		public ArrayList<DataType> getInputs(){
			return null;
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.Integer(currentlyOpen.canvas.getHeight());
		}
		
		public Get_Canvas_Height(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Canvas_Height(){
			
		}
	}
	
	public static class Fill_Rectangle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;

		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					" X ",
					" Y ",
					"Width",
					"Height"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			addShape(new Rectangle2D.Double(((
					VariableData.Double) input[0]).value,
					((VariableData.Double) input[1]).value,
					((VariableData.Double) input[2]).value,
					((VariableData.Double) input[3]).value
					),true);
			
			return null;
			
		}
		
		public Fill_Rectangle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Fill_Rectangle(){
			
		}
	}
	
	public static class Draw_Rectangle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					" X ",
					" Y ",
					"Width",
					"Height"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			addShape(new Rectangle2D.Double(((
						VariableData.Double) input[0]).value,
						((VariableData.Double) input[1]).value,
						((VariableData.Double) input[2]).value,
						((VariableData.Double) input[3]).value
					),false);
			
			return null;
			
		}
		
		public Draw_Rectangle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Draw_Rectangle(){
			
		}
	}
	
	public static class Fill_Circle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					" X ",
					" Y ",
					"Width",
					"Height"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			addShape(new Ellipse2D.Double(((
						VariableData.Double) input[0]).value,
						((VariableData.Double) input[1]).value,
						((VariableData.Double) input[2]).value,
						((VariableData.Double) input[3]).value
					),true);
			
			return null;
			
		}
		
		public Fill_Circle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Fill_Circle(){
			
		}
		
	}
	public static class Draw_Circle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;

		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					" X ",
					" Y ",
					"Width",
					"Height"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			addShape(new Ellipse2D.Double(((
						VariableData.Double) input[0]).value,
						((VariableData.Double) input[1]).value,
						((VariableData.Double) input[2]).value,
						((VariableData.Double) input[3]).value
					),false);
			
			return null;
			
		}
		
		public Draw_Circle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Draw_Circle(){
			
		}
	}
	
	public static class Draw_Line extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE,
					DataType.DOUBLE));
		}
		
		@Override
		public ArrayList<String> getInputTooltips(){
			return new ArrayList<String>(Arrays.asList(
					"x1",
					"y1",
					"x2",
					"y2"));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			addShape(new Line2D.Double(((
						VariableData.Double) input[0]).value,
						((VariableData.Double) input[1]).value,
						((VariableData.Double) input[2]).value,
						((VariableData.Double) input[3]).value
					),false);
			
			return null;
			
		}
		
		public Draw_Line(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Draw_Line(){
			
		}
	}
	
	private static class Canvas extends JFrame{
		JPanel canvas;
		
		Canvas(){
			super();
			canvas = new JPanel(){
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void paintComponent(Graphics g){
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g;
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(Color.BLACK);
					for(int i = 0; i < shapes.size(); i++){
						Object o = shapes.get(i);
						if(o instanceof Color){
							g2.setColor((Color) o);
						}else if(o instanceof Shape){
							if(isFill.get(i)){
								g2.fill((Shape) o);
							}else{
								g2.draw((Shape) o);
							}
						}else if(o instanceof Stroke){
							g2.setStroke((Stroke) o);
						}else if(o instanceof String){
							if(((String) o).equals("ANTIALIASING_ENABLED")){
								g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
								g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
							}else{
								g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
								g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
							}
						}
					}
				}
				{}};
			getContentPane().add(canvas);
			setResizable(false);
		}
	}
}