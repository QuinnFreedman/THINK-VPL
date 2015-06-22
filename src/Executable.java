import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Executable extends VObject{
	private static final long serialVersionUID = 1L;

	protected Color color;
	protected static enum Mode{
		IN,OUT,BOTH,NONE
	};
	NodeHolder inputNodeHolder;
	NodeHolder outputNodeHolder;
	public Mode getPrimairyMode(){return Mode.BOTH;};
	private ArrayList<Node> inputNodes;
	private ArrayList<Node> outputNodes;
	protected int activeNode;
	public ArrayList<VariableData> workingData;
	protected boolean selected = false;
	protected boolean executeOnce;
	protected boolean hasExecuted = false;
	public ArrayList<VariableData> outputData;
	
	protected int defaultActiveNode = 1;
	
	protected Class<? extends Module> getParentMod(){
		return null;
	}
	
	public ArrayList<Variable.DataType> getInputs(){
		ArrayList<Variable.DataType> list = new ArrayList<Variable.DataType>();
		for(Node n : getInputNodes()){
			list.add(n.dataType);
		}
		return list;
	};
	public ArrayList<Variable.DataType> getOutputs(){
		ArrayList<Variable.DataType> list = new ArrayList<Variable.DataType>();
		for(Node n : getOutputNodes()){
			list.add(n.dataType);
		}
		return list;
	};
	
	protected ArrayList<Node> getInputNodes(){
		return inputNodes;
	}
	protected void setInputNodes(ArrayList<Node> nodes){
		inputNodes = nodes;
	}
	protected ArrayList<Node> getOutputNodes(){
		return outputNodes;
	}
	protected void setOutputNodes(ArrayList<Node> nodes){
		outputNodes = nodes;
	}
	protected void addInputNode(Node node) {
		inputNodes.add(node);
		inputNodeHolder.add(node);
		inputNodeHolder.revalidate();
		inputNodeHolder.repaint();
	}
	protected void addOutputNode(Node node) {
		outputNodes.add(node);
		outputNodeHolder.add(node);
		outputNodeHolder.revalidate();
		outputNodeHolder.repaint();
	}

	public void removeInputNode(Node n) {
		Node.clearChildren(n);
		inputNodeHolder.remove(n);
		owner.getNodes().remove(n);
		inputNodes.remove(n);
		inputNodeHolder.revalidate();
		inputNodeHolder.repaint();
	}

	public void removeOutputNode(Node n) {
		Node.clearChildren(n);
		outputNodeHolder.remove(n);
		owner.getNodes().remove(n);
		outputNodes.remove(n);
		outputNodeHolder.revalidate();
		outputNodeHolder.repaint();
	}
	public void setSelected(boolean b){
		selected = b;
	}
	public void resetActiveNode() {
		if(!this.getInputNodes().isEmpty() && this.getInputNodes().get(0).dataType == Variable.DataType.GENERIC){
			this.activeNode = 1;
		}else{
			this.activeNode = 0;
		}
	}
	public int getActiveNode() {
		return activeNode;
	}
	public void incrementActiveNode() {
		activeNode++;
	}

	public ArrayList<Variable.DataType> getTypeInputs(){
		return null;
	}
	public ArrayList<Variable.DataType> getTypeOutputs(){
		return null;
	}
	
	public ArrayList<String> getInputTooltips(){
		return null;
	}
	
	public ArrayList<String> getOutputTooltips(){
		return null;
	}
	
	Executable(Point pos, GraphEditor owner){
		super(owner);
		this.color = Color.BLACK;
		
		inputNodes = new ArrayList<Node>();
		outputNodes = new ArrayList<Node>();
		
		inputNodeHolder = new NodeHolder();
		outputNodeHolder = new NodeHolder();
		
		this.add(inputNodeHolder,BorderLayout.PAGE_START);
		this.add(outputNodeHolder,BorderLayout.PAGE_END);

		if(!(this instanceof EntryPoint || this instanceof Constant
				|| this instanceof Cast || this instanceof FunctionEditor.FunctionIO
				|| this instanceof PrimitiveFunction)){
			body.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			if(!(this instanceof Arithmetic || this instanceof Logic))
				headerLabel.setText(getSimpleName());
			body.add(headerLabel,gbc);
		}
		if(!(this instanceof PrimitiveFunction)){
			if(getInputs() != null){
				for(Variable.DataType dt : getInputs()){
					if(dt == Variable.DataType.GENERIC)
						addInputNode(new Node(Node.NodeType.RECIEVING,this,dt,true));
					else
						addInputNode(new Node(Node.NodeType.RECIEVING,this,dt,false));
				}
			}
			if(getOutputs() != null){
				for(Variable.DataType dt : getOutputs()){
					boolean b = (dt != Variable.DataType.GENERIC);
					addOutputNode(new Node(Node.NodeType.SENDING,this,dt,b));
				}
			}
		}
		if(getInputTooltips() != null){
			for(int i = 0; i < getInputTooltips().size(); i++){
				getInputNodes().get(i + 1).setToolTipText(getInputTooltips().get(i));
			}
		}
		if(getOutputTooltips() != null){
			for(int i = 0; i < getInputTooltips().size(); i++){
				getOutputNodes().get(i + 1).setToolTipText(getOutputTooltips().get(i));
			}
		}
		
		if(pos != null)
			setBounds(new Rectangle(pos,getSize()));
	}
	
	Executable() {
		super();
	}
	public VariableData execute(VariableData[] inputs){
		return null;
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(selected){
			 Point2D start = new Point2D.Float(0, 0);
		     Point2D end = new Point2D.Float(this.getWidth()/2, 0);
		     float[] dist = {0.0f, 0.7f};
		     Color[] colors = {new Color(0,0,0,0), Color.YELLOW};
		     LinearGradientPaint p =
		         new LinearGradientPaint(start, end, dist, colors, MultipleGradientPaint.CycleMethod.REFLECT);
			
			g2.setPaint(p);
			g2.setStroke(new BasicStroke(5));
			g2.draw(new RoundRectangle2D.Double(-3, inputNodeHolder.getHeight()-8, this.getSize().width+4, this.body.getSize().height+14, 20, 20));
		}
		GradientPaint gradient = new GradientPaint(0, 
				inputNodeHolder.getHeight()-5, 
				color,
				0,
				this.getHeight()/2,
				new Color(20,20,20,127),true);
		
		g2.setPaint(gradient);
	    g2.fill(new RoundRectangle2D.Double(0, inputNodeHolder.getHeight()-5, this.getSize().width, this.body.getSize().height+10, 20, 20));
	    g2.setPaint(Color.black);
	}
	static class NodeHolder extends JPanel{
		private static final long serialVersionUID = 1L;

		NodeHolder(){
			this.setOpaque(false);
			//this.setPreferredSize(new Dimension(15,15));
			((FlowLayout) this.getLayout()).setVgap(0);
		}
		
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(Math.max((((FlowLayout) this.getLayout()).getHgap()+15)*this.getComponentCount(),15),15);
		}
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
	public String getPathName(){
		String s = "";
		if(this instanceof PrimitiveFunction && this.owner != ((PrimitiveFunction) this).getParentVariable().getOwner()){
			System.out.println("this instanceof PrimitiveFunction");
			//System.out.println(this.owner+" != "+((PrimitiveFunction) this).getParentVar().getOwner());
			if(((PrimitiveFunction) this).getParentVariable().getOwner() instanceof Blueprint){
				s += (((Blueprint) ((PrimitiveFunction) this).getParentVariable().getOwner()).getName()+" > ");
				System.out.println(s);
			}else if(((PrimitiveFunction) this).getParentVariable().getOwner() instanceof FunctionEditor){
				s += (((Blueprint) ((FunctionEditor) ((PrimitiveFunction) this).getParentVariable().getOwner()).getOverseer()).getName()+" > ");
			}
		}else if(this instanceof UserFunc){
			if(((UserFunc) this).getParentVar() instanceof SidebarItem){
				if(this.owner != ((VFunction) ((UserFunc) this).getParentVar()).getOwner()){
					s+= (((Blueprint) ((VFunction) ((UserFunc) this).getParentVar()).getOwner()).getName()+" > ");
				}
				if(/*!((VFunction) ((UserFunc) this).getParentVar()).isStatic && */
						((VFunction) ((UserFunc) this).getParentVar()).parentInstance != null){
					s += (((VFunction) ((UserFunc) this).getParentVar()).parentInstance.getID()+" > ");
				}
				s += ((VFunction) ((UserFunc) this).getParentVar()).getID();
			}else{
				s += "CONSTRUCTOR";
			}
			
		}
		return s;
	}
	public String getSimpleName(){
		return ((getParentMod() == null) ? "" : getParentMod().getName()+" > ") + this.getClass().getSimpleName().replace('_',' ');
	}
}