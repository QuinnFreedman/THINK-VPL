import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class CanvasModule extends Module{
	static Canvas currentlyOpen;
	private static ArrayList<Object> shapes= new ArrayList<Object>();
	private static ArrayList<Boolean> isFill = new ArrayList<Boolean>();
	
	public static void addShape(Shape a, Boolean solid){
		shapes.add(a);
		isFill.add(solid);
	}
	
	public static void setColor(Color a){
		shapes.add(a);
		isFill.add(false);
	}
	
	public static void setLineWidth(float a){
		shapes.add(new BasicStroke(a));
		isFill.add(false);
	}
	
	public static void clear(){
		shapes.clear();
		isFill.clear();
	}
	
	public static void update(){
		currentlyOpen.canvas.repaint();
	}
	
	@Override
	public String getName(){
		return "Canvas";
	}
	
	@Override
	public void setup(){
		currentlyOpen = new Canvas();
		setCanvasSize(200,200);
		functions.add(new Update());
		functions.add(new Clear());
		functions.add(new Set_Color());
		functions.add(new Set_Line_Width());
		functions.add(new Set_Canvas_Size());
		functions.add(new Hide_Canvas());
		functions.add(new Show_Canvas());
		functions.add(new Get_Canvas_Width());
		functions.add(new Get_Canvas_Height());
		functions.add(new Set_Antialiasing_Enabled());
		functions.add(new Fill_Rectangle());
		functions.add(new Draw_Rectangle());
		functions.add(new Fill_Circle());
		functions.add(new Draw_Circle());
		functions.add(new Draw_Line());
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
	
	static class CanvasExecutable extends Executable{
		private static final long serialVersionUID = 1L;
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}

		protected Class<? extends Module> getParentMod(){
			return CanvasModule.class;
		}
		
		CanvasExecutable(Point pos, GraphEditor owner) {
			super(pos, owner);
			
		}
		
		CanvasExecutable(){
			
		}
	}
	
	static class Update extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
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
	
	static class Clear extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			CanvasModule.clear();
			return null;
		}
		
		Clear(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Clear(){
			
		}
	}
	
	static class Set_Color extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER,
					Variable.DataType.INTEGER,
					Variable.DataType.INTEGER));
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
		
		Set_Color(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Color(){
			
		}
	}
	
	static class Set_Line_Width extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.FLOAT));
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
		
		Set_Line_Width(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Line_Width(){
			
		}
	}
	
	static class Set_Canvas_Size extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.INTEGER,
					Variable.DataType.INTEGER));
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
		
		Set_Canvas_Size(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Canvas_Size(){
			
		}
	}
	
	static class Show_Canvas extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			currentlyOpen.setVisible(true);
			return null;
		}
		
		Show_Canvas(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Show_Canvas(){
			
		}
	}
	
	static class Hide_Canvas extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			currentlyOpen.setVisible(false);
			return null;
		}
		
		Hide_Canvas(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Hide_Canvas(){
			
		}
	}
	
	static class Set_Antialiasing_Enabled extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			shapes.add(((VariableData.Boolean) input[0]).value ? "ANTIALIASING_ENABLED" : "ANTIALIASING_DISABLED");
			isFill.add(false);
			return null;
		}
		
		Set_Antialiasing_Enabled(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Antialiasing_Enabled(){
			
		}
	}
	
	static class Get_Canvas_Width extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return null;
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.Integer(currentlyOpen.canvas.getWidth());
		}
		
		Get_Canvas_Width(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Canvas_Width(){
			
		}
	}
	
	static class Get_Canvas_Height extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public ArrayList<Variable.DataType> getOutputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.INTEGER));
		}
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return null;
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			return new VariableData.Integer(currentlyOpen.canvas.getHeight());
		}
		
		Get_Canvas_Height(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Canvas_Height(){
			
		}
	}
	
	static class Fill_Rectangle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE));
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
		
		Fill_Rectangle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Fill_Rectangle(){
			
		}
	}
	
	static class Draw_Rectangle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE));
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
		
		Draw_Rectangle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Draw_Rectangle(){
			
		}
	}
	
	static class Fill_Circle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE));
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
		
		Fill_Circle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Fill_Circle(){
			
		}
		
	}
	static class Draw_Circle extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE));
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
		
		Draw_Circle(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Draw_Circle(){
			
		}
	}
	
	static class Draw_Line extends CanvasExecutable{
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.GENERIC,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE,
					Variable.DataType.DOUBLE));
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
		
		Draw_Line(Point pos, GraphEditor owner) {
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